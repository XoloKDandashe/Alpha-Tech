package com.alphatech.www.nfcbusinesscardlocal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Xolo Kagiso Dandashe on 24 Apr 2018.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Get a Calendar instance
        final Calendar calendar = Calendar.getInstance();
        // Get the current hour and minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        /*
            Creates a new time picker dialog.
                TimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener listener,
                    int hourOfDay, int minute, boolean is24HourView)
         */
        // Create a TimePickerDialog with current time
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),this,hour,minute,false);
        // Return the TimePickerDialog
        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        // Do something with the returned time
        Button btn=(Button)getActivity().findViewById(R.id.meetingtime);
        String hours="",minutes="";
        if(minute<=9)
            minutes="0"+minute;
        else
            minutes=""+minute;
        if(hourOfDay<=9)
            hours="0"+hourOfDay;
        else
            hours=""+hourOfDay;
        btn.setText(hours + ":" + minutes);
        Toast.makeText(getActivity(), "Time is set.", Toast.LENGTH_SHORT).show();
    }
}
