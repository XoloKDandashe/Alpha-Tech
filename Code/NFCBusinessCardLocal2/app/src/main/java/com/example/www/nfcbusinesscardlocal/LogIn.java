package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity {

    ArrayList<TestUser> UserList=new ArrayList<>();
    TestUser tu;
    int user=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Intent intent=getIntent();
        if(intent.hasExtra("newUser")) {
            tu = (TestUser) intent.getSerializableExtra("newUser");
            TestUser newUser= new TestUser(tu);
            UserList.add(newUser);
        }
        TestUser admin=new TestUser();
        admin.setFullname("Admin");
        admin.setJobTitle("Manager");
        admin.setMobileNumber("+27 74 935 9620");
        admin.setWorkTelephone("+27 14 935 9620");
        admin.setEmailAddress("admin@info.com");
        admin.setPassword("admin");
        UserList.add(admin);
    }
    public void openMainMenu(View view){
        String email,password;
        EditText editText=(EditText)findViewById(R.id.emaillogin);
        email=editText.getText().toString();
        editText=(EditText)findViewById(R.id.passwordlogin);
        password=editText.getText().toString();

        for(int i=0;i<UserList.size();i++)
        {
            if(UserList.get(i).getEmailAddress().compareTo(email)==0&&UserList.get(i).getPassword().compareTo(password)==0)
            {
                user=i;
            }
        }
        if(user==-1)
        {
            Toast.makeText(this, "Account does not exist.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent= new Intent(this,MainActivity.class);
        intent.putExtra("LoginUser",UserList.get(user));
        startActivity(intent);
    }
    public void openRegistration(View view){
        Intent intent = new Intent(this,Registration.class);
        intent.putExtra("userlist",UserList);
        startActivity(intent);
    }
}
