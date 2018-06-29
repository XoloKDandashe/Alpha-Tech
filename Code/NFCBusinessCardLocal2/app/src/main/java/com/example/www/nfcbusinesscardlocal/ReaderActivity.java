package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.www.nfcbusinesscardlocal.ViewCardDetails.READ_EXST;

public class ReaderActivity extends AppCompatActivity {

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

        textView= (TextView) findViewById(R.id.scannedDetails);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                details=result.getContents().split(",");
                List<TestUser> arrayList=null;
                SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
                Gson gson= new Gson();
                String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
                if(jsonConverter.isEmpty())
                {
                    arrayList=new ArrayList<>();
                }
                else
                {
                    Type type= new TypeToken<List<TestUser>>(){}.getType();
                    arrayList=gson.fromJson(jsonConverter,type);
                }
                view.setVisibility(View.VISIBLE);
                TestUser newCard=new TestUser();
                newCard.setFullname(details[0]);
                newCard.setJobTitle(details[1]);
                newCard.setCompanyName(details[2]);
                newCard.setEmailAddress(details[3]);
                newCard.setMobileNumber(details[4]);
                newCard.setWorkTelephone(details[5]);
                newCard.setWorkAddress(details[6]);
                //Toast.makeText(this,details[6],Toast.LENGTH_LONG).show();

                arrayList.add(newCard);

                SharedPreferences.Editor editor=sharedPreferences.edit();
                String jsonEncode= gson.toJson(arrayList);
                editor.putString("jsonreceivedlist",jsonEncode);
                editor.commit();
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
                for (int i=0;i<details.length;i++)
                {
                    resultString+=details[i];
                    if(i+1<details.length)
                        resultString+="\n";
                }
                textView.setText(resultString);}
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}
