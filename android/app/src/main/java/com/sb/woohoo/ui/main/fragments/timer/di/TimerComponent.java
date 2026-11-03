package com.sb.woohoo.ui.main.fragments.timer.di;

import com.sb.woohoo.ui.main.fragments.timer.TimerFragment;

import dagger.Component;

@Component(modules = TimerModule.class)
@TimerScope
public interface TimerComponent {
    void injectTimerFragment(TimerFragment fragment);
}
