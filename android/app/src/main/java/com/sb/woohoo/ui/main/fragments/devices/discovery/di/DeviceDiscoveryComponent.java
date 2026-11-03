package com.sb.woohoo.ui.main.fragments.devices.discovery.di;

import com.sb.woohoo.ui.main.fragments.devices.discovery.DeviceDiscoveryFragment;

import dagger.Component;

@DeviceDiscoveryScope
@Component(modules = DeviceDiscoveryModule.class)
public interface DeviceDiscoveryComponent {

    void injectFragment(DeviceDiscoveryFragment deviceDiscoveryFragment);
}


