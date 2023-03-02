package com.legato.taskreminder.models;

import android.text.TextUtils;

import com.kyle.calendarprovider.calendar.CalendarEvent;

public class CalEvent{
    int id;
    long eventID;
    String eventTitle;
    String eventDesc;
    String location;
    long start;
    long end;
    String rRule;
    String freq;
    int hasAlarm;
    String status;
    int isDeleted;

    public static final String TABLE_NAME = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_RRULE = "rrule";
    public static final String COLUMN_FREQ = "freq";
    public static final String COLUMN_ALARM = "alarm";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_IS_DELETED = "deleted";


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EVENT_ID + " INTEGER,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_DESC + " TEXT,"
                    + COLUMN_LOCATION + " TEXT,"
                    + COLUMN_START + " TEXT,"
                    + COLUMN_END + " TEXT,"
                    + COLUMN_RRULE + " TEXT,"
                    + COLUMN_FREQ + " TEXT,"
                    + COLUMN_ALARM + " INTEGER,"
                    + COLUMN_STATUS + " TEXT,"
                    + COLUMN_IS_DELETED + " INTEGER"
                    + ")";

    public CalEvent() {
    }

    public CalEvent(int id, long eventID, String eventTitle, String eventDesc, String location, long start, long end,
                    String rRule, String freq, int hasAlarm, String status, int isDeleted) {
        this.id = id;
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDesc = eventDesc;
        this.location = location;
        this.start = start;
        this.end = end;
        this.rRule = rRule;
        this.freq = freq;
        this.hasAlarm = hasAlarm;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public CalEvent(long eventID, String eventTitle, String eventDesc, String location, long start, long end,
                    String rRule, String freq, int hasAlarm, String status, int isDeleted) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDesc = eventDesc;
        this.location = location;
        this.start = start;
        this.end = end;
        this.rRule = rRule;
        this.freq = freq;
        this.hasAlarm = hasAlarm;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getrRule() {
        return rRule;
    }

    public void setrRule(String rRule) {
        this.rRule = rRule;
    }

    public int getHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(int hasAlarm) {
        this.hasAlarm = hasAlarm;
    }
    public CalEvent setCalendarEvent(CalendarEvent calendarEvent){
        return new CalEvent(calendarEvent.getId(), calendarEvent.getTitle(), calendarEvent.getDescription(), calendarEvent.getEventLocation(),
                calendarEvent.getStart(), calendarEvent.getEnd(), calendarEvent.getRRule(), setFrequency(calendarEvent.getRRule()),
                calendarEvent.getHasAlarm(), "", 0);

    }
    public CalendarEvent getCalendarEvent(){
        return new CalendarEvent(
                getEventID(),
                getEventTitle(),
                getEventDesc(),
                getLocation(),
                getStart(),
                getEnd(),
                0,
                getrRule(),
                getHasAlarm(),
                getStatus()
        );
    }
    public String setFrequency(String rule){
        if (!TextUtils.isEmpty(rule)){
            if(rule.contains("DAILY")){
                return "Daily";
            }else if(rule.contains("WEEKLY")){
                return "Weekly";
            }else if(rule.contains("MONTHLY")){
                return "Monthly";
            }else if(rule.contains("YEARLY")){
                return "Yearly";
            }else {
                return "Once";
            }
        }else {
            return "Once";
        }
    }
}
