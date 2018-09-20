package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig extends AppCompatActivity {

    Button btn_accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_config);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch(Exception e){}
        SharedPreferences pref=getApplicationContext().getSharedPreferences("Agreement_Signature",0);
        SharedPreferences.Editor editor=pref.edit();
        String key_email=pref.getString("Accepted",null);
        if(key_email.compareTo("I Accept All Conditions Stated in Agreement.")==0)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),LogIn.class));
        }
        btn_accept=(Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref=getApplicationContext().getSharedPreferences("Agreement_Signature",0);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("Accepted","I Accept All Conditions Stated in Agreement.");
                editor.commit();
                finish();
                startActivity(new Intent(getApplicationContext(),LogIn.class));
            }
        });
    }
}
