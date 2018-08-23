package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        TextView text1 =findViewById(R.id.textView1);
        ImageView imageView1 =findViewById(R.id.imageV1);
        TextView text2 =findViewById(R.id.textView2);
        ImageView imageView2 =findViewById(R.id.imageV2);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        Animation animationfadeout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
        text1.startAnimation(animation);
        imageView1.startAnimation(animation);
        text2.startAnimation(animation);
        imageView2.startAnimation(animation);




        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);

                    Intent intent= new Intent(getApplicationContext(),LogIn.class);
                    startActivity(intent);
                    finish();
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.start();
    }
}
