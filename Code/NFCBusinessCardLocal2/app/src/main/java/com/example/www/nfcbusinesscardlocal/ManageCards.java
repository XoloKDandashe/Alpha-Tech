package com.example.www.nfcbusinesscardlocal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
        if(jsonConverter.isEmpty())
        {
            Toast.makeText(this,"You have no cards to view.",Toast.LENGTH_LONG).show();
            return;
        }
        Intent mov = new Intent(this, ViewCardsInterface.class);
        startActivity(mov);
    }
    public void backManageCards(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
