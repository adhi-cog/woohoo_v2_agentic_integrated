package com.sb.woohoo.ui.onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiPresenter;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubParameters;
import com.sb.woohoo.ui.onboarding.timezone.TimeZoneSelectionFragment;
import com.sb.woohoo.utils.Constants;

import java.util.Locale;

/**
 * Helper Class to generate parameters for API : Register Hub.
 */
public class BuildRegisterHubParameters {

    // Constants.
    private final String HUB_NAME = "name";
    private final String USER_ID = "id";
    private final String MAC_ADDRESS = "macAddress";
    private final String IP_ADDRESS = "ipAddress";
    private final String CONNECTED_WIFI = "wifiName";
    private final String TIME_ZONE = "timeZone";
    // Dependencies.
    private Activity mActivity;
    private WifiFragment mWiFiSelectionFragment;
    //private TimeZoneSelectionFragment mTimeZoneSelectionFragment;
    //private double latLng[];
    private String lat;
    private String lng;

    BuildRegisterHubParameters(
            Activity activity,
            WifiFragment wifiFragment,
            double[] latLng) {
        this.mActivity = activity;
        this.mWiFiSelectionFragment = wifiFragment;
        this.lat = String.valueOf(latLng[0]);
        this.lng = String.valueOf(latLng[1]);
    }

    /**
     * Build the Parameters.
     *
     * @return Instance of {@link RegisterHubParameters} representing Parameter for API : Register Hub.
     */
    public RegisterHubParameters build() {

        String deviceUniqueId = getDeviceID();


        RegisterHubParameters parameters = new RegisterHubParameters();
        parameters.setName(getDataFromSharedPrefsHelper(HUB_NAME));
        parameters.setId(deviceUniqueId);
        parameters.setMacAddress(getDataFromWifiSelectionFragment(MAC_ADDRESS));
        parameters.setIpAddress(getDataFromWifiSelectionFragment(IP_ADDRESS));
        parameters.setFirmwareVersion(getFirmwareVersion());
        parameters.setAppVersion(getAppVersion());
        parameters.setRegisteredUser(getDataFromSharedPrefsHelper(USER_ID));
        parameters.setLatitude(lat);
        parameters.setLongitude(lng);
        parameters.setLocale(getLocale());
        parameters.setWifiConnected(getDataFromWifiSelectionFragment(CONNECTED_WIFI));
        parameters.setSerialNumber(deviceUniqueId);
        parameters.setAiEngine("ok woohoo"); // TODO : Apply logic to dynamically update it.
        parameters.setTimeZone(getDataFromSharedPrefsHelper(TIME_ZONE));

        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_DEVICE_ID, deviceUniqueId);

