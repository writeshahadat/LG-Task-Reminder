package com.legato.taskreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.legato.taskreminder.models.CalEvent;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    ImageView backIV, doneIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        backIV = findViewById(R.id.setup_back_iv);
        doneIV = findViewById(R.id.setup_done_iv);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        doneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void purgeEvents(View view){
        DbManager dbManager = new DbManager(this);
        List<CalEvent> calendarEvents = new ArrayList<>();
        calendarEvents = dbManager.getEvents();
        for (CalEvent event : calendarEvents){
            CalendarProviderManager.deleteCalendarEvent(this, event.getEventID());
        }
        long calID = CalendarProviderManager.obtainCalendarAccountID(this);
        List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(this, calID);
        for (CalendarEvent calendarEvent : events){
            CalendarProviderManager.deleteCalendarEvent(this, calendarEvent.getId());
            CalEvent calEvent = new CalEvent();
            calEvent = calEvent.setCalendarEvent(calendarEvent);
            calEvent.setStatus("Deleted");
            calEvent.setIsDeleted(1);
            dbManager.updateEvent(calEvent);
        }
        CalendarProviderManager.deleteCalendarAccountByName(this);

//        dbManager.purgeTable();
        Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}