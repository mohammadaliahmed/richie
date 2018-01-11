package com.richierich.apps.richierich;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.richierich.apps.richierich.DBModel.TotalEarnings;
import com.richierich.apps.richierich.MyAccount.MyDetails;
import com.richierich.apps.richierich.Walkthrough.PrefManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.util.Date;



public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,RewardedVideoAdListener{
    private FirebaseAnalytics mFirebaseAnalytics;
    TextView countDisplay, boostTimeLeft;
    int count;
    Button clickHere, boostButton, freeClicks;
    ImageView ad;
    private SharedPreferences userPref;
    private SharedPreferences mutePrefs;
    private SharedPreferences clicksPrefs;
    private AlphaAnimation buttonClick = new AlphaAnimation(0.1F, 0.5F);
    String userNameLogedIn;
    double totalEarnings;
    private RewardedVideoAd mRewardedVideoAd;

    private static final String TAG = "MainActivity";

    private AdView mAdView,adView1;
    private SharedPreferences timePref;
    long prefTime;



    Button reward;


    String currentDateTimeString;
    DatabaseReference mDatabase,appVersionFromDb;
    boolean sound = true;
    double earnings;
    int level;
    int boosted = 0;
    int soundId;
    SoundPool sp;
    PrefManager prefManager;
    TextView usernameNavi;
    int clicked = 0;
    int bost = 1;


    private Handler repeatUpdateHandler = new Handler();
    private boolean mAutoDecrement = false;
    private final long REP_DELAY = 200;
    WifiManager wifi;

    AlertDialog.Builder builder;
    int primaryColor, cancelColor;
    private InterstitialAd mInterstitialAd;
    long currentTime;

    String tokenn;

    int muted;

    int versionCode;
    private ImageButton buttonPlayAdOptions;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode=pInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        Toast.makeText(this, ""+versionCode, Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//       vunglePub= VunglePub.getInstance();
//        final String app_id = "59f42d6b0138f88a3e0062ea";
//
//        // initialize the Publisher SDK
//        vunglePub.init(this, app_id);
//
//
//        vunglePub.setEventListeners(vungleDefaultListener, vungleSecondListener);
       
        // set any configuration options you like.
        // For a full description of available options, see the 'Configuration Options' section.

        //
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        appVersionFromDb=FirebaseDatabase.getInstance().getReference();
        appVersionFromDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    int app_version=dataSnapshot.child("appVersion").getValue(Integer.class);
                    if(app_version>versionCode){
                        Toast.makeText(MainScreen.this, "Please update to new version", Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("market://details?id=" + MainScreen.this.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + MainScreen.this.getPackageName())));
                        }
                    }
//                    Toast.makeText(MainScreen.this, "from db"+app_version, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//

        MobileAds.initialize(getApplicationContext()," ca-app-pub-7410831996888265~1314101509");
        mRewardedVideoAd=MobileAds.getRewardedVideoAdInstance(MainScreen.this);
        loadRewardedVideoAd();


        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        tokenn= FirebaseInstanceId.getInstance().getToken();
        Log.d("MYTAG", "This is your Firebase token  "+  tokenn);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7646715033242980/6605408864");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(MainScreen.this);


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        countDisplay = (TextView) findViewById(R.id.textView);
        clicksPrefs = getSharedPreferences("clicksLocal", Context.MODE_PRIVATE);
        count = clicksPrefs.getInt("count", 0);
        countDisplay.setText("" + count);

//        mutePrefs =getSharedPreferences("mute", Context.MODE_PRIVATE);
//
//            muted = mutePrefs.getInt("mute", 1);

        mutePrefs = getSharedPreferences("mute", Context.MODE_PRIVATE);
        muted = mutePrefs.getInt("mute", 0);



        boostTimeLeft = (TextView) findViewById(R.id.time_left);
        currentTime = System.currentTimeMillis();
//        freeClicks = (Button) findViewById(R.id.freeclicks);
        timePref =getSharedPreferences("boostTime", Context.MODE_PRIVATE);
        prefTime = timePref.getLong("boostTime", 0);
        if (prefTime != 0) {

            long td = prefTime - currentTime;
            if (td != 0) {
                new CountDownTimer(td, 1000) {

                    public void onTick(long millisUntilFinished) {
                        boostTimeLeft.setText("Next boost " + convertSecondsToHMmSs(millisUntilFinished / 1000));
                        bost = 0;

                    }

                    public void onFinish() {
                        bost = 1;
                        clicked=0;
                        boostTimeLeft.setText("");
                        SharedPreferences pref2 = MainScreen.this.getSharedPreferences("boostTime", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = pref2.edit();
                        editor2.putLong("boostTime", 0);
                        editor2.apply();

                    }
                }.start();


            }
        }
            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            primaryColor = ContextCompat.getColor(MainScreen.this, R.color.colorPrimary);
            cancelColor = ContextCompat.getColor(MainScreen.this, R.color.cancel);

            builder = new AlertDialog.Builder(MainScreen.this);
            builder.setMessage("\n Retry?");
            builder.setTitle("No Internet Connection");
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Toast.makeText(MainScreen.this,"Ok Fine",2000).show();
                    //wifi.setWifiEnabled(true);

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Toast.makeText(MainScreen.this,"No Problem",2000).show();

                }
            });
            reward = (Button) findViewById(R.id.reward);
//        reward.setVisibility(View.GONE);
//       if(mAd.isLoaded()) {
//           reward.setVisibility(View.VISIBLE);
//       }

        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!vunglePub.isAdPlayable()) {
//                    return;
//                }
//                PlayAdIncentivized();
//                if(mAd.isLoaded()){
//                    mAd.show();
//                }
                loadRewardedVideoAd();
//                showRewardedVideo();

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                } else {
                    Toast.makeText(MainScreen.this, "Sorry, no offers available this time", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        freeClicks.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlayAd();
//                vunglePub.playAd();
//
//            }
//        });

            startService(new Intent(this, BackgroundService.class));
            class RptUpdater implements Runnable {
                public void run() {

                    if (mAutoDecrement) {
                        sound = true;
                        decrement(sound);
                        repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
                    }
                }
            }
            prefManager = new PrefManager(this);
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            currentDateTimeString = (String) df.format("dd-MM-yyyy hh:mm a", new Date());

            userPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
            userNameLogedIn = userPref.getString("username", "");


            // Toast.makeText(this, ""+phone, Toast.LENGTH_SHORT).show();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("" + userNameLogedIn);

            countDisplay.setShadowLayer(10, 0, 0, Color.WHITE);
            //ad = (ImageView) findViewById(R.id.image);

            //Glide.with(this).asGif().load(R.drawable.ad).into(ad);


            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            soundId = sp.load(MainScreen.this, R.raw.click, 1);
//            freeClicks = (Button) findViewById(R.id.freeclicks);

//            freeClicks.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isNetworkAvailable()) {
////                        if (mInterstitialAd.isLoaded()) {
////                            mInterstitialAd.show();
////
////                        }
////                        if (count <= 1000) {
////                            Toast.makeText(MainScreen.this, "Spinner limit over!", Toast.LENGTH_SHORT).show();
////                        } else {
////
////                            startActivityForResult(i, 2);
////                        }
//                        if (mAd.isLoaded()) {
//                            mAd.show();
//
////                            startActivityForResult(i, 2);
//                        } else {
//                            Toast.makeText(MainScreen.this, "No ads available this time", Toast.LENGTH_SHORT).show();
//                        }
//                        // Intent i = new Intent(MainScreen.this, PrizwWheelFinal.class);
//                        // startActivity(i);
//
//
//                    } else {
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor);
//                    }
//                }
//            });

            boostButton = (Button) findViewById(R.id.boost_btn);

            boostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadRewardedVideoAd();

                    if (isNetworkAvailable()) {
                        if (count <= 300) {
                            Toast.makeText(MainScreen.this, "Boost has ended", Toast.LENGTH_SHORT).show();
                        } else {

                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();

                            }
                            if (bost == 1) {

                                if (clicked == 0) {


                                    Toast.makeText(MainScreen.this, "Boost Started", Toast.LENGTH_SHORT).show();
                                    boosted = 1;
                                    long millis = System.currentTimeMillis() + 240000L;
//                                    long millis = System.currentTimeMillis() + 35000L;
                                    SharedPreferences pref2 = getSharedPreferences("boostTime", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = pref2.edit();
                                    editor2.putLong("boostTime", millis);
                                    editor2.apply();
                                    new CountDownTimer(60000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            boostTimeLeft.setText("Boost Time left 00:" + millisUntilFinished / 1000);
                                            clicked = 1;
                                        }

                                        public void onFinish() {
                                            if (mInterstitialAd.isLoaded()) {
                                                mInterstitialAd.show();

                                            }
                                            mAutoDecrement = false;
                                            //boostTimeLeft.setText("");
                                            boosted = 0;
                                            Toast.makeText(MainScreen.this, "Boost end", Toast.LENGTH_SHORT).show();
                                            clicked = 0;

                                            timePref =getSharedPreferences("boostTime", Context.MODE_PRIVATE);
                                           long  ptime = timePref.getLong("boostTime", 0);
                                            long curtim = System.currentTimeMillis();

                                            new CountDownTimer(ptime - curtim, 1000) {

                                                public void onTick(long millisUntilFinished) {
                                                    boostTimeLeft.setText("Next boost " + convertSecondsToHMmSs(millisUntilFinished / 1000));
                                                    clicked = 1;
                                                }

                                                public void onFinish() {

                                                    boostTimeLeft.setText("");
                                                    SharedPreferences pref2 = getSharedPreferences("boostTime", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor2 = pref2.edit();
                                                    editor2.putLong("boostTime", 0);
                                                    editor2.apply();
                                                    clicked=0;

                                                }
                                            }.start();

                                        }
                                    }.start();

                                }
                            }
                        }
                    } else {
                        AlertDialog dialog = builder.create();
                        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));


                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor);
                    }
                }
            });

            clickHere = (Button) findViewById(R.id.click_here);

            clickHere.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        public boolean onLongClick(View arg0) {
                            if (isNetworkAvailable()) {
                                if (boosted == 1) {
                                    mAutoDecrement = true;
                                }

                                repeatUpdateHandler.post(new RptUpdater());
                            } else {
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor);
                            }
                            return false;

                        }
                    }
            );

            clickHere.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                            && mAutoDecrement) {
                        mAutoDecrement = false;
                    }
                    return false;
                }
            });

            clickHere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);

                    if (isNetworkAvailable()) {
                        if(muted==0) {
                            sp.play(soundId, 1, 1, 0, 0, 1);
                        }
                        count--;
                        countDisplay.setText("" + count);
                        if (count <= 0) {

                            level = userPref.getInt("level", 0);
                            totalEarnings = Double.parseDouble(userPref.getString("earnings", ""));


                            totalEarnings = totalEarnings + 1.0;
                            mDatabase.child("totalEarnings").setValue(new TotalEarnings(earnings));

                            level = level + 1;
                            mDatabase.child("accountDetails").child("level").setValue(level);

                            SharedPreferences pref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("level", level);
                            editor.apply();


                            BackgroundService backgroundService = new BackgroundService();
                            backgroundService.execute("" + count, userNameLogedIn);
                            count = 25000;
                            SharedPreferences pref2 = getSharedPreferences("clicksLocal", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.putInt("count", count);
                            editor2.apply();
                        }


                    } else {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor);
                    }
                }

            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.getHeaderView(0);
            usernameNavi = (TextView) headerView.findViewById(R.id.userdetails_navi);
            usernameNavi.setText(userNameLogedIn);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//
//                }
//                handler.postDelayed(this, 60000); //now is every 2 minutes
//            }
//        }, 60000);




        }

    private void loadRewardedVideoAd() {
        if(!mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.loadAd("ca-app-pub-7646715033242980/9613757720", new AdRequest.Builder().build());
        }
    }
//    private void PlayAd() {
//        vunglePub.playAd();
//    }
//
//    private void PlayAdOptions() {
//        // create a new AdConfig object
//        final AdConfig overrideConfig = new AdConfig();
//
//        // set any configuration options you like.
//        overrideConfig.setOrientation(Orientation.autoRotate);
//        overrideConfig.setSoundEnabled(false);
//        overrideConfig.setBackButtonImmediatelyEnabled(false);
//        overrideConfig.setPlacement("StoreFront");
//        //overrideConfig.setExtra1("LaunchedFromNotification");
//
//        // the overrideConfig object will only affect this ad play.
//        vunglePub.playAd(overrideConfig);
//    }
//
//    private void PlayAdIncentivized() {
//        // create a new AdConfig object
//        final AdConfig overrideConfig = new AdConfig();
//
//        // set incentivized option on
//        overrideConfig.setIncentivized(true);
//        overrideConfig.setIncentivizedCancelDialogTitle("Careful!");
//        overrideConfig.setIncentivizedCancelDialogBodyText("If the video isn't completed you won't get your reward! Are you sure you want to close early?");
//        overrideConfig.setIncentivizedCancelDialogCloseButtonText("Close");
//        overrideConfig.setIncentivizedCancelDialogKeepWatchingButtonText("Keep Watching");
//
//        // the overrideConfig object will only affect this ad play.
//        vunglePub.playAd(overrideConfig);
//    }
//    private final EventListener vungleDefaultListener = new EventListener() {
//
//        @Override
//        public void onAdStart() {
//            // Called before playing an ad.
//        }
//
//        @Override
//        public void onAdUnavailable(String reason) {
//            // Called when VunglePub.playAd() was called but no ad is available to show to the user.
//        }
//
//        @Override
//        public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
//            // Called when the user leaves the ad and control is returned to your application.
//        }
//
//        @Override
//        public void onAdPlayableChanged(boolean isAdPlayable) {
//            // Called when ad playability changes.
//            Log.d("DefaultListener", "This is a default eventlistener.");
//            final boolean enabled = isAdPlayable;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    // Called when ad playability changes.
////                    setButtonState(buttonPlayAd, enabled);
////                    setButtonState(buttonPlayAdIncentivized, enabled);
////                    setButtonState(buttonPlayAdOptions, enabled);
//                }
//            });
//        }
//    };

//    private final EventListener vungleSecondListener = new EventListener() {
//        // Vungle SDK allows for multiple listeners to be attached. This secondary event listener is only
//        // going to print some logs for now, but it could be used to Pause music, update a badge icon, etc.
//
//        @Override
//        public void onAdStart() {
//            Log.d("SecondListener", "This is a second event listener. Ad is about to play now!");
//        }
//
//        @Override
//        public void onAdUnavailable(String reason) {
//            Log.d("SecondListener", String.format("This is a second event listener. Ad is unavailable to play - %s", reason));
//        }
//
//        @Override
//        public void onAdEnd(boolean wasSuccessfulView, boolean wasCallToActionClicked) {
//
//            if(count<=210) {
//                MainScreen.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(MainScreen.this, "Reward limit has been reached", Toast.LENGTH_SHORT).show();
//                    }
//                });
//           // Toast.makeText(this, "Reward limit has been reached", Toast.LENGTH_SHORT).show();
//        }else{
//                MainScreen.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(MainScreen.this, "You got " + 200, Toast.LENGTH_SHORT).show();
//                        ValueAnimator valueAnimator = ValueAnimator.ofInt(count, count - 200);
//                        valueAnimator.setDuration(10000);
//
//
//                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                countDisplay.setText(valueAnimator.getAnimatedValue().toString());
//
//                            }
//                        });
//                        valueAnimator.start();
//                        for (int i = 0; i < 200; i++) {
//                            sound = false;
//
//                            decrement(sound);
//
//                        } //mDatabase.child("clicks").child("clicks").setValue(count);
//
//                        BackgroundService backgroundService = new BackgroundService();
//                        backgroundService.execute("" + count, userNameLogedIn);
//                    }
//                });
//           // Toast.makeText(this, "You got " + 200, Toast.LENGTH_SHORT).show();
//
//
//
//        }
//
//
//
//            //Toast.makeText(MainActivity.this, "you got 1000", Toast.LENGTH_LONG).show();
//
//            Log.d("SecondListener", String.format("This is a second event listener. Ad finished playing, wasSuccessfulView - %s, wasCallToActionClicked - %s", wasSuccessfulView, wasCallToActionClicked));
//        }
//
//        @Override
//        public void onAdPlayableChanged(boolean isAdPlayable) {
//            Log.d("SecondListener", String.format("This is a second event listener. Ad playability has changed, and is now: %s", isAdPlayable));
//        }
//    };
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {
                int luckynumber = Integer.parseInt(data.getStringExtra("luckyNumber"));
                Toast.makeText(this, "You got " + luckynumber, Toast.LENGTH_SHORT).show();

                ValueAnimator valueAnimator = ValueAnimator.ofInt(count, count - luckynumber);
                valueAnimator.setDuration(luckynumber * 10);

                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        countDisplay.setText(valueAnimator.getAnimatedValue().toString());
                    }
                });
                valueAnimator.start();
                for (int i = 0; i < luckynumber; i++) {
                    sound = false;

                    decrement(sound);

                } //mDatabase.child("clicks").child("clicks").setValue(count);
            }
            BackgroundService backgroundService = new BackgroundService();
            backgroundService.execute("" + count, userNameLogedIn);


        }
    }

