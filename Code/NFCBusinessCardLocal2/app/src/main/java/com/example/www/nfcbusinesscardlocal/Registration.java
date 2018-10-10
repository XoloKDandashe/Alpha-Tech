package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Registration extends AppCompatActivity {
    String lattitude,longitude;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    LocationManager locationManager;
    private User newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            Intent intent= new Intent(getApplicationContext(),MainActivity.class);
            finish();
            startActivity(intent);
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
                boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                {
                    Toast.makeText(getApplicationContext(), "Internet connection needed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    progressDialog.setMessage("Searching for your location. Please wait.");
                    progressDialog.show();
                    getLocation();
                    progressDialog.dismiss();
                }
            }
        });
    }
    private String getAddres(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String var;
                var = address.getAddressLine(0);
                EditText editTe = (EditText)findViewById(R.id.input_address);
                editTe.setText(var, TextView.BufferType.EDITABLE);

                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(Registration.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (Registration.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Registration.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                getAddres(latti,longi);

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                getAddres(latti,longi);


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                getAddres(latti,longi);


            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }
    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void addUser(View view){
        progressDialog.setMessage("Checking details...");
        progressDialog.show();
        String emailneedle,password,confirmpassword,inputCheck;
        EditText editText;
        newUser=new User();
        /*Check all neccessary fields have information */
        editText=(EditText)findViewById(R.id.input_name);
        inputCheck=editText.getText().toString().trim();
        //fullname
        if(inputCheck.isEmpty() || inputCheck.length() == 0 || inputCheck.equals("") || inputCheck == null)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Enter Your Full Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        /*Check if email exists*/
        editText=(EditText)findViewById(R.id.input_email);
        emailneedle=editText.getText().toString().trim();
        if(emailneedle.isEmpty() || emailneedle.length() == 0 || emailneedle.equals("") || emailneedle == null)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Email must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setEmailAddress(emailneedle);
        /*passwords are the same*/
        editText=(EditText)findViewById(R.id.input_password);
        password=editText.getText().toString();
        if(password.isEmpty() || password.length() == 0 || password.equals("") || password == null)
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Password must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() <8 )
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        editText=(EditText)findViewById(R.id.input_confirmPassword);
        confirmpassword=editText.getText().toString();
        if(password.compareTo(confirmpassword)!=0) {
            progressDialog.dismiss();
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setPassword(password);

        /*If all checks out*/
        newUser.setFullname(inputCheck);
        editText=(EditText)findViewById(R.id.input_jobtitle);
        newUser.setJobTitle(editText.getText().toString().trim());
        editText=(EditText)findViewById(R.id.input_companyname);
        newUser.setCompanyName(editText.getText().toString().trim());
        editText=(EditText)findViewById(R.id.input_mobile);
        newUser.setMobileNumber(editText.getText().toString().trim());
        editText=(EditText)findViewById(R.id.input_telephone);
        newUser.setWorkTelephone(editText.getText().toString().trim());
        editText=(EditText)findViewById(R.id.input_address);
        newUser.setWorkAddress(editText.getText().toString().trim());
        newUser.setRecievedCards("");
        progressDialog.dismiss();

        progressDialog.setMessage("Creating profile...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(newUser.getEmailAddress(),newUser.getPassword())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(Registration.this, "Profile Created.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Profile not Created. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });
        progressDialog.setMessage("Saving information...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(newUser.getEmailAddress(),newUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            successfulRegistration(newUser);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Log in failed.", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }
                });
    }
    private void successfulRegistration(User user){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference.child(firebaseUser.getUid()).setValue(user);
    }
    public void backToLogin(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
