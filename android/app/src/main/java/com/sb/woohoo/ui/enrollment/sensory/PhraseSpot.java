package com.sb.woohoo.ui.enrollment.sensory;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sensory.speech.snsr.Snsr;
import com.sensory.speech.snsr.SnsrRC;
import com.sensory.speech.snsr.SnsrSession;
import com.sensory.speech.snsr.SnsrStream;

import java.util.Locale;

@SuppressWarnings({"SameParameterValue", "CanBeFinal", "UnusedReturnValue"})
public class PhraseSpot implements SnsrSession.Listener {
    // Constants.
    private final String TAG = "PhraseSpot";

    // Other Components.
    private Thread mRecogThread;
    private WakeWordDetect mListener;
    private SnsrStream audio;

    // Variables.
    private String mModelPath;
    private double mTimeout = 0.0;
    private int mSampleRate;
    private double mSamples;
    private double mSamplesTimeoutBegin;
    private boolean isActive = false;

    public PhraseSpot(String model, WakeWordDetect wakeWordDetect) {
        mModelPath = model;
        mSamples = mSamplesTimeoutBegin = 0;
        this.mListener = wakeWordDetect;
    }

    public synchronized void start() {
        LoggerWrapper.i(TAG, "start ()");
        if (mRecogThread == null || mRecogThread.getState() == Thread.State.TERMINATED) {
            LoggerWrapper.i(TAG, "Starting recognition thread.");
            mRecogThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doPhraseSpot();
                }
            });
            mRecogThread.start();
        }
    }

    public synchronized void stop() {
        if (mRecogThread != null && mRecogThread.isAlive()) {
            LoggerWrapper.i(TAG, "Stopping recognition thread.");
            closeStream();
            mRecogThread.interrupt();
            mRecogThread = null;
        } else {
            LoggerWrapper.e(TAG, "stop:mRecogThread is  null or not not alive");
            closeStream();
        }
    }

    private SnsrRC doPhraseSpot() {
        audio = SnsrStream.fromAudioDevice();
        // Could also chain these, like so:
        // SnsrSession session = new SnsrSession().load(mModelPath).require(..).setStream(..).setHandler(..)
        SnsrSession session = new SnsrSession();
        try {
            session.load(mModelPath);

            session.require(Snsr.TASK_TYPE, Snsr.PHRASESPOT);
            session.setStream(Snsr.SOURCE_AUDIO_PCM, audio);
            session.setHandler(Snsr.RESULT_EVENT, this);

            // In case timeout set
            mSampleRate = session.getInt(Snsr.SAMPLE_RATE);
            session.setHandler(Snsr.SAMPLES_EVENT, this);
            session.run();
        } catch (Exception e) {
            LoggerWrapper.e("PhraseSpot", e.getMessage());
            /* ignore */
        }
        this.onEvent(session, "stopped");

        SnsrRC rc = session.rC();
        // Release the underlying C handles immediately, rather than waiting for GC.
        session.release();
        audio.release();
        return rc;
    }

    @Override
    public SnsrRC onEvent(SnsrSession s, String key) {
        if (!Snsr.SAMPLES_EVENT.equals(key))
            LoggerWrapper.i(TAG, "SNSR Event: " + key);
        switch (key) {
            case Snsr.SAMPLES_EVENT:
                isActive = true;
                mListener.onWakeWordListening();
                if (mTimeout == 0) return SnsrRC.OK;
                mSamples = s.getDouble(Snsr.RES_SAMPLES);
                double elapsedSamples = mSamples - mSamplesTimeoutBegin;
                if (elapsedSamples > mTimeout * mSampleRate) {
                    return SnsrRC.TIMED_OUT;
                } else
                    return SnsrRC.OK;
            case Snsr.RESULT_EVENT:
                // Start timeout all over again when we hear the trigger
                mSamplesTimeoutBegin = mSamples;
                LoggerWrapper.i(TAG, String.format(Locale.US, "\"%s\", score: %.3f",
                        s.getString(Snsr.RES_TEXT), s.getDouble(Snsr.RES_SV_SCORE)));
                // Try changing this to Snsr.PHONE_LIST
                s.forEach(Snsr.WORD_LIST, new SnsrSession.Listener() {
                    @Override
                    public SnsrRC onEvent(SnsrSession s, String key) {
                        LoggerWrapper.i(TAG, String.format(Locale.US, "  [%4.0f, %4.0f] %s\n",
                                s.getDouble(Snsr.RES_BEGIN_MS),
                                s.getDouble(Snsr.RES_END_MS),
                                s.getString(Snsr.RES_TEXT)));
                        return SnsrRC.OK;
                    }
                });
                mListener.onWakeWordDetect();

                return SnsrRC.OK;
            case "stopped":
                isActive = false;
                mListener.onWakeWordStopped();
                return SnsrRC.OK;
            default:
                isActive = false;
                mListener.onWakeWordError();
                LoggerWrapper.e(TAG, "Failed to implement handler for: " + key);
                return SnsrRC.OK;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void closeStream() {
        if (audio != null) {
            audio.release();
        }
    }
}