        return parameters;
    }

    private String getHubTimeZone(SharedPrefsHelper helper) {
        //TODO Can be changed according to backend requirement
        return helper.get(SharedPrefsKey.PREF_KEY_TIME_ZONE_PLACE, "").length() == 0 ? "NA" : helper.get(SharedPrefsKey.PREF_KEY_TIME_ZONE_PLACE, "");
    }

    /**
     * Fetches Required Fields from {@link android.content.SharedPreferences} using {@link SharedPrefsHelper}.
     *
     * @param type Type of data to be fetched from {@link SharedPrefsHelper}.
     * @return String representing data from {@link SharedPrefsHelper}.
     */
    private String getDataFromSharedPrefsHelper(String type) {
        String data = "NA";
        SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
        switch (type) {
            case HUB_NAME:
                data = getHubName(helper);
                break;
            case USER_ID:
                data = getUserID(helper);
                break;
            case TIME_ZONE:
                data = getHubTimeZone(helper);
        }
        return data;
    }

    /**
     * Fetches Hub Name from {@link SharedPrefsHelper}.
     *
     * @param helper Instance of {@link SharedPrefsHelper}.
     * @return String representing Name of Hub.
     */
    private String getHubName(SharedPrefsHelper helper) {
        String firstName = helper.get(SharedPrefsKey.PREF_KEY_FIRST_NAME, "").length() == 0 ? "NA" : helper.get(SharedPrefsKey.PREF_KEY_FIRST_NAME, "");
        return firstName + "'s Hub";
    }

    /**
     * Fetches User ID from {@link SharedPrefsHelper}.
     *
     * @param helper Instance of {@link SharedPrefsHelper}.
     * @return String representing User ID.
     */
    private String getUserID(SharedPrefsHelper helper) {
        return helper.get(SharedPrefsKey.PREF_KEY_USER_ID, "").length() == 0 ? "NA" : helper.get(SharedPrefsKey.PREF_KEY_USER_ID, "");
    }

    /**
     * Gets Device ID through {@link TelephonyManager}.
     *
     * @return String representing Device ID.
     */
    @SuppressLint("HardwareIds")
    private String getDeviceID() {
        String id = null;
        String testID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_TEST_DEVICE_ID, Constants.USE_SYSTEM_ID);
        if (!testID.equalsIgnoreCase(Constants.USE_SYSTEM_ID)) {
            id = testID;
        }  else {
            id = WooHooApp.sDeviceId;
        }
        // we are using ssaid since we are going android-9.
        /*{
            TelephonyManager manager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (manager != null) id = manager.getDeviceId();
            }
        }*/

        if (TextUtils.isEmpty(id)) {
            id = getDataFromWifiSelectionFragment(MAC_ADDRESS);
        }
        return id;
    }

    /**
     * Fetches DeviceData from {@link WifiFragment} through {@link WifiPresenter}.
     *
     * @param type Type of data to be fetched from {@link WifiPresenter}.
     * @return String representing data returned from {@link WifiPresenter}.
     */
    private String getDataFromWifiSelectionFragment(String type) {
        String data = "NA";
        if (mWiFiSelectionFragment != null && mWiFiSelectionFragment.getWifiPresenter() != null) {
            switch (type) {
                case MAC_ADDRESS:
                    data = getMACAddress(mWiFiSelectionFragment.getWifiPresenter());
                    break;
                case IP_ADDRESS:
                    data = getIPAddress(mWiFiSelectionFragment.getWifiPresenter());
                    break;
                case CONNECTED_WIFI:
                    data = getConnectedWiFi(mWiFiSelectionFragment.getWifiPresenter());
                    break;
            }
        }
        return data;
    }

    /**
     * Fetches MAC Address of Device from {@link WifiPresenter}.
     *
     * @param presenter Instance of {@link WifiPresenter}.
     * @return String representing MAC Address of Device.
     */
    private String getMACAddress(WifiPresenter presenter) {
        return presenter.getWifiInformation("MAC_ADDRESS");
    }

    /**
     * Fetches IP Address of Network Connected from {@link WifiPresenter}.
     *
     * @param presenter Instance of {@link WifiPresenter}.
     * @return String representing IP Address of Network Connected.
     */
    private String getIPAddress(WifiPresenter presenter) {
        return presenter.getWifiInformation("IP_ADDRESS");
    }

    /**
     * Fetches Name of Connected WiFi Router from {@link WifiPresenter}.
     *
     * @param presenter Instance of {@link WifiPresenter}.
     * @return String representing MAC Address of Device.
     */
    private String getConnectedWiFi(WifiPresenter presenter) {
        return presenter.getWifiInformation("CONNECTED_WIFI");
    }

    /**
     * Fetches Android Firmware Version from {@link Build}.
     *
     * @return String representing Android Firmware Version.
     */
    private int getFirmwareVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Fetches App's Version from {@link BuildConfig}.
     *
     * @return String representing App's Version.
     */
    private int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * Fetches {@link Locale} from {@link android.content.res.Configuration}
     *
     * @return String representing Locale.
     */
    private String getLocale() {
        String localeValue;
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = mActivity.getResources().getConfiguration().getLocales().get(0);
        else locale = mActivity.getResources().getConfiguration().locale;
        localeValue = locale.toString();
        return localeValue;
    }

    /**Fetches Time Zone selected by user from {@link TimeZoneSelectionFragment}.
     * @return String representing Time-Zone.*/
//	private String getTimeZone () {
//		if ( mTimeZoneSelectionFragment == null ) return "NA";
//		TimeZonePresenter presenter = mTimeZoneSelectionFragment.getTimeZonePresenter ();
//		return presenter.getTimeZone ();
//	}
}