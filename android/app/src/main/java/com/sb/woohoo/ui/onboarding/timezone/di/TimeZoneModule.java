package com.sb.woohoo.ui.onboarding.timezone.di;

import com.sb.woohoo.ui.onboarding.timezone.TimeZoneAdapter;
import com.sb.woohoo.ui.onboarding.timezone.TimeZonePresenter;
import com.sb.woohoo.ui.onboarding.timezone.TimeZoneSelectionFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by admin on 1/30/18.
 */

@Module
public class TimeZoneModule {
    private TimeZoneSelectionFragment timeZoneSelectionFragment;

    public TimeZoneModule(TimeZoneSelectionFragment timeZoneSelectionFragment) {
        this.timeZoneSelectionFragment = timeZoneSelectionFragment;
    }

    @Provides
    public TimeZoneSelectionFragment timeZoneSelectionFragment() {
        return timeZoneSelectionFragment;
    }

    @Provides
    public TimeZonePresenter getTimeZonePresenter() {
        return new TimeZonePresenter(timeZoneSelectionFragment);
    }

    @Provides
    public TimeZoneAdapter getTimeZoneAdapter() {
        return new TimeZoneAdapter(timeZoneSelectionFragment);
    }
}
