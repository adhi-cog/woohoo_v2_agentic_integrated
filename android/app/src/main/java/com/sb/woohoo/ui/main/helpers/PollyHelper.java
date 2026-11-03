package com.sb.woohoo.ui.main.helpers;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.utils.VolumeControl;
import com.sb.woohoo.utils.voice.PollyTextToSpeech;
import com.sb.woohoo.utils.voice.SpeechCompleteCallback;

import javax.inject.Inject;

public class PollyHelper extends BaseHelper {
    private PollyTextToSpeech pollyTextToSpeech;
    private PollyCallback pollyCallback;
    private SpeechCompleteCallback speechCompleteCallback = new SpeechCompleteCallback() {
        @Override
        public void onSpeechStarted(int type) {
            pollyCallback.onSpeechStart(type);
        }

        @Override
        public void onSpeechComplete(int type) {
            pollyCallback.onSpeechComplete(type);
        }

        @Override
        public void onError() {
            pollyCallback.onSpeechError();
        }
    };

    @Inject
    public PollyHelper(PollyTextToSpeech pollyTextToSpeech) {
        LoggerWrapper.d(PollyHelper.class.getSimpleName(), "PollyTextToSpeech:Constructor");
        this.pollyTextToSpeech = pollyTextToSpeech;
    }

    public void setListener(PollyCallback callback) {
        this.pollyCallback = callback;
    }

    public void setVolumeListener(VolumeControl.VolumeControlCallbacks listener) {
        this.pollyTextToSpeech.setListener(listener);
    }

    public void initializeComponent() {
        pollyTextToSpeech.mediaPlayerSetUp();
    }

    public void startSpeaking(String text, int streamType, int callbackType, String lang) {
        LoggerWrapper.d(PollyHelper.class.getSimpleName(), "startSpeaking text=" + text + ", streamType=" + streamType + ", callbackType=" + callbackType + ", lang=" + lang);
        pollyTextToSpeech.speak(text, streamType, speechCompleteCallback, callbackType, lang);
    }

    public void stopSpeaking(boolean needCallback) {
        if(pollyTextToSpeech!=null){
            pollyTextToSpeech.stopSpeech(needCallback);
        } else {
            LoggerWrapper.e(PollyHelper.class.getSimpleName(), "stopSpeaking: PollyTextToSpeech is null");

        }
    }

    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        pollyTextToSpeech.pollyCleanUps();
        pollyTextToSpeech = null;
        pollyCallback = null;
    }

    public interface PollyCallback {
        void onSpeechStart(int type);

        void onSpeechComplete(int type);

        void onSpeechError();
    }
}
