package com.example.prasachd.ixonos;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.MapsInitializer;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 5000;

    private final UserLocationFragment userLocationFragment = new UserLocationFragment();
    private Tracker mTracker;
    private final String TAG="SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, UserDetailsActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titleColor)));
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        ImageView splashImageView  = (ImageView)this.findViewById(R.id.imageView);
        Bitmap splashImage = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.splashscreen);
        Bitmap scaled = Bitmap.createScaledBitmap(splashImage, width, height, true);
        splashImageView.setImageBitmap(scaled);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent intent = getIntent();
        String scheme = intent.getScheme();
        if(scheme.equals("prateekscheme")) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Launched via Link")
                    .build());
        }
    }
}
