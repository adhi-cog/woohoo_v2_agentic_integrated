package com.sb.woohoo.ui.main.helpers;

import android.content.Context;
import android.media.SoundPool;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;


public class AudioHelper extends BaseHelper {

    private final float SOUND_POOL_LEFT_VOLUME = 1.0f;
    private final float SOUND_POOL_RIGHT_VOLUME = 1.0f;
    private final float SOUND_POOL_RATE = 1.0f;
    private final int SOUND_POOL_PRIORITY = 0;
    private final int SOUND_POOL_LOOP = 0;
    private SoundPool soundPool;
    private int playingResource;
    private Context context;

    @Inject
    public AudioHelper(SoundPool soundPool, Context context) {
        LoggerWrapper.i(AudioHelper.class.getSimpleName(), "Constructor");
        this.soundPool = soundPool;
        this.context = context;

        SoundPool.OnLoadCompleteListener onLoadCompleteListener
                = (soundPool1, sampleId, status)
                -> playingResource
                = soundPool1.play(sampleId, SOUND_POOL_LEFT_VOLUME,
                SOUND_POOL_RIGHT_VOLUME, SOUND_POOL_PRIORITY, SOUND_POOL_LOOP,
                SOUND_POOL_RATE);

        soundPool.setOnLoadCompleteListener(onLoadCompleteListener);
    }

    public void playAudioNotificationResource(int resourceId) {
        if (MethodUtils.isResource(context, resourceId)) {
            soundPool.load(context, resourceId, 1);
        }
    }

    public void stopSoundPool() {
        if (soundPool != null) {
            soundPool.stop(playingResource);
        }
    }

    @Override
    public void onHelperCleanUp() {
        if (soundPool != null) {
            soundPool.stop(playingResource);
            soundPool.release();
        }
        soundPool = null;
    }
}
