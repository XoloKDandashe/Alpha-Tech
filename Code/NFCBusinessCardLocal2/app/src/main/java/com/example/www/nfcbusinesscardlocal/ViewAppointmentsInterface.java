package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentsInterface extends AppCompatActivity {
    ListView scrollView;
    AppointmentListAdapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments_interface);
        scrollView= (ListView) findViewById(R.id.scrollappointments);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check();
        setViews();
    }
    public void check(){
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String jsonConverter=sharedPreferences.getString("appointmentList","");
        if(jsonConverter.isEmpty())
        {
            Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
    public void setViews()
    {
        List<Appointment> arrayList=null;
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String jsonConverter=sharedPreferences.getString("jsonappointmentList","");
        if(jsonConverter.isEmpty())
        {
            Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Type type= new TypeToken<List<Appointment>>(){}.getType();
        arrayList=gson.fromJson(jsonConverter,type);
        if(arrayList==null)
            {
                Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

        ArrayList<String> adapter=new ArrayList<>();

        for (Appointment appointment:arrayList) {
            adapter.add(appointment.getAppointmentDetails());
        }
        ad=new AppointmentListAdapter(this,adapter);
        scrollView.setAdapter(ad);

    }
    public void goback(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
