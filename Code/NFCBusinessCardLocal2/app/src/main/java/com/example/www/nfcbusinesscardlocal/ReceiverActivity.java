package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ReceiverActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    TestUser person=null;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public String etname, etpos, etphon,etmail,etOff,etWAddress;
    private TextView tvIncomingMessage;
    private NfcAdapter nfcAdapter;
    private Button btn;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    private static final String VCF_DIRECTORY = "/vcf_demonuts";
    private File vcfFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        if (!isNfcSupported()) {
            Toast.makeText(ReceiverActivity.this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(ReceiverActivity.this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        initViews();
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
    }

    // need to check NfcAdapter for nullability. Null means no NFC support on the device
    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    private void initViews() {
        this.tvIncomingMessage = (TextView) findViewById(R.id.tv_in_message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // also reading NFC message from here in case this activity is already started in order
        // not to start another instance of this activity
        receiveMessageFromDevice(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Preparing transfer...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(TestUser.class);
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
    @Override
    protected void onResume() {
        super.onResume();

        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, this.nfcAdapter);
        if(person!=null)
        receiveMessageFromDevice(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, this.nfcAdapter);
    }

    private void receiveMessageFromDevice(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(parcelables==null)
            {
                Toast.makeText(this, "No information read.", Toast.LENGTH_SHORT).show();
                return ;
            }
            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            if(inNdefMessage==null)
            {
                Toast.makeText(this, "No information available.", Toast.LENGTH_SHORT).show();
                return ;
            }
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdefRecords[0];

            String inMessage = new String(ndefRecord_0.getPayload());
            if(inMessage.trim().compareTo("")==0||inMessage.trim().length()==0)
            {
                Toast.makeText(this, "No information available.", Toast.LENGTH_SHORT).show();
                return ;
            }
            String[] shred=inMessage.split("\n");
            if(shred.length==0)
            {
                Toast.makeText(ReceiverActivity.this, "No information read.", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(shred.length<6){
                Toast.makeText(ReceiverActivity.this, "Information is not for our application.", Toast.LENGTH_SHORT).show();
                return;
            }
            String payload="";
            int length=shred.length;
            if(length>7)
                length--;
            for(int i=0;i<length;i++) {
                payload += shred[i];
                if((i+1)<length)
                    payload+="\n";
            }
            this.tvIncomingMessage.setText(payload);

            String [] details=inMessage.split("\n");
            List<TestUser> arrayList=null;
            Gson gson= new Gson();
            String jsonConverter=person.getRecievedCards();
            if(jsonConverter.isEmpty())
            {
                arrayList=new ArrayList<>();
            }
            else
            {
                Type type= new TypeToken<List<TestUser>>(){}.getType();
                arrayList=gson.fromJson(jsonConverter,type);
            }

            TestUser newCard=new TestUser();
            newCard.setFullname(details[0]);
            newCard.setJobTitle(details[1]);
            newCard.setCompanyName(details[2]);
            newCard.setEmailAddress(details[3]);
            newCard.setMobileNumber(details[4]);
            newCard.setWorkTelephone(details[5]);
            newCard.setWorkAddress(details[6]);
            if(details.length==8)
            newCard.setImageUrl(details[7]);

            arrayList.add(newCard);
            String jsonEncode= gson.toJson(arrayList);
            person.setRecievedCards(jsonEncode);
            saveupdate(person);

            etname = details[0].toString();
            if(details[2].compareTo("n/a")==0)
                etpos=details[1];
            else
                etpos = details[2]+"-"+details[1];
            etphon = details[5];
            etOff = details[4];
            etmail = details[3];
            etWAddress = details[6];
            btn = (Button) findViewById(R.id.btn);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // File vcfFile = new File(this.getExternalFilesDir(null), "generated.vcf");
                        File vdfdirectory = new File(
                                Environment.getExternalStorageDirectory() + VCF_DIRECTORY);
                        // have the object build the directory structure, if needed.
                        if (!vdfdirectory.exists()) {
                            vdfdirectory.mkdirs();
                        }
                        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                        vcfFile = new File(vdfdirectory, etname.toString()  + ".vcf");

                        FileWriter fw = null;
                        fw = new FileWriter(vcfFile);
                        fw.write("BEGIN:VCARD\r\n");
                        fw.write("VERSION:3.0\r\n");
                        // fw.write("N:" + p.getSurname() + ";" + p.getFirstName() + "\r\n");
                        fw.write("FN:" + etname.toString() + "\r\n");
                        fw.write("ORG:" + etpos + "\r\n");
                        //  fw.write("TITLE:" + p.getTitle() + "\r\n");
                        fw.write("TEL;TYPE=WORK,VOICE:" + etphon.toString() + "\r\n");
                        fw.write("TEL;TYPE=HOME,VOICE:" + etOff.toString() + "\r\n");
                        fw.write("ADR;TYPE=WORK:;;" + etWAddress.toString()+"\r\n");
                        fw.write("EMAIL;TYPE=PREF,INTERNET:" + etmail.toString() + "\r\n");
                        fw.write("END:VCARD\r\n");
                        fw.close();

                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
                        Intent i = new Intent(); //this will import vcf in contact list
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(FileProvider.getUriForFile(ReceiverActivity.this,BuildConfig.APPLICATION_ID+".provider",vcfFile), "text/x-vcard");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try{startActivity(i);}catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Please Give Permission", Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Toast.makeText(this,"Business card is saved.",Toast.LENGTH_LONG).show();
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ReceiverActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReceiverActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ReceiverActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ReceiverActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    public void ask(){
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
    }

    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED

    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well

    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    private void saveupdate(TestUser user){
        databaseReference.setValue(user);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

