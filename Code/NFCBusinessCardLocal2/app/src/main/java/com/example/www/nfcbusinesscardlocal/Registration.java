package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.AlertDialog;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Registration extends AppCompatActivity {
    ArrayList<TestUser> userlist=new ArrayList<>();
    String lattitude,longitude;

    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final Button button = findViewById(R.id.button);
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
    public void addUser(View view){
        String emailneedle,password,confirmpassword,inputCheck;
        EditText editText;
        TestUser newUser=new TestUser();
        /*Check all neccessary fields have information */
        editText=(EditText)findViewById(R.id.input_name);
        inputCheck=editText.getText().toString().trim();
        if(inputCheck.isEmpty() || inputCheck.length() == 0 || inputCheck.equals("") || inputCheck == null)
        {
            Toast.makeText(this, "Please Enter Your Full Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setFullname(inputCheck);

        editText=(EditText)findViewById(R.id.input_jobtitle);
        newUser.setJobTitle(editText.getText().toString());
        editText=(EditText)findViewById(R.id.input_companyname);
        newUser.setCompanyName(editText.getText().toString());
        editText=(EditText)findViewById(R.id.input_mobile);
        newUser.setMobileNumber(editText.getText().toString());
        editText=(EditText)findViewById(R.id.input_telephone);
        newUser.setWorkTelephone(editText.getText().toString());
        editText=(EditText)findViewById(R.id.input_address);
        newUser.setWorkAddress(editText.getText().toString());


        /*Check if email exists*/
        editText=(EditText)findViewById(R.id.input_email);
        emailneedle=editText.getText().toString().trim();
        if(emailneedle.isEmpty() || emailneedle.length() == 0 || emailneedle.equals("") || emailneedle == null)
        {
            Toast.makeText(this, "Email must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0;i<userlist.size();i++)
        {
            if(emailneedle.compareTo(userlist.get(i).getEmailAddress())==0)
            {
                Toast.makeText(this, "Email already Exists.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        newUser.setEmailAddress(emailneedle);
        /*passwords are the same*/
        editText=(EditText)findViewById(R.id.input_password);
        password=editText.getText().toString();
        if(password.isEmpty() || password.length() == 0 || password.equals("") || password == null)
        {
            Toast.makeText(this, "Password must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        editText=(EditText)findViewById(R.id.input_confirmPassword);
        confirmpassword=editText.getText().toString();
        if(password.compareTo(confirmpassword)!=0) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setPassword(password);
        /*If all checks out, add to arraylist*/
        //userlist.add(newUser);
        Toast.makeText(this, "Profile Created. Please log in.", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,LogIn.class);
        intent.putExtra("newUser",newUser);
        startActivity(intent);
    }
    public void backToLogin(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
