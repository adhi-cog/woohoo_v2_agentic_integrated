package com.sb.woohoo.ui.main.fragments.timer.alert.di;

import com.sb.woohoo.ui.main.fragments.timer.alert.TimerAlertFragment;

import dagger.Component;

@Component(modules = TimerAlertModule.class)
@TimerAlertScope
public interface TimerAlertComponent {
    void injectTimerAlertFragment(TimerAlertFragment fragment);
}
