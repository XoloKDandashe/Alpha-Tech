package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

public class ViewCardDetails extends AppCompatActivity {
    TestUser person;
    int ArrayIndex=-1;
    static final Integer LOCATION = 0x1;
    private File vcfFile;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    private static final String VCF_DIRECTORY = "/vcf_demonuts";
    Button saveButton,backButton,deletebutton,appointmentbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_details);
        Intent intent=getIntent();
        if(intent.hasExtra("ViewUser")) {
            person = (TestUser) intent.getSerializableExtra("ViewUser");
        }
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String m=person.getWorkAddress();
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
        saveButton= (Button) findViewById(R.id.save_view_user);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVCard();
            }
        });
        backButton=(Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToViewCardsInterface();
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
    }
    protected void onResume()
    {
        super.onResume();
        setDetails();
    }
    public void setDetails()
    {
        TextView textView=(TextView)findViewById(R.id.fullname_view_user);
        textView.setText(person.getFullname());
        textView=(TextView)findViewById(R.id.jobtitle_view_user);
        textView.setText(person.getJobTitle());
        textView=(TextView)findViewById(R.id.company_view_user);
        textView.setText(person.getCompanyName());
        textView=(TextView)findViewById(R.id.emailAddress_view_user);
        textView.setText(person.getEmailAddress());
      //  Toast.makeText(ViewCardDetails.this, person.getWorkAddress(), Toast.LENGTH_SHORT).show();
        textView=(TextView)findViewById(R.id.physAddress_view_user);
        textView.setText(person.getWorkAddress());
        textView=(TextView)findViewById(R.id.personalnumber_view_user);
        textView.setText(person.getMobileNumber());
        textView=(TextView)findViewById(R.id.officenumber_view_user);
        textView.setText(person.getWorkTelephone());
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
                etname = person.getFullname();
                if(person.getCompanyName().compareTo("n/a")==0)
                    etpos = person.getJobTitle();
                else
                    etpos = person.getCompanyName()+"-"+person.getJobTitle();
                etmail = person.getEmailAddress();
                etphon = person.getMobileNumber();
                etOff = person.getWorkTelephone();
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
                        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
                        Gson gson= new Gson();
                        String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
                        if(jsonConverter.isEmpty())
                        {
                            Toast.makeText(ViewCardDetails.this,"You have no cards received.",Toast.LENGTH_LONG);
                            finish();
                        }
                        else
                        {
                            Type type= new TypeToken<List<TestUser>>(){}.getType();
                            arrayList=gson.fromJson(jsonConverter,type);
                        }

                        for (int i=0;i<arrayList.size();i++) {
                            if(arrayList.get(i).getEmailAddress().trim().compareTo(person.getEmailAddress().trim())==0)
                            {
                                arrayList.remove(i);
                                File vdfdirectory = new File(
                                        Environment.getExternalStorageDirectory() + VCF_DIRECTORY);
                                vcfFile = new File(vdfdirectory, person.getFullname()  + ".vcf");
                                if(vcfFile.exists())
                                vcfFile.delete();
                            }

                        }
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        String jsonEncode= gson.toJson(arrayList);
                        editor.putString("jsonreceivedlist",jsonEncode);
                        editor.commit();
                        Toast.makeText(ViewCardDetails.this, "Delete Successful.", Toast.LENGTH_LONG).show();
                        finish();
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
        builder.setTitle("Appointment: "+textView.getText())
                .setMessage("Do you want to set an appointment?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getApplicationContext(),Appointments.class);
                        intent.putExtra("ViewUser",person);
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
    public void backToViewCardsInterface(){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}