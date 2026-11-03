package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.di;

import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.SurveillanceInitialFragment;

import dagger.Component;

@Component(modules = SurveillanceInitialModule.class)
public interface SurveillanceInitialComponent {
    void injectInitialSurveillanceFragment(SurveillanceInitialFragment surveillanceInitialFragment);
}
