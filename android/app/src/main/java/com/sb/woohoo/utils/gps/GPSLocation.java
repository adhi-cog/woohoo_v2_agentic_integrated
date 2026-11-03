package com.sb.woohoo.utils.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import androidx.core.app.ActivityCompat;

import static com.sileria.android.Kit.getSystemService;


/**
 * Toggles Device's Location Service and Provides Location.
 *
 * @author Ritwik Jamuar.
 */
@SuppressLint("MissingPermission")
public class GPSLocation implements LocationListener {
    private  final String TAG = GPSLocation.class.getSimpleName();
    private  final float MIN_DIST = 10;
    private  final long MIN_TIME = 10;
    @Inject
    ConnectivityHelper connectivityHelper = new ConnectivityHelper();
    private LocationManager mManager;
    private GPSListener mListener;
    private Context mContext;

    @Inject
    public GPSLocation(BaseActivity activity) {
        this.mManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.mContext = activity;
        this.mListener = activity;
    }

    /**
     * Checks whether the Location Permission has been given to this app by the Device or not.
     *
     * @return {@code true}, when permissions are granted, else {@code false}.
     */
    private boolean checkLocationPermission() {
        String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
        int permissionGranted = PackageManager.PERMISSION_GRANTED;
        boolean fineLocation = ActivityCompat.checkSelfPermission(mContext, fineLocationPermission) == permissionGranted;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(mContext, coarseLocationPermission) == permissionGranted;
        return fineLocation && coarseLocation;
    }

    /**
     * Requests Device's Location Service to request for Location Update.
     */
    public void requestLocationUpdate() {
        // TODO : Enable location providers programmatically (if available without Location Play Services).
        requestLastKnownLocation();
        requestLocationFromPassive();
        requestLocationFromNetwork();
        requestLocationFromGPS();
        checkForActiveInternet();

    }

    private void checkForActiveInternet() {
        connectivityHelper.setListener(new ConnectivityHelper.ConnectivityHandlerCallback() {
            @Override
            public void internetDown() {
                Toast.makeText(mContext, mContext.getString(R.string.no_active_internet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void serverDown() {
                Toast.makeText(mContext, mContext.getString(R.string.network_issues), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void internetUp() {
                requestLocationFromWifi();
            }
        });
        connectivityHelper.checkInternet();
    }

    /**
     * Requests Location from {@code IP address through API call}.
     */

    private void requestLocationFromWifi() {
        wifiIpAddress();
    }

    private void wifiIpAddress() {
        try {
            if(isOnline()) {
                new getIp().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Requests Location from {@code LocationManager.GPS_PROVIDER}.
     */
    private void requestLocationFromGPS() {
        Location location = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        onLocationChanged(location);
        if (checkLocationPermission()) {
            try {
                mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME,
                        MIN_DIST,
                        GPSLocation.this);
            } catch (Exception e) {
                LoggerWrapper.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Requests Location from {@code LocationManager.PASSIVE_PROVIDER}.
     */
    private void requestLocationFromPassive() {
        if (checkLocationPermission()) {
            Location location = mManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            onLocationChanged(location);
            mManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    MIN_TIME,
                    MIN_DIST,
                    GPSLocation.this
            );
        }
    }

    /**
     * Requests Location from {@code LocationManager.NETWORK_PROVIDER}.
     */
    private void requestLocationFromNetwork() {
        Location location = mManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        if (checkLocationPermission()) {
            try {
                mManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME,
                        MIN_DIST,
                        GPSLocation.this
                );
            } catch (Exception e) {
                LoggerWrapper.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Requests Device's Last Known Location from GPS.
     * Uses {@link LocationManager}'s {@code PASSIVE_PROVIDER} to obtain Last Known Location
     * either from {@code GPS_PROVIDER} or {@code NETWORK_PROVIDER}.
     */
    private void requestLastKnownLocation() {
        if (checkLocationPermission()) {
            List<String> providers = mManager.getAllProviders();
            for (String provider : providers) {
                Location location = mManager.getLastKnownLocation(provider);
                if (location != null) onLocationChanged(location);
            }
        }
    }

    /**
     * Requests Device's Location Service to cancel the Location Update.
     */
    public void cancelLocationUpdate() {
        mManager.removeUpdates(GPSLocation.this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mListener.onLocationChanged(location.getLatitude(), location.getLongitude());
        }
    }

    public class getIp extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL whatismyip = new URL("http://icanhazip.com");
                BufferedReader in = null;
                in = new BufferedReader(
                        new InputStreamReader(whatismyip.openStream())
                );
                result = in.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String ip) {
            super.onPostExecute(ip);
            mListener.onIpAdressFound(ip);
        }
    }

}