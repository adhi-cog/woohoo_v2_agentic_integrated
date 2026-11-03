package com.sb.woohoo.ui.main.fragments.timer;

public interface TimerContract {
    interface View {
        void cancelTimer();

        void startTimer(double time, String unit);

        void checkAndShow();
    }
}
