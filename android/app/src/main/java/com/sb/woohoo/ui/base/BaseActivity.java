package com.sb.woohoo.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.utils.gps.GPSListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BaseActivity extends AppCompatActivity implements GPSListener {
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Thread.setDefaultUncaughtExceptionHandler(new WoohooExceptionHandler(this));

        mFragmentManager = getSupportFragmentManager();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // TODO: domain related case for theme setup
        setTheme(R.style.AppTheme);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = FragmentTransactionManager.getCurrentFragment(mFragmentManager);
        if (currentFragment instanceof HomeFragment) {
            return;
        }
        if (currentFragment instanceof IBackPressInterceptor) {
            ((IBackPressInterceptor) currentFragment).onBackPressed(new IBackPressInterceptor.BackCallback() {
                @Override
                public void backCompleted() {
                    mFragmentManager.popBackStackImmediate();
                }
            });
        } else {
            try {
                super.onBackPressed();
            } catch(IllegalStateException e) {
                LoggerWrapper.e("BaseACtivity", e.getMessage());
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(double lat, double lon) {

    }

    @Override
    public void onIpAdressFound(String wifiIP) {

    }


    public void onRespondToUser(String message) {

    }

    public boolean isGpsSupported() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    protected void configureRotation() {
        if(BuildConfig.BUILD_TYPE.equals("debugLandscape") || BuildConfig.BUILD_TYPE.equals("releaseLandscape")){
            return;
        }
        Activity activity = this;
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int currentOrientation = activity.getResources().getConfiguration().orientation;
        int orientation = 0;
        switch(currentOrientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
        activity.setRequestedOrientation(orientation);
    }

}
