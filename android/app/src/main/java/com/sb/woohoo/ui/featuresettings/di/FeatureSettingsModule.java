package com.sb.woohoo.ui.featuresettings.di;

import com.sb.woohoo.ui.featuresettings.FeatureSettingsActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class FeatureSettingsModule {

    private FeatureSettingsActivity mActivity;

    public FeatureSettingsModule(FeatureSettingsActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    public FeatureSettingsActivity providesFeatureSettingsActivity() {
        return mActivity;
    }
}
