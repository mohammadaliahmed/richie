package com.richierich.apps.richierich.MyAccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.richierich.apps.richierich.DBModel.AccountDetails;
import com.richierich.apps.richierich.DBModel.AccountStatus;
import com.richierich.apps.richierich.DBModel.TotalEarnings;
import com.richierich.apps.richierich.DBModel.Users;
import com.richierich.apps.richierich.R;
import com.richierich.apps.richierich.Withdraw;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyDetails extends AppCompatActivity {
    DatabaseReference mDatabase;
    String userNameLogedIn;
    private SharedPreferences userPref;
    TextView amountEarned, clicksDone, accountStatus, email, level, phone, time, username;
    Button withdraw;

    double amount;


    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details);
        //Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        withdraw = (Button) findViewById(R.id.withdraw);

        amountEarned = (TextView) findViewById(R.id.amount);
        email = (TextView) findViewById(R.id.email);
        username = (TextView) findViewById(R.id.username);
        phone = (TextView) findViewById(R.id.phone);
        time = (TextView) findViewById(R.id.time);


        clicksDone = (TextView) findViewById(R.id.clicksdone);
        accountStatus = (TextView) findViewById(R.id.accountstatus);
        level = (TextView) findViewById(R.id.level);

        pd = new ProgressDialog(MyDetails.this);
        pd.setMessage("Working..");
        pd.setTitle("Account Details");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        pd.show();
        userPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userNameLogedIn = userPref.getString("username", "");
        //Toast.makeText(this, ""+userNameLogedIn, Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("" + userNameLogedIn);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.child("userDetails").getValue(Users.class);
                AccountDetails accountDetails = dataSnapshot.child("accountDetails").getValue(AccountDetails.class);
                AccountStatus status = dataSnapshot.child("accountStatus").getValue(AccountStatus.class);



                amount = (accountDetails.getLevel() + accountDetails.getAmountEarned() - 1);
                double value2 = Double.parseDouble(String.format("%.2f", amount));
                mDatabase.child("totalEarnings").setValue(new TotalEarnings(value2));
                amountEarned.setText("$ " + value2);

                username.setText(user.getUsername());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                time.setText("" + user.getTime());

                clicksDone.setText("" + accountDetails.getClicksDone());
                level.setText("" + accountDetails.getLevel());
                accountStatus.setText(status.getActive());

                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount >= 100) {
                    Intent i = new Intent(MyDetails.this, Withdraw.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MyDetails.this, "Minimum payout is $100.00\nYou have $" + amount + " in your account", Toast.LENGTH_LONG).show();
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
