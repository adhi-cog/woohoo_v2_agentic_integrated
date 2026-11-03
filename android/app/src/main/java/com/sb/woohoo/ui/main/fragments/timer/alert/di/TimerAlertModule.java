package com.sb.woohoo.ui.main.fragments.timer.alert.di;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.timer.alert.TimerAlertFragment;
import com.sb.woohoo.ui.main.fragments.timer.alert.TimerAlertPresenter;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerAlertModule {
    private TimerAlertFragment timerAlertFragment;

    public TimerAlertModule(TimerAlertFragment timerAlertFragment) {
        this.timerAlertFragment = timerAlertFragment;
    }

    @Provides
    public TimerAlertPresenter getTimerAlertPresenter() {
        return new TimerAlertPresenter(timerAlertFragment);
    }

    @Provides
    MediaPlayerUtil getMediaPlayerUtil() {
        return new MediaPlayerUtil(timerAlertFragment.getContext(), R.raw.alert_ringtone);
    }
}