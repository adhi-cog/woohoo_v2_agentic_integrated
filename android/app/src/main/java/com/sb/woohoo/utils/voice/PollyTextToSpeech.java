package com.sb.woohoo.utils.voice;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.VolumeControl;

import java.io.IOException;
import java.net.URL;

public class PollyTextToSpeech
        implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {
    // Constants.
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int CALLBACK_WAKEWORD = 0;
    public static final int CALLBACK_FOLLOWUP = 1;
    public static final int CALLBACK_NEWS = 3;
    public static final int CALLBACK_ERROR_MSG = 4;
    public static final int CALLBACK_OTHER = 5;
    public static final int CALLBACK_NEWS_DETAILS = 6;
    public static final String COGNITO_POOL_ID = "us-west-2:fa059688-e9c5-44b2-b709-ec0a1fc24f1b";
    public static final Regions MY_REGION = Regions.US_WEST_2;
    private final int STREAM_OTHER = 1;
    private  final String TAG = PollyTextToSpeech.class.getSimpleName();

    // Variables.
    public int CALLBACK_CURRENT;

    // Components.
    private MediaPlayer speechPlayer;
    private GetSpeechUrl getSpeechUrlTask;
    private AmazonPollyPresigningClient amazonPollyPresigningClient;

    // FIXME
    private AudioManager audioManager;
    private int previousAudioMode = AudioManager.MODE_NORMAL;
    private boolean previousSpeakerphoneOn = false;
    private boolean ttsAudioRouteOverridden = false;

    // Listeners.
    private SpeechCompleteCallback speechCompleteCallback;
    private VolumeControl.VolumeControlCallbacks volumeControlCallbacks;

    public PollyTextToSpeech(AmazonPollyPresigningClient amazonPollyPresigningClient, MediaPlayer speechPlayer, MainActivity context) {
        LoggerWrapper.i(PollyTextToSpeech.class.getSimpleName(), "Constructor");
        this.amazonPollyPresigningClient = amazonPollyPresigningClient;
        this.speechPlayer = speechPlayer;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static PollyTextToSpeech getInstance(
            AmazonPollyPresigningClient amazonPollyPresigningClient,
            MediaPlayer speechPlayer,
            MainActivity context) {
        return new PollyTextToSpeech(amazonPollyPresigningClient, speechPlayer, context);
    }

    // FIXME : Check later for same instance of VolumeControlCallbacks in VolumeControl.
    public void setListener(VolumeControl.VolumeControlCallbacks volumeControlCallbacks) {
        this.volumeControlCallbacks = volumeControlCallbacks;
    }

    public void speakShortPhrase(String text, int streamType, SpeechCompleteCallback speechCompleteCallback) {
        LoggerWrapper.i(TAG, "Starting Short Speech Url fetch");
        LoggerWrapper.i(TAG, "Speech text : " + text);
        this.speechCompleteCallback = speechCompleteCallback;
        if (speechPlayer != null) {
            setSpeechPlayerStream(streamType);
        }
        getSpeechUrlTask = new GetSpeechUrl(streamType, audioManager.getStreamVolume(streamType));
        getSpeechUrlTask.execute("Joanna", text);
    }

    public void speak(String text, int streamType, SpeechCompleteCallback speechCompleteCallback, int callbackType, String lang) {
        LoggerWrapper.i(TAG, "Starting Speech Url fetch");
        LoggerWrapper.i(TAG, "Speech text : " + text);
        LoggerWrapper.i(TAG, "Stream type : " + streamType);
        LoggerWrapper.i(TAG, "Stream volume : " + audioManager.getStreamVolume(streamType));

        this.speechCompleteCallback = speechCompleteCallback;
        this.CALLBACK_CURRENT = callbackType;
        if (speechPlayer != null) {
            setSpeechPlayerStream(streamType);
        }
        volumeControlCallbacks.onStreamChange(null, streamType);
        getSpeechUrlTask = new GetSpeechUrl(streamType, audioManager.getStreamVolume(streamType));

        if(lang.equalsIgnoreCase(Constants.LANGUAGE.CHINESH)) {
            getSpeechUrlTask.execute("Zhiyu", text);
        } else {
            getSpeechUrlTask.execute("Joanna", text);
        }
    }

    public void pauseSpeech() {
        if (speechPlayer != null && speechPlayer.isPlaying()) {
            speechPlayer.pause();
        }
    }

    public void resumeSpeech() {
        if (speechPlayer != null) {
            speechPlayer.start();
        }
    }

    private void setSpeechPlayerStream(int streamType) {
        switch (streamType) {
            case STREAM_MUSIC:
                volumeControlCallbacks.onStreamChange(speechPlayer, AudioManager.STREAM_MUSIC);
                break;
            case STREAM_OTHER:
                volumeControlCallbacks.onStreamChange(speechPlayer, AudioManager.STREAM_SYSTEM);
                break;
            case STREAM_NOTIFICATION:
            default:
                volumeControlCallbacks.onStreamChange(speechPlayer, AudioManager.STREAM_NOTIFICATION);
                break;

        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        LoggerWrapper.i(TAG, "Media player play complete");
        restoreTtsAudioRoute();
        if (speechCompleteCallback != null) {
            speechCompleteCallback.onSpeechComplete(CALLBACK_CURRENT);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        LoggerWrapper.i(TAG, "Media player prepared");
        if (getSpeechUrlTask != null && (getSpeechUrlTask.getStatus() != AsyncTask.Status.RUNNING || getSpeechUrlTask.getStatus() != AsyncTask.Status.PENDING)) {
            mediaPlayer.start();
        }
        if (speechCompleteCallback != null) {
            speechCompleteCallback.onSpeechStarted(CALLBACK_CURRENT);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        LoggerWrapper.i(TAG, "Media player play error");
        restoreTtsAudioRoute();
        if (speechCompleteCallback != null) {
            speechCompleteCallback.onError();
        }
        LoggerWrapper.e(TAG, "Media player error!");
        return false;
    }

    private URL getSpeechUrl(String voiceId, String text) {
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        .withText(text)
                        .withVoiceId(voiceId)
                        .withOutputFormat(OutputFormat.Mp3);

        return amazonPollyPresigningClient.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);
    }

    public void mediaPlayerSetUp() {
        speechPlayer.setOnCompletionListener(this);
        speechPlayer.setOnErrorListener(this);
        speechPlayer.setOnPreparedListener(this);
        setSpeechPlayerStream(STREAM_NOTIFICATION);
    }

    private void playSpeechUrl(URL speechUrl, int streamType, int streamVolume) {
        LoggerWrapper.e(TAG, "Attempting to play Speech Url on Stream : " + streamType + " , volume : " + streamVolume);
        if (speechPlayer != null) {
            try {
                if (speechPlayer.isPlaying()) {
                    speechPlayer.stop();
                }
                speechPlayer.reset();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        attributesBuilder.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_NONE);
                    }
                    speechPlayer.setAudioAttributes(attributesBuilder.build());
                } else {
                    speechPlayer.setAudioStreamType(streamType);
                }
                speechPlayer.setVolume(1.0f, 1.0f);
//            speechPlayer.setVolume();
                // hardcode stream type??
//            speechPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
                speechPlayer.setDataSource(speechUrl.toString());
            } catch (IOException e) {
                LoggerWrapper.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
            }
            speechPlayer.prepareAsync();
        } else {
            LoggerWrapper.e(TAG, "Unable to play speech: MediaPlayer is null");
            if (speechCompleteCallback != null) {
                speechCompleteCallback.onError();
            }
        }
    }

    public void stopSpeech(boolean needCallback) {
        LoggerWrapper.i(TAG, "Stop Speech");
        if (getSpeechUrlTask != null) {
            if (getSpeechUrlTask.getStatus() == AsyncTask.Status.RUNNING || getSpeechUrlTask.getStatus() == AsyncTask.Status.PENDING) {
                getSpeechUrlTask.cancel(true);
            }
        }
        if (speechPlayer != null) {
            if (speechPlayer.isPlaying()) {
                speechPlayer.stop();
            }
            speechPlayer.reset();
        }
        restoreTtsAudioRoute();
        if (speechCompleteCallback != null && needCallback) {
            speechCompleteCallback.onSpeechComplete(CALLBACK_CURRENT);
        }
    }

    public void pollyCleanUps() {
        stopSpeech(false);
        if (speechPlayer != null) {
            speechPlayer.release();
        }
        speechPlayer = null;
        getSpeechUrlTask = null;
        if (amazonPollyPresigningClient != null) {
            amazonPollyPresigningClient.shutdown();
        }
        amazonPollyPresigningClient = null;
    }

    private class GetSpeechUrl extends AsyncTask<String, Void, URL> {

        private int streamType;
        private int streamVolume;

        public GetSpeechUrl(int streamType, int streamVolume) {
            this.streamType = streamType;
            this.streamVolume = streamVolume;
        }

        @Override
        protected URL doInBackground(String... params) {
            URL speechUrl = null;
            try {
                speechUrl = getSpeechUrl(params[0], params[1]);
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    LoggerWrapper.e(TAG, "GetSpeechUrl:" + e.getMessage());
                }
            }
            return speechUrl;
        }

        @Override
        protected void onPostExecute(URL speechUrl) {
            LoggerWrapper.i(TAG, "Speech Url fetched");
            if (!isCancelled()) {
                if (speechUrl != null) {
                    playSpeechUrl(speechUrl, streamType, streamVolume);
                } else {
                    LoggerWrapper.e(TAG, "GetSpeechUrl:Speech Url is null!");
                    if (speechCompleteCallback != null) {
                        speechCompleteCallback.onError();
                    }
                }
            }
        }
    }

    private void prepareTtsAudioRoute() {
        // TTS uses normal media routing so Android can choose Bluetooth A2DP when connected
        // and the device speaker when no external route is active.
    }

    private void restoreTtsAudioRoute() {
        if (audioManager == null || !ttsAudioRouteOverridden) {
            return;
        }
        try {
            audioManager.setSpeakerphoneOn(previousSpeakerphoneOn);
            audioManager.setMode(previousAudioMode);
        } catch (Exception e) {
            LoggerWrapper.e(TAG, "Unable to restore TTS audio route: " + e.getMessage());
        } finally {
            ttsAudioRouteOverridden = false;
        }
    }
}
