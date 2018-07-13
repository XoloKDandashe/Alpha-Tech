package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDetails extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    TestUser person=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
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
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(TestUser.class);
                mProgressDialog.dismiss();
                setDetails();
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

    public void openUpdateDetails(View view){
        Intent intent = new Intent(this,updateDetails.class);
        startActivity(intent);
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
        onBackPressed();
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
