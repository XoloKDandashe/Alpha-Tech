package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_config);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        finish();
        startActivity(new Intent(getApplicationContext(),LogIn.class));
    }
}
