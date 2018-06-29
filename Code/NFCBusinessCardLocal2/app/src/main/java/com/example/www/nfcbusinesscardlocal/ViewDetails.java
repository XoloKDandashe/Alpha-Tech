package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewDetails extends AppCompatActivity {

    TestUser person=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        Intent intent=getIntent();
        if(intent.hasExtra("LoginUser")) {
            person = (TestUser) intent.getSerializableExtra("LoginUser");
        }
        //setDetails();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setDetails();
    }
    public void openUpdateDetails(View view){
        Intent intent = new Intent(this,updateDetails.class);
        intent.putExtra("LoginUser",person);
        startActivity(intent);
        finish();
    }
    public void setDetails()
    {
        TextView textView=(TextView)findViewById(R.id.fullname);
        textView.setText(person.getFullname());
        textView=(TextView)findViewById(R.id.jobtitle);
        textView.setText(person.getJobTitle());
        textView=(TextView)findViewById(R.id.company);
        textView.setText(person.getCompanyName());
        textView=(TextView)findViewById(R.id.emailAddress);
        textView.setText(person.getEmailAddress());
        textView=(TextView)findViewById(R.id.physAddress);
        textView.setText(person.getWorkAddress());
        textView=(TextView)findViewById(R.id.personalnumber);
        textView.setText(person.getMobileNumber());
        textView=(TextView)findViewById(R.id.officenumber);
        textView.setText(person.getWorkTelephone());
    }
    public void backViewDetails(View view){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("LoginUser",person);
        startActivity(intent);
        finish();
    }
    public void viewAppointments(View view){
        Intent intent=new Intent(this,ViewAppointmentsInterface.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
