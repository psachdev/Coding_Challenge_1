package com.example.prasachd.ixonos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by prasachd on 2/25/16.
 */
public class SignupFragment extends Fragment {

    private UserDetailsActivity context;
    private EditText firstNameEditTxt;
    private EditText lastNameEditTxt;
    private EditText emailAddressTxt;

    private Tracker mTracker;
    private final String TAG="SignupFragment";

    public SignupFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (UserDetailsActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        AnalyticsApplication application = (AnalyticsApplication) this.context.getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View signupFragment = inflater.inflate(R.layout.signup, null);

        firstNameEditTxt = (EditText) signupFragment.findViewById(R.id.firstNameTxt_id);
        lastNameEditTxt = (EditText) signupFragment.findViewById(R.id.lastNameTxt_id);
        emailAddressTxt = (EditText) signupFragment.findViewById(R.id.emailTxt_id);



        Button goButton = (Button) signupFragment.findViewById(R.id.go_button);
        if(goButton!=null){
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context==null){
                        return;
                    }
                    String emailAddrress = emailAddressTxt.getText().toString();
                    if ((emailAddrress==null) ||
                            (emailAddrress!=null && !isValidEmail(emailAddrress))){
                        alertUser();
                        return;
                    }
                    String firstName = firstNameEditTxt.getText().toString();
                    String lastName = lastNameEditTxt.getText().toString();
                    String name = firstName + " " + lastName;

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Signup")
                            .build());

                    context.setName(name);

                }
            });
        }

        return signupFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context=null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean isValidEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void alertUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alert, null);
        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        //alert.setContentView(dialogView);
        alert.getWindow().setBackgroundDrawable(null);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = context.getActionBar().getHeight();
        int width = displaymetrics.widthPixels;

        ImageButton imageButton = (ImageButton) dialogView.findViewById(R.id.imageButton);
        Bitmap splashImage = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.invalid_email_banner);
        Bitmap scaled = Bitmap.createScaledBitmap(splashImage, width, height, true);
        imageButton.setImageBitmap(scaled);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        WindowManager.LayoutParams wmlp = alert.getWindow().getAttributes();

        //wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.gravity = Gravity.TOP;
        wmlp.width = height;
        wmlp.height = width;
        //wmlp.x = -50;
        //wmlp.y = -50;

        alert.show();

        Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {

            public void run() {
                if (alert != null && alert.isShowing()) alert.dismiss();
            }
        };
        mHandler.postDelayed(mRunnable, 3000);
    }

}
