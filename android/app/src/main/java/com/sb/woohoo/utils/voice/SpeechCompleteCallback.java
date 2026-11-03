package com.sb.woohoo.utils.voice;

public interface SpeechCompleteCallback {

    void onSpeechStarted(int type);

    void onSpeechComplete(int type);

    void onError();
}
