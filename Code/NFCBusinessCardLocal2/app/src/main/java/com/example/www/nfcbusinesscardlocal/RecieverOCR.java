package com.example.www.nfcbusinesscardlocal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import com.example.www.nfcbusinesscardlocal.Ocr.OCRCapture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.guna.ocrlibrary.OcrCaptureActivity.TextBlockObject;

public class RecieverOCR extends AppCompatActivity {

    private TextView textView;
    private final int CAMERA_SCAN_TEXT = 0;
    private final int LOAD_IMAGE_RESULTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_orc);
        textView = findViewById(R.id.reciever_tv);
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
                    textView.setText(data.getStringExtra(TextBlockObject));
                }
            } else if (requestCode == LOAD_IMAGE_RESULTS) {
                Uri pickedImage = data.getData();
                String text = OCRCapture.Builder(this).getTextFromUri(pickedImage);
                textView.setText(text);
            }
        }
    }

    public void processImage(String Ocr){
      extractTelephone(Ocr);
        extractPhone(Ocr);
        extractAddress(Ocr);
       extractName(Ocr);
       extractEmail(Ocr);

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
    public void extractUrl(String str){
        System.out.println("Getting the url");
        final String URL_REGEX= "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(URL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if(m.find()){
            System.out.println(m.group());
            displayJobTitle.setText(m.group());

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
        final String ADDRESS_REGEX="\\s+(\\d{2,5}\\s+)(?![a|p]m\\b)(([a-zA-Z|\\s+]{1,5}){1,2})?([\\s|\\,|.]+)?(([a-zA-Z|\\s+]{1,30}){1,4})(court|ct|street|st|drive|Cnr|dr|lane|ln|road|rd|blvd)([\\s|\\,|.|\\;]+)?(([a-zA-Z|\\s+]{1,30}){1,2})([\\s|\\,|.]+)";
        Pattern p = Pattern.compile(ADDRESS_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            displayAddress.setText(m.group());
        }


    }





}

