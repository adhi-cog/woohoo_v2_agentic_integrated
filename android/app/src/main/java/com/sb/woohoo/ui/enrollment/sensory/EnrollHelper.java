package com.sb.woohoo.ui.enrollment.sensory;

import android.content.Context;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sensory.speech.snsr.Snsr;
import com.sensory.speech.snsr.SnsrRC;
import com.sensory.speech.snsr.SnsrSession;
import com.sensory.speech.snsr.SnsrStream;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@SuppressWarnings({"SameParameterValue", "SameReturnValue"})
public class EnrollHelper implements SnsrSession.Listener {
    // Constants.
    private final String TAG = "EnrollHelper";
    private final String ENROLL_VERSION = "~0.8.0 || 1.0.0";
    private final String TARGET = null; // Set to (e.g.) "pc38" to produce embedded output.
    private final Boolean SAVE_ENROLLMENT_AUDIO = false;
    private final double MIN_SAMPLES = (16000 * 0.2);
    private static final String EnrollSubDir = "/enroll/";
    private final String SENSORY_FILE_MODE = "w";

    // Variables.
    private int mEnroll = 0;
    private final String mModelFile, mOutFile, mTriggerPhrase;
    // Other Components.
    private final EnrollListener mEnrollListener;
    private final File enrollDir;
    private Boolean mShowPrompt = true;
    private int mContextIndex = 0;
    private SnsrStream mAudio; // saved for use across event handlers
    private Thread mRecogThread;

    public EnrollHelper(
            EnrollListener listener,
            String triggerPhrase,
            String modelFile,
            String outFile,
            Context context) {
        mEnrollListener = listener;
        mTriggerPhrase = triggerPhrase;
        mModelFile = modelFile;
        mOutFile = outFile;
        enrollDir = getOutDir(context);

        //noinspection ResultOfMethodCallIgnored
        enrollDir.mkdirs();
    }

    public static File getOutDir(Context context) {
        return new File(context.getFilesDir(), EnrollSubDir);
    }

    private File getDirectory() {
        return enrollDir;
    }

    private void saveEmbeddedModel(SnsrSession task, String streamKey, String fileName) {
        SnsrStream out = SnsrStream.fromFileName(fileName, SENSORY_FILE_MODE);
        try {
            out.copy(task.getStream(streamKey));
            LoggerWrapper.i(TAG, "Wrote " + streamKey + " to " + fileName);
        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        } finally {
            out.close();
            out.release();
        }
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
        } finally {
            task.release();
        }
    }

    private String getPath(String fileName) {
        return new File(getDirectory(), fileName).getAbsolutePath();
    }

    private String getOutPath() {
        return getPath(mOutFile);
    }

    public synchronized void start() {
        if (mRecogThread == null) {
            LoggerWrapper.d(TAG, "Starting enrolling thread.");
            mRecogThread = new Thread(() -> {
                try {
                    doEnroll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mRecogThread.start();
        }
    }

    public synchronized void stop() {
        if (mRecogThread != null && mRecogThread.isAlive()) {
            LoggerWrapper.i(TAG, "Stopping enroll thread.");
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
        session.setHandler(Snsr.SAMPLES_EVENT, (snsrSession, s) -> {
                    if (mShowPrompt && snsrSession.getDouble(Snsr.RES_SAMPLES) >= MIN_SAMPLES) {
                        promptForPhrase();
                        mShowPrompt = false;
                    }
                    return SnsrRC.OK;
                }
        );

        mShowPrompt = true;
        try {
            // FIXME : Crash -> java.lang.OutOfMemoryError: snsrRun(): task.chunk: Audio chunk input buffer overflow: limit is 10.000 seconds.
            // FIXME : Scenario -> Happens when User say briefly and don't say afterwards for another 10 seconds.
            session.run();
            // Optional: save enrollment context
            // session.save(SnsrDataFormat.RUNTIME, xyz);

        } catch (IOException e) {
            LoggerWrapper.e(TAG, e.toString());
        } catch (OutOfMemoryError e) {
            LoggerWrapper.e(TAG, e.getMessage());
        }
        // Optional but good practice. finalize() will (eventually) release.
        session.release();
        mAudio.release();
    }

    public SnsrRC onEvent(SnsrSession s, String key) {
        LoggerWrapper.i(TAG, "SNSR Event: " + key);
        switch (key) {
            case Snsr.FAIL_EVENT: {
                return onFail(s);
            }

            case Snsr.PASS_EVENT: {
                return onPass(s);
            }

            case Snsr.PROG_EVENT: {
                return onProgress(s);
            }

            case Snsr.PAUSE_EVENT: {
                return onPause(s);
            }

            case Snsr.RESUME_EVENT: {
                return onResume(s);
            }

            case Snsr.DONE_EVENT: {
                return onDone(s);
            }

            default: {
                LoggerWrapper.e(TAG, "Failed to implement handler for: " + key);
                return SnsrRC.OK;
            }
        }
    }

    private SnsrRC onFail(SnsrSession s) {
        LoggerWrapper.e(TAG, "onFail:reason: " + s.getString(Snsr.RES_REASON) + ", guidance:" + s.getString(Snsr.RES_GUIDANCE));

        switch (s.getString(Snsr.RES_REASON)) {
            case "phrase-quality": {
                mEnrollListener.onPoorPhraseQuality(s.getString(Snsr.RES_GUIDANCE));
            }
            break;

            default: {
                mEnrollListener.onAudioCaptureFailed(s.getString(Snsr.RES_REASON), s.getString(Snsr.RES_GUIDANCE));
            }
        }

        /* Save failed enrollment recording for debugging
         *  can get it with ADB */
        // Commented below code as it was causing OutOfMemoryError when too many failed recordings were recorded.
		/*if ( SAVE_ENROLLMENT_AUDIO ) {
			SnsrStream audio = s.getStream ( Snsr.AUDIO_STREAM );
			if ( audio != null ) {
				final String path = getPath ( String.format ( Locale.US, "fail-%02d.wav", mEnroll++ ) );
				SnsrStream   out  = SnsrStream.fromAudioFile ( path, SENSORY_FILE_MODE );
				try {
					out.copy ( audio );
				} catch ( IOException e ) {
					LoggerWrapper.e ( TAG, e.toString () );
				} finally {
					out.release ();
				}
			}
		}*/

        return SnsrRC.OK;
    }

    private SnsrRC onPass(SnsrSession s) {
        mEnrollListener.onGoodAudioCaptured();

        /* Save good enrollment recording for debugging
         *  Can be retrieved via ADB */
        if (SAVE_ENROLLMENT_AUDIO) {
            SnsrStream audio = s.getStream(Snsr.AUDIO_STREAM);
            if (audio != null) {
                final String path = getPath(String.format(Locale.US, "pass-%02d.wav", mEnroll++));
                SnsrStream out = SnsrStream.fromAudioFile(path, SENSORY_FILE_MODE);
                try {
                    out.copy(audio);
                } catch (IOException e) {
                    LoggerWrapper.e(TAG, e.toString());
                } finally {
                    out.release();
                }
            }
        }

        return SnsrRC.OK;
    }

    private SnsrRC onProgress(SnsrSession session) {
        if (Thread.interrupted()) return SnsrRC.INTERRUPTED;
        double progress = session.getDouble(Snsr.RES_PERCENT_DONE);
		/*String progressNotice = String.format ( Locale.US, "Adapting: %3.0f%% done.", p );
		if ( p >= 100 ) progressNotice = "Adapting complete!";*/
        /*mEnrollListener.log ( progressNotice );*/
        mEnrollListener.onAudioTrainingProgress((int) progress);
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
        /*mEnrollListener.notify ( UiState.ENROLLED );*/
        mEnrollListener.onAudioEnrolled();
        return SnsrRC.STOP;
    }

    private void promptForPhrase() {
        //String prompt = "SAY: " + mTriggerPhrase;
        //if (mContextIndex >= 0) prompt += " " + PromptContext[mContextIndex];
        /*mEnrollListener.log ( prompt );*/
        mEnrollListener.promptNextAudioCapture();
    }
}