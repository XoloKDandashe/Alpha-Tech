package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Xolo Kagiso Dandashe on 24 Apr 2018.
 */

public class Appointment {
    String name,notes,date, time;
//DatePicker for updatedate|() for notification
    public Appointment()
    {
        name="";
        notes="";
        date="";
        time="";
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        if(notes.compareTo("")==0)return ""; return notes;
    }
    public String getAppointmentDetails()
    {
        return getName()+"\n"+getDate()+"\n"+getTime()+"\n"+getNotes();
    }
}
