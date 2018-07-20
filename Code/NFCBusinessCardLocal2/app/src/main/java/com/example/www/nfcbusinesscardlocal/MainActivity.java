package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        user=firebaseAuth.getCurrentUser();
    }
    public void openViewDetails(View view){
        Intent intent = new Intent(this,ViewDetails.class);
        startActivity(intent);
    }
    public void openManageCards(View view){
        Intent intent = new Intent(this,ManageCards.class);
        startActivity(intent);
    }
    public void openSendCard(View view){
        Intent intent = new Intent(this,SendInterface.class);
        startActivity(intent);
    }
    public void openReceiveCard(View view){
        Intent intent = new Intent(this,ReceiverInterface.class);
        startActivity(intent);
    }
    public void backMainActivity(View view){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("You are about to Sign Out.")
                .setMessage("Are you sure you want to Sign Out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with sign out
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(), "You have Signed Out.", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(MainActivity.this,LogIn.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(getApplicationContext(), "Sign Out Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
