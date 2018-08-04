package com.example.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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

public class ViewAppointmentsInterface extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    private TestUser person=null;
    private AppointmentListAdapter ad;
    SwipeMenuListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments_interface);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        listView=(SwipeMenuListView) findViewById(R.id.appointment_List);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_trash_can);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        List<Appointment> arrayList=null;
                        //SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
                        Gson gson= new Gson();
                        String jsonConverter=person.getAppointmentlist();
                        if(jsonConverter.isEmpty()||jsonConverter.compareTo("")==0)
                        {
                            Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
                            finish();
                            return false;
                        }
                        else
                        {
                            Type type= new TypeToken<List<Appointment>>(){}.getType();
                            arrayList=gson.fromJson(jsonConverter,type);
                        }
                        arrayList.remove(position);
                        String jsonEncode= gson.toJson(arrayList);
                        person.setAppointmentlist(jsonEncode);
                        saveupdate(person);
                        Toast.makeText(ViewAppointmentsInterface.this, "Appointment has been deleted.", Toast.LENGTH_SHORT).show();
                        setViews(person.getAppointmentlist());
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }
    private void saveupdate(TestUser user){
        databaseReference.setValue(user);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog.setMessage("Retrieving Appointments...");
        mProgressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person=dataSnapshot.getValue(TestUser.class);
                mProgressDialog.dismiss();
                setViews(person.getAppointmentlist());
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
    public void setViews(String appointmentlist)
    {
        List<Appointment> arrayList=null;
        //SharedPreferences sharedPreferences=getApplication().getSharedPreferences("appointmentList", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        //String jsonConverter=sharedPreferences.getString("jsonappointmentList","");
        if(appointmentlist.isEmpty()||appointmentlist.compareTo("")==0||appointmentlist.compareTo("[]")==0)
        {
            Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Type type= new TypeToken<List<Appointment>>(){}.getType();
        arrayList=gson.fromJson(appointmentlist,type);
        if(arrayList==null)
            {
                Toast.makeText(ViewAppointmentsInterface.this, "No appointments to view", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

        ArrayList<String> adapter=new ArrayList<>();

        for (Appointment appointment:arrayList) {
            adapter.add(appointment.getAppointmentDetails());
        }
        ad=new AppointmentListAdapter(this,adapter);
        listView.setAdapter(ad);
    }
    public void goback(View view){
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
