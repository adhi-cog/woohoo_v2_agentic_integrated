package com.sb.woohoo.ui.main.fragments.alarm.alarm.di;

import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmFragment;

import dagger.Component;

@Component(modules = AlarmModule.class)
public interface AlarmComponent {
    void injectAlarmFragment(AlarmFragment alarmFragment);
}
