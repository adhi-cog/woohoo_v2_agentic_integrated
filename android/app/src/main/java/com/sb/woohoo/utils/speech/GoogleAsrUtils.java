package com.sb.woohoo.utils.speech;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class GoogleAsrUtils {

    public static final String ASR_PACKAGE_NAME = "com.googleasr.speech";
    public static final String ASR_SERVICE_NAME = "com.googleasr.speech.SpeechService";
    public static final String ASR_LAUNCHER_ACTIVITY = "com.googleasr.speech.MainActivity";

    private GoogleAsrUtils() {

    }

    public static boolean isGoogleAsrAppInstalled(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(ASR_PACKAGE_NAME);
            return intent != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void startASRService(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(GoogleAsrUtils.ASR_PACKAGE_NAME, GoogleAsrUtils.ASR_SERVICE_NAME));
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void openAsrApp(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(ASR_PACKAGE_NAME, ASR_LAUNCHER_ACTIVITY));
        context.startActivity(intent);
    }

}
