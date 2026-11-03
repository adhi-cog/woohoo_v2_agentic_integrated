package com.sb.woohoo.ui.main.fragments.settings.di;

import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;

import dagger.Component;

/**
 * {@link Component} for {@link SettingsFragment}.
 */
@SettingsScope
@Component(modules = SettingsModule.class)
public interface SettingsComponent {
    void injectSettingsFragment(SettingsFragment fragment);
}