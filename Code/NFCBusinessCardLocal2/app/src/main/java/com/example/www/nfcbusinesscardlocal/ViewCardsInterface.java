package com.example.www.nfcbusinesscardlocal;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewCardsInterface extends AppCompatActivity implements SearchView.OnQueryTextListener{
    ListView scrollView;
    TestUser viewUser;
    Intent intent;
    SearchView searchView;
    ArrayAdapter<String> arrayadapter;
    ClientListAdapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cards_interface);
        searchView=(SearchView) findViewById(R.id.filterlist);
        searchView.setOnQueryTextListener(this);
        scrollView= (ListView) findViewById(R.id.scrollusers);

        scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent=new Intent(getApplicationContext(),ViewCardDetails.class);

                String [] details=parent.getItemAtPosition(position).toString().split("\n");
                viewUser=new TestUser();
                viewUser.setFullname(details[0]);
                viewUser.setJobTitle(details[1]);
                viewUser.setCompanyName(details[2]);
                viewUser.setEmailAddress(details[3]);
                viewUser.setMobileNumber(details[4]);
                viewUser.setWorkTelephone(details[5]);
                viewUser.setWorkAddress(details[6]);


                intent.putExtra("ViewUser",viewUser);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
        setViews();
    }
    public void setViews()
    {
        List<TestUser> arrayList=null;
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("receivedlist", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String jsonConverter=sharedPreferences.getString("jsonreceivedlist","");
        if(jsonConverter.isEmpty())
        {
            Toast.makeText(ViewCardsInterface.this,"You have no cards received.",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else
        {
            Type type= new TypeToken<List<TestUser>>(){}.getType();
            arrayList=gson.fromJson(jsonConverter,type);
        }
        ArrayList<String> adapter=new ArrayList<>();

        for (TestUser user:arrayList) {
            adapter.add(user.getDetails());
        }
        ad=new ClientListAdapter(this,adapter);
        ad.sortAlphabetically(ad.getArrayList());
        scrollView.setAdapter(ad);

    }
    public void sortAlphabetically(View view){
        ad.sortAlphabetically(ad.getArrayList());
        scrollView.setAdapter(ad);
    }
    public void sortLatest(View view){
        ad.sortDate(ad.getArrayList());
        scrollView.setAdapter(ad);
    }
    public void sortSkills(View view){
        ad.sortJob(ad.getArrayList());
        scrollView.setAdapter(ad);
    }
    public void backMainActivity(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ad.filter(s);
        scrollView.setAdapter(ad);
        return false;
    }
}
