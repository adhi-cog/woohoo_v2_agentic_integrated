package com.sb.woohoo.ui.featuresettings.di;

import com.sb.woohoo.ui.featuresettings.FeatureSettingsActivity;

import dagger.Component;

@FeatureSettingsScope
@Component(modules = FeatureSettingsModule.class)
public interface FeatureSettingsComponent {
    void injectFeatureSettings(FeatureSettingsActivity activity);
}
