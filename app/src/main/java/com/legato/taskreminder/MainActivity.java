package com.legato.taskreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.legato.taskreminder.adapter.EventAdapter;
import com.legato.taskreminder.models.CalEvent;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements  ActionListenerInterface, EasyPermissions.PermissionCallbacks{

    public static final String EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";
    public static final String EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    TextView nameTV;
    static int CALENDAR_READ = 100;

    TabLayout tabLayout;

    SwipeableRecyclerView mRecyclerView;
    String[] perms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"), new Scope("https://www.googleapis.com/auth/calendar.events"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        tabLayout = findViewById(R.id.tabs);
        mRecyclerView = findViewById(R.id.events_rv);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        viewPending();
                        break;
                    case 1:
                        viewCompleted();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ImageView googleIV = findViewById(R.id.google_iv);
        googleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        perms = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};

       askPermission();



    }

    public void askPermission(){
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, CALENDAR_READ, perms)
                        .setRationale(R.string.calendar_read_write_rationale)
                        .setPositiveButtonText(R.string.rationale_ask_ok)
                        .setNegativeButtonText(R.string.rationale_ask_cancel)
//                        .setTheme(R.style.my_fancy_style)perms
                        .build());
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsGranted: " );
        viewPending();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: " );
        askPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == 0){
            if (tabLayout.getSelectedTabPosition() == 0){
                viewPending();
            }else {
                viewCompleted();
            }
        }

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }




    @Override
    public void doneClicked(CalEvent calendarEvent) {
        changeStatus(calendarEvent, "Completed");
    }
    @Override
    public void cancelClicked(CalEvent calendarEvent) {
        changeStatus(calendarEvent, "Cancelled");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        askPermission();
    }

    public void viewPending(){
        List<CalEvent> pendingEvents = new ArrayList<>();
        DbManager dbManager = new DbManager(this);
        long calID = CalendarProviderManager.obtainCalendarAccountID(this);
        List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(this, calID);
        for (CalendarEvent event : events) {
            if (!dbManager.searchEvent(event.getId())){
                CalEvent newEvent = new CalEvent();
                if (!event.getDescription().contains("Observance") && !event.getDescription().contains("Public holiday")){
                    pendingEvents.add(newEvent.setCalendarEvent(event));
                }

            }
        }
        EventAdapter eventAdapter = new EventAdapter(pendingEvents, this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(eventAdapter);
        mRecyclerView.setRightBg(R.color.red);
        mRecyclerView.setRightImage(R.drawable.ic_baseline_white_close_24);
        mRecyclerView.setRightText("Canceled ");

        mRecyclerView.setLeftBg(R.color.green);
        mRecyclerView.setLeftImage(R.drawable.ic_baseline_check_24);
        mRecyclerView.setLeftText("   Completed");
        mRecyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                changeStatus(pendingEvents.get(position), "Canceled");
                pendingEvents.remove(position);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwipedRight(int position) {
                changeStatus(pendingEvents.get(position), "Completed");
                pendingEvents.remove(position);
                eventAdapter.notifyDataSetChanged();
            }
        });
    }
    public void changeStatus(CalEvent calEvent, String status){
        DbManager dbManager = new DbManager(this);

        if (CalendarProviderManager.deleteCalendarEvent(MainActivity.this, calEvent.getCalendarEvent().getId()) != -2){
            Toast.makeText(this, "Moved to Completed Tab!", Toast.LENGTH_SHORT).show();
        }
        calEvent.setStatus(status);
        dbManager.insertEvent(calEvent);
    }

    public void viewCompleted(){
        DbManager dbManager = new DbManager(this);
        List<CalEvent> calEvents = dbManager.getEvents();
        EventAdapter eventAdapter = new EventAdapter(calEvents, this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(eventAdapter);

        mRecyclerView.setRightBg(R.color.red);
        mRecyclerView.setRightImage(R.drawable.ic_baseline_delete_forever_24);
        mRecyclerView.setRightText("Delete");

        mRecyclerView.setActivated(false);


        mRecyclerView.setLeftBg(R.color.red);
        mRecyclerView.setLeftImage(R.drawable.ic_baseline_delete_forever_24);
        mRecyclerView.setLeftText("Delete");
        mRecyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
                CalEvent calendarEvent = calEvents.get(position);
                calendarEvent.setStatus("Deleted");
                calendarEvent.setIsDeleted(1);
                removeEvent(calendarEvent);

                calEvents.remove(position);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwipedRight(int position) {
                CalEvent calendarEvent = calEvents.get(position);
                calendarEvent.setStatus("Deleted");
                calendarEvent.setIsDeleted(1);
                removeEvent(calendarEvent);

                calEvents.remove(position);
                eventAdapter.notifyDataSetChanged();
            }
        });
    }
    public void moveToPending(CalEvent calEvent){
        CalendarProviderManager.addCalendarEvent(this, calEvent.getCalendarEvent());
        DbManager dbManager = new DbManager(this);
        dbManager.deleteEvent(calEvent);
        Toast.makeText(this, "Moved to Pending Tab!", Toast.LENGTH_SHORT).show();
    }
    public void removeEvent(CalEvent calEvent){
        DbManager dbManager = new DbManager(this);
        dbManager.updateEvent(calEvent);
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
    }




    public void openSetup(View view){
        startActivity(new Intent(this, SetupActivity.class));
    }


    public void createEvent(View view){
        startActivity(new Intent(this, CreateEventActivity.class));
    }


    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }


    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    public void signOut(View view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
        findViewById(R.id.before_ll).setVisibility(View.VISIBLE);
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    private void updateUI(@Nullable GoogleSignInAccount account) {
        tabLayout.getTabAt(0).select();
        if (account != null) {
            ImageView photoIV = findViewById(R.id.photo_iv);
            nameTV = findViewById(R.id.name_tv);
            nameTV.setText(account.getDisplayName());
            findViewById(R.id.before_ll).setVisibility(View.GONE);
            findViewById(R.id.after_ll).setVisibility(View.VISIBLE);
            if (account.getPhotoUrl() != null){
                new LoadProfileImage(photoIV).execute(account.getPhotoUrl().toString());
            }


        } else {
            findViewById(R.id.after_ll).setVisibility(View.GONE);
            findViewById(R.id.before_ll).setVisibility(View.VISIBLE);
        }
    }

    public void openCalendar(View view) {
        Uri calendarUri = CalendarContract.CONTENT_URI
                .buildUpon()
                .appendPath("time")
                .build();
        startActivity(new Intent(Intent.ACTION_VIEW, calendarUri));
    }


    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(MainActivity.this,
                        resized,250,200,200,
                        false, false, false, false));
                bmImage.setVisibility(View.VISIBLE);
            }
        }
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}