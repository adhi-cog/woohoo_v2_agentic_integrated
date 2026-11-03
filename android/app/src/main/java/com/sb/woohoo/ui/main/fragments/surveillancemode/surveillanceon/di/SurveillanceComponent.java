package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.di;

import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;

import dagger.Component;

@Component(modules = SurveillanceModule.class)
public interface SurveillanceComponent {

    void injectPrivateLiveFeedComponent(SurveillanceFragment surveillanceFragment);
}
