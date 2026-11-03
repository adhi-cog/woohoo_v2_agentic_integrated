package com.sb.woohoo.ui.main.di;

import android.media.AudioManager;

import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.helpers.HandlerHelper;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.VolumeControl;
import com.sb.woohoo.utils.others.CameraUtils;

import dagger.Module;
import dagger.Provides;

@Module
public class HardwareModule {

    @Provides
    @MainScope
    public CameraUtils providesCameraUtils(HandlerHelper manager) {
        return new CameraUtils(manager);
    }

    @Provides
    @MainScope
    public VolumeControl providesVolumeControl(AudioManager audioManager, MainActivity context) {
        return new VolumeControl(audioManager, context.getContentResolver());
    }
}
