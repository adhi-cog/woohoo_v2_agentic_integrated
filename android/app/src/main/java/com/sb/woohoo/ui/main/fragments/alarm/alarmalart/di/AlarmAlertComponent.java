package com.sb.woohoo.ui.main.fragments.alarm.alarmalart.di;

import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.AlarmAlertFragment;

import dagger.Component;

@Component(modules = AlarmAlertModule.class)
public interface AlarmAlertComponent {
    void injectAlarmAlertFragment(AlarmAlertFragment alarmAlertFragment);
}
