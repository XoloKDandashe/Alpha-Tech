package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Button;
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

public class SendInterface extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private TestUser person=null;
    private ImageView mImageView;
    public Button button1;


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
                startActivity(mov);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_interface);
        init();
        init2();
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        mImageView=(ImageView)findViewById(R.id.send_picture);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person = dataSnapshot.getValue(TestUser.class);
                mProgressDialog.dismiss();
                minorDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to load details, try again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }
    public void minorDetails(){
        loadPicture(person);
        TextView textView=(TextView)findViewById(R.id.send_fullname);
        textView.setText(person.getFullname());
        textView=(TextView)findViewById(R.id.send_jobtitle);
        textView.setText(person.getJobTitle());

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
                    .into(mImageView);
        }
    }
    public void backMainActivity(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
