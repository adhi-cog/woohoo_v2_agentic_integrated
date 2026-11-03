package com.sb.woohoo.custom;

import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

import com.sb.woohoo.utils.VolumeControl;

import androidx.annotation.NonNull;

public class VolumeContentObserver extends ContentObserver {


    private final VolumeControl.VolumeControlCallbacks mListener;
    private final AudioManager mAudioManager;
    private final int mAudioStreamType;
    private int mLastVolume;

    public VolumeContentObserver(@NonNull Handler handler,
                                 @NonNull AudioManager mAudioManager,
                                 int audioStreamType,
                                 @NonNull VolumeControl.VolumeControlCallbacks listener) {
        super(handler);
        this.mAudioManager = mAudioManager;
        this.mAudioStreamType = audioStreamType;
        this.mListener = listener;
        mLastVolume = mAudioManager.getStreamVolume(mAudioStreamType);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (mAudioManager != null && mListener != null) {
            int maxVolume = mAudioManager.getStreamMaxVolume(mAudioStreamType);
            int currentVolume = mAudioManager.getStreamVolume(mAudioStreamType);
            if (currentVolume != mLastVolume) {
                mLastVolume = currentVolume;
                mListener.onVolumeUpdate(currentVolume, maxVolume);
            }
        }
    }
}
