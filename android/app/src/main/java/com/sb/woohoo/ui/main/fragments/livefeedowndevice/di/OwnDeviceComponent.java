package com.sb.woohoo.ui.main.fragments.livefeedowndevice.di;

import com.sb.woohoo.ui.main.fragments.livefeedowndevice.LiveFeedOwnDeviceFragment;

import dagger.Component;

@Component(modules = OwnDeviceModule.class)
public interface OwnDeviceComponent {
    void injectOwnDeviceFragment(LiveFeedOwnDeviceFragment ownDeviceFragment);
}

