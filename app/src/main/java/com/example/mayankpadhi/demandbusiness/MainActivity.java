package com.example.mayankpadhi.demandbusiness;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static int Splash_Time_Out = 5000;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        // ImageView rotateImage = (ImageView) findViewById(R.id.img);
        //Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        //rotateImage.startAnimation(startRotateAnimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(currentUser==null){
                    Intent Login = new Intent(MainActivity.this, Login.class);
                    startActivity(Login);
                    finish();
                }
                else{
                    Intent Login = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(Login);
                    finish();
                }
            }
        },Splash_Time_Out);
    }
}