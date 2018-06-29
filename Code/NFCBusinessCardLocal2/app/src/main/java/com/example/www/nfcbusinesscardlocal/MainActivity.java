package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TestUser person=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        if(intent.hasExtra("LoginUser")) {
            person = (TestUser) intent.getSerializableExtra("LoginUser");
        }
    }
    public void openViewDetails(View view){
        Intent intent = new Intent(this,ViewDetails.class);
        intent.putExtra("LoginUser",person);
        startActivity(intent);
        finish();
    }
    public void openManageCards(View view){
        Intent intent = new Intent(this,ManageCards.class);
        startActivity(intent);
    }
    public void openSendCard(View view){
        Intent intent = new Intent(this,SendInterface.class);
        intent.putExtra("LoginUser",person);
        startActivity(intent);
    }
    public void openReceiveCard(View view){
        Intent intent = new Intent(this,ReceiverInterface.class);
        intent.putExtra("LoginUser",person);
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
