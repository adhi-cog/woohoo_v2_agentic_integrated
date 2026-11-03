package com.sb.woohoo.ui.enrollment.sensory;

public interface WakeWordDetect {
    void onWakeWordDetect();

    void onWakeWordListening();

    void onWakeWordStopped();

    void onWakeWordError();
}