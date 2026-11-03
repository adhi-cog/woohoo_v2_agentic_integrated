package com.sb.woohoo.ui.main;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;

public class HandlerManager extends Handler {
    public static final int WAKE_WORD_DETECTED = 202;
    public static final int CAMERA_ROTATION = 203;

    private ForegroundHandlerCallbacks foregroundHandlerCallbacks;
    private BackgroundHandlerCallbacks backgroundHandlerCallbacks;

    private Runnables wakeWordRunnable;
    private Runnables cameraRotationRunnable;
    private Thread mUiThread;

    public HandlerManager() {
        wakeWordRunnable = new Runnables(this).setRunnable(WAKE_WORD_DETECTED);
        cameraRotationRunnable = new Runnables(this).setRunnable(CAMERA_ROTATION);
        mUiThread = Thread.currentThread();
    }

    public HandlerManager(HandlerThread handlerThread) {
        super(handlerThread.getLooper());
        mUiThread = Thread.currentThread();
    }

    public void setForegroundListener(ForegroundHandlerCallbacks callbacks) {
        this.foregroundHandlerCallbacks = callbacks;
    }

    public void setBackgroundListener(BackgroundHandlerCallbacks callbacks) {
        this.backgroundHandlerCallbacks = callbacks;
    }

    public void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() != mUiThread) {
            post(runnable);
        } else {
            runnable.run();
        }
    }

    public void postMessage(int message, long delayMillis) {
        Runnables runnable;

        runnable = getRunnable(message);

        if (runnable != null) {
            postDelayed(runnable, delayMillis);
        } else {
            LoggerWrapper.e(HandlerManager.class.getSimpleName(), "Error running thread!");
        }
    }

    public void removeCallbacks(int... message) {
        for (int aMessage : message) {
            removeCallbacks(getRunnable(aMessage));
        }
    }

    private Runnables getRunnable(int message) {
        switch (message) {
            case WAKE_WORD_DETECTED:
                return wakeWordRunnable;
            case CAMERA_ROTATION:
                return cameraRotationRunnable;
        }
        return null;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WAKE_WORD_DETECTED:
                foregroundHandlerCallbacks.onWakeWord();
                break;
            case CAMERA_ROTATION:
                foregroundHandlerCallbacks.onCameraRotation();
                break;
        }
    }

    public interface ForegroundHandlerCallbacks {

        void onWakeWord();

        void onCameraRotation();
    }

    public interface BackgroundHandlerCallbacks {

    }
}
