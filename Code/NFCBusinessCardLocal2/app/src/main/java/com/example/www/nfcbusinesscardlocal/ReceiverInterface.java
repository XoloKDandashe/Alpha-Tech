package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ReceiverInterface extends AppCompatActivity {

    TestUser person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_interface);
    }
    public void openNFCRecieve(View view)
    {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(ReceiverInterface.this, "NFC is not available. Use QR option.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, ReceiverActivity.class);
        startActivity(mov);
    }
    public void openQRRecieve(View view)
    {
        Intent intent = new Intent(this,ReaderActivity.class);
        startActivity(intent);
    }
    public void openRecieveImport(View view)
    {
        Intent intent = new Intent(this,ReceiveImportCardActivity.class);
        startActivity(intent);
    }
    public void backMainActivity(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
