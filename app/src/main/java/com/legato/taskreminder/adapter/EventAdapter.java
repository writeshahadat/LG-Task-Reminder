package com.legato.taskreminder.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.legato.taskreminder.ActionListenerInterface;
import com.legato.taskreminder.R;
import com.legato.taskreminder.models.CalEvent;
import com.legato.taskreminder.viewholder.EventVH;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventVH> {

    List<CalEvent> eventList;
    Context context;
    ActionListenerInterface anInterface;
    ImageView doneIV, cancelIV;

    public EventAdapter(List<CalEvent> eventList, Context context, ActionListenerInterface actionListenerInterface) {
        this.eventList = eventList;
        this.context = context;
        this.anInterface = actionListenerInterface;
    }



    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        doneIV = view.findViewById(R.id.done_iv);
        cancelIV = view.findViewById(R.id.cancel_iv);
        return new EventVH(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {
        holder.bindData(eventList.get(position));

        if (!TextUtils.isEmpty(eventList.get(position).getStatus())){
            if (eventList.get(position).getStatus().equals("Completed")){
                TextView textView = holder.itemView.findViewById(R.id.event_status_tv);
                textView.setTextColor(ContextCompat.getColor(context, R.color.green));
            }
        }

        doneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.doneClicked(eventList.get(position));
                eventList.remove(position);
                notifyDataSetChanged();
            }
        });
        cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.cancelClicked(eventList.get(position));
                eventList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
