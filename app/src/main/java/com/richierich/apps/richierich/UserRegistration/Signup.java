package com.richierich.apps.richierich.UserRegistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.richierich.apps.richierich.DBModel.AccountDetails;
import com.richierich.apps.richierich.DBModel.AccountStatus;
import com.richierich.apps.richierich.DBModel.Clicks;
import com.richierich.apps.richierich.DBModel.CompletedHundered;
import com.richierich.apps.richierich.DBModel.HasCompleted;
import com.richierich.apps.richierich.DBModel.TotalEarnings;
import com.richierich.apps.richierich.DBModel.Users;
import com.richierich.apps.richierich.MainScreen;
import com.richierich.apps.richierich.R;
import com.richierich.apps.richierich.Walkthrough.PrefManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Signup extends AppCompatActivity {
    EditText username, email, phone, password;
    TextView login;
    Button signup;

    DatabaseReference mDatabase;
    String currentDateTimeString;
    private PrefManager prefManager;
    ProgressDialog pd;


    List<String> userNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        pd = new ProgressDialog(Signup.this);
        pd.setMessage("Signing up!");
        pd.setTitle("Richie Rich");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        currentDateTimeString = (String) df.format("dd-MM-yyyy hh:mm a", new Date());


        username = (EditText) findViewById(R.id.username_signup_field);
        email = (EditText) findViewById(R.id.email_signup_field);
        phone = (EditText) findViewById(R.id.phone_signup_field);
        password = (EditText) findViewById(R.id.password_signup_field);

        signup = (Button) findViewById(R.id.signup_button);


        login = (TextView) findViewById(R.id.signin_text);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userNameList.add(dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isConnected();
                    if (isConnected()) {
                        //Toast.makeText(Signup.this, "connected", Toast.LENGTH_SHORT).show();
                        if (username.getText().toString().length() == 0) {
                            username.setError("A username is required!");
                        } else if (username.getText().toString().length() > 0 && username.getText().toString().length() < 6) {
                            username.setError("Atleast 6 characters required");
                        } else if (email.getText().toString().length() == 0) {
                            email.setError("Email adress cannot be left blank!");
                        } else if (password.getText().toString().length() == 0) {
                            password.setError("Password cannot be left blank!");
                        } else if (password.getText().toString().length() > 0 && password.getText().toString().length() < 8) {
                            password.setError("Atleast set 8 character password");
                        } else if (phone.getText().toString().length() == 0) {
                            phone.setError("Phone number cannot be left blank!");
                        } else {
                            setUserDetails();

                        }
                    } else {
                        Toast.makeText(Signup.this, "No Internet Connection\nPlease turn on Wifi", Toast.LENGTH_SHORT).show();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        pd.dismiss();
        startActivity(new Intent(Signup.this, MainScreen.class));

        finish();
    }

    private void setUserDetails() {

        String userName, emailId, phoneNumber, pass;
        userName = username.getText().toString();
        emailId = email.getText().toString();
        phoneNumber = phone.getText().toString();
        pass = password.getText().toString();


        if (userNameList != null) {
            if (userNameList.contains(userName)) {
                Toast.makeText(this, "Username is taken\nPlease choose another", Toast.LENGTH_SHORT).show();

            } else {
                mDatabase.child(userName).child("userDetails").setValue(new Users(userName, emailId, pass, phoneNumber, currentDateTimeString));

                mDatabase.child(userName).child("accountDetails").setValue(new AccountDetails(0, 0, 1));
                mDatabase.child(userName).child("accountStatus").setValue(new AccountStatus("active"));
                mDatabase.child(userName).child("clicks").setValue(new Clicks(25000));
                mDatabase.child(userName).child("hasCompleted").setValue(new HasCompleted("no"));
                mDatabase.child(userName).child("totalEarnings").setValue(new TotalEarnings(0));
                mDatabase.child(userName).child("completed").setValue(new CompletedHundered("no",""));
                SharedPreferences pref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("username", userName);
                editor.putInt("level", 1);
                editor.putString("earnings", "" + 0);
                editor.apply();

                SharedPreferences pref2 = getSharedPreferences("clicksLocal", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pref2.edit();
                editor2.putInt("count", 25000);
                editor2.apply();
                Toast.makeText(this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                launchHomeScreen();

            }
        }
    }
}
