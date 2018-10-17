package com.alphatech.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewCardsInterface extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    User person=null;
    ListView scrollView;
    User viewUser;
    Intent intent;
    SearchView searchView;
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
                viewUser=new User();
                viewUser.setFullname(details[0]);
                viewUser.setJobTitle(details[1]);
                viewUser.setCompanyName(details[2]);
                viewUser.setEmailAddress(details[3]);
                viewUser.setMobileNumber(details[4]);
                viewUser.setWorkTelephone(details[5]);
                viewUser.setWorkAddress(details[6]);
                viewUser.setWebsite(details[7]);
                if(details.length>8)
                viewUser.setImageUrl(details[8]);
                intent.putExtra("ViewUser",viewUser);
                startActivity(intent);
            }
        });
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Retrieving Appointments...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person=dataSnapshot.getValue(User.class);
                mProgressDialog.dismiss();
                setViews(person.getRecievedCards());
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
    public void setViews(String cardlist)
    {
        List<User> arrayList=null;
        Gson gson= new Gson();
        if(cardlist.isEmpty()||cardlist.compareTo("")==0||cardlist.compareTo("[]")==0)
        {
            Toast.makeText(ViewCardsInterface.this,"You have no cards received.",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else
        {
            Type type= new TypeToken<List<User>>(){}.getType();
            arrayList=gson.fromJson(cardlist,type);
        }
        ArrayList<String> adapter=new ArrayList<>();
        for (User user:arrayList) {
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
