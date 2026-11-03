package com.sb.woohoo.ui.main.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.AssetUtils;
import com.sb.woohoo.utils.sensory.PhraseSpot;
import com.sb.woohoo.utils.sensory.WakeWordDetect;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_MUTE_STATE;
import static com.sileria.android.Resource.getResources;

public class WakeWordHelper extends BaseHelper implements WakeWordDetect {
    private static final String TAG_FLOW = "ASR_FLOW";
    private static final long WAKEWORD_DEBOUNCE_MS = 800;
    private PhraseSpot phraseSpot;
    private WakeWordCallback wakeWordCallback;
    private AtomicBoolean isWakeWordActive;
    private final AtomicLong lastWakeWordMs = new AtomicLong(0L);
    private Handler handler = new Handler();
    private Context context;

    @Inject
    public WakeWordHelper(Context context) {
        File file = AssetUtils.getModelFile(context);
        if (file != null) {
            LoggerWrapper.i(WakeWordHelper.class.getSimpleName(), file.toString());
            phraseSpot = new PhraseSpot(file.toString(), this);
        }
        this.context = context;
        isWakeWordActive = new AtomicBoolean(false);
    }

    public void setListener(WakeWordCallback callback) {
        this.wakeWordCallback = callback;
    }

    public void onWakeWordChanged() {
        File file = AssetUtils.getModelFile(context);
        if (file != null) {
            LoggerWrapper.i(WakeWordHelper.class.getSimpleName(), file.toString());
            phraseSpot = new PhraseSpot(file.toString(), this);
        }

        isWakeWordActive = new AtomicBoolean(false);
    }

    @Override
    public void onWakeWordDetect() {
        long now = SystemClock.elapsedRealtime();
        long last = lastWakeWordMs.get();
        if (now - last < WAKEWORD_DEBOUNCE_MS) {
            LoggerWrapper.i(TAG_FLOW, "wakeword_ignored debounce");
            return;
        }
        lastWakeWordMs.set(now);
        LoggerWrapper.i(WakeWordHelper.class.getSimpleName(), "onWakeWordDetect");
        LoggerWrapper.i(TAG_FLOW, "wakeword_detected sensory");
        stopPhraseSpotRecognizer();
        wakeWordCallback.onWakeWordDetected();
    }

    @Override
    public void onWakeWordListening() {
    }

    @Override
    public void onWakeWordStopped() {
        stopPhraseSpotRecognizer();
        LoggerWrapper.i(WakeWordHelper.class.getSimpleName(), "Wake Word Stopped");
    }

    @Override
    public void onWakeWordError() {
        stopPhraseSpotRecognizer();
        LoggerWrapper.e(WakeWordHelper.class.getSimpleName(), "Wake Word Error");
    }

    private void startPhraseSpotRecognizer() {
        if (phraseSpot != null) {
            LoggerWrapper.d(WakeWordHelper.class.getSimpleName(), "WakeWordHelper:startPhraseSpotRecognizer:audioInstance.start()");
            handler.postDelayed(() -> phraseSpot.start(), 500);
            //Not sure why but getting crash in FirebaseCrash Analatics
            if (isWakeWordActive != null) {
                isWakeWordActive.compareAndSet(false, true);
            }
        } else  {
            LoggerWrapper.e(WakeWordHelper.class.getSimpleName(), "WakeWordHelper:startPhraseSpotRecognizer:audioInstance is null");
        }
    }

    public void stopPhraseSpotRecognizer() {
        if (phraseSpot != null) {
            LoggerWrapper.d(WakeWordHelper.class.getSimpleName(), "WakeWordHelper:stopPhraseSpotRecognizer");
            phraseSpot.stop();
        }
        if (isWakeWordActive != null) {
            isWakeWordActive.compareAndSet(true, false);
        }
    }

    public void runWakeWordRecognition() {
        // Start wake word recognition thread
        if (!WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
            if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.GBTB_DOMAIN_ID) ||
                    (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID))) {
                return;
            }

            //Not sure why but getting crash in FirebaseCrash Analatics
            if (isWakeWordActive != null && !isWakeWordActive.get()) {
                LoggerWrapper.d(WakeWordHelper.class.getSimpleName(), "Starting wake word thread");
                startPhraseSpotRecognizer();
            } else {
                LoggerWrapper.d(WakeWordHelper.class.getSimpleName(), "Wake-word thread already running!");
            }
        }
    }

    @Override
    public void onHelperCleanUp() {
        LoggerWrapper.d(WakeWordHelper.class.getSimpleName(), "onHelperCleanUp() called");
        super.onHelperCleanUp();
        stopPhraseSpotRecognizer();
        phraseSpot = null;
        wakeWordCallback = null;
        isWakeWordActive = null;
    }

    public interface WakeWordCallback {

        void onWakeWordDetected();
    }
}
