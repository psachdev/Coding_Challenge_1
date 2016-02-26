package com.example.prasachd.ixonos;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by prasachd on 2/25/16.
 */
public class AboutFragment extends Fragment {
    private Tracker mTracker;
    private final String TAG="AboutFragment";
    private Activity context;


    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context=activity;
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
        View aboutFragment = inflater.inflate(R.layout.about, null);
        return aboutFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
