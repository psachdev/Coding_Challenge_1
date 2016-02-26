package com.example.prasachd.ixonos;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by prasachd on 2/12/16.
 */
public class UserDetailsActivity extends Activity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private UserLocationFragment userLocationFragment;
    private SignupFragment signupFragment;
    private AboutFragment aboutFragment;

    private final String USER_LOCATION_FRAGMENT_TAG="USER_LOCATION_FRAGMENT_TAG";
    private final String SIGNUP_FRAGMENT_TAG="SIGNUP_FRAGMENT_TAG";
    private final String ABOUT_FRAGMENT_TAG="ABOUT_FRAGMENT_TAG";


    private final String CONNECTIVITY_CHECK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private String name;
    private final String NAME = "NAME";
    private int ABOUT_MENU_POSITION=1;
    private int HOME_MENU_POSITION=0;
    private Tracker mTracker;
    private final String TAG="UserDetailsActivity";

    public void setName(String name){
        this.name=name;
        attachHomeFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityCheckReceiver);
    }

    private String[] mMenutitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);

        mTitle = getResources().getString(R.string.drawer_open);
        mDrawerTitle = getResources().getString(R.string.drawer_close);

        mMenutitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_list_item, mMenutitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setIcon(R.drawable.ic_drawer);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titleColor)));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setCustomView(R.layout.custom_action_bar);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);


        mDrawerToggle = new ActionBarDrawerToggle(
                UserDetailsActivity.this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                System.out.println("PRATEEK ITEM ID " + item.getItemId());
                return super.onOptionsItemSelected(item);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fragmentManager = getFragmentManager();
        userLocationFragment = (UserLocationFragment) fragmentManager.findFragmentByTag(USER_LOCATION_FRAGMENT_TAG);
        if(userLocationFragment==null) {
            userLocationFragment = new UserLocationFragment();
        }
        signupFragment = (SignupFragment) fragmentManager.findFragmentByTag(SIGNUP_FRAGMENT_TAG);
        if(signupFragment==null) {
            signupFragment = new SignupFragment();
        }
        aboutFragment=(AboutFragment) fragmentManager.findFragmentByTag(ABOUT_FRAGMENT_TAG);
        if(aboutFragment==null) {
            aboutFragment = new AboutFragment();
        }

        String name=null;
        if(savedInstanceState!=null) {
            name = savedInstanceState.getString(NAME);
        }

        IntentFilter filter = new IntentFilter(CONNECTIVITY_CHECK_ACTION);
        this.registerReceiver(connectivityCheckReceiver, filter);


        attachHomeFragment();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(NAME, this.name);
        super.onSaveInstanceState(outState);
    }

    private final BroadcastReceiver connectivityCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            Log.i("UserDetailsActivity", "Status : " + noConnectivity + ", Reason :" + reason + ", FailOver :" + isFailover + ", Current Network Info : " + currentNetworkInfo + ", OtherNetwork Info :" + otherNetworkInfo);

            boolean mStatus = noConnectivity;
            Log.d("UserDetailsActivity", "Status :" + mStatus);

            if(mStatus){
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
                LayoutInflater inflater = UserDetailsActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_alert, null);
                builder.setView(dialogView);
                final AlertDialog alert = builder.create();

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = getActionBar().getHeight();
                int width = displaymetrics.widthPixels;


                ImageButton imageButton = (ImageButton)dialogView.findViewById(R.id.imageButton);
                Bitmap splashImage = BitmapFactory.decodeResource(getResources(),
                        R.drawable.no_connection_alert);
                Bitmap scaled = Bitmap.createScaledBitmap(splashImage, width, height, true);
                imageButton.setImageBitmap(scaled);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });

                WindowManager.LayoutParams wmlp = alert.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.width = width;
                wmlp.height = height;
                //wmlp.x = -100;
                //wmlp.y = 100;

                alert.show();
                Handler mHandler = new Handler();
                Runnable mRunnable = new Runnable() {

                    public void run() {
                        if (alert != null && alert.isShowing()) alert.dismiss();
                    }
                };
                mHandler.postDelayed(mRunnable, 3000);
            }
            else {
                System.out.println("Connection is Available");
            }
        }
    };

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Toast.makeText(getApplicationContext(), "Menu Item Clicked " + mMenutitles[position], Toast.LENGTH_LONG);
        if(position == ABOUT_MENU_POSITION){
            if(!aboutFragment.isAdded()) {
                removeCurrentFragment();
                attachAboutFragment();
            }
        }
        if(position == HOME_MENU_POSITION){
            removeCurrentFragment();
            attachHomeFragment();
        }

    }

    private void removeCurrentFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(userLocationFragment.isAdded()){
            fragmentTransaction.remove(userLocationFragment);
        }else if(signupFragment.isAdded()){
            fragmentTransaction.remove(signupFragment);
        }else if(aboutFragment.isAdded()){
            fragmentTransaction.remove(aboutFragment);
        }
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void attachAboutFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, aboutFragment, ABOUT_FRAGMENT_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void attachHomeFragment(){
        TextView userNameTxt = (TextView)getActionBar().getCustomView().findViewById(R.id.username_id);
        if(userNameTxt!=null){
            userNameTxt.setText(name);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(name!=null && name.trim().length()>0) {
            if(!userLocationFragment.isAdded()) {
                fragmentTransaction.add(R.id.content_frame, userLocationFragment, USER_LOCATION_FRAGMENT_TAG);
            }else if(userLocationFragment.isDetached()){
                fragmentTransaction.attach(userLocationFragment);
            }
        }else{
            if(!signupFragment.isAdded()) {
                fragmentTransaction.add(R.id.content_frame, signupFragment, SIGNUP_FRAGMENT_TAG);
            }else if(signupFragment.isDetached()){
                fragmentTransaction.attach(signupFragment);
            }
        }

        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
