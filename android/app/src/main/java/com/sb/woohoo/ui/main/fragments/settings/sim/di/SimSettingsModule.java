package com.sb.woohoo.ui.main.fragments.settings.sim.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.settings.sim.SimSettingsFragment;
import com.sb.woohoo.utils.others.SIMUtils;

import dagger.Module;
import dagger.Provides;

@Module
public class SimSettingsModule {

    private SimSettingsFragment simSettingsFragment;

    public SimSettingsModule(SimSettingsFragment simSettingsFragment) {
        this.simSettingsFragment = simSettingsFragment;
    }

    @Provides
    SimSettingsFragment providesSettingsFragment() {
        return simSettingsFragment;
    }

    @Provides
    SIMUtils providesSimUtils() {
        return new SIMUtils(simSettingsFragment.getParentActivity());
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(simSettingsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
