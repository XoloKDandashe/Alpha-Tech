package com.example.www.nfcbusinesscardlocal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Appointments extends AppCompatActivity {
    TestUser person;
    EditText title,notes;
    Button setAppointment;
    Button datebutton;
    Button timebutton;
    Button backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        Intent intent=getIntent();
        if(intent.hasExtra("ViewUser")) {
            person = (TestUser) intent.getSerializableExtra("ViewUser");
        }
        title=(EditText) findViewById(R.id.meetingtitle);
        notes=(EditText) findViewById(R.id.meetingnotes);
        setAppointment=(Button) findViewById(R.id.saveappointment);
        datebutton=(Button) findViewById(R.id.meetingdate);
        timebutton=(Button) findViewById(R.id.meetingtime);
        backbutton=(Button) findViewById(R.id.meetingback);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goback();
            }
        });
        datebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DialogFragment dialogFragment= new DatePickerFragment();
                dialogFragment.show(getFragmentManager(),"Date Picker");
            }
        });
        timebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment= new TimePickerFragment();
                dialogFragment.show(getFragmentManager(),"Time Picker");

            }
        });
        setAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointment();
            }
        });
    }
    public void goback(){
        onBackPressed();
        Toast.makeText(getApplicationContext(), "Appointment not set.", Toast.LENGTH_SHORT).show();
    }
    public void saveAppointment(){
        if(title.getText().toString().compareTo("")==0)
        {
            Toast.makeText(getApplicationContext(), "Enter type of Meeting", Toast.LENGTH_LONG).show();
            return;
        }
        if(datebutton.getText().toString().compareTo("Click to Set Date")==0)
        {
            Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_LONG).show();
            return;
        }
        if(timebutton.getText().toString().compareTo("Click to Set Time")==0)
        {
            Toast.makeText(getApplicationContext(), "Set Time", Toast.LENGTH_LONG).show();
            return;
        }
        List<Appointment> arrayList=null;
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String jsonConverter=sharedPreferences.getString("jsonappointmentList","");
        if(jsonConverter.isEmpty())
        {
            arrayList=new ArrayList<>();
        }
        else
        {
            Type type= new TypeToken<List<Appointment>>(){}.getType();
            arrayList=gson.fromJson(jsonConverter,type);
        }
        Appointment appointment=new Appointment();
        appointment.setName(title.getText().toString().trim()+"-"+person.getFullname());
        appointment.setDate(datebutton.getText().toString());
        appointment.setTime(timebutton.getText().toString());
        appointment.setNotes(notes.getText().toString());

        arrayList.add(appointment);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String jsonEncode= gson.toJson(arrayList);
        editor.putString("jsonappointmentList",jsonEncode);
        editor.commit();

        Toast.makeText(this, "Appointment has been set.", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //Handle date here
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
            return  dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            // Do something with the chosen date
            Button btn=(Button)getActivity().findViewById(R.id.meetingdate);
            btn.setText(day+"/"+month+"/"+year);
            int actualMonth = month+1; // Because month index start from zero
            Toast.makeText(getActivity(), "Date has been set.", Toast.LENGTH_LONG).show();
        }
    }
}
