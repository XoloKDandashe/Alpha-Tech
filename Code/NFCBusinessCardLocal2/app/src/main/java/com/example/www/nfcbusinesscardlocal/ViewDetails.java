package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewDetails extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private ImageView imageView;
    private TestUser person=null;
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
        imageView=(ImageView) findViewById(R.id.profilePicture);
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
        loadPicture(person);
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
    private void loadPicture(TestUser user){
        ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Toast.makeText(getApplicationContext(), "Unable to get image, internet connection needed.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user.getImageUrl()!=""){
            StorageReference httpRef= FirebaseStorage.getInstance().getReferenceFromUrl(user.getImageUrl());
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(httpRef)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imageView);
        }
    }
    public void backViewDetails(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
