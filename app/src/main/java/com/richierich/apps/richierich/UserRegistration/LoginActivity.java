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
import com.richierich.apps.richierich.DBModel.Clicks;
import com.richierich.apps.richierich.DBModel.TotalEarnings;
import com.richierich.apps.richierich.DBModel.Users;
import com.richierich.apps.richierich.MainScreen;
import com.richierich.apps.richierich.R;
import com.richierich.apps.richierich.Walkthrough.PrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    EditText userNa, password;
    Button login;

    String userName;
    TextView signup;
    String pass;
    DatabaseReference fdb;

    ProgressDialog pd;
    private PrefManager prefManager;
    int count;
    int level;
    double totalEarnings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        userNa = (EditText) findViewById(R.id.username_login_field);
        password = (EditText) findViewById(R.id.password_login_field);
        login = (Button) findViewById(R.id.login_button);
        signup = (TextView) findViewById(R.id.signup_text);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, Signup.class);
                startActivity(i);
                finish();


            }
        });

        prefManager = new PrefManager(LoginActivity.this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();

        }


        pd = new ProgressDialog(LoginActivity.this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isConnected();
                    if (isConnected()) {
                        //Toast.makeText(LoginActivity.this, "connected", Toast.LENGTH_SHORT).show();
                        if (userNa.getText().toString().length() == 0) {
                            userNa.setError("Field cannot be left blank");
                        } else if (password.getText().toString().length() == 0) {
                            password.setError("Field cannot be left blank");
                        } else {
                            pd.setMessage("Loging in..");
                            pd.setTitle("Please Wait..");
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
                            pd.show();
                            userName = userNa.getText().toString();
                            pass = password.getText().toString();

                            fdb = FirebaseDatabase.getInstance().getReference().child("users").child("" + userName);
                            fdb.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Users us = dataSnapshot.child("userDetails").getValue(Users.class);
                                    if (us != null) {
                                        String passww = us.getPassword();
                                        pd.dismiss();
                                        if (passww.equals(pass)) {
                                            Clicks clicks = dataSnapshot.child("clicks").getValue(Clicks.class);
                                            if (clicks != null) {
                                                count = clicks.getClicks();
                                            }
                                            AccountDetails accountDetails=dataSnapshot.child("accountDetails").getValue(AccountDetails.class);
                                            if(accountDetails!=null){
                                                level=accountDetails.getLevel();
                                            }
                                            TotalEarnings earn=dataSnapshot.child("totalEarnings").getValue(TotalEarnings.class);
                                            if(earn!=null){
                                                totalEarnings=earn.getTotalEarned();
                                            }
                                            sharedPrefs(userName,level,totalEarnings);
                                            //Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                                            launchHomeScreen();


                                        } else {
                                            Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet Connection\nPlease turn on Wifi", Toast.LENGTH_SHORT).show();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void launchHomeScreen() {



//        Intent intent=new Intent();
//       // intent.putExtra("luckyNumber",""+luckyNumber);
//        setResult(5,intent);
//        finish();



//        SharedPreferences pref= getSharedPreferences("logedIn",Context.MODE_PRIVATE);
//        SharedPreferences.Editor edt=pref.edit();
//        edt.putBoolean("activity_executed",true);
//        edt.commit();
        Intent i=new Intent(LoginActivity.this,MainScreen.class);
        startActivity(i);
        prefManager.setFirstTimeLaunch(false);
       finish();

    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    private void sharedPrefs(String usr,int level, double earned) {
        SharedPreferences pref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", usr);
        editor.putString("earnings", ""+earned);
        editor.putInt("level", level);

        editor.apply();

        SharedPreferences pref2 = getSharedPreferences("clicksLocal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref2.edit();
        editor2.putInt("count", count);
        editor2.apply();

    }

}
