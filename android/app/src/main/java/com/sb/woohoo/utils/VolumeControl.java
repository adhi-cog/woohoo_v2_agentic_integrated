package com.sb.woohoo.utils;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.HashMap;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class VolumeControl {
    //  Constants.
    public static final int STEP_SINGLE = 1;
    private static final float DUCK_PERCENT = 0.1f;
    private static final Uri CONTENT_URI = android.provider.Settings.System.CONTENT_URI;
    private final int ERROR_OUT_OF_RANGE = 0;
    private final int STREAM_NONE = AudioManager.STREAM_MUSIC;
    // Components.
    private AudioManager audioManager;
    private VolumeControlCallbacks volumeControlCallbacks;
    private HashMap<Integer, Integer> lastSetVolume;
    private ContentResolver mContentResolver;
    private VolumeObserver mVolumeObserver;
    private HashMap<Integer, Boolean> mutedStreams;
    private boolean isDucked = false;

    // Variables.
    private int currentStream = AudioManager.STREAM_MUSIC;

    @Inject
    public VolumeControl(AudioManager audioManager, ContentResolver contentResolver) {
        LoggerWrapper.i(VolumeControl.class.getSimpleName(), "Constructor");
        this.audioManager = audioManager;
        this.mContentResolver = contentResolver;
        lastSetVolume = new HashMap<>();
        mutedStreams = new HashMap<>();

        lastSetVolume.put(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        lastSetVolume.put(AudioManager.STREAM_MUSIC, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        lastSetVolume.put(AudioManager.STREAM_SYSTEM, audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }

    /********************************************* {@code private} Methods ********************************************/

    private void raiseStreamVolume(int streamType, int step) {
        int targetVolume = getTargetVolume(streamType, step, true);
        alterStreamVolume(streamType, targetVolume, true);
        if (volumeControlCallbacks != null) {
            volumeControlCallbacks.onVolumeChanged(streamType, targetVolume);
        }
    }

    private void raiseCurrentVolume(int step) {
        int targetVolume = getTargetVolume(currentStream, step, true);
        alterStreamVolume(currentStream, targetVolume, true);
    }

    private void lowerStreamVolume(int streamType, int step) {
        int targetVolume = getTargetVolume(streamType, step, false);
        alterStreamVolume(streamType, targetVolume, true);
        if (volumeControlCallbacks != null) {
            volumeControlCallbacks.onVolumeChanged(streamType, targetVolume);
        }
    }

    private void lowerCurrentVolume(int step) {
        int targetVolume = getTargetVolume(currentStream, step, false);
        alterStreamVolume(currentStream, targetVolume, true);
        if (volumeControlCallbacks != null) {
            volumeControlCallbacks.onVolumeChanged(currentStream, targetVolume);
        }
    }

    private void muteVolume(int streamType, boolean state) {
        mutedStreams.put(streamType, state);
        if (audioManager != null) {
            if (state) {
                int previousVolume = audioManager.getStreamVolume(streamType);
                LoggerWrapper.i(VolumeControl.class.getSimpleName(),
                        "Saving volume from mute " + currentStream + " - volume : " + previousVolume);
                alterStreamVolume(streamType, 0, false);
            } else {
                if (lastSetVolume.containsKey(streamType)) {
                    alterStreamVolume(streamType, lastSetVolume.get(streamType), false);

                } else {
                    LoggerWrapper.e(VolumeControl.class.getSimpleName(),
                            "Volume can not be restored, invalid last set value!");
                }
            }
            if (volumeControlCallbacks != null) {
                volumeControlCallbacks.onVolumeMuted(streamType, state);
            }
        }
    }

    private int getTargetVolume(int streamType, int step, boolean increment) {
        final float SCALE_MAX = 10;
        float currentVolume = lastSetVolume.get(streamType);
        float maxStreamVolume = audioManager.getStreamMaxVolume(streamType);
        float stepCurrentStream = maxStreamVolume / SCALE_MAX;
        float targetVolume = increment
                ? (currentVolume + stepCurrentStream * step)
                : (currentVolume - stepCurrentStream * step);
        return Math.round(targetVolume > 0 ? targetVolume : 0);
    }

    private void alterStreamVolume(int streamType, int amount, boolean stackVolume) {
        if (audioManager != null) {
            audioManager.setStreamVolume(streamType, amount, AudioManager.ADJUST_SAME);
            if (volumeControlCallbacks != null) {
                volumeControlCallbacks.onVolumeChanged(streamType, amount);
            }
            if (stackVolume) {
                LoggerWrapper.i(
                        VolumeControl.class.getSimpleName(),
                        "Saving volume from alter stream " + currentStream + " - volume :  " + amount);
                lastSetVolume.put(streamType, amount);
            }
        }
    }

    private void alterCurrentStreamVolume(int amount) {
        audioManager.setStreamVolume(currentStream, amount, AudioManager.ADJUST_SAME);
        volumeControlCallbacks.onVolumeChanged(currentStream, amount);
        lastSetVolume.put(currentStream, amount);
    }

    /********************************************* {@code public} Methods *********************************************/

    public void setVolumeControlCallbacks(VolumeControlCallbacks volumeControlCallbacks) {
        this.volumeControlCallbacks = volumeControlCallbacks;
    }

    public void setCurrentActiveStream(MediaPlayer player, int streamType) {
        LoggerWrapper.i(VolumeControl.class.getSimpleName(), "Changing stream to with media player" + streamType);
        if (player != null) {
            player.setAudioStreamType(streamType);
        }
    }

    public void gainAudioFocus(int streamType) {
        LoggerWrapper.i(VolumeControl.class.getSimpleName(), "Changing stream to " + streamType);
        audioManager.requestAudioFocus(null, streamType,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    public void duckVolume(int audioStream) {
        if (!mutedStreams.containsKey(audioStream) || !mutedStreams.get(audioStream)) {
            int currentVolume = audioManager.getStreamVolume(audioStream);
            int currentStreamMaxVolume = audioManager.getStreamMaxVolume(audioStream);

            if (!isDucked) {
                lastSetVolume.put(audioStream, currentVolume);
                LoggerWrapper.i(VolumeControl.class.getSimpleName(),
                        "Saving volume from duck volume " + audioStream + " - volume : " + audioStream);

                int duckVolume = (int) (DUCK_PERCENT * currentStreamMaxVolume);
                alterStreamVolume(audioStream, duckVolume, false);
                isDucked = true;
            }
        }
    }

    public void restoreVolume(int audioStream) {
        if (!mutedStreams.containsKey(audioStream) || !mutedStreams.get(audioStream)) {
            alterStreamVolume(audioStream, lastSetVolume.get(audioStream), false);
            isDucked = false;
        }
    }

    public void raiseVolume(int step) {
        raiseCurrentVolume(step);
    }

    public void controlSystemVolume(IntentInfo intentInfo) {
        if (intentInfo.getAction() == Categories.Controls.Volume.VOLUME_MUTE) {
            muteVolume(AudioManager.STREAM_MUSIC, true);
            muteVolume(AudioManager.STREAM_NOTIFICATION, true);
            muteVolume(AudioManager.STREAM_SYSTEM, true);
        } else if (intentInfo.getAction() == Categories.Controls.Volume.VOLUME_UNMUTE) {
            muteVolume(AudioManager.STREAM_MUSIC, false);
            muteVolume(AudioManager.STREAM_NOTIFICATION, false);
            muteVolume(AudioManager.STREAM_SYSTEM, false);
        } else if (intentInfo.getAction() == Categories.Controls.Volume.VOLUME_SET) {
            if (intentInfo.getParameters().containsKey("value")) {
                String value = (String) intentInfo.getParameters().get("value");
                if (value != null) {
                    int val = Integer.parseInt(String.valueOf(value));
                    if (val < 11 && val > -1) {
                        alterStreamVolume(currentStream, val, true);
                    } else {
                        if (volumeControlCallbacks != null) {
                            volumeControlCallbacks.onVolumeError(
                                    ERROR_OUT_OF_RANGE,
                                    "Please select a volume between 0 and 10.");
                        }
                    }
                }
            }
        } else if (intentInfo.getAction() == Categories.Controls.Volume.VOLUME_UP) {
            if (intentInfo.getParameters().containsKey("percentage") && !intentInfo.getParameters().get("percentage").equals("")) {
                String value = (String) intentInfo.getParameters().get("percentage");
                if (value != null) {
                    value = value.replace("%", "");
                    int volume = Integer.parseInt(value);
                    volume = volume / 10;
                    setVolume(volume);
                }
            } else if (intentInfo.getParameters().containsKey("extremes") && !intentInfo.getParameters().get("extremes").equals("")) {
                String value = (String) intentInfo.getParameters().get("extremes");
                if (value != null) {
                    int volume = Integer.parseInt(value);
                    setVolume(volume);
                }
            } else if (intentInfo.getParameters().containsKey("value") && !intentInfo.getParameters().get("value").equals("")) {
                String value = (String) intentInfo.getParameters().get("value");
                if (value != null) {
                    raiseCurrentVolume(Integer.parseInt(String.valueOf(value)));
                }
            }
        } else if (intentInfo.getAction() == Categories.Controls.Volume.VOLUME_DOWN) {
            if (intentInfo.getParameters().containsKey("value")) {
                String value = (String) intentInfo.getParameters().get("value");
                if (value != null) {
                    lowerCurrentVolume(Integer.parseInt(String.valueOf(value)));
                }
            }
        }
    }

    public void muteCurrentStream(boolean muteState) {
        muteVolume(currentStream, muteState);
    }

    public int getVolume() {
        if (audioManager != null) {
            return audioManager.getStreamVolume(currentStream);
        }
        return 0;
    }

    public void setVolume(int volume) {
        alterStreamVolume(currentStream, volume, true);
    }

    public int getMaxVolume() {
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(currentStream);
        }
        return 10;
    }

    public void onVolumeButtonPressed() {
        lastSetVolume.put(currentStream, getVolume());
        if (volumeControlCallbacks != null) {
            volumeControlCallbacks.updateDrawer(getVolume());
        }
    }

    /**
     * <p>Sets the Volume Observer that observes the change in Volume by any event.</p>
     * <p>Once the audio stream is selected, then {@link VolumeObserver} will make callback to change of volume in the
     * specified stream</p>
     * <p>To change the stream of Volume Observer, proceed like :
     * <ol>
     * <li>Un-Register Volume Observer using method {@code unregisterVolumeObserver ()}.</li>
     * <li>Set the Volume observer using method {@code setVolumeObserver ( audioStreamType )}.</li>
     * <li>Register the Volume Observer using method {@code registerVolumeObserver}</li>
     * </ol>
     * </p>
     *
     * @param audioStreamType Can be any one of stream value from {@link AudioManager}.
     */
    public void setVolumeObserver(int audioStreamType) {
        // FIXME : For now, a new Handler is created. But change this statement with injecting Handler.
        Handler handler = new Handler();
        this.mVolumeObserver = new VolumeObserver(handler, audioStreamType);
    }

    public void registerVolumeObserver() {
        mContentResolver.registerContentObserver(CONTENT_URI, true, mVolumeObserver);
    }

    public void unregisterVolumeObserver() {
        mContentResolver.unregisterContentObserver(mVolumeObserver);
    }

    /*********************************************** {@link interface}s ***********************************************/

    public interface VolumeControlCallbacks {
        void onVolumeUpdate(int streamVolume, int maxVolume);

        void onVolumeChanged(int streamType, int streamVolume);

        void onVolumeMuted(int streamType, boolean streamMuteStatus);

        void onVolumeError(int error, String message);

        void onVolumeResponse(String response);

        void onStreamChange(MediaPlayer mediaPlayer, int streamType);

        void updateDrawer(int volume);
    }

    /**********************************************************************************************************/

    class VolumeObserver extends ContentObserver {
        private final int mAudioStreamType;
        private int mLastVolume;

        private VolumeObserver(@NonNull Handler handler, int audioStreamType) {
            super(handler);
            this.mAudioStreamType = audioStreamType;
            mLastVolume = audioManager.getStreamVolume(mAudioStreamType);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            int maxVolume = audioManager.getStreamMaxVolume(mAudioStreamType);
            int currentVolume = audioManager.getStreamVolume(mAudioStreamType);
            if (currentVolume != mLastVolume) {
                mLastVolume = currentVolume;
                if (volumeControlCallbacks != null) {
                    volumeControlCallbacks.onVolumeUpdate(currentVolume, maxVolume);
                }
            }
        }
    }
}
