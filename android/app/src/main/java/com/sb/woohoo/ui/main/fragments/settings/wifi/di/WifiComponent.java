package com.sb.woohoo.ui.main.fragments.settings.wifi.di;

import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;

import dagger.Component;

/**
 * Created by Shrey on 27/02/18.
 */

@Component(modules = WifiModule.class)
@WifiScope
public interface WifiComponent {

    void injectWifiSelectionFragment(WifiFragment wifiFragment);
}
