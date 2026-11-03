package com.sb.woohoo.ui.main.fragments.settings.sim.di;

import com.sb.woohoo.ui.main.fragments.settings.sim.SimSettingsFragment;

import dagger.Component;

@SimSettingsScope
@Component(modules = SimSettingsModule.class)
public interface SimSettingsComponent {
    void injectSimSettings(SimSettingsFragment fragment);
}
