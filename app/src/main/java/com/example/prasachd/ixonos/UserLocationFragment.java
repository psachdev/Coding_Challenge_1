package com.example.prasachd.ixonos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by prasachd on 2/13/16.
 */
public class UserLocationFragment extends Fragment implements LocationListener {
    GoogleMap googleMap;
    private AddressResultReceiver mResultReceiver;
    private Activity context;
    private Location mLastLocation;
    private TextView addressText;
    private Tracker mTracker;
    private final String TAG="UserLocationFragment";

    protected void startIntentService() {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        context.startService(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        AnalyticsApplication application = (AnalyticsApplication) this.context.getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);


        try {
            MapsInitializer.initialize(getActivity());
            this.mapView = new MapView(getActivity());
            if(this.mapView==null){
                Log.i("TAG", "mapView null");
                throw new IllegalStateException("mapView is null");
            }
            this.mapView.onCreate(savedInstanceState);
            mLastLocation=null;
            setUpGooglePlayServices();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please install Google Play Store and retry again!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            getActivity().finish();
        }
    }

    private void setUpGooglePlayServices(){
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        }else { // Google Play Services are available


            // Getting reference to the SupportMapFragment of activity_main.xml
            //MapView smf = (MapView) fragmentView.findViewById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = this.mapView.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);


            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.user_location, null);
        ((ViewGroup) fragmentView.findViewById(R.id.mapViewHolder)).addView(this.mapView);
        addressText = (TextView) fragmentView.findViewById(R.id.addressId);

        return fragmentView;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        startIntentService();

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.clear();
        googleMap.setMyLocationEnabled(true);
        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        //Marker hamburg = googleMap.addMarker(new MarkerOptions().position(latLng)
        //        .title("Current Position"));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context=activity;
        this.mResultReceiver=new AddressResultReceiver(new Handler());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onResume() {
        mTracker.setScreenName("" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(mapView!=null)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView!=null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView!=null)
            mapView.onLowMemory();
    }

    @SuppressLint("ParcelCreator")
    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //GEOCODING : http://developer.android.com/training/location/display-address.html
            if (resultCode == Constants.SUCCESS_RESULT) {
                String address = (String)resultData.getString(Constants.RESULT_DATA_KEY);
                Location location = (Location)resultData.getParcelable(Constants.LOCATION_KEY);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(address));
                addressText.setText(address);
            }
        }
    }

}
