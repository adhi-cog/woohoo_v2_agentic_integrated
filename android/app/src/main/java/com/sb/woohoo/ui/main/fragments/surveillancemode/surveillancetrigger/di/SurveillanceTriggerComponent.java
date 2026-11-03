package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.di;

import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.SurveillanceTriggerFragment;

import dagger.Component;

@Component(modules = SurveillanceTriggerModule.class)
public interface SurveillanceTriggerComponent {
    void injectFragment(SurveillanceTriggerFragment surveillanceTriggerFragment);
}
