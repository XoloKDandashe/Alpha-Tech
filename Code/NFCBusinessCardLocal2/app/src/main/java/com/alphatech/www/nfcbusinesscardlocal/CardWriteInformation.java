package com.alphatech.www.nfcbusinesscardlocal;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardWriteInformation extends AppCompatActivity implements Listener {
    //firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    User person=null;
    //nfc
    public static final String TAG = CardWriteInformation.class.getSimpleName();
    private Button mBtWrite;
    private Button mBtLoad;
    private CardWriteInformationFragment cardWriteInformationFragment;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_write_information);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        mBtWrite = (Button) findViewById(R.id.btn_write);
        mBtLoad = (Button) findViewById(R.id.card_write_load_info);
        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWriteFragment();
            }
        });
        mBtLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydetails();
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(User.class);
                mProgressDialog.dismiss();
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
    private void mydetails(){
        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(CardWriteInformation.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(CardWriteInformation.this);
        }
        builder.setTitle("Writing to cards.")
                .setMessage("Do you want to load your information?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText;
                        editText=(EditText) findViewById(R.id.card_write_name);
                        editText.setText(person.getFullname());
                        editText=(EditText)findViewById(R.id.card_write_jobtitle);
                        editText.setText(person.getJobTitle());
                        editText=(EditText)findViewById(R.id.card_write_companyname);
                        editText.setText(person.getCompanyName());
                        editText=(EditText)findViewById(R.id.card_write_email);
                        editText.setText(person.getEmailAddress());
                        editText=(EditText)findViewById(R.id.card_write_telephone);
                        editText.setText(person.getWorkTelephone());
                        editText=(EditText)findViewById(R.id.card_write_mobile);
                        editText.setText(person.getMobileNumber());
                        editText=(EditText)findViewById(R.id.card_write_address);
                        editText.setText(person.getWorkAddress());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(CardWriteInformation.this, "Load Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void showWriteFragment() {

        isWrite = true;

        cardWriteInformationFragment= (CardWriteInformationFragment) getFragmentManager().findFragmentByTag(CardWriteInformationFragment.TAG);

        if (cardWriteInformationFragment == null) {

            cardWriteInformationFragment = CardWriteInformationFragment.newInstance();
        }
        cardWriteInformationFragment.show(getFragmentManager(),CardWriteInformationFragment.TAG);

    }
    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;
    }
    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(nfcAdapter!= null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfcAdapter!= null)
            nfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

                    String messageToWrite = "";
                    EditText editText;
                    editText=(EditText)findViewById(R.id.card_write_name);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_jobtitle);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your job title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_companyname);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your company name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_email);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_mobile);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_telephone);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your telephone/alternative number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText()+"\n";
                    editText=(EditText)findViewById(R.id.card_write_address);
                    if(editText.getText().toString().isEmpty()){
                        Toast.makeText(this, "Please enter your work address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    messageToWrite+=editText.getText();
                    if(messageToWrite.contains(person.getEmailAddress()))
                    {
                        messageToWrite+="\n"+person.getImageUrl();
                    }
                    cardWriteInformationFragment= (CardWriteInformationFragment) getFragmentManager().findFragmentByTag(CardWriteInformationFragment.TAG);
                    cardWriteInformationFragment.onNfcDetected(ndef,messageToWrite);

                } else {

                    Toast.makeText(this,"Unable to write.",Toast.LENGTH_SHORT);
                    finish();
                    return;
                }
            }
        }
    }
}
