package com.alphatech.www.nfcbusinesscardlocal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Appointments extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    User person=null;
    User appointmentuser;
    EditText title,notes,lengthOfMeeting;
    Button setAppointment;
    Button datebutton;
    Button timebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        Intent intent=getIntent();
        if(intent.hasExtra("ViewUser")) {
            appointmentuser = (User) intent.getSerializableExtra("ViewUser");
        }
        title=(EditText) findViewById(R.id.meetingtitle);
        notes=(EditText) findViewById(R.id.meetingnotes);
        setAppointment=(Button) findViewById(R.id.saveappointment);
        lengthOfMeeting=(EditText) findViewById(R.id.etlengthOfTime);
        datebutton=(Button) findViewById(R.id.meetingdate);
        timebutton=(Button) findViewById(R.id.meetingtime);
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
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Retrieving Appointments...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(User.class);
                mProgressDialog.dismiss();            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Unable to load details, try again.",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }
    public void saveAppointment(){
        if(title.getText().toString().compareTo("")==0)
        {
            Toast.makeText(getApplicationContext(), "Enter type of Meeting", Toast.LENGTH_LONG).show();
            return;
        }
        if(datebutton.getText().toString().compareTo("Click to Set Date")==0)
        {
            Toast.makeText(getApplicationContext(), "Please select Date", Toast.LENGTH_LONG).show();
            return;
        }
        if(timebutton.getText().toString().compareTo("Click to Set Time")==0)
        {
            Toast.makeText(getApplicationContext(), "Please set the Time", Toast.LENGTH_LONG).show();
            return;
        }
        Appointment appointment=new Appointment();
        appointment.setName(title.getText().toString().trim()+"-"+ appointmentuser.getFullname());
        appointment.setDate(datebutton.getText().toString());
        appointment.setTime(timebutton.getText().toString());
        appointment.setNotes(notes.getText().toString());
        appointment.setLengthOfAppointment(Integer.parseInt(lengthOfMeeting.getText().toString()));
        appointment.setClientEmail(appointmentuser.getEmailAddress());
        Toast.makeText(this, "Appointment is ready.", Toast.LENGTH_SHORT).show();
        addToCalendar(appointment);
        finish();
    }
    private void addToCalendar(Appointment appointment)
    {
        Calendar beginTime=Calendar.getInstance();
        Calendar endTime=Calendar.getInstance();
        String[] dateDetails=appointment.breakdownDate();
        String[] timeDetails=appointment.breakdownTime();
        int year=Integer.parseInt(dateDetails[2]),
                month=Integer.parseInt(dateDetails[1])-1,
                day=Integer.parseInt(dateDetails[0]),
                hours=Integer.parseInt(timeDetails[0]),
                minutes=Integer.parseInt(timeDetails[1]);

        beginTime.set(year,month,day,hours,minutes);
        endTime.set(year,month,day,(hours+appointment.getLengthOfAppointment()),minutes);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE,appointment.getName())
                .putExtra(CalendarContract.Events.DESCRIPTION,appointment.getNotes())
                .putExtra(Intent.EXTRA_EMAIL,appointment.getClientEmail());

        startActivity(intent);
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
            // Because month index start from zero
            int actualMonth = month+1;
            btn.setText(day+"/"+actualMonth+"/"+year);
            Toast.makeText(getActivity(), "Date has been set.", Toast.LENGTH_LONG).show();
        }
    }
}
