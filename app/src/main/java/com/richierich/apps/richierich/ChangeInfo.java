package com.richierich.apps.richierich;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.richierich.apps.richierich.DBModel.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeInfo extends AppCompatActivity {
    private SharedPreferences userPref;
    String userNameLogedIn;
    DatabaseReference mDatabase;
    String password;
    EditText old,newPass,confirm;
    Button change;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        change=(Button)findViewById(R.id.change);
        old=(EditText)findViewById(R.id.old);
        newPass=(EditText)findViewById(R.id.newpass);
        confirm=(EditText)findViewById(R.id.confirm);
        username=(TextView)findViewById(R.id.username);

        userPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userNameLogedIn = userPref.getString("username", "");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(userNameLogedIn);

        username.setText("Signed in as: "+userNameLogedIn);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users=dataSnapshot.child("userDetails").getValue(Users.class);
                if(users!=null) {
                    password = users.getPassword();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String o=old.getText().toString();
                if(o.equals(password)) {
                    String newPassword = newPass.getText().toString();
                    String confrimPassword = confirm.getText().toString();
                    if (newPass.getText().toString().length() < 8) {
                        newPass.setError("8 or more characters are required");
                    } else if (confirm.getText().toString().length() < 8) {
                        confirm.setError("8 or more characters are required");
                    }else{
                    if (newPassword.equals(confrimPassword)) {
                        mDatabase.child("userDetails").child("password").setValue(newPassword);
                        Toast.makeText(ChangeInfo.this, "Password changed", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ChangeInfo.this, MainScreen.class);
                        startActivity(i);
                    } else {
                        confirm.setError("Password do no match");
                    }
                }
                }else {
                    old.setError("Current password does not match");
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
