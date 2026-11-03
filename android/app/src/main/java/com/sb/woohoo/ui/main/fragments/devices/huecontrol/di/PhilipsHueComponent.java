package com.sb.woohoo.ui.main.fragments.devices.huecontrol.di;

import com.sb.woohoo.ui.main.fragments.devices.huecontrol.PhilipsHueFragment;

import dagger.Component;

@Component(modules = PhilipsHueModule.class)
public interface PhilipsHueComponent {
    void injectPhilipsHueFragment(PhilipsHueFragment philipsHueFragment);
}
