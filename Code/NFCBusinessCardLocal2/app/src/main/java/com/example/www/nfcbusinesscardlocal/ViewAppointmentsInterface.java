package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentsInterface extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    TestUser person=null;
    ListView scrollView;
    AppointmentListAdapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments_interface);
        scrollView= (ListView) findViewById(R.id.scrollappointments);
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(TestUser.class);
                mProgressDialog.dismiss();
                setViews(person.getAppointmentlist());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Unable to load details, try again.",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }
    public void setViews(String appointmentlist)
    {
        List<Appointment> arrayList=null;
        //SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        //String jsonConverter=sharedPreferences.getString("jsonappointmentList","");
        if(appointmentlist.isEmpty()||appointmentlist.compareTo("")==0)
        {
            Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Type type= new TypeToken<List<Appointment>>(){}.getType();
        arrayList=gson.fromJson(appointmentlist,type);
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
