package com.sb.woohoo.ui.main.fragments.darkMode.di;

import com.sb.woohoo.ui.main.fragments.darkMode.DarkModeFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;

import dagger.Component;

@Component(modules = DarkModeModule.class)
public interface DarkModeComponent {

    void injectPrivateLiveFeedComponent(DarkModeFragment surveillanceFragment);
}
