package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements Listener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private Button btn_signout;
    private ProgressDialog mProgressDialog;
    private CircleImageView imageView;
    private TestUser person=null;

    public static final String TAG = MainActivity.class.getSimpleName();
    private SendInterface sendInterface;
    private ReceiverInterface receiverInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        mProgressDialog=new ProgressDialog(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        imageView=(CircleImageView) findViewById(R.id.main_pic);
        btn_signout=(Button)findViewById(R.id.btn_signout);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backMainActivity();
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
    public void setDetails(){
        loadPicture(person);
        TextView textView=(TextView) findViewById(R.id.main_name);
        String[] breakdown=person.getFullname().split(" ");
        String firstname="";
        for(int i=0;i<breakdown.length-1;i++)
        {
            firstname+=breakdown[i]+" ";
        }
        textView.setText(firstname.trim());
        textView=(TextView) findViewById(R.id.main_surname);
        textView.setText(breakdown[breakdown.length-1]);
        textView=(TextView) findViewById(R.id.main_job);
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
                    .into(imageView);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "You have no image.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void openViewDetails(View view){
        Intent intent = new Intent(this,ViewDetails.class);
        startActivity(intent);
    }
    public void openManageCards(View view){
        Intent intent = new Intent(this,ViewCardsInterface.class);
        startActivity(intent);
    }
    public void openReadNFCCard(View view)
    {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not available on device.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, CardReadInformation.class);
        startActivity(mov);
    }
    public void openSendCard(View view){
        sendInterface= (SendInterface) getFragmentManager().findFragmentByTag(sendInterface.TAG);

        if (sendInterface == null) {

            sendInterface = sendInterface.newInstance();
        }
        sendInterface.show(getFragmentManager(),sendInterface.TAG);

    }
    public void openReceiveCard(View view){
        receiverInterface= (ReceiverInterface) getFragmentManager().findFragmentByTag(receiverInterface.TAG);

        if (receiverInterface == null) {

            receiverInterface = receiverInterface.newInstance();
        }
        receiverInterface.show(getFragmentManager(),receiverInterface.TAG);
    }
    public void backMainActivity(){

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

    @Override
    public void onDialogDisplayed() {

    }

    @Override
    public void onDialogDismissed() {

    }
}
