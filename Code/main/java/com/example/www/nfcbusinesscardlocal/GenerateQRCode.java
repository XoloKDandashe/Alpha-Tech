package com.example.www.nfcbusinesscardlocal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import org.json.JSONException;
import org.json.JSONObject;
public class GenerateQRCode extends AppCompatActivity {

    EditText FirstName;
    EditText LastName;
    Button gen_btn;
    ImageView image;
    String text2Qr;
    String text3Qr;
    private EditText mEditText;
    TestUser person=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mEditText = (EditText) findViewById(R.id.edit_text_field);
        setContentView(R.layout.activity_generate_qrcode);
        Intent intent=getIntent();
        if(intent.hasExtra("LoginUser")) {
            person = (TestUser) intent.getSerializableExtra("LoginUser");
            //mEditText.setText(person.getDetails());
            //Toast.makeText(this, person.getDetails(), Toast.LENGTH_SHORT).show();
        }
        //FirstName = (EditText) findViewById(R.id.firstname);
        //FirstName.setText(person.getFullname());
        //LastName = (EditText) findViewById(R.id.lastname);
        //LastName.setText(person.getJobTitle());
        //final String qrstring = FirstName.toString();

        gen_btn = (Button) findViewById(R.id.gen_btn);
        image = (ImageView) findViewById(R.id.image);

        gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //text2Qr = FirstName.getText().toString().trim();
                //text3Qr = LastName.getText().toString().trim();

                final String result = person.generateDetails();

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{

                    BitMatrix bitMatrix = multiFormatWriter.encode(result, BarcodeFormat.QR_CODE,200,200);
                    // BitMatrix bitMatrix2 = multiFormatWriter.encode(text3Qr, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    // Bitmap bitmap2 = barcodeEncoder.createBitmap(bitMatrix2);
                    image.setImageBitmap(bitmap);
                    // image.setImageBitmap(bitmap2);
                }
                catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });

    }
}
