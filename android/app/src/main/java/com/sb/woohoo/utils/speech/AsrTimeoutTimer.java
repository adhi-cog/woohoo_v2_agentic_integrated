package com.sb.woohoo.utils.speech;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AsrTimeoutTimer {

    private static final String TAG = "EmptyTimer";
    private Handler handler;
    private Runnable callback;

    private static final int TIMEOUT_SECONDS = 5000;

    public AsrTimeoutTimer() {
        handler = new Handler(Looper.getMainLooper()); // Initialize the handler with the main thread's Looper
    }

    public void startTimer(final TimerCallback timerCallback) {
        callback = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run() called timer started");
                timerCallback.onTimerFinished(); // Callback when the timer finishes
            }
        };
        handler.postDelayed(callback, TIMEOUT_SECONDS); // Delay for 5 seconds (5000 milliseconds)
    }

    public void cancelTimer() {
        if (callback != null) {
            Log.d(TAG, "cancelTimer() called");
            handler.removeCallbacks(callback); // Cancel the timer
        }
    }

    public interface TimerCallback {
        void onTimerFinished();
    }

}
