package com.sb.woohoo.custom;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.callbacks.BrightnessCallback;

public class BrightnessObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    private BrightnessCallback brightnessCallback;

    public BrightnessObserver(Handler handler, BrightnessCallback brightnessCallback) {
        super(handler);
        this.brightnessCallback = brightnessCallback;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        LoggerWrapper.i(BrightnessObserver.class.getSimpleName(), "selfChange " + selfChange);
        super.onChange(selfChange);

        brightnessCallback.onBrightnessChange();
    }
}
