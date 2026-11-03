package com.sb.woohoo.ui.main.fragments.timer;

public interface CounterCallbacks {
    void onTick(long millisUntilFinished);

    void onFinish();

    void registerActions(CounterActions counterActions);
}
