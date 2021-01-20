package com.button_time;

import android.text.format.Time;

/**
 * Created by MaKsa on 10.05.16.
 */
public class CurrEvent {
    int id;
    long date_long_event;
    Time date_time_event;
    String name_event;
    String note_event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate_long_event() {
        return date_long_event;
    }

    public void setDate_long_event(long date_long_event) {
        this.date_long_event = date_long_event;
    }

    public Time getDate_time_event() {
        return date_time_event;
    }

    public void setDate_time_event(Time date_time_event) {
        this.date_time_event = date_time_event;
        this.date_long_event=this.date_time_event.toMillis(false);
    }

    public String getName_event() {
        return name_event;
    }

    public void setName_event(String name_event) {
        this.name_event = name_event;
    }

    public String getNote_event() {
        return note_event;
    }

    public void setNote_event(String note_event) {
        this.note_event = note_event;
    }

}
