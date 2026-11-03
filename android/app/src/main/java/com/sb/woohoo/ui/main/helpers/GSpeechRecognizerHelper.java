package com.sb.woohoo.ui.main.helpers;

import static com.sb.woohoo.utils.Constants.LANGUAGE.ENGLISH_FOR_STT;
import static com.sb.woohoo.utils.others.LangUtils.DEFAULT_LANG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.speech.GoogleCloudChirp3StreamingAsrClient;
import com.sb.woohoo.utils.speech.GoogleAsrUtils;
import com.sb.woohoo.utils.speech.GoogleCloudStreamingAsrClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class GSpeechRecognizerHelper extends BaseHelper implements RecognitionListener {

    private static final String TAG = "GSpeechRecognizer_ASR_V2";
    private static final String ASR_LOG_TAG = "ASR_FLOW";
    public static int listeningCount = 0;
    public static final int START_RECORDING = 1;
    public static final int STOP_RECORDING = 0;

    private final Handler handler = new Handler();
    private final Context context;
    private final AtomicBoolean asrStatus;
    private final boolean useCloudStt = true;
    private final GoogleCloudStreamingAsrClient googleCloudStreamingAsrClient;
    private GoogleCloudChirp3StreamingAsrClient googleCloudChirp3StreamingAsrClient;
    private String activeCloudLanguageCode;

    private SpeechRecognizer mSpeechRecognizer;
    public Intent mSpeechRecognizerIntent;
    private boolean isGoogleSpeechInstalled;
    private boolean mIsGoogleAsrAppInstalled;
    private boolean mIsConnectedToAsrServiceApp;
    private boolean suppressNextClientError;
    private GSpeechRecognizerCallback gSpeechRecognizerCallback;
    private boolean isTimeout = true;
    private int time = 12;
    private final int thresholdlistenTillCount = 500;
    private String targetLang = DEFAULT_LANG;

    private Messenger serviceMessenger;
    private final Messenger clientMessenger = new Messenger(new ClientHandler());

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            mIsConnectedToAsrServiceApp = true;
            LoggerWrapper.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsConnectedToAsrServiceApp = false;
            LoggerWrapper.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    };

    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            LoggerWrapper.d(TAG, "handleMessage() called with: msg = [" + msg.what + "]");
            String transcript = msg.getData() != null ? msg.getData().getString("ASR") : null;
            if (transcript != null && gSpeechRecognizerCallback != null) {
                Log.d(ASR_LOG_TAG, "Fallback ASR result: " + transcript);
                gSpeechRecognizerCallback.asrInActive();
                gSpeechRecognizerCallback.onSpeechRecognized(transcript, activeCloudLanguageCode);
                Log.d(ASR_LOG_TAG, "ASR completed successfully");
            }
            asrStatus.compareAndSet(true, false);
            sendMessageToGoogleAsr(STOP_RECORDING);
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time > 0) {
                isTimeout = false;
                time--;
                handler.postDelayed(runnable, 1000);
            } else {
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "timeout");
                isTimeout = true;
                time = 12;
                handler.removeCallbacks(runnable);
            }
        }
    };

    @Inject
    public GSpeechRecognizerHelper(Context context) {
        this.context = context;
        initSpeechRecognizer(context);
        asrStatus = new AtomicBoolean(false);
        Log.d(ASR_LOG_TAG, "ASR config initialized: USE_CHIRP3_ASR="
                + BuildConfig.USE_CHIRP3_ASR
                + ", defaultModel=latest_short"
                + ", experimentalModel=chirp_3");
        googleCloudStreamingAsrClient = new GoogleCloudStreamingAsrClient(context,
                new GoogleCloudStreamingAsrClient.Callback() {
                    @Override
                    public void onStreamingReady() {
                        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrActive();
                            gSpeechRecognizerCallback.onAsrRecording(true);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                        }
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {
                        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrRmsChanged(rmsdB);
                        }
                    }

                    @Override
                    public void onPartialTranscript(String transcript) {
                        // Existing helper contracts only consume final text. Partials are logged.
                    }

                    @Override
                    public void onFinalTranscript(String transcript) {
                        asrStatus.compareAndSet(true, false);
                        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrInActive();
                            gSpeechRecognizerCallback.onAsrRecording(false);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                            gSpeechRecognizerCallback.onSpeechRecognized(transcript, activeCloudLanguageCode);
                        }
                        Log.d(ASR_LOG_TAG, "ASR completed successfully");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        asrStatus.compareAndSet(true, false);
                        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrInActive();
                            gSpeechRecognizerCallback.onAsrRecording(false);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                        }
                        Log.e(ASR_LOG_TAG, "gRPC ASR failed", throwable);
                        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.handleSpeechRecognizerError();
                        }
                    }
                });
    }

    private GoogleCloudChirp3StreamingAsrClient getChirp3StreamingAsrClient() {
        if (googleCloudChirp3StreamingAsrClient == null) {
            googleCloudChirp3StreamingAsrClient = new GoogleCloudChirp3StreamingAsrClient(context,
                    new GoogleCloudChirp3StreamingAsrClient.Callback() {
                        @Override
                        public void onStreamingReady() {
                            if (gSpeechRecognizerCallback != null) {
                                gSpeechRecognizerCallback.asrActive();
                                gSpeechRecognizerCallback.onAsrRecording(true);
                                gSpeechRecognizerCallback.onAsrProcessing(false);
                            }
                        }

                        @Override
                        public void onRmsChanged(float rmsdB) {
                            if (gSpeechRecognizerCallback != null) {
                                gSpeechRecognizerCallback.asrRmsChanged(rmsdB);
                            }
                        }

                        @Override
                        public void onPartialTranscript(String transcript) {
                            // Existing helper contracts only consume final text. Partials are logged.
                        }

                        @Override
                        public void onFinalTranscript(String transcript, String languageCode) {
                            asrStatus.compareAndSet(true, false);
                            if (gSpeechRecognizerCallback != null) {
                                gSpeechRecognizerCallback.asrInActive();
                                gSpeechRecognizerCallback.onAsrRecording(false);
                                gSpeechRecognizerCallback.onAsrProcessing(false);
                                gSpeechRecognizerCallback.onSpeechRecognized(transcript, normalizeRecognizedLanguageCode(languageCode));
                            }
                            Log.d(ASR_LOG_TAG, "ASR completed successfully");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(ASR_LOG_TAG, "Chirp 3 ASR failed; falling back to v1 latest_short", throwable);
                            fallbackToV1CloudAsr();
                        }
                    });
        }
        return googleCloudChirp3StreamingAsrClient;
    }

    private void fallbackToV1CloudAsr() {
        if (!asrStatus.get()) {
            return;
        }
        if (activeCloudLanguageCode == null || activeCloudLanguageCode.trim().isEmpty()) {
            activeCloudLanguageCode = resolveCloudLanguageCode();
        }
        googleCloudStreamingAsrClient.start(activeCloudLanguageCode);
    }

    public void setSpeechRecognizerListener(GSpeechRecognizerCallback gSpeechRecognizerCallback) {
        this.gSpeechRecognizerCallback = gSpeechRecognizerCallback;
    }

    public void warmUpCloudAsr() {
        if (useCloudStt) {
            Log.d(ASR_LOG_TAG, "ASR warmup: v1 latest_short fallback enabled"
                    + ", USE_CHIRP3_ASR=" + BuildConfig.USE_CHIRP3_ASR);
            googleCloudStreamingAsrClient.warmUp();
            if (BuildConfig.USE_CHIRP3_ASR) {
                Log.d(ASR_LOG_TAG, "ASR warmup: Chirp 3 enabled, warming experimental path");
                getChirp3StreamingAsrClient().warmUp();
            }
        }
    }

    private void initSpeechRecognizer(Context context) {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mIsGoogleAsrAppInstalled = GoogleAsrUtils.isGoogleAsrAppInstalled(context);
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            if (LocaleHelper.getLanguage(context).equalsIgnoreCase(Constants.LANGUAGE.CHINESH)) {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Constants.LANGUAGE.CHINESH);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Constants.LANGUAGE.CHINESH);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, Constants.LANGUAGE.CHINESH);
            } else if (LocaleHelper.getLanguage(context).equalsIgnoreCase(Constants.LANGUAGE.ARABIC)) {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Constants.LANGUAGE.ENGLISH_FOR_STT);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, ENGLISH_FOR_STT);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, Constants.LANGUAGE.ENGLISH_FOR_STT);
            } else {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, ENGLISH_FOR_STT);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ENGLISH_FOR_STT);
            }
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            isGoogleSpeechInstalled = true;
        } else if (mIsGoogleAsrAppInstalled) {
            GoogleAsrUtils.startASRService(context, serviceConnection);
        } else {
            isGoogleSpeechInstalled = false;
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "initSpeechRecognizer:isGoogleSpeechInstalled is false");
            MethodUtils.showToastMessage(context, context.getString(R.string.install_google_app));
        }
    }

    public void startSpeechToText() {
        if (useCloudStt) {
            startGrpcSpeechToText();
            return;
        }
        startFallbackSpeechToText();
    }

    private void startGrpcSpeechToText() {
        stopAsrListeningThread();
        asrStatus.compareAndSet(false, true);
        activeCloudLanguageCode = resolveCloudLanguageCode();
        if (BuildConfig.USE_CHIRP3_ASR) {
            Log.d(ASR_LOG_TAG, "ASR model selected: chirp_3"
                    + ", autoLanguage=true"
                    + ", v1FallbackModel=latest_short"
                    + ", fallbackLanguage=" + activeCloudLanguageCode);
            getChirp3StreamingAsrClient().start(activeCloudLanguageCode);
            return;
        }
        Log.d(ASR_LOG_TAG, "ASR model selected: latest_short"
                + ", language=" + activeCloudLanguageCode
                + ", chirp3Flag=false");
        googleCloudStreamingAsrClient.start(activeCloudLanguageCode);
    }

    private void startFallbackSpeechToText() {
        Log.d(ASR_LOG_TAG, "Using fallback ASR (HTTP/local)");
        if (isGoogleSpeechInstalled) {
            LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "GSpeechRecognizerHelper:startSpeechToText");
            if (mSpeechRecognizer == null) {
                initSpeechRecognizer(context);
            }
            suppressNextClientError = true;
            mSpeechRecognizer.cancel();
            handler.post(() -> {
                try {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    asrStatus.compareAndSet(false, true);
                } catch (Exception e) {
                    suppressNextClientError = false;
                    asrStatus.compareAndSet(true, false);
                    LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(),
                            "startFallbackSpeechToText failed: " + e.getMessage());
                    if (gSpeechRecognizerCallback != null) {
                        gSpeechRecognizerCallback.handleSpeechRecognizerError();
                    }
                }
            });
        } else if (mIsGoogleAsrAppInstalled) {
            asrStatus.compareAndSet(false, true);
            if (mIsConnectedToAsrServiceApp) {
                sendMessageToGoogleAsr(START_RECORDING);
            } else {
                GoogleAsrUtils.startASRService(context, serviceConnection);
                sendMessageToGoogleAsr(START_RECORDING);
            }
        } else {
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "GSpeechRecognizerHelper:startSpeechToText: isGoogleSpeechInstalled is false");
            if (gSpeechRecognizerCallback != null) {
                gSpeechRecognizerCallback.onGoogleVoiceNotInstalled();
            }
        }
    }

    private void sendMessageToGoogleAsr(int what) {
        if (mIsConnectedToAsrServiceApp) {
            Message msg = Message.obtain(null, what, 0, 0);
            msg.replyTo = clientMessenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "startSpeechToText: ", e);
            }
        } else {
            Log.e(TAG, "Not connected to ASR apk unable to send request");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrActive();
                            gSpeechRecognizerCallback.onAsrRecording(true);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                        }
        if (isTimeout) {
            isTimeout = false;
            handler.post(runnable);
        }
        LoggerWrapper.d(GSpeechRecognizerHelper.class.getSimpleName(), "onReadyForSpeech()");
    }

    @Override
    public void onBeginningOfSpeech() {
        LoggerWrapper.d(GSpeechRecognizerHelper.class.getSimpleName(), "onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        if (gSpeechRecognizerCallback != null) {
            gSpeechRecognizerCallback.asrRmsChanged(rmsdB);
        }

        if (Float.compare(rmsdB, -2.12f) >= 0) {
            listeningCount++;
        } else {
            listeningCount = 0;
        }

        if (listeningCount == thresholdlistenTillCount) {
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(),
                    "Threshold Count reached closing asr and handling error");
            resetThread();
            if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrInActive();
                            gSpeechRecognizerCallback.onAsrRecording(false);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                        }
            asrStatus.compareAndSet(true, false);
            mSpeechRecognizer.stopListening();
            listeningCount = 0;
            cancelASR();
            if (gSpeechRecognizerCallback != null) {
                gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
            }
        }
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onBufferReceived()");
    }

    @Override
    public void onEndOfSpeech() {
        LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onEndOfSpeech()");
    }

    @Override
    public void onError(int error) {
        LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError(): " + error);

        switch (error) {
            case SpeechRecognizer.ERROR_CLIENT:
                if (suppressNextClientError) {
                    suppressNextClientError = false;
                    LoggerWrapper.d(GSpeechRecognizerHelper.class.getSimpleName(),
                            "Ignoring expected ERROR_CLIENT during recognizer restart");
                    return;
                }
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : ERROR_INSUFFICIENT_PERMISSIONS");
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError STARTED: ERROR_RECOGNIZER_BUSY ");
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : ERROR_RECOGNIZER_BUSY");
                cancelASR();
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : Network Issue");
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            case SpeechRecognizer.ERROR_SERVER:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : Network Issue while processing ASR!");
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerError();
                }
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : ERROR_SPEECH_TIMEOUT");
                cancelASR();
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "onError : ERROR_NO_MATCH");
                if (gSpeechRecognizerCallback != null) {
                    gSpeechRecognizerCallback.handleSpeechRecognizerTimeOutError();
                }
                break;
            default:
                break;
        }

        if (error != SpeechRecognizer.ERROR_CLIENT) {
            suppressNextClientError = false;
            asrStatus.compareAndSet(true, false);
        }
    }

    @Override
    public void onResults(Bundle results) {
        LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onResult");
        suppressNextClientError = false;
        resetThread();
        if (gSpeechRecognizerCallback != null) {
                            gSpeechRecognizerCallback.asrInActive();
                            gSpeechRecognizerCallback.onAsrRecording(false);
                            gSpeechRecognizerCallback.onAsrProcessing(false);
                        }
        asrStatus.compareAndSet(true, false);
        mSpeechRecognizer.stopListening();
        float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches != null && !matches.isEmpty()) {
            Log.d(ASR_LOG_TAG, "Fallback ASR result: " + matches.get(0));
            if (gSpeechRecognizerCallback != null) {
                gSpeechRecognizerCallback.onSpeechRecognized(matches.get(0), resolveCloudLanguageCode());
            }
            Log.d(ASR_LOG_TAG, "ASR completed successfully");
            LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), matches.get(0));
            if (confidence != null) {
                LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), String.valueOf(confidence[0]));
            }
            for (String result : matches) {
                LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onResults:Texts:" + result);
            }
        } else {
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "GSpeechRecognizerHelper::onResults:match is null");
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onPartialResults()");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        LoggerWrapper.i(GSpeechRecognizerHelper.class.getSimpleName(), "onEvent()");
    }

    private void resetThread() {
        time = 12;
        isTimeout = true;
        handler.removeCallbacks(runnable);
    }

    public boolean getASRProcessingStatus() {
        return asrStatus.get();
    }

    private void cancelASR() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
        }
    }

    public void stopAsrListeningThread() {
        asrStatus.compareAndSet(true, false);
        suppressNextClientError = false;
        googleCloudStreamingAsrClient.stop();
        if (googleCloudChirp3StreamingAsrClient != null) {
            googleCloudChirp3StreamingAsrClient.stop();
        }
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            resetThread();
        }
        if (mIsConnectedToAsrServiceApp) {
            sendMessageToGoogleAsr(STOP_RECORDING);
        }
    }

    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        googleCloudStreamingAsrClient.stop();
        if (googleCloudChirp3StreamingAsrClient != null) {
            googleCloudChirp3StreamingAsrClient.stop();
        }
        try {
            mSpeechRecognizer.destroy();
        } catch (IllegalArgumentException e) {
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), e.getMessage());
        }
    }

    public void setLang(String lang) {
        targetLang = lang;
        if (mSpeechRecognizerIntent != null) {
            String recognitionLang = resolveCloudLanguageCode();
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLang);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, recognitionLang);
        } else {
            LoggerWrapper.e(GSpeechRecognizerHelper.class.getSimpleName(), "setLang:" + lang + " failed as mSpeechRecognizerIntent is null");
        }
    }

    private String resolveCloudLanguageCode() {
        if (targetLang == null || targetLang.trim().isEmpty()) {
            targetLang = DEFAULT_LANG;
        }
        if (Constants.LANGUAGE.CHINESH.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.CHINESE_CN.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.CHINESE_CN;
        }
        if (Constants.LANGUAGE.ARABIC.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.ARABIC_SA.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.ARABIC_SA;
        }
        if (Constants.LANGUAGE.GERMAN.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.GERMAN_DE.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.GERMAN_DE;
        }
        if (Constants.LANGUAGE.HINDI.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.HINDI_IN.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.HINDI_IN;
        }
        if (Constants.LANGUAGE.ENGLISH_GB.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.ENGLISH_GB;
        }
        if (Constants.LANGUAGE.FRENCH.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.FRENCH_FR.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.FRENCH_FR;
        }
        if (Constants.LANGUAGE.DUTCH.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.DUTCH_NL.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.DUTCH_NL;
        }
        if (Constants.LANGUAGE.RUSSIAN.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.RUSSIAN_RU.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.RUSSIAN_RU;
        }
        if (Constants.LANGUAGE.SPANISH.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.SPANISH_ES.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.SPANISH_ES;
        }
        if (Constants.LANGUAGE.ITALIAN.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.ITALIAN_IT.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.ITALIAN_IT;
        }
        if (Constants.LANGUAGE.ENGLISH.equalsIgnoreCase(targetLang)
                || Constants.LANGUAGE.ENGLISH_US.equalsIgnoreCase(targetLang)
                || DEFAULT_LANG.equalsIgnoreCase(targetLang)) {
            return Constants.LANGUAGE.ENGLISH_FOR_STT;
        }
        return targetLang;
    }

    private String normalizeRecognizedLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return activeCloudLanguageCode != null ? activeCloudLanguageCode : resolveCloudLanguageCode();
        }
        String normalized = languageCode.trim();
        if ("hi".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.HINDI_IN;
        }
        if ("en".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.ENGLISH_FOR_STT;
        }
        if ("ar".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.ARABIC_SA;
        }
        if ("zh".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.CHINESE_CN;
        }
        if ("de".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.GERMAN_DE;
        }
        if ("fr".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.FRENCH_FR;
        }
        if ("es".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.SPANISH_ES;
        }
        if ("it".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.ITALIAN_IT;
        }
        if ("nl".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.DUTCH_NL;
        }
        if ("ru".equalsIgnoreCase(normalized)) {
            return Constants.LANGUAGE.RUSSIAN_RU;
        }
        return normalized;
    }

    public interface GSpeechRecognizerCallback {

        void onSpeechRecognized(String text, String languageCode);

        void handleSpeechRecognizerError();

        void handleSpeechRecognizerTimeOutError();

        void onGoogleVoiceNotInstalled();

        void asrActive();

        void asrInActive();

        void asrNoResponse();

        void asrRmsChanged(float modulationValue);

        default void onAsrRecording(boolean recording) {}

        default void onAsrProcessing(boolean processing) {}

    }
}
