package com.alphatech.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private User person=null;
    private Button scan_btn;
    private View view;
    public String etname, etpos, etphon,etmail,etOff,etWAddress;
    private String[] details;
    private TextView textView;
    private Button btn;
    private static final String VCF_DIRECTORY = "/vcf_demonuts";
    private File vcfFile;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan Code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
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
    public void backMainActivity(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ReaderActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReaderActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ReaderActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ReaderActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    public void ask(){
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        view=(View) findViewById(R.id.scanned);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                details=result.getContents().split("\n");
                if(details.length==0)
                {
                    Toast.makeText(ReaderActivity.this, "No information available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(details.length<6){
                    Toast.makeText(ReaderActivity.this, "Information is not for our application.", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<User> arrayList=null;
                Gson gson= new Gson();
                String jsonConverter=person.getRecievedCards();
                if(jsonConverter.isEmpty())
                {
                    arrayList=new ArrayList<>();
                }
                else
                {
                    Type type= new TypeToken<List<User>>(){}.getType();
                    arrayList=gson.fromJson(jsonConverter,type);
                }
                view.setVisibility(View.VISIBLE);
                User newCard=new User();
                newCard.setFullname(details[0]);
                newCard.setJobTitle(details[1]);
                newCard.setCompanyName(details[2]);
                newCard.setEmailAddress(details[3]);
                newCard.setMobileNumber(details[4]);
                newCard.setWorkTelephone(details[5]);
                newCard.setWorkAddress(details[6]);
                newCard.setWebsite(details[7]);
                if(details.length==9)
                newCard.setImageUrl(details[8]);
                //check if it exists

                TextView tvIncomingMessage=findViewById(R.id.rec_qr_fullname);
                tvIncomingMessage.setText(newCard.getFullname());
                tvIncomingMessage=findViewById(R.id.rec_qr_jobTitle);
                tvIncomingMessage.setText(newCard.getJobTitle());
                tvIncomingMessage=findViewById(R.id.rec_qr_company);
                tvIncomingMessage.setText(newCard.getCompanyName());
                tvIncomingMessage=findViewById(R.id.rec_qr_emailAddress);
                tvIncomingMessage.setText(newCard.getEmailAddress());
                tvIncomingMessage=findViewById(R.id.rec_qr_personalnumber);
                tvIncomingMessage.setText(newCard.getMobileNumber());
                tvIncomingMessage=findViewById(R.id.rec_qr_officenumber);
                tvIncomingMessage.setText(newCard.getWorkTelephone());
                tvIncomingMessage=findViewById(R.id.rec_qr_physAddress);
                tvIncomingMessage.setText(newCard.getWorkAddress());
                tvIncomingMessage=findViewById(R.id.rec_qr_webAddress);
                tvIncomingMessage.setText(newCard.getWebsite());
                for(int i=0;i<arrayList.size();i++){
                    if(arrayList.get(i).getEmailAddress().compareTo(newCard.getEmailAddress())==0)
                    {
                        arrayList.remove(i);
                    }
                }
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

                btn = (Button) findViewById(R.id.qrbtn);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                            File vdfdirectory = new File(
                                    Environment.getExternalStorageDirectory() + VCF_DIRECTORY);
                            // have the object build the directory structure, if needed.
                            if (!vdfdirectory.exists()) {
                                vdfdirectory.mkdirs();
                            }

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
                            //   fw.write("ADR;TYPE=WORK:;;" + p.getStreet() + ";" + p.getCity() + ";" + p.getState() + ";" + p.getPostcode() + ";" + p.getCountry() + "\r\n");
                            fw.write("EMAIL;TYPE=PREF,INTERNET:" + etmail.toString() + "\r\n");
                            fw.write("END:VCARD\r\n");
                            fw.close();

                            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
                            Intent i = new Intent(); //this will import vcf in contact list
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(FileProvider.getUriForFile(ReaderActivity.this,BuildConfig.APPLICATION_ID+".provider",vcfFile), "text/x-vcard");
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
                String resultString="";
                int length=details.length;
                if(details.length==8)
                    length--;
                for (int i=0;i<length;i++)
                {
                    resultString+=details[i];
                    if(i+1<length)
                        resultString+="\n";
                }
                }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void saveupdate(User user){
        databaseReference.setValue(user);
    }
}
