package com.legato.taskreminder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.takisoft.datetimepicker.DatePickerDialog;
import com.takisoft.datetimepicker.TimePickerDialog;
import com.takisoft.datetimepicker.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    Button startTime, endTime, createBtn;
    Calendar cal;
    String startDate, endDate;
    SimpleDateFormat appDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    Date start, end;
    Spinner freqSpinner;
    EditText eventET, eventDescET, eventLocET;
    CheckBox autoCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        startTime = findViewById(R.id.ev_date_tv);
        endTime = findViewById(R.id.ev_time_tv);

        eventET = findViewById(R.id.ev_title_et);
        eventDescET = findViewById(R.id.ev_desc_et);
        eventLocET = findViewById(R.id.ev_location_tv);
        ImageView backIV = findViewById(R.id.create_back_iv);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        freqSpinner = findViewById(R.id.frequency_sp);
        String[] frequencies = {"Does not Repeat", "Everyday", "Every Week", "Every Month", "Every Year"};
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(
                this,
                R.layout.sp_item, frequencies);
        freqSpinner.setAdapter(spAdapter);
        freqSpinner.setSelection(0);

        cal = Calendar.getInstance();

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(CreateEventActivity.this, (view1, year, month, dayOfMonth) -> {
                    startDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", month + 1) + "-" + String.format("%d", year);
                    startTime.setText(startDate);
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dpd.show();
                dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        openForStart();
                    }
                });
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(CreateEventActivity.this, (view1, year, month, dayOfMonth) -> {
                    endDate = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", month + 1) + "-" + String.format("%d", year);
                    endTime.setText(startDate);
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dpd.show();
                dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        openForEnd();
                    }
                });
            }
        });

        autoCB = findViewById(R.id.auto_chk);
        autoCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!startTime.getText().toString().equals("Select Start Time")){
                        String time = startTime.getText().toString();
                        LocalTime localTime = LocalTime.parse(time);
                        localTime.plusMinutes(10);
                        endTime.setText(appDateFormat.format(localTime).toString());
                    }else {
                        Toast.makeText(CreateEventActivity.this, "Please Select The Start Time First!", Toast.LENGTH_SHORT).show();
                        autoCB.setChecked(false);
                    }
                }else {
                    endTime.setText("Select End Time");
                }
            }
        });


    }

    public void openForStart(){
        TimePickerDialog tpd = new TimePickerDialog(CreateEventActivity.this, (view1, hourOfDay, minute) -> {
            startDate = startDate + " " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            try {
                start = dateFormat.parse(startDate);
                startTime.setText(appDateFormat.format(start));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        tpd.show();
    }
    public void openForEnd(){
        TimePickerDialog tpd = new TimePickerDialog(CreateEventActivity.this, (view1, hourOfDay, minute) -> {
            endDate = endDate + " " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            try {
                end = dateFormat.parse(endDate);
                endTime.setText(appDateFormat.format(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        tpd.show();
    }
    public void createEvent(View view){
        String freq = null;
        if (!eventET.getText().toString().isEmpty() && !eventDescET.getText().toString().isEmpty() && start != null && end != null){

            switch (freqSpinner.getSelectedItemPosition()){
                case 0:
                    freq = "";
                    break;
                case 1:
                    freq = "FREQ=DAILY;INTERVAL=1";
                    break;
                case 2:
                    freq = "FREQ=WEEKLY;INTERVAL=1;WKST=SU;";
                    break;
                case 3:
                    freq = "FREQ=MONTHLY;INTERVAL=1;WKST=SU;";
                    break;
                case 4:
                    freq = "FREQ=YEARLY;BYYEARDAY=1,-1";
                    break;
            }
            CalendarEvent calendarEvent = new CalendarEvent(
                    eventET.getText().toString(),
                    eventDescET.getText().toString(),
                    eventLocET.getText().toString(),
                    start.getTime(),
                    end.getTime(),
                    0, freq, 1
            );
            Log.e("checking", "createEvent: " + calendarEvent.toString() );

            // 添加事件
            int result = CalendarProviderManager.addCalendarEvent(this, calendarEvent);
            if (result == 0) {
                Toast.makeText(this, "Event Created successfully", Toast.LENGTH_SHORT).show();
            } else if (result == -1) {
                Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
            } else if (result == -2) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            finish();
        }else{
            Toast.makeText(this, "Please fill up the form properly", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}