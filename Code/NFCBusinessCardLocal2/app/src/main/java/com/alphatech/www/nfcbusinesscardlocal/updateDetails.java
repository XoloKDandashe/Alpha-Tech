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
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class updateDetails extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 234;
    User person=null;
    String lattitude,longitude;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private Button btnOpenUpload;
    private Uri photo;
    private Uri filepath;
    private ImageView imageView;
    private Intent intent;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);
        final Button button =(Button) findViewById(R.id.update_button);
        btnOpenUpload=(Button)findViewById(R.id.btn_open_upload);
        btnOpenUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
                boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                {
                    Toast.makeText(getApplicationContext(), "Internet connection needed for this action.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(updateDetails.this,updatePicture.class);
                startActivity(intent);
            }
        });
        imageView=(ImageView) findViewById(R.id.avatarpic);
        //firebase
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference();
        //firebase

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
    protected void onResume() {
        super.onResume();
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person=dataSnapshot.getValue(User.class);
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
    public void setDetails(){
        String detail="";
        loadPicture(person);
        EditText editText=(EditText)findViewById(R.id.update_input_name);
        editText.setText(check(person.getFullname()));
        editText=(EditText)findViewById(R.id.update_input_jobtitle);
        editText.setText(check(person.getJobTitle()));
        editText=(EditText)findViewById(R.id.update_input_companyname);
        editText.setText(check(person.getCompanyName()));
        editText=(EditText)findViewById(R.id.update_input_mobile);
        editText.setText(check(person.getMobileNumber()));
        editText=(EditText)findViewById(R.id.update_input_telephone);
        editText.setText(check(person.getWorkTelephone()));
        editText=(EditText)findViewById(R.id.update_input_address);
        editText.setText(check(person.getWorkAddress()));
        editText=(EditText)findViewById(R.id.update_input_web);
        editText.setText(check(person.getWebsite()));
    }
    public String check(String word){
        if(word.compareTo("n/a")==0)
        return "";
        return word;
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
        editText=(EditText) findViewById(R.id.update_input_web);
        person.setWebsite(editText.getText().toString());
        mProgressDialog.dismiss();
        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_LONG).show();
        mProgressDialog.setMessage("Saving information...");
        mProgressDialog.show();
        saveupdate(person);
        mProgressDialog.dismiss();
        finish();
    }

    private void loadPicture(User user){
        ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Toast.makeText(getApplicationContext(), "Unable to get image, internet connection needed.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user.getImageUrl()!=""){
            StorageReference httpRef=FirebaseStorage.getInstance().getReferenceFromUrl(user.getImageUrl());
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(httpRef)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imageView);
        }
    }
    private void saveupdate(User user){
        databaseReference.setValue(user);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
