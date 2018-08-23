package com.example.www.nfcbusinesscardlocal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView1 =findViewById(R.id.imageV);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        Animation animationfadeout = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
        imageView1.startAnimation(animation);
        //imageView1.startAnimation(animationfadeout);
      //  imageView1.setVisibility(View.INVISIBLE);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(4000);

                    Intent intent= new Intent(getApplicationContext(),Splash2.class);
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
