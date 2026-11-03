package com.sb.woohoo.utils.sensory;

import android.content.Context;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sensory.speech.snsr.Snsr;
import com.sensory.speech.snsr.SnsrRC;
import com.sensory.speech.snsr.SnsrSession;
import com.sensory.speech.snsr.SnsrStream;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@SuppressWarnings({"SameParameterValue", "SameReturnValue"})
public class Enroll implements SnsrSession.Listener {
    private  final String TAG = "Enroll";
    private  final String ENROLL_VERSION = "~0.8.0 || 1.0.0";
    private  final String TARGET = null; // Set to (e.g.) "pc38" to produce embedded output.
    private  final Boolean SAVE_ENROLLMENT_AUDIO = false;
    private   double MIN_SAMPLES = (16000 * 0.2);
    private static final String EnrollSubDir = "/enroll/";
    private static int mEnroll = 0;
    private final SensoryActivity mUi;
    private final String mModelFile, mOutFile, mTriggerPhrase;
    private final File enrollDir;
    private Boolean mShowPrompt = true;
    private int mContextIndex = 0;
    private SnsrStream mAudio; // saved for use across event handlers
    private Thread mRecogThread;

    public Enroll(SensoryActivity sensoryActivity, String triggerPhrase, String modelFile, String outFile) {
        mUi = sensoryActivity;
        mTriggerPhrase = triggerPhrase;
        mModelFile = modelFile;
        enrollDir = getOutDir(sensoryActivity);
        //noinspection ResultOfMethodCallIgnored
        enrollDir.mkdirs();
        mOutFile = outFile;
    }

    //    private static final String[] PromptContext = {
//        "it is me.", "will it rain tomorrow?", "what is Google trading at?"
//    };
    public static File getOutDir(Context context) {
        return new File(context.getFilesDir(), EnrollSubDir);
    }

    private File getDirectory() {
        return enrollDir;
    }

    private void saveEmbeddedModel(SnsrSession task, String streamKey, String fileName) {
        SnsrStream out = SnsrStream.fromFileName(fileName, "w");
        try {
            out.copy(task.getStream(streamKey));
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        }
        out.close();
        out.release();
        LoggerWrapper.i(TAG, "Wrote " + streamKey + " to " + fileName);
    }

    private void saveEmbeddedModels(SnsrSession s, String target, String filePrefix) {
        SnsrSession task = new SnsrSession();
        try {
            task.load(s.getStream(Snsr.MODEL_STREAM));
            task.setString(Snsr.EMBEDDED_TARGET, target);
            saveEmbeddedModel(task, Snsr.EMBEDDED_HEADER_STREAM, getPath(filePrefix + "-sch.h"));
            saveEmbeddedModel(task, Snsr.EMBEDDED_SEARCH_STREAM, getPath(filePrefix + "-sch.bin"));
            saveEmbeddedModel(task, Snsr.EMBEDDED_ACMODEL_STREAM, getPath(filePrefix + "-net.bin"));
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        }
        task.release();
    }

    private String getPath(String fileName) {
        return new File(getDirectory(), fileName).getAbsolutePath();
    }

    String getOutPath() {
        return getPath(mOutFile);
    }

    public synchronized void start() {
        if (mRecogThread == null) {
            LoggerWrapper.d(TAG, "Starting enrolling thread.");
            mRecogThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doEnroll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mRecogThread.start();
        }
    }

