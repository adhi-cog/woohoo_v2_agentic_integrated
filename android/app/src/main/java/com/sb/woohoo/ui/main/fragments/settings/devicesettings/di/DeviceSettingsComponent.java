package com.sb.woohoo.ui.main.fragments.settings.devicesettings.di;

import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment;

import dagger.Component;

@DeviceSettingsScope
@Component(modules = DeviceSettingsModule.class)
public interface DeviceSettingsComponent {
    void injectDeviceName(DeviceSettingsFragment deviceSettingsFragment);
}
