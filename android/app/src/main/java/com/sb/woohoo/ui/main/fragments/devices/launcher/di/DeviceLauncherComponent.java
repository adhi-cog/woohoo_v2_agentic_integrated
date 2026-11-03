package com.sb.woohoo.ui.main.fragments.devices.launcher.di;

import com.sb.woohoo.ui.main.fragments.devices.launcher.DeviceLauncherFragment;

import dagger.Component;

@com.sb.woohoo.ui.main.fragments.devices.launcher.di.DeviceLauncherScope
@Component(modules = com.sb.woohoo.ui.main.fragments.devices.launcher.di.DeviceLauncherModule.class)
public interface DeviceLauncherComponent {
    void injectFragment(DeviceLauncherFragment deviceLauncherFragment);
}
