package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.example.www.nfcbusinesscardlocal.Ocr.OCRCapture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.www.nfcbusinesscardlocal.Ocr.OcrCaptureActivity.TextBlockObject;

public class RecieverOCR extends AppCompatActivity {

    private TextView textView;
    private TextView textView0;
    private final int CAMERA_SCAN_TEXT = 0;
    private final int LOAD_IMAGE_RESULTS = 1;
    EditText displayJobTitle;
    EditText displayWebsite;
    EditText displayEmail;
    EditText displayCompanyName;
    EditText displayPhone;
    EditText displayTelephone;
    EditText displayName;
    EditText displayAddress;
    Button saveButton;
    //firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private User person=null;
    //end firebase
    String OCRresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_orc);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        //
        displayEmail=findViewById(R.id.importEmail);
        displayJobTitle=findViewById(R.id.import_jobtitle);
        displayTelephone = findViewById(R.id.importWorkNumber);
        displayPhone=findViewById(R.id.importMobileNumber);
        displayName=findViewById(R.id.import_Name);
        displayCompanyName=findViewById(R.id.importCompanyName);
        displayAddress=findViewById(R.id.import_Adress);
        displayWebsite=findViewById(R.id.import_website);
        saveButton = findViewById(R.id.saveBtn) ;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCard();
            }
        });
        textView = findViewById(R.id.reciever_tv);
        textView0 = findViewById(R.id.text0);

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
    private void addCard(){
        if(displayName.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Full name must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(displayJobTitle.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Profession must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(displayEmail.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Email must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(displayPhone.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Mobile number must be given.", Toast.LENGTH_SHORT).show();
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

        User newCard=new User();
        newCard.setFullname(displayName.getText().toString().trim());
        newCard.setJobTitle(displayJobTitle.getText().toString().trim());
        newCard.setCompanyName(displayCompanyName.getText().toString().trim());
        newCard.setEmailAddress(displayEmail.getText().toString().trim());
        newCard.setMobileNumber(displayPhone.getText().toString().trim());
        newCard.setWorkTelephone(displayTelephone.getText().toString().trim());
        newCard.setWorkAddress(displayAddress.getText().toString().trim());
        newCard.setWebsite(displayWebsite.getText().toString().trim());
        arrayList.add(newCard);
        String jsonEncode= gson.toJson(arrayList);
        person.setRecievedCards(jsonEncode);
        saveupdate(person);
        Toast.makeText(getApplicationContext(), "Business card is saved.", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void saveupdate(User user){
        databaseReference.setValue(user);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reciever_orc_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionCamera:
                OCRCapture.Builder(this)
                        .setUseFlash(false)
                        .setAutoFocus(true)
                        .buildWithRequestCode(CAMERA_SCAN_TEXT);
                break;
            case R.id.actionPhoto:

                if (hasPermission()) {

                    pickImage();
                } else {
                    getPermission();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPermission() {
// Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //TODO:
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickImage();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void pickImage() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, LOAD_IMAGE_RESULTS);
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CAMERA_SCAN_TEXT) {
                if (resultCode == CommonStatusCodes.SUCCESS) {

                    textView0.setVisibility(View.GONE);
                    displayTelephone.setVisibility(View.VISIBLE);
                    displayCompanyName.setVisibility(View.VISIBLE);
                    displayEmail.setVisibility(View.VISIBLE);
                    displayPhone.setVisibility(View.VISIBLE);
                    displayName.setVisibility(View.VISIBLE);
                    displayWebsite.setVisibility(View.VISIBLE);
                    displayJobTitle.setVisibility(View.VISIBLE);
                    displayAddress.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(data.getStringExtra(TextBlockObject));
                    OCRresult=null;
                 OCRresult = data.getStringExtra(TextBlockObject);
                    //OCRresult ="hello";
                  //  displayName.setText("hello");
                    if(OCRresult != null){
                        processImage(OCRresult);
                       // extractPhone(OCRresult);
                        //Toast.makeText(RecieverOCR.this,OCRresult,Toast.LENGTH_SHORT).show();

                        }
                    else{
                        Toast.makeText(RecieverOCR.this,"Failed to scan text! try again",Toast.LENGTH_SHORT).show();
                    }

                }
            } else if (requestCode == LOAD_IMAGE_RESULTS) {
                Uri pickedImage = data.getData();
                String text = OCRCapture.Builder(this).getTextFromUri(pickedImage);
                textView0.setVisibility(View.GONE);

                displayTelephone.setVisibility(View.VISIBLE);
                displayJobTitle.setVisibility(View.VISIBLE);
                displayWebsite.setVisibility(View.VISIBLE);
                displayEmail.setVisibility(View.VISIBLE);
                displayCompanyName.setVisibility(View.VISIBLE);
                displayPhone.setVisibility(View.VISIBLE);
                displayName.setVisibility(View.VISIBLE);
                displayAddress.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(text);
                OCRresult=null;
                OCRresult = text;

                if(OCRresult != null){
                    processImage(OCRresult);
                    // extractPhone(OCRresult);
                    //Toast.makeText(RecieverOCR.this,OCRresult,Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(RecieverOCR.this,"Failed to scan text! try again",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void processImage(String Ocr){
      extractTelephone(Ocr);
        extractPhone(Ocr);
        extractUrl(Ocr);
        parseResults(Ocr);
        extractAddress(Ocr);
       extractName(Ocr);
       extractEmail(Ocr);

    }
    /**
     * Parses phoneNumbers from a string using Google's libphonenumber library
     *
     * @param bCardText, The text obtained from the vision API processing
     * @return ArrayList of parsed phone numbers from the vision API processed text string
     */
    private void parseResults(String bCardText) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> numberMatches = phoneNumberUtil.findNumbers(bCardText, Locale.US.getCountry());
        ArrayList<String> data = new ArrayList<>();
        for(PhoneNumberMatch number : numberMatches){
            String s = number.rawString();
            data.add(s);
            String result = data.get(0);
           Toast.makeText(RecieverOCR.this,result,Toast.LENGTH_SHORT).show();
           displayPhone.setText(result);
            displayTelephone.setText(s);
        }
      //  return data;
    }
    public void extractUrl(String str){
        System.out.println("Getting the url");
/*
        final String URL_REGEX= "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
       // final String URL_REGEX ="^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(URL_REGEX,Pattern.CASE_INSENSITIVE| Pattern.MULTILINE| Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if(m.find()){
            System.out.println(m.group());
            displayWebsite.setText(m.group());

        }*/

        String re1="((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))(?![\\w\\.])";	// Fully Qualified Domain Name 1

        Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            String fqdn1=m.group(1);
            displayWebsite.setText(fqdn1.toString());
          //  System.out.print("("+fqdn1.toString()+")"+"\n");
        }
    }

    public void extractTelephone(String str){
        String temp =str;
        String re1="([-+]\\d+)";	// Integer Number 1
        String re2="(\\s+)";	// White Space 1
        String re3="(1)";	// Any Single Character 1
        String re4="(\\d)";	// Any Single Digit 1
        String re5="(\\s+)";	// White Space 2
        String re6="(\\d+)";	// Integer Number 1
        String re7="(\\s+)";	// White Space 3
        String re8="(\\d+)";	// Integer Number 2

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            String signed_int1=m.group(1);
            String ws1=m.group(2);
            String c1=m.group(3);
            String d1=m.group(4);
            String ws2=m.group(5);
            String int1=m.group(6);
            String ws3=m.group(7);
            String int2=m.group(8);
           // displayTelephone.setText("hello");
            displayTelephone.setText(signed_int1.toString()+ws1.toString()+c1.toString()+d1.toString()+ws2.toString()+int1.toString()+ws3.toString()+int2.toString());
            //Toast.makeText(this,signed_int1.toString()+ws1.toString()+c1.toString()+d1.toString()+ws2.toString()+int1.toString()+ws3.toString()+int2.toString(),Toast.LENGTH_SHORT).show();
            //  System.out.print("("+signed_int1.toString()+")"+"("+ws1.toString()+")"+"("+c1.toString()+")"+"("+d1.toString()+")"+"("+ws2.toString()+")"+"("+int1.toString()+")"+"("+ws3.toString()+")"+"("+int2.toString()+")"+"\n");
        }
        String re11="([-+]\\d+)";	// Integer Number 1
        String re21="(\\s+)";	// White Space 1
        String re31="(\\(.*\\))";	// Round Braces 1
        String re41="(1)";	// Any Single Character 1
        String re51="(\\d)";	// Any Single Digit 1
        String re61="(\\s+)";	// White Space 2
        String re71="(\\d+)";	// Integer Number 1
        String re81="(\\s+)";	// White Space 3
        String re91="(\\d+)";	// Integer Number 2

        Pattern p1 = Pattern.compile(re11+re21+re31+re41+re51+re61+re71+re81+re91,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m1 = p1.matcher(str);
        if (m1.find()) {
            String signed_int1 = m1.group(1);
            String ws1 = m1.group(2);
            String rbraces1 = m1.group(3);
            String c1 = m1.group(4);
            String d1 = m1.group(5);
            String ws2 = m1.group(6);
            String int1 = m1.group(7);
            String ws3 = m1.group(8);
            String int2 = m.group(9);
            displayTelephone.setText(signed_int1 + ws1 + rbraces1 + c1 + d1 + ws2 + int1 + ws3 + int2);
        }
        String re12="(\\d)";	// Any Single Digit 1
        String re22="(1)";	// Any Single Character 1
        String re32="(\\d)";	// Any Single Digit 2
        String re42="(\\s+)";	// White Space 1
        String re52="(\\d+)";	// Integer Number 1
        String re62="(\\s+)";	// White Space 2
        String re72="(\\d+)";	// Integer Number 2

        Pattern p2 = Pattern.compile(re12+re22+re32+re42+re52+re62+re72,Pattern.CASE_INSENSITIVE |Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m2 = p2.matcher(str);
        if (m2.find())
        {
            String d1=m2.group(1);
            String c1=m2.group(2);
            String d2=m2.group(3);
            String ws1=m2.group(4);
            String int1=m2.group(5);
            String ws2=m2.group(6);
            String int2=m2.group(7);
            displayTelephone.setText(d1+c1+d2+ws1+int1+ws2+int2);
            //System.out.print("("+d1.toString()+")"+"("+c1.toString()+")"+"("+d2.toString()+")"+"("+ws1.toString()+")"+"("+int1.toString()+")"+"("+ws2.toString()+")"+"("+int2.toString()+")"+"\n");
        }

    }


    public void extractCompanyName(String str){
        String re1="((?:[a-z][a-z]+))";	// Word 1

        Pattern p = Pattern.compile(re1,Pattern.MULTILINE| Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            String word1=m.group(1);
            Toast.makeText(this,word1.toString(),Toast.LENGTH_SHORT).show();
            //System.out.print("("+word1.toString()+")"+"\n");
        }

    }

    public void extractName(String str){

        String re1="((?:[a-z][a-z]+))";	// Word 1
        String re2="(\\s+)";	// White Space 1
        String re3="((?:[a-z][a-z]+))";	// Word 2

        Pattern p = Pattern.compile(re1+re2+re3,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(OCRresult);
        if (m.find())
        {
            String word1=m.group(1);
            String ws1=m.group(2);
            String word2=m.group(3);
            displayName.setText(word1.toString()+ws1.toString()+word2.toString());
        }

    }

    public void extractEmail(String str) {
        String re1="([\\w-+]+(?:\\.[\\w-+]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7})";	// Email Address 1

        Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            String email1=m.group(1);
            displayEmail.setText(email1);
           // Toast.makeText(RecieverOCR.this,email1,Toast.LENGTH_SHORT).show();
            // displayEmail.setText(email1);
            //System.out.print("("+email1.toString()+")"+"\n");
        }
    }

    public void extractPhone(String str){
        String re1="([-+]\\d+)";	// Integer Number 1
        String re2="(\\s+)";	// White Space 1
        String re3="(\\d)";	// Any Single Digit 1
        String re4="(\\d)";	// Any Single Digit 2
        String re5="(\\s+)";	// White Space 2
        String re6="(\\d+)";	// Integer Number 1
        String re7="(\\s+)";	// White Space 3
        String re8="(\\d+)";	// Integer Number 2

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
        {
            String signed_int1=m.group(1);
            String ws1=m.group(2);
            String d1=m.group(3);
            String d2=m.group(4);
            String ws2=m.group(5);
            String int1=m.group(6);
            String ws3=m.group(7);
            String int2=m.group(8);
            displayPhone.setText(signed_int1.toString()+ws1.toString()+d1.toString()+d2.toString()+ws2.toString()+int1.toString()+ws3.toString()+int2.toString());
            //Toast.makeText(this,signed_int1.toString()+ws1.toString()+d1.toString()+d2.toString()+ws2.toString()+int1.toString()+ws3.toString()+int2.toString(),Toast.LENGTH_SHORT).show();
          //  System.out.print("("+signed_int1.toString()+")"+"("+ws1.toString()+")"+"("+d1.toString()+")"+"("+d2.toString()+")"+"("+ws2.toString()+")"+"("+int1.toString()+")"+"("+ws3.toString()+")"+"("+int2.toString()+")"+"\n");
        }
        String re11="([-+]\\d+)";	// Integer Number 1
        String re21="(\\s+)";	// White Space 1
        String re31="(\\(.*\\))";	// Round Braces 1
        String re41="(.)";	// Any Single Character 1
        String re51="(\\d)";	// Any Single Digit 1
        String re61="(\\s+)";	// White Space 2
        String re71="(\\d+)";	// Integer Number 1
        String re81="(\\s+)";	// White Space 3
        String re91="(\\d+)";	// Integer Number 2

        Pattern p1 = Pattern.compile(re11+re21+re31+re41+re51+re61+re71+re81+re91,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m1 = p1.matcher(str);
        if (m1.find()) {
            String signed_int1 = m1.group(1);
            String ws1 = m1.group(2);
            String rbraces1 = m1.group(3);
            String c1 = m1.group(4);
            String d1 = m1.group(5);
            String ws2 = m1.group(6);
            String int1 = m1.group(7);
            String ws3 = m1.group(8);
            String int2 = m1.group(9);
            displayPhone.setText(signed_int1 + ws1 + rbraces1 + c1 + d1 + ws2 + int1 + ws3 + int2);
        }
        String re12="(\\d+)";	// Integer Number 1
        String re22="(\\d)";	// Any Single Digit 1
        String re32="(\\s+)";	// White Space 1
        String re42="(\\d+)";	// Integer Number 2
        String re52="(\\s+)";	// White Space 2
        String re62="(\\d+)";	// Integer Number 3

        Pattern p2 = Pattern.compile(re12+re22+re32+re42+re52+re62,Pattern.CASE_INSENSITIVE |Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m2 = p2.matcher(str);
        if (m2.find())
        {

            String int1=m2.group(1);
            String d1=m2.group(2);
            String ws1=m2.group(3);
            String int2=m2.group(4);
            String ws2=m2.group(5);
            String int3=m2.group(6);
            displayPhone.setText(int1+d1+ws1+int2+ws2+int3);
            //System.out.print("("+int1.toString()+")"+"("+d1.toString()+")"+"("+ws1.toString()+")"+"("+int2.toString()+")"+"("+ws2.toString()+")"+"("+int3.toString()+")"+"\n");
        }

    }

    public void extractAddress(String str){
        System.out.println("Getting the Address");
        str = str.toLowerCase();
        final String ADDRESS_REGEX="\\s+(\\d{2,5}\\s+)(?![a|p]m\\b)(([a-zA-Z|\\s+]{1,5}){1,2})?([\\s|\\,|.]+)?(([a-zA-Z|\\s+]{1,30}){1,4})(court|ct|Street|street|st|drive|Cnr|dr|lane|ln|Road|road|rd|blvd)([\\s|\\,|.|\\;]+)?(([a-zA-Z|\\s+]{1,30}){1,2})([\\s|\\,|.]+)";
        Pattern p = Pattern.compile(ADDRESS_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            displayAddress.setText(m.group());
        }



    }



}

