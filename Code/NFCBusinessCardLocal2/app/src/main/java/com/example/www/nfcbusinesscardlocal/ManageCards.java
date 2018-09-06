package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class ManageCards extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managecards);
    }
    public void openViewCardsInterface(View view)
    {
        Intent mov = new Intent(this, ViewCardsInterface.class);
        startActivity(mov);
    }
    public void openCardReadInformation(View view)
    {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not available on device.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, CardReadInformation.class);
        startActivity(mov);
    }
    public void openCardDeleteInformation(View view)
    {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not available on device.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, CardDeleteInformation.class);
        startActivity(mov);
    }
    public void openCardWriteInformation(View view)
    {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            Toast.makeText(this, "NFC is not available on device.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, CardWriteInformation.class);
        startActivity(mov);
    }
    public void openReadNFC(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
