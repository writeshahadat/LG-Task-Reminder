package com.legato.taskreminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.legato.taskreminder.models.CalEvent;
import com.tsuryo.swipeablerv.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public class DbManager extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static String DB_PATH ="/data/user/0/"+ BuildConfig.APPLICATION_ID+"/databases/";
    // Database Name
    private static final String DATABASE_NAME = "taskrem.sqlite";

    SQLiteDatabase database;

    public DbManager(@Nullable Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CalEvent.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CalEvent.TABLE_NAME);
    }

    public long insertEvent(CalEvent event){
        database = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(CalEvent.COLUMN_EVENT_ID, event.getEventID());
        contentValue.put(CalEvent.COLUMN_TITLE, event.getEventTitle());
        contentValue.put(CalEvent.COLUMN_DESC, event.getEventDesc());
        contentValue.put(CalEvent.COLUMN_LOCATION, event.getLocation());
        contentValue.put(CalEvent.COLUMN_START, String.valueOf(event.getStart()));
        contentValue.put(CalEvent.COLUMN_END, String.valueOf(event.getEnd()));
        contentValue.put(CalEvent.COLUMN_RRULE, event.getrRule());
        contentValue.put(CalEvent.COLUMN_FREQ, event.setFrequency(event.getrRule()));
        contentValue.put(CalEvent.COLUMN_ALARM, event.getHasAlarm());
        contentValue.put(CalEvent.COLUMN_STATUS, event.getStatus());
        contentValue.put(CalEvent.COLUMN_IS_DELETED, event.getIsDeleted());
        return this.database.insert(CalEvent.TABLE_NAME, null, contentValue);
    }

    @SuppressLint("Range")
    public List<CalEvent> getEvents(){
        database = this.getReadableDatabase();
        Cursor cursor = this.database.query(CalEvent.TABLE_NAME, new String[] {CalEvent.COLUMN_ID, CalEvent.COLUMN_EVENT_ID, CalEvent.COLUMN_TITLE, CalEvent.COLUMN_DESC,
                CalEvent.COLUMN_LOCATION, CalEvent.COLUMN_START, CalEvent.COLUMN_END, CalEvent.COLUMN_RRULE, CalEvent.COLUMN_FREQ, CalEvent.COLUMN_ALARM, CalEvent.COLUMN_STATUS, CalEvent.COLUMN_IS_DELETED},
                CalEvent.COLUMN_IS_DELETED + "= ?", new String[]{"0"}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        List<CalEvent> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CalEvent calendarEvent = new CalEvent(
                        cursor.getLong(cursor.getColumnIndex(CalEvent.COLUMN_EVENT_ID)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_DESC)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_LOCATION)),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_START))),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_END))),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_RRULE)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_FREQ)),
                        cursor.getInt(cursor.getColumnIndex(CalEvent.COLUMN_ALARM)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(CalEvent.COLUMN_IS_DELETED))
                );


                events.add(calendarEvent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return events;
    }
    @SuppressLint("Range")
    public boolean searchEvent(long id){
        database = this.getReadableDatabase();
        Cursor cursor = this.database.query(CalEvent.TABLE_NAME, new String[] {CalEvent.COLUMN_ID, CalEvent.COLUMN_EVENT_ID, CalEvent.COLUMN_TITLE, CalEvent.COLUMN_DESC,
                        CalEvent.COLUMN_LOCATION, CalEvent.COLUMN_START, CalEvent.COLUMN_END, CalEvent.COLUMN_RRULE, CalEvent.COLUMN_FREQ,
                        CalEvent.COLUMN_ALARM, CalEvent.COLUMN_STATUS, CalEvent.COLUMN_IS_DELETED},
                CalEvent.COLUMN_EVENT_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        List<CalendarEvent> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                        CalendarEvent calendarEvent = new CalendarEvent(
                        cursor.getLong(cursor.getColumnIndex(CalEvent.COLUMN_EVENT_ID)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_DESC)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_LOCATION)),
                                Long.parseLong(cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_START))),
                                Long.parseLong(cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_END))),
                        0,
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_RRULE)),
                        cursor.getInt(cursor.getColumnIndex(CalEvent.COLUMN_ALARM)),
                        cursor.getString(cursor.getColumnIndex(CalEvent.COLUMN_STATUS))
                );


                events.add(calendarEvent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        if (events.size() > 0){
            return true;
        }else {
            return false;
        }
    }

    public void updateEvent(CalEvent calEvent){
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalEvent.COLUMN_EVENT_ID,calEvent.getEventID());
        contentValues.put(CalEvent.COLUMN_TITLE,calEvent.getEventTitle());
        contentValues.put(CalEvent.COLUMN_DESC,calEvent.getEventDesc());
        contentValues.put(CalEvent.COLUMN_LOCATION,calEvent.getLocation());
        contentValues.put(CalEvent.COLUMN_START,String.valueOf(calEvent.getStart()));
        contentValues.put(CalEvent.COLUMN_END,String.valueOf(calEvent.getEnd()));
        contentValues.put(CalEvent.COLUMN_RRULE, calEvent.getrRule());
        contentValues.put(CalEvent.COLUMN_FREQ, calEvent.getFreq());
        contentValues.put(CalEvent.COLUMN_ALARM, calEvent.getHasAlarm());
        contentValues.put(CalEvent.COLUMN_STATUS, calEvent.getStatus());
        contentValues.put(CalEvent.COLUMN_IS_DELETED, calEvent.getIsDeleted());
        database.update(CalEvent.TABLE_NAME,contentValues,CalEvent.COLUMN_EVENT_ID + "= ?",new String[]{String.valueOf(calEvent.getEventID())});
        database.close();
    }
    public void deleteEvent(CalEvent calEvent){
        database = this.getWritableDatabase();
        database.delete(CalEvent.TABLE_NAME,CalEvent.COLUMN_EVENT_ID + "= ?",new String[]{String.valueOf(calEvent.getEventID())});
    }

    public void purgeTable(){
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM "+ CalEvent.TABLE_NAME);
        database.close();
    }
}
