package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveImportCardActivity extends AppCompatActivity {
    TestUser newUser;
    Button button ;
    Button backbutton,saveButton;
    ImageView imageView ;
    File photo;
    Intent intent ;
    public  static final int RequestPermissionCode  = 1 ;
    private float curScale = 1F;
    int bitWidth,bitHeight;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    final int CROP_PIC_REQUEST_CODE = 5;
    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    Button runOCR;
    EditText displayJobTitle;
    EditText displayEmail;
    EditText displayPhone;
    EditText displayMobileNumber;
    EditText displayName;
    EditText displayAddress;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    TestUser person=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_import_card);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        button = (Button)findViewById(R.id.rec_button);
        saveButton = (Button)findViewById(R.id.rec_import_btn_signup);
        imageView = (ImageView)findViewById(R.id.rec_imageView);
        runOCR=(Button) findViewById(R.id.rec_scan_button);
        displayEmail=(EditText)findViewById(R.id.rec_import_email);
        displayJobTitle=(EditText)findViewById(R.id.rec_import_jobtitle);
        displayPhone=(EditText)findViewById(R.id.rec_import_telephone);
        displayName=(EditText)findViewById(R.id.rec_import_name);
        displayAddress=(EditText)findViewById(R.id.rec_import_address);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        EnableRuntimePermission();
        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();
        backbutton=(Button) findViewById(R.id.rec_buttonback);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                backToReceive();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SaveCard();
            }
        });
        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);

        //run the OCR on the test_image...
        runOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(ReceiveImportCardActivity.this);
                mProgressDialog.setMessage("Scanning Card");
                mProgressDialog.show();

                            processImage();



            }

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File vdfdirectory = new File(
                        Environment.getExternalStorageDirectory() + "/businesscards");
                if (!vdfdirectory.exists()) {
                    vdfdirectory.mkdirs();
                }

                photo = new File(vdfdirectory, "/businesscard.jpeg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ReceiveImportCardActivity.this,BuildConfig.APPLICATION_ID+".provider",photo));
                startActivityForResult(intent, 7);

                //image = BitmapFactory.decodeResource(getResources(), imageView.get);


            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Preparing transfer...");
        mProgressDialog.show();
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
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    public void processImage(){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        //displayText.setText(OCRresult);
        extractName(OCRresult);
        extractEmail(OCRresult);
        extractPhone(OCRresult);
        extractAddress(OCRresult);
        extractUrl(OCRresult);
        mProgressDialog.dismiss();
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

    public void extractName(String str){
        System.out.println("Getting the Name");
        final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
        Matcher m =  p.matcher(str);
        if(m.find()){
            System.out.println(m.group());
            displayName.setText(m.group());
        }
    }

    public void extractEmail(String str) {
        System.out.println("Getting the email");
        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            displayEmail.setText(m.group());
        }
    }

    public void extractPhone(String str){
       /* if(checkedNumber == false){
            System.out.println("Getting Phone Number");

             final String PHONE_REGEX="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
            //final String PHONE_REGEX = "(?:(?:(\\s*\\(?([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\)?\\s*(?:[.-]\\s*)?)([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})";


            Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
            Matcher m = p.matcher(str);   // get a matcher object
            if (m != null) {
                if (m.find()) {
                    System.out.println(m.group());
                    displayPhone.setText(m.group());
                }
                checkedNumber = true;
            }
        }*/

        System.out.println("Getting Phone Number");

        // final String PHONE_REGEX="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        final String PHONE_REGEX1="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        final String PHONE_REGEX = "(?:(?:(\\s*\\(?([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\)?\\s*(?:[.-]\\s*)?)([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})";
        final String PHONE_REGEX2 = "^\\(?([0-9|l*]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$";
        Pattern p1 = Pattern.compile(PHONE_REGEX1,Pattern.MULTILINE);
        Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
        Pattern p2 = Pattern.compile(PHONE_REGEX2, Pattern.MULTILINE);
        Matcher m1 = p1.matcher(str);

        if (m1.find()) {
            System.out.println(m1.group());
            displayPhone.setText(m1.group());
        }
        Matcher m2 = p2.matcher(str);
        if(m2.find()){
            System.out.println(m2.group());
            displayPhone.setText(m2.group());
        }
        Matcher m = p.matcher(str);   // get a matcher object
        if (m.find()) {
            System.out.println(m.group());
            displayPhone.setText(m.group());
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

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode  == 4 && resultCode == RESULT_OK)
        {
            try {

                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(FileProvider.getUriForFile(ReceiveImportCardActivity.this,BuildConfig.APPLICATION_ID+".provider",photo), "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 128);
                cropIntent.putExtra("outputY", 128);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, 7);
            }
            // respond to users whose devices do not support the crop action
            catch (ActivityNotFoundException anfe) {
                // display an error message
                String errorMessage = "Whoops - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
            return;
        }
        if (requestCode == 7 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

            bitWidth=bitmap.getWidth();
            bitHeight=bitmap.getHeight();
            //if(bitWidth<bitHeight) {
            Matrix matrix = new Matrix();
            matrix.postScale(curScale, curScale);
            image = Bitmap.createBitmap(bitmap, 0, 0, bitWidth, bitHeight, matrix, true);
            //edgesim(image);
            imageView.setImageBitmap(image);
            /*}
            else
            {
                Matrix matrix = new Matrix();
                matrix.postScale(curScale, curScale);
                image = Bitmap.createBitmap(bitmap, 0, 0, bitHeight, bitWidth, matrix, true);
                imageView.setImageBitmap(image);
            }*/
            //doCrop(getImageUri(this,image));
            return;
        }
        if (requestCode == CROP_PIC_REQUEST_CODE) {
            if (data != null) {
                Bundle extras = data.getExtras();
                image= extras.getParcelable("data");

                imageView.setImageBitmap(image);
            }
        }
    }
    /*public static Bitmap edgesim(Bitmap first) {

        Bitmap image1;

        ///////////////transform back to Mat to be able to get Canny images//////////////////
        //Size size=new Size();
        //size.width=Double.parseDouble(first.getWidth()+"");
        //size.height=Double.parseDouble(first.getHeight()+"");
        Mat img1=new Mat();
        Utils.bitmapToMat(first,img1);

        //mat gray img1 holder
        Mat imageGray1 = new Mat();

        //mat canny image
        Mat imageCny1 = new Mat();

        /////////////////////////////////////////////////////////////////

        //Convert img1 into gray image
        Imgproc.cvtColor(img1, imageGray1, Imgproc.COLOR_BGR2GRAY);

        //Canny Edge Detection
        Imgproc.Canny(imageGray1, imageCny1, 10, 100, 3, true);

        ///////////////////////////////////////////////////////////////////

        //////////////////Transform Canny to Bitmap/////////////////////////////////////////
        image1= Bitmap.createBitmap(imageCny1.cols(), imageCny1.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageCny1, image1);

        return image1;
    }*/
    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 2);
            cropIntent.putExtra("outputX", 1024);
            cropIntent.putExtra("outputY", 1024);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(ReceiveImportCardActivity.this, Manifest.permission.CAMERA))
        {

            Toast.makeText(ReceiveImportCardActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(ReceiveImportCardActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(ImportCardDetails.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(ReceiveImportCardActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
    public void SaveCard(){
        String emailneedle,inputCheck;
        EditText editText;
        newUser=new TestUser();
        /*Check all neccessary fields have information */
        editText=(EditText)findViewById(R.id.rec_import_name);
        inputCheck=editText.getText().toString().trim();
        if(inputCheck.isEmpty() || inputCheck.length() == 0 || inputCheck.equals("") || inputCheck == null)
        {
            Toast.makeText(this, "Please Enter Your Full Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setFullname(inputCheck);

        editText=(EditText)findViewById(R.id.rec_import_jobtitle);
        newUser.setJobTitle(editText.getText().toString());
        editText=(EditText)findViewById(R.id.rec_import_companyname);
        newUser.setCompanyName(editText.getText().toString());
        editText=(EditText)findViewById(R.id.rec_import_telephone);
        newUser.setWorkTelephone(editText.getText().toString());
        editText=(EditText)findViewById(R.id.rec_import_address);
        newUser.setWorkAddress(editText.getText().toString());
        editText=(EditText)findViewById(R.id.rec_import_mobile);
        newUser.setMobileNumber(editText.getText().toString());


        /*Check if email exists*/
        editText=(EditText)findViewById(R.id.rec_import_email);
        emailneedle=editText.getText().toString().trim();
        if(emailneedle.isEmpty() || emailneedle.length() == 0 || emailneedle.equals("") || emailneedle == null)
        {
            Toast.makeText(this, "Email must be given.", Toast.LENGTH_SHORT).show();
            return;
        }
        newUser.setEmailAddress(emailneedle);

        List<TestUser> arrayList=null;
        //SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        //String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
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
        arrayList.add(newUser);
        String jsonEncode= gson.toJson(arrayList);
        person.setRecievedCards(jsonEncode);

        Toast.makeText(this,"Business card is saved.",Toast.LENGTH_LONG).show();
        finish();
    }
    public void backToReceive(){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
