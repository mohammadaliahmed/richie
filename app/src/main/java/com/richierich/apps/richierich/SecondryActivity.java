package com.richierich.apps.richierich;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.richierich.apps.richierich.UserRegistration.LoginActivity;

public class SecondryActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondry);
        btn=(Button)findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SecondryActivity.this,LoginActivity.class);
                startActivityForResult(i,5);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==5){
            btn.setText("Get Started");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    if(pref.getBoolean("activity_executed", false)){
                        Intent intent = new Intent(SecondryActivity.this, MainScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        SharedPreferences.Editor ed = pref.edit();
                        ed.putBoolean("activity_executed", true);
                        ed.commit();
                    }

                }
            });
        }
    }
}
