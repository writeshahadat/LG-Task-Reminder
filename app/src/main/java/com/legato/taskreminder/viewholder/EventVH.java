package com.legato.taskreminder.viewholder;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.legato.taskreminder.R;
import com.legato.taskreminder.models.CalEvent;

public class EventVH extends RecyclerView.ViewHolder {
    TextView dateTV, titleTV, descTV, idTV, statusTV, freqTV;
    ImageView doneIV, cancelIV;
    public EventVH(@NonNull View itemView) {
        super(itemView);
        dateTV = itemView.findViewById(R.id.date_tv);
        titleTV = itemView.findViewById(R.id.title_tv);
        descTV = itemView.findViewById(R.id.desc_tv);
        idTV = itemView.findViewById(R.id.event_id_tv);
        statusTV = itemView.findViewById(R.id.event_status_tv);
        doneIV = itemView.findViewById(R.id.done_iv);
        cancelIV = itemView.findViewById(R.id.cancel_iv);
        freqTV = itemView.findViewById(R.id.freq_tv);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void bindData(CalEvent calendarEvent){
        dateTV.setText(getDate(calendarEvent.getStart()));
        titleTV.setText(calendarEvent.getEventTitle());
        descTV.setText(calendarEvent.getEventDesc());
        freqTV.setText(calendarEvent.getFreq());
        idTV.setText(String.valueOf(calendarEvent.getId()));
        if (!TextUtils.isEmpty(calendarEvent.getStatus())){
            statusTV.setVisibility(View.VISIBLE);
            statusTV.setText(calendarEvent.getStatus());
            doneIV.setVisibility(View.GONE);
            cancelIV.setVisibility(View.GONE);
        }else {
            statusTV.setVisibility(View.GONE);
            doneIV.setVisibility(View.VISIBLE);
            cancelIV.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
