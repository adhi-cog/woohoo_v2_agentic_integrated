package com.sb.woohoo.utils.sensory;

public interface WakeWordDetect {
    void onWakeWordDetect();

    void onWakeWordListening();

    void onWakeWordStopped();

    void onWakeWordError();
}
