package com.richierich.apps.richierich;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.richierich.apps.richierich.UserRegistration.LoginActivity;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i=new Intent(Splash.this,LoginActivity.class);
                startActivity(i);
                finish();
//                SharedPreferences pref=getSharedPreferences("logedIn", Context.MODE_PRIVATE);
//                if(pref.getBoolean("activity_executed",false)) {
//                    Intent i = new Intent(Splash.this, MainScreen.class);
//
//                    startActivity(i);
//                    finish();
//                }
//                else{
//                    Intent i =new Intent(Splash.this,Login.class);
//                    startActivity(i);
//                    finish();
//                }

            }
        }, SPLASH_TIME_OUT);
    }
}