//    public boolean isConnected() throws InterruptedException, IOException
//    {
//        String command = "ping -c 1 google.com";
//        return (Runtime.getRuntime().exec (command).waitFor() == 0);
//    }

    public void decrement(boolean playSound) {
        count--;
        countDisplay.setText("" + count);
        if(muted==0) {
            if (playSound == true) {
                sp.play(soundId, 1, 1, 0, 0, 1);
            } else {

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    protected void exitByBackKey() {
        //mDatabase.child("clicks").child("clicks").setValue(count);
        BackgroundService backgroundService = new BackgroundService();
        backgroundService.execute("" + count, userNameLogedIn);
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
        alertbox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
        alertbox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            Intent i = new Intent(MainScreen.this, HowToUse.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.account) {
            Intent i = new Intent(MainScreen.this, MyDetails.class);
            startActivity(i);
            return true;
        }
//        if (id == R.id.logout) {
//            // mDatabase.child("clicks").child("clicks").setValue(count);
//            SharedPreferences.Editor ed = userPref.edit();
//            ed.clear();
//            ed.commit();
//            //finish();
//
//            Intent i = new Intent(MainScreen.this, LoginActivity.class);
//
//            startActivity(i);
//            prefManager.setFirstTimeLaunch(true);
//
//            // Toast.makeText(this, "Log out!", Toast.LENGTH_SHORT).show();
//            finish();
//            //return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.help) {
            Uri uri = Uri.parse("https://m.me/253666605157464");

            Intent toMessenger= new Intent(Intent.ACTION_VIEW, uri);
            startActivity(toMessenger);
            try {
                startActivity(toMessenger);
            }
            catch (android.content.ActivityNotFoundException ex)
            {
                Toast.makeText(MainScreen.this, "Please Install Facebook Messenger",    Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(MainScreen.this, ChangeInfo.class);
            startActivity(i);

        } else if (id == R.id.mute) {
                muteOption(item);

        }else if (id == R.id.how_to) {
            Intent i = new Intent(MainScreen.this, HowToUse.class);
            startActivity(i);

        } else if (id == R.id.withdraw) {
            Intent i = new Intent(MainScreen.this, MyDetails.class);
            startActivity(i);

        } else if (id == R.id.feedback) {
            Uri uri = Uri.parse("market://details?id=" + MainScreen.this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + MainScreen.this.getPackageName())));
            }
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Richie app app\n Download Now\n"+"http://play.google.com/store/apps/details?id=" + MainScreen.this.getPackageName());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
            startActivity(Intent.createChooser(shareIntent, "Share App via.."));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mRewardedVideoAd.pause(this);
//        mDatabase.child("clicks").child("clicks").setValue(count);
        SharedPreferences.Editor ed = clicksPrefs.edit();
        ed.putInt("count", count);

        ed.commit();
        BackgroundService backgroundService = new BackgroundService();
        backgroundService.execute("" + count, userNameLogedIn);
//    }


    }
    public void muteOption(MenuItem item){
        if(muted==1) {
            muted = 0;
            SharedPreferences pref2 = getSharedPreferences("mute", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = pref2.edit();
            editor2.putInt("mute", muted);
            editor2.apply();
            item.setTitle("Mute");
            item.setIcon(R.drawable.mute);
            Toast.makeText(this, "unmuted", Toast.LENGTH_SHORT).show();
            return;
        }
        if(muted==0) {
            muted = 1;
            SharedPreferences pref2 = getSharedPreferences("mute", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = pref2.edit();
            editor2.putInt("mute", muted);
            editor2.apply();
            item.setTitle("Unmute");
            item.setIcon(R.drawable.unmute);
            Toast.makeText(this, "muted", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    @Override
    public void onClick(View v) {

    }
    private void showRewardedVideo() {

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
//        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        // Preload the next video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
//        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
//        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
//        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem reward) {
//        Toast.makeText(this,
//                String.format(" onRewarded! currency: %s amount: %d", reward.getType(),
//                        reward.getAmount()),
//                Toast.LENGTH_SHORT).show();
        if(count<=210) {
            MainScreen.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainScreen.this, "Reward limit has been reached", Toast.LENGTH_SHORT).show();
                }
            });
            // Toast.makeText(this, "Reward limit has been reached", Toast.LENGTH_SHORT).show();
        }else {
            MainScreen.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainScreen.this, "You got " + 200, Toast.LENGTH_SHORT).show();
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(count, count - 200);
                    valueAnimator.setDuration(10000);


                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            countDisplay.setText(valueAnimator.getAnimatedValue().toString());

                        }
                    });
                    valueAnimator.start();
                    for (int i = 0; i < 200; i++) {
                        sound = false;

                        decrement(sound);

                    } //mDatabase.child("clicks").child("clicks").setValue(count);

                    BackgroundService backgroundService = new BackgroundService();
                    backgroundService.execute("" + count, userNameLogedIn);
                }
            });
            // Toast.makeText(this, "You got " + 200, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRewardedVideoStarted() {
//        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }
}