    public synchronized void stop() {
        if (mRecogThread != null && mRecogThread.isAlive()) {
            LoggerWrapper.d(TAG, "Stopping enroll thread.");
            mRecogThread.interrupt();
            try {

                mRecogThread.join();
                mRecogThread = null;
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
    }

    private void doEnroll() {
        mAudio = SnsrStream.fromAudioDevice();
        // Could also chain these, like so:
        // SnsrSession session = new SnsrSession().load(mModelFile).require(..).setStream(..).setString(..).setHandler(..)
        SnsrSession session = new SnsrSession();
        try {
            session.load(mModelFile)
                    .require(Snsr.TASK_TYPE, Snsr.ENROLL)
                    .require(Snsr.TASK_VERSION, ENROLL_VERSION);
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        }
        session.setStream(Snsr.SOURCE_AUDIO_PCM, mAudio);
        // the user defined phrase to be enrolled
        session.setString(Snsr.USER, mTriggerPhrase);

        // Add in some handlers for important lifecycle events
        session.setHandler(Snsr.FAIL_EVENT, this);
        session.setHandler(Snsr.PASS_EVENT, this);
        session.setHandler(Snsr.PROG_EVENT, this);
        session.setHandler(Snsr.PAUSE_EVENT, this);
        session.setHandler(Snsr.RESUME_EVENT, this);
        session.setHandler(Snsr.DONE_EVENT, this);

        // You can also define a handler class anonymously inline
        session.setHandler(Snsr.SAMPLES_EVENT, new SnsrSession.Listener() {
                    @Override
                    public SnsrRC onEvent(SnsrSession snsrSession, String s) {
                        if (mShowPrompt && snsrSession.getDouble(Snsr.RES_SAMPLES) >= MIN_SAMPLES) {
                            promptForPhrase();
                            mShowPrompt = false;
                        }
                        return SnsrRC.OK;
                    }
                }
        );

        mShowPrompt = true;
        try {
            session.run();
            // Optional: save enrollment context
            // session.save(SnsrDataFormat.RUNTIME, xyz);

        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        }
        // Optional but good practice. finalize() will (eventually) release.
        session.release();
        mAudio.release();
    }

    public SnsrRC onEvent(SnsrSession s, String key) {
        LoggerWrapper.i(TAG, "SNSR Event: " + key);
        switch (key) {
            case Snsr.FAIL_EVENT:
                return onFail(s);
            case Snsr.PASS_EVENT:
                return onPass(s);
            case Snsr.PROG_EVENT:
                return onProgress(s);
            case Snsr.PAUSE_EVENT:
                return onPause(s);
            case Snsr.RESUME_EVENT:
                return onResume(s);
            case Snsr.DONE_EVENT:
                return onDone(s);
            default:
                LoggerWrapper.e(TAG, "Failed to implement handler for: " + key);
                return SnsrRC.OK;
        }
    }

    private SnsrRC onFail(SnsrSession s) {
        LoggerWrapper.e(TAG, "FAILED: " + s.getString(Snsr.RES_REASON));
        LoggerWrapper.e(TAG, "   FIX: " + s.getString(Snsr.RES_GUIDANCE));

        mUi.log("FAILED: " + s.getString(Snsr.RES_REASON));
        mUi.log("   FIX: " + s.getString(Snsr.RES_GUIDANCE));

        /* Save failed enrollment recording for debugging
         *  can get it with ADB */

        if (SAVE_ENROLLMENT_AUDIO) {
            SnsrStream audio = s.getStream(Snsr.AUDIO_STREAM);
            if (audio != null) {
                final String path = getPath(String.format(Locale.US, "fail-%02d.wav", mEnroll++));
                SnsrStream out = SnsrStream.fromAudioFile(path, "w");
                try {
                    out.copy(audio);
                } catch (IOException e) {
                    LoggerWrapper.e(TAG, e.toString());
                }
                out.release();
            }
        }
        return SnsrRC.OK;
    }

    private SnsrRC onPass(SnsrSession s) {
        mUi.log("Audio is good.");
        /* Save good enrollment recording for debugging
         *  Can be retrieved via ADB */

        if (SAVE_ENROLLMENT_AUDIO) {
            SnsrStream audio = s.getStream(Snsr.AUDIO_STREAM);
            if (audio != null) {
                final String path = getPath(String.format(Locale.US, "pass-%02d.wav", mEnroll++));
                SnsrStream out = SnsrStream.fromAudioFile(path, "w");
                try {
                    out.copy(audio);
                } catch (IOException e) {
                    LoggerWrapper.e(TAG, e.toString());
                }
                out.release();
            }
        }
        return SnsrRC.OK;
    }

    private SnsrRC onProgress(SnsrSession s) {
        if (Thread.interrupted()) return SnsrRC.INTERRUPTED;
        double p = s.getDouble(Snsr.RES_PERCENT_DONE);
        String progressNotice = String.format(Locale.US, "Adapting: %3.0f%% done.", p);
        if (p >= 100) progressNotice = "Adapting complete!";
        mUi.log(progressNotice);
        return SnsrRC.OK;
    }

    @SuppressWarnings("UnusedParameters")
    private SnsrRC onPause(SnsrSession s) {
        mAudio.close();
        return SnsrRC.OK;
    }

    @SuppressWarnings("UnusedParameters")
    private SnsrRC onResume(SnsrSession s) {
        try {
            if (s.getInt(Snsr.ADD_CONTEXT) == 0) mContextIndex = -1;
            //else mContextIndex = (new Random()).nextInt(PromptContext.length);
            mShowPrompt = true;
            mAudio.open();
        } catch (IOException e) {
            LoggerWrapper.e(TAG, "Error resuming audio: " + e);
            return SnsrRC.STREAM;
        }
        LoggerWrapper.d(TAG, "open RC: " + mAudio.rC());
        return SnsrRC.OK;
    }

    private SnsrRC onDone(SnsrSession s) {
        final String outPath = getOutPath();
        SnsrStream out = SnsrStream.fromFileName(outPath, "w");
        try {
            out.copy(s.getStream(Snsr.MODEL_STREAM));
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        }
        out.close();
        //noinspection ConstantConditions
        if (TARGET != null) saveEmbeddedModels(s, TARGET, "embedded-" + TARGET);
        mUi.notify(UiState.ENROLLED);
        return SnsrRC.STOP;
    }

    private void promptForPhrase() {
        String prompt = "SAY: " + mTriggerPhrase;
        //if (mContextIndex >= 0) prompt += " " + PromptContext[mContextIndex];
        mUi.log(prompt);
    }
}
