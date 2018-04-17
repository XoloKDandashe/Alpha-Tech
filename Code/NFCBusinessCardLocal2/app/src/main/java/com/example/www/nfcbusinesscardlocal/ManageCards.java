package com.example.www.nfcbusinesscardlocal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ManageCards extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managecards);
    }
    public void backManageCards(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
