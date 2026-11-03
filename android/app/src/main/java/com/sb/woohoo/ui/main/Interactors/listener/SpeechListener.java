package com.sb.woohoo.ui.main.Interactors.listener;

public interface SpeechListener {
    void startSpeaking(String text, int streamType, int callbackType);

    void stopSpeaking(boolean needCallback);
}