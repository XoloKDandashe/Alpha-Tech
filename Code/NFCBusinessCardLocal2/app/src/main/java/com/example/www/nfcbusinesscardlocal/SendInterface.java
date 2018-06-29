package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class SendInterface extends AppCompatActivity {

    TestUser person=null;
    public Button button1;
    public Button button2;

    public void init() {
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
                {
                    Toast.makeText(SendInterface.this, "NFC is not available. Use QR option.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent mov = new Intent(SendInterface.this, NFCActivity.class);
                mov.putExtra("LoginUser",person);
                startActivity(mov);
            }
        });
    }
    public void init2() {
        button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mov = new Intent(SendInterface.this, GenerateQRCode.class);
                mov.putExtra("LoginUser",person);
                startActivity(mov);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_interface);
        Intent intent=getIntent();
        if(intent.hasExtra("LoginUser")) {
            person = (TestUser) intent.getSerializableExtra("LoginUser");
        }
        init();
        init2();
        minorDetails();
    }
    public void minorDetails(){
        TextView textView=(TextView)findViewById(R.id.sendfullname);
        textView.setText(person.getFullname());
        textView=(TextView)findViewById(R.id.sendjobtitle);
        textView.setText(person.getJobTitle());
        textView=(TextView)findViewById(R.id.sendemailAddress);
        textView.setText(person.getEmailAddress());
        textView=(TextView)findViewById(R.id.sendpersonalnumber);
        textView.setText(person.getMobileNumber());
        textView=(TextView)findViewById(R.id.sendofficenumber);
        textView.setText(person.getWorkTelephone());
        textView=(TextView)findViewById(R.id.sendLocation);
        textView.setText(person.getWorkAddress());

    }
    public void backMainActivity(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
