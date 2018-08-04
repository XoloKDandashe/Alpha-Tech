package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            Intent intent= new Intent(getApplicationContext(),MainActivity.class);
            finish();
            startActivity(intent);
        }
    }
    public void openMainMenu(View view){
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        String email,password;
        EditText editText=(EditText)findViewById(R.id.emaillogin);
        email=editText.getText().toString().trim();
        editText=(EditText)findViewById(R.id.passwordlogin);
        password=editText.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            progressDialog.dismiss();
            Toast.makeText(this,"Please enter email.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            progressDialog.dismiss();
            Toast.makeText(this,"Please enter password.",Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email,password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LogIn.this,"Unable to log in. Please try again.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    public void openRegistration(View view){

        Intent intent = new Intent(this,Registration.class);
        startActivity(intent);
    }
    public void openImportCard(View view){
        Intent intent = new Intent(this,ImportCardDetails.class);
        startActivity(intent);
    }
}
