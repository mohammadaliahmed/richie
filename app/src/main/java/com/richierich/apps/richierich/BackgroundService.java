package com.richierich.apps.richierich;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by apps on 10/19/2017.
 */

public class BackgroundService extends AsyncTask<String, Void, String> {
    Context context;
    double earnings;
    int level;
    SharedPreferences clicksPrefs;
    DatabaseReference mDatabase;
//    public BackgroundService(Context ctx) {
//        context=ctx;
//
//    }

    public BackgroundService() {

    }

    @Override
    protected String doInBackground(String... params) {
//        clicksPrefs = context.getSharedPreferences("clicksLocal", Context.MODE_PRIVATE);
        int count = Integer.parseInt(params[0]);
        String user = params[1];
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user);
        mDatabase.child("clicks").child("clicks").setValue(count);
        double val = (25000 - count) / 24999.99;
        double value2 = Double.parseDouble(String.format("%.2f", val));

        mDatabase.child("accountDetails").child("amountEarned").setValue(value2);
        mDatabase.child("accountDetails").child("clicksDone").setValue(25000 - count);
//        if(count==0) {
//
//            mDatabase.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    AccountDetails accountDetails=dataSnapshot.child("accountDetails").getValue(AccountDetails.class);
//                    level=accountDetails.getLevel();
//
//
//                    TotalEarnings totalEarnings = dataSnapshot.child("accountDetails").getValue(TotalEarnings.class);
//                    earnings=totalEarnings.getTotalEarned();
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            earnings=earnings+1.0;
//            mDatabase.child("totalEarnings").setValue(new TotalEarnings(earnings));
//
//            level=level+1;
//            mDatabase.child("accountDetails").child("level").setValue(level);
//        }
        return null;
    }


}
