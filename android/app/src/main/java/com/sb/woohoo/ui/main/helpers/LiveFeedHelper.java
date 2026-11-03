package com.sb.woohoo.ui.main.helpers;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonObject;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseHelper;

import javax.inject.Inject;

/*
Handles all callbacks made by Live Feed
 */
public class LiveFeedHelper extends BaseHelper {

    // Constants.
    private static final String TAG = LiveFeedHelper.class.getSimpleName();
    private LiveFeedHelper.LiveFeedListener mListener;
    private boolean isRotating = false;
    private String remoteDeviceID;

    @Inject
    public LiveFeedHelper() {
        LoggerWrapper.i(LiveFeedHelper.class.getSimpleName(), "Constructor");
    }

    public void setListener(LiveFeedHelper.LiveFeedListener mListener) {
        this.mListener = mListener;
    }

    public LiveFeedHelper.LiveFeedListener getmListener() {
        return mListener;
    }

    public void rotateCamera(boolean rotate, JsonObject jsonObject) {
        mListener.rotateCamera(rotate, jsonObject);
    }

    public void setRotation(boolean isRotating) {
        this.isRotating = isRotating;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public String getRemoteDeviceID() {
        return remoteDeviceID;
    }

    public void setRemoteDeviceID(String remoteDeviceID) {
        this.remoteDeviceID = remoteDeviceID;
    }

    public JsonObject toRotateCamera(int angle, int isRotating,String receverUserId) {
        JsonObject hubCameraPanObj = new JsonObject();
        JsonObject direction = new JsonObject();

        direction.addProperty("direction", angle);
        direction.addProperty("rotation_status", isRotating);
        hubCameraPanObj.addProperty("receiverHubId", remoteDeviceID);
        hubCameraPanObj.addProperty("receiverUserId", receverUserId);
        hubCameraPanObj.addProperty("token", WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, ""));
        hubCameraPanObj.add("data", direction);


        LoggerWrapper.i("Rotation", hubCameraPanObj.toString());
        return hubCameraPanObj;
    }

    @Override
    public void onHelperCleanUp() {
        mListener = null;
    }

    /*
    CallBacks for Live Feed
     */
    public interface LiveFeedListener {
        void rotateCamera(boolean rotate, JsonObject jsonObject);
    }
}
