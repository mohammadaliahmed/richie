package com.richierich.apps.richierich;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.richierich.apps.richierich.DBModel.CompletedHundered;
import com.richierich.apps.richierich.DBModel.HasCompleted;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Withdraw extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton pp, sk, ep;
    Button withdraw;
    EditText et;
    String selected;
    DatabaseReference mDatabase;
    private SharedPreferences userPref;
    String userNameLogedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        userPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userNameLogedIn = userPref.getString("username", "");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userNameLogedIn);


        et = (EditText) findViewById(R.id.em);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        withdraw = (Button) findViewById(R.id.withdrawnow);
        int checked = radioGroup.getCheckedRadioButtonId();
        pp = (RadioButton) findViewById(checked);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                //Toast.makeText(Withdraw.this, "" + radioButton.getText(), Toast.LENGTH_SHORT).show();
                //lu selected =pp.getText().toString();
                selected = "" + radioButton.getText();
                if (selected.equals("Paypal")) {
                    //  et.setHint("Enter Paypal Email Id");
                }
                if (selected.equals("Skrill")) {
                    // et.setHint("Enter Skrill Email Id");
                }
                if (selected.equals("EasyPaisa")) {
                    // et.setHint("Enter EasyPaisa CNIC or Phone #");
                }
            }
        });


        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String data = et.getText().toString();
                if (et.getText().toString().length() == 0) {
                    et.setError("Field cannot be left blank");
                } else {
                    mDatabase.child("hasCompleted").setValue(new HasCompleted("completed"));
                    Toast.makeText(Withdraw.this, "Your request has been received\n" +
                            "You will recieve the earnings in 3-5 days ", Toast.LENGTH_LONG).show();
                    mDatabase.child("completed").setValue(new CompletedHundered("completed",""+data));

                    Intent i = new Intent(Withdraw.this, MainScreen.class);
                    startActivity(i);
                    finish();

                    //Toast.makeText(Withdraw.this, selected, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
