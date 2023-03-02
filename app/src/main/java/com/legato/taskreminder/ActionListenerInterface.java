package com.legato.taskreminder;


import com.legato.taskreminder.models.CalEvent;

public interface ActionListenerInterface {
    public void doneClicked(CalEvent calEvent);
    public void cancelClicked(CalEvent calEvent);
}
