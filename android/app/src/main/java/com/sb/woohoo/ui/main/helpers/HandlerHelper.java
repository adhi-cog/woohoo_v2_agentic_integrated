package com.sb.woohoo.ui.main.helpers;

import android.os.Handler;
import android.os.HandlerThread;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.HandlerManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class HandlerHelper {

    public static final int THREAD_FOREGROUND = 991;
    private static final int THREAD_BACKGROUND = 990;
    private HandlerManager mForegroundHandler;
    private HandlerManager mBackgroundHandler;

    @Inject
    public HandlerHelper() {
        LoggerWrapper.i(HandlerHelper.class.getSimpleName(), "Constructor");
        mForegroundHandler = new HandlerManager();
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBackgroundHandler = new HandlerManager(handlerThread);
    }

    public void setForegroundHandlerCallback(HandlerManager.ForegroundHandlerCallbacks callback) {
        mForegroundHandler.setForegroundListener(callback);
    }

    public void setBackgroundHandlerCallback(HandlerManager.BackgroundHandlerCallbacks callback) {
        mBackgroundHandler.setBackgroundListener(callback);
    }

    /**
     * Runs the Runnable on the Thread defined by threadType.
     *
     * @param threadType {@code THREAD_BACKGROUND or THREAD_FOREGROUND} Defines the runnable to run on the UI THREAD
     *                   OR BACKGROUND THREAD
     * @param runnable   Runnable Object that will run on the thread, Runnable Object cannot be null.
     */
    public void runThread(int threadType, @NonNull Runnable runnable) {
        switch (threadType) {
            case THREAD_FOREGROUND:
                runOnUiThread(runnable);
                break;
            case THREAD_BACKGROUND:
                break;
        }
    }

    /**
     * Defines a handler on the UI Thread
     *
     * @param message     Should be defined in {@link HandlerManager }
     * @param delayMillis Delay for the runnable to run
     */
    public void postForegroundMessage(int message, long delayMillis) {
        mForegroundHandler.postMessage(message, delayMillis);
    }

    /**
     * Defines a handler on the Background Thread
     *
     * @param message     Should be defined in {@link HandlerManager }
     * @param delayMillis Delay for the runnable to run
     */
    public void setBackgroundHandler(int message, long delayMillis) {
        mBackgroundHandler.postMessage(message, delayMillis);
    }

    /**
     * Removes the runnable from the handler
     *
     * @param message Should be defined in {@link HandlerManager }
     */
    public void removeForegroundHandler(int... message) {
        mForegroundHandler.removeCallbacks(message);
    }

    /**
     * Removes the runnable from the handler
     *
     * @param message Should be defined in {@link HandlerManager }
     */
    public void removeBackgroundHandler(int message) {
        mBackgroundHandler.removeCallbacks(message);
    }

    /**
     * @return Returns the Background Handler
     */
    public Handler getBackgroundHandler() {
        return mBackgroundHandler;
    }

    /**
     * @return Returns the Foreground Handler
     */
    public Handler getForegroundHandler() {
        return mForegroundHandler;
    }

    /**
     * Queues the runnable on the UI Thread
     *
     * @param runnable Runnable Object that should run on the UI Thread, runnable cannot be null
     */
    private void runOnUiThread(Runnable runnable) {
        mForegroundHandler.runOnUiThread(runnable);
    }
}
