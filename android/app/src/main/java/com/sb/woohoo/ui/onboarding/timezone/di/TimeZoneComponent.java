package com.sb.woohoo.ui.onboarding.timezone.di;

import com.sb.woohoo.ui.onboarding.timezone.TimeZoneSelectionFragment;

import dagger.Component;

/**
 * Created by admin on 1/30/18.
 */

@Component(modules = TimeZoneModule.class)
@TimeZoneScope
public interface TimeZoneComponent {

    void injectTimeZoneFragment(TimeZoneSelectionFragment timeZoneSelectionFragment);
}
