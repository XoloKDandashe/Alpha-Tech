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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class updateDetails extends AppCompatActivity {
    TestUser person=null;
    String lattitude,longitude;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);
        final Button button = findViewById(R.id.update_button);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                }
            }
        });
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
    private String getAddres(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String var;
                var = address.getAddressLine(0);
                EditText editTe = (EditText)findViewById(R.id.update_input_address);
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
        if (ActivityCompat.checkSelfPermission(updateDetails.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (updateDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(updateDetails.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

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

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

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
    public void setDetails(){
        EditText editText=(EditText)findViewById(R.id.update_input_name);
        editText.setText(person.getFullname());
        editText=(EditText)findViewById(R.id.update_input_jobtitle);
        editText.setText(person.getJobTitle());
        editText=(EditText)findViewById(R.id.update_input_companyname);
        editText.setText(person.getCompanyName());
        /*editText=(EditText)findViewById(R.id.update_input_email);
        editText.setText(viewUser.getEmailAddress());*/
        editText=(EditText)findViewById(R.id.update_input_mobile);
        editText.setText(person.getMobileNumber());
        editText=(EditText)findViewById(R.id.update_input_telephone);
        editText.setText(person.getWorkTelephone());
        /*editText=(EditText)findViewById(R.id.update_input_password);
        editText.setText(viewUser.getPassword());
        editText=(EditText)findViewById(R.id.update_input_confirmPassword);
        editText.setText(viewUser.getPassword());*/
        editText=(EditText)findViewById(R.id.update_input_address);
        editText.setText(person.getWorkAddress());
    }
    public void updateUser(View view){
        EditText editText;
        String inputCheck,emailneedle,confirmpassword,password;
        mProgressDialog.setMessage("Checking details...");
        mProgressDialog.show();
        /*Check if name is entered*/
        editText=(EditText)findViewById(R.id.update_input_name);
        inputCheck=editText.getText().toString().trim();
        if(inputCheck.isEmpty() || inputCheck.length() == 0 || inputCheck.equals("") || inputCheck == null)
        {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Please Enter Your Full Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        /*Check if email exists*/
        /*editText=(EditText)findViewById(R.id.update_input_email);
        emailneedle=editText.getText().toString().trim();
        if(emailneedle.isEmpty() || emailneedle.length() == 0 || emailneedle.equals("") || emailneedle == null)
        {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Email must be given.", Toast.LENGTH_SHORT).show();
            return;
        }*/
        /*passwords are the same*/
        /*editText=(EditText)findViewById(R.id.update_input_password);
        password=editText.getText().toString();
        if(password.isEmpty() || password.length() == 0 || password.equals("") || password == null)
        {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Password must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        editText=(EditText)findViewById(R.id.update_input_confirmPassword);
        confirmpassword=editText.getText().toString();
        if(password.compareTo(confirmpassword)!=0) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }*/
        editText=(EditText)findViewById(R.id.update_input_name);
        inputCheck=editText.getText().toString().trim();
        person.setFullname(inputCheck);
        editText=(EditText)findViewById(R.id.update_input_jobtitle);
        person.setJobTitle(editText.getText().toString());
        editText=(EditText)findViewById(R.id.update_input_companyname);
        person.setCompanyName(editText.getText().toString());
        editText=(EditText)findViewById(R.id.update_input_mobile);
        person.setMobileNumber(editText.getText().toString());
        editText=(EditText)findViewById(R.id.update_input_telephone);
        person.setWorkTelephone(editText.getText().toString());
        editText=(EditText)findViewById(R.id.update_input_address);
        person.setWorkAddress(editText.getText().toString());
        /*editText=(EditText)findViewById(R.id.update_input_email);
        emailneedle=editText.getText().toString().trim();
        viewUser.setEmailAddress(emailneedle);*/
        /*passwords are the same*/
        /*editText=(EditText)findViewById(R.id.update_input_password);
        password=editText.getText().toString();
        editText=(EditText)findViewById(R.id.update_input_confirmPassword);
        confirmpassword=editText.getText().toString();
        viewUser.setPassword(password);*/
        /*If all checks out, add to arraylist*/
        mProgressDialog.dismiss();
        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_LONG).show();
        mProgressDialog.setMessage("Saving information...");
        mProgressDialog.show();
        saveupdate(person);
        mProgressDialog.dismiss();
        finish();
    }
    private void saveupdate(TestUser user){
        databaseReference.setValue(user);
    }
    public void backToViewDetails(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
