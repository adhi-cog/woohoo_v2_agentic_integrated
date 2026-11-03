package com.sb.woohoo.ui.main.helpers;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.BrightnessObserver;
import com.sb.woohoo.ui.main.callbacks.BrightnessCallback;

import javax.inject.Inject;

/**
 * Handles the Brightness of Device.
 *
 * @author Ritwik Jamuar.
 */
public class BrightnessHelper {
    // Constants.
    private static final String TAG = BrightnessHelper.class.getSimpleName();
    private static final String mSettingNameBrightness = Settings.System.SCREEN_BRIGHTNESS;
    private static final Uri mBrightnessURI = Settings.System.getUriFor("screen_brightness");
    // Other Components.
    private ContentResolver mContentResolver;
    private Handler mHandler;
    private BrightnessObserver brightnessObserver;

    /**
     * Components needed to use this Handler class.
     *
     * @param contentResolver Instance of {@link ContentResolver}.
     * @param handler         Instance of {@link Handler} which executes in Foreground Thread.
     */
    @Inject
    public BrightnessHelper(ContentResolver contentResolver, Handler handler) {
        LoggerWrapper.i(ContentResolver.class.getSimpleName(), "Constructor");
        this.mContentResolver = contentResolver;
        this.mHandler = handler;
    }

    /**
     * Sets the callback to listen to change in Brightness by other components of Android.
     *
     * @param callback Instance of {@link BrightnessCallback}.
     */
    public void setBrightnessChangeObserver(BrightnessCallback callback) {
        brightnessObserver = new BrightnessObserver(mHandler, callback);
        mContentResolver.registerContentObserver(mBrightnessURI, true, brightnessObserver);
    }

    /**
     * Get the current Brightness level of Device.
     *
     * @return Level of Brightness. Value ranges between 0 and 255.
     */
    public int getBrightness() {
        printLog("getBrightness ()");
        return Settings.System.getInt(mContentResolver, mSettingNameBrightness, 0);
    }

    /**
     * Set the Brightness of Device
     *
     * @param brightness Level of Brightness. Value ranges between 0 and 255.
     */
    public void setBrightness(int brightness) {
        printLog("setBrightness ( " + brightness + " )");
        //TODO new firmware issue : less than 5 will set the screen fully black
        if(brightness>5){
            Settings.System.putInt(mContentResolver, mSettingNameBrightness, brightness);
        }
    }

    private void printLog(String message) {
        LoggerWrapper.i(TAG, message);
    }

    /**
     * Nullifies the components of BrightnessHelper.
     */
    public void releaseResources() {
        if (brightnessObserver != null) {
            mContentResolver.unregisterContentObserver(brightnessObserver);
        }
        brightnessObserver = null;
        mContentResolver = null;
    }
}