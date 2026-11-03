package com.sb.woohoo.ui.main.fragments.timer.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.timer.TimerFragment;
import com.sb.woohoo.ui.main.fragments.timer.TimerPresenter;
import com.sb.woohoo.ui.main.helpers.TimerHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerModule {
    private TimerFragment timerFragment;

    public TimerModule(TimerFragment timerFragment) {
        this.timerFragment = timerFragment;
    }

    @Provides
    public TimerPresenter getTimerPresenter() {
        return new TimerPresenter(timerFragment);
    }

    @Provides
    public TimerHelper getTimerHelper() {
        return new TimerHelper(timerFragment.getParentActivity());
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(timerFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
