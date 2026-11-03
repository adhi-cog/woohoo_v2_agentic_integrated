package com.sb.woohoo.ui.main.Interactors;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.Interactors.listener.PresenterListener;
import com.sb.woohoo.ui.main.Interactors.listener.SpeechListener;
import com.sb.woohoo.ui.main.MainPresenter;

import javax.inject.Inject;

public class SpeechManager implements PresenterListener.Speech {
    private SpeechListener mListener;

    @Inject
    public SpeechManager() {
        LoggerWrapper.i(SpeechManager.class.getSimpleName(), "Constructor");
    }

    @Override
    public void setListener(MainPresenter listener) {
        this.mListener = listener;
    }

    @Override
    public void speakUsingPolly(String text, int streamType, int callbackType) {
        mListener.startSpeaking(text, streamType, callbackType);
    }

    @Override
    public void stopPolly(boolean needCallback) {
        if(mListener!=null)
        mListener.stopSpeaking(needCallback);
    }
}