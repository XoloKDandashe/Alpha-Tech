package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ViewCardDetails extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private ImageView imageView;
    FirebaseUser firebaseUser;
    TestUser person=null;
    Intent intent=null;
    TestUser viewUser;
    static final Integer LOCATION = 0x1;
    private File vcfFile;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    private static final String VCF_DIRECTORY = "/vcf_demonuts";
    Button saveButton,deletebutton,appointmentbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_details);
        intent=getIntent();
        if(intent.hasExtra("ViewUser")) {
            viewUser = (TestUser) intent.getSerializableExtra("ViewUser");
        }
        final Button button =(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String m= viewUser.getWorkAddress();
                if(m.isEmpty()||m.compareTo("n/a")==0)
                {
                    Toast.makeText(ViewCardDetails.this, "No address given", Toast.LENGTH_SHORT).show();
                    return;
                }
                String var = "google.navigation:q="+m;
                Uri gmmIntentUri = Uri.parse(var);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        imageView=(ImageView) findViewById(R.id.profilePicture_view_user);
        saveButton= (Button) findViewById(R.id.save_view_user);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVCard();
            }
        });
        deletebutton=(Button) findViewById(R.id.deletecontact);
        deletebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deleteContact();
            }
        });
        appointmentbutton=(Button) findViewById(R.id.appointmentcard);
        appointmentbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setAppointment();
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
    protected void onResume()
    {
        super.onResume();
        if(intent.hasExtra("ViewUser")) {
            viewUser = (TestUser) intent.getSerializableExtra("ViewUser");
            setDetails();
            SharedPreferences pref=getApplicationContext().getSharedPreferences("Viewed_User",0);
            SharedPreferences.Editor editor=pref.edit();
            editor.putString("Email",viewUser.getEmailAddress().trim());
            editor.commit();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Loading contact...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(TestUser.class);
                if(!intent.hasExtra("ViewUser"))
                {
                    SharedPreferences pref=getApplicationContext().getSharedPreferences("Viewed_User",0);
                    SharedPreferences.Editor editor=pref.edit();
                    String key_email=pref.getString("Email",null);
                    String cardlist=person.getRecievedCards();
                    List<TestUser> arrayList=null;
                    Gson gson= new Gson();
                    if(cardlist.isEmpty()||cardlist.compareTo("")==0||cardlist.compareTo("[]")==0)
                    {
                        Toast.makeText(ViewCardDetails.this,"You have no cards received.",Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    else
                    {
                        Type type= new TypeToken<List<TestUser>>(){}.getType();
                        arrayList=gson.fromJson(cardlist,type);
                    }
                    for(TestUser user: arrayList){
                        if(user.getEmailAddress().compareTo(key_email)==0)
                        {
                            viewUser=user;
                            setDetails();
                        }
                    }
                }
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
    public void setDetails()
    {
        loadPicture(viewUser);
        TextView textView=(TextView)findViewById(R.id.fullname_view_user);
        textView.setText(viewUser.getFullname());
        textView=(TextView)findViewById(R.id.jobtitle_view_user);
        textView.setText(viewUser.getJobTitle());
        textView=(TextView)findViewById(R.id.company_view_user);
        textView.setText(viewUser.getCompanyName());
        textView=(TextView)findViewById(R.id.emailAddress_view_user);
        textView.setText(viewUser.getEmailAddress());
        textView=(TextView)findViewById(R.id.physAddress_view_user);
        textView.setText(viewUser.getWorkAddress());
        textView=(TextView)findViewById(R.id.personalnumber_view_user);
        textView.setText(viewUser.getMobileNumber());
        textView=(TextView)findViewById(R.id.officenumber_view_user);
        textView.setText(viewUser.getWorkTelephone());
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ViewCardDetails.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewCardDetails.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ViewCardDetails.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ViewCardDetails.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    public void ask(){
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        //askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
    }
    public void saveVCard(){
        String etname, etpos, etphon,etmail,etOff;
                etname = viewUser.getFullname();
                if(viewUser.getCompanyName().compareTo("n/a")==0)
                    etpos = viewUser.getJobTitle();
                else
                    etpos = viewUser.getCompanyName()+"-"+ viewUser.getJobTitle();
                etmail = viewUser.getEmailAddress();
                etphon = viewUser.getMobileNumber();
                etOff = viewUser.getWorkTelephone();
                try {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                            // File vcfFile = new File(this.getExternalFilesDir(null), "generated.vcf");
                            File vdfdirectory = new File(Environment.getExternalStorageDirectory() + VCF_DIRECTORY);
                            // have the object build the directory structure, if needed.
                            if (!vdfdirectory.exists()) {
                                vdfdirectory.mkdirs();
                            }

                            vcfFile = new File(vdfdirectory, "/"+etname+ ".vcf");

                            FileWriter fw = null;
                            fw = new FileWriter(vcfFile);

                            fw.write("BEGIN:VCARD\r\n");
                            fw.write("VERSION:3.0\r\n");
                            // fw.write("N:" + p.getSurname() + ";" + p.getFirstName() + "\r\n");
                            fw.write("FN:" + etname.toString() + "\r\n");

                            fw.write("ORG:" + etpos + "\r\n");
                            //  fw.write("TITLE:" + p.getTitle() + "\r\n");
                            fw.write("TEL;TYPE=WORK,VOICE:" + etOff.toString() + "\r\n");
                            fw.write("TEL;TYPE=HOME,VOICE:" + etphon.toString() + "\r\n");
                            //   fw.write("ADR;TYPE=WORK:;;" + p.getStreet() + ";" + p.getCity() + ";" + p.getState() + ";" + p.getPostcode() + ";" + p.getCountry() + "\r\n");
                            fw.write("EMAIL;TYPE=PREF,INTERNET:" + etmail.toString() + "\r\n");
                            fw.write("END:VCARD\r\n");

                            fw.close();

                            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
                             Intent i = new Intent(); //this will import vcf in contact list
                             i.setAction(android.content.Intent.ACTION_VIEW);
                             i.setDataAndType(FileProvider.getUriForFile(ViewCardDetails.this,BuildConfig.APPLICATION_ID+".provider",vcfFile), "text/x-vcard");
                             i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try{startActivity(i);}catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Please Give Permission", Toast.LENGTH_LONG).show();
                                }

                             //Toast.makeText(getApplicationContext(), "Saved to phone!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
    }
    public void deleteContact(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        TextView textView=(TextView) findViewById(R.id.fullname_view_user);
        builder.setTitle("Delete "+textView.getText()+"'s Details?")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        List<TestUser> arrayList=null;
                        //SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
                        Gson gson= new Gson();
                        //String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
                        String jsonConverter=person.getRecievedCards();
                        if(jsonConverter.isEmpty())
                        {
                            Toast.makeText(ViewCardDetails.this,"You have no cards received.",Toast.LENGTH_LONG);
                            finish();
                            return;
                        }
                        else
                        {
                            Type type= new TypeToken<List<TestUser>>(){}.getType();
                            arrayList=gson.fromJson(jsonConverter,type);
                        }

                        for (int i=0;i<arrayList.size();i++) {
                            if(arrayList.get(i).getEmailAddress().trim().compareTo(viewUser.getEmailAddress().trim())==0)
                            {
                                arrayList.remove(i);
                                File vdfdirectory = new File(
                                        Environment.getExternalStorageDirectory() + VCF_DIRECTORY);
                                vcfFile = new File(vdfdirectory, viewUser.getFullname()  + ".vcf");
                                if(vcfFile.exists())
                                vcfFile.delete();
                            }

                        }
                        String jsonEncode= gson.toJson(arrayList);
                        person.setRecievedCards(jsonEncode);
                        saveupdate(person);
                        Toast.makeText(ViewCardDetails.this, "Delete Successful.", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(ViewCardDetails.this, "Delete Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void setAppointment(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        TextView textView=(TextView) findViewById(R.id.fullname_view_user);
        builder.setTitle("About to set Appointment with: "+textView.getText())
                .setMessage("Do you want to set an appointment?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getApplicationContext(),Appointments.class);
                        intent.putExtra("ViewUser", viewUser);
                        startActivity(intent);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(ViewCardDetails.this, "Appointment not set.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void saveupdate(TestUser user){
        databaseReference.setValue(user);
    }
}