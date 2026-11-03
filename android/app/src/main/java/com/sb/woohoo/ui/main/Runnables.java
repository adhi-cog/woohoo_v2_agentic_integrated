package com.sb.woohoo.ui.main;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;

public class Runnables implements Runnable {

    private HandlerManager handlerManager;
    private int callbackCode;

    public Runnables(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    public int getCallbackCode() {
        return callbackCode;
    }

    public Runnables setRunnable(int callbackCode) {
        this.callbackCode = callbackCode;
        return this;
    }

    @Override
    public void run() {
        LoggerWrapper.i("RUNNABLE", "THREAD RUNNING");
        handlerManager.sendEmptyMessage(callbackCode);
    }
}
