package com.example.www.nfcbusinesscardlocal;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewDetails extends AppCompatActivity implements Listener{
    public static final String TAG = ViewDetails.class.getSimpleName();
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private CircleImageView imageView;
    private User person=null;
    private NfcAdapter nfcAdapter;
    //write fragment
    private Button btn_write;
    private Boolean isDialogDisplayed=false,isWrite=false;
    private CardWriteInformationFragment cardWriteInformationFragment;
    //delete fragment
    private Button btn_clear;
    private Boolean isDelete=false;
    private CardDeleteInformationFragment cardDeleteInformationFragment;

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
        btn_write=(Button)findViewById(R.id.btn_write_card);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWriteFragment();
            }
        });
        btn_clear=(Button) findViewById(R.id.btn_clear_card);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteFragment();
            }
        });
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            btn_clear.setVisibility(View.INVISIBLE);
            btn_write.setVisibility(View.INVISIBLE);
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        imageView=(CircleImageView) findViewById(R.id.profilePicture);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(nfcAdapter!= null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

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
    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter!= null)
            nfcAdapter.disableForegroundDispatch(this);
    }
    public void openUpdateDetails(View view){
        Intent intent = new Intent(this,updateDetails.class);
        startActivity(intent);
    }
    public void setDetails()
    {
        loadPicture(person);
        TextView textView=(TextView)findViewById(R.id.view_name);
        String[] breakdown=person.getFullname().split(" ");
        String firstname="";
        for(int i=0;i<breakdown.length-1;i++)
        {
            firstname+=breakdown[i]+" ";
        }
        textView.setText(firstname.trim());
        textView=(TextView) findViewById(R.id.view_surname);
        textView.setText(breakdown[breakdown.length-1]);
        textView=(TextView)findViewById(R.id.jobtitle);
        textView.setText(person.getJobTitle());
        textView=(TextView)findViewById(R.id.company);
        textView.setText(person.getWebsite());
        textView=(TextView)findViewById(R.id.webAddress);
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
            StorageReference httpRef= FirebaseStorage.getInstance().getReferenceFromUrl(user.getImageUrl());
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(httpRef)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imageView);
        }
    }
    private void showWriteFragment() {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not supported on this device.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            return;
        }
        isWrite = true;

        cardWriteInformationFragment= (CardWriteInformationFragment) getFragmentManager().findFragmentByTag(CardWriteInformationFragment.TAG);

        if (cardWriteInformationFragment == null) {

            cardWriteInformationFragment = CardWriteInformationFragment.newInstance();
        }
        cardWriteInformationFragment.show(getFragmentManager(),CardWriteInformationFragment.TAG);

    }
    private void showDeleteFragment() {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not supported on this device.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            return;
        }
        isDelete= true;

        cardDeleteInformationFragment= (CardDeleteInformationFragment) getFragmentManager().findFragmentByTag(CardDeleteInformationFragment.TAG);

        if (cardDeleteInformationFragment == null) {

            cardDeleteInformationFragment = CardDeleteInformationFragment.newInstance();
        }
        cardDeleteInformationFragment.show(getFragmentManager(),CardDeleteInformationFragment.TAG);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed=true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed=false;
        isWrite=false;
        isDelete=false;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {
                if (isWrite) {
                    String messageToWrite =person.getDetails();
                    cardWriteInformationFragment= (CardWriteInformationFragment) getFragmentManager().findFragmentByTag(CardWriteInformationFragment.TAG);
                    cardWriteInformationFragment.onNfcDetected(ndef,messageToWrite);
                    Toast.makeText(this, "Your information has been written.", Toast.LENGTH_SHORT).show();
                    cardWriteInformationFragment.dismiss();
                }else if(isDelete){
                    cardDeleteInformationFragment= (CardDeleteInformationFragment) getFragmentManager().findFragmentByTag(CardDeleteInformationFragment.TAG);
                    cardDeleteInformationFragment.onNfcDetected(ndef,"");
                    Toast.makeText(this, "Information has been deleted.", Toast.LENGTH_SHORT).show();
                    cardDeleteInformationFragment.dismiss();
                }
                    else {

                    Toast.makeText(this,"Unable to write.",Toast.LENGTH_SHORT);
                    cardDeleteInformationFragment.dismiss();
                    cardWriteInformationFragment.dismiss();
                    return;
                }
            }
        }
    }
}
