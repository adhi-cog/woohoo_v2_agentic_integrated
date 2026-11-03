package com.sb.woohoo.data.prefs;

import android.content.SharedPreferences;

/**
 * Created by admin on 1/29/18.
 */


public class SharedPrefsHelper {


    private SharedPreferences mSharedPreferences;


    public SharedPrefsHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String get(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public Integer get(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public Float get(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public Boolean get(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Enquires {@link SharedPreferences} to check whether a given Key exists in it or not.
     *
     * @param key One of key Belonging to {@link SharedPrefsKey}.
     * @return {@code true} if the key is present in {@link SharedPreferences}, else {@code false}.
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    public void clear(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    /**
     * Clears {@link SharedPreferences} file from DeviceData folders of this application in the File System.
     */
    public void logOut() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setIntroVideo(String key, String aVideoLink) {
        mSharedPreferences.edit().putString(key, aVideoLink).apply();
    }

    public void setHubMode(String key, boolean isSurveillanceModeOn) {
        mSharedPreferences.edit().putBoolean(key, isSurveillanceModeOn).apply();
    }

    public boolean getHubMode(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void setMuteMode(String key, boolean muteMode) {
        mSharedPreferences.edit().putBoolean(key, muteMode).apply();
    }

    public boolean getMuteMode(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getHubPinCode() {
        return "1234";
    }

    public void setUserDetails(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public String getUserDetails(String key) {
        return mSharedPreferences.getString(key, "");
    }
}
