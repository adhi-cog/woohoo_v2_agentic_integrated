package com.sb.woohoo.custom;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;

import com.sb.woohoo.utils.VolumeControl;

import androidx.annotation.NonNull;

public class VolumeObserver {

    private Context mContext;
    private AudioManager mAudioManager;
    private VolumeContentObserver mVolumeContentObserver;

    public VolumeObserver(@NonNull Context mContext) {
        this.mContext = mContext;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void register(int audioStreamType, @NonNull VolumeControl.VolumeControlCallbacks listener) {

        Handler handler = new Handler();

        mVolumeContentObserver = new VolumeContentObserver(handler, mAudioManager, audioStreamType, listener);

        mContext.getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI,
                true,
                mVolumeContentObserver);
    }

    public void unregister() {
        if (mVolumeContentObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mVolumeContentObserver);
            mVolumeContentObserver = null;
        }
    }

}
