package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Xolo Kagiso Dandashe on 24 Apr 2018.
 */

public class Appointment {
    String name;
    String notes;
    String date;
    String time;



    String clientEmail;
    int LengthOfAppointment;
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

    public void setClientEmail(String clientEmail){this.clientEmail=clientEmail;}

    public void setLengthOfAppointment(int Length){this.LengthOfAppointment=Length;}

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

    public String getClientEmail() {
        return clientEmail;
    }

    public int getLengthOfAppointment() {
        return LengthOfAppointment;
    }

    public String getNotes() {
        if(notes.compareTo("")==0)return ""; return notes;
    }
    public String getAppointmentDetails()
    {
        return getName()+"\n"+getDate()+"\n"+getTime()+"\n"+getNotes();
    }
    public String [] breakdownDate(){

        return getDate().split("/");
    }
    public String [] breakdownTime(){

        return getTime().split(":");
    }
}
