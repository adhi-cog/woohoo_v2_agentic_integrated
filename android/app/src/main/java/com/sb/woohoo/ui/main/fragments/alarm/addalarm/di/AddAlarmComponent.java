package com.sb.woohoo.ui.main.fragments.alarm.addalarm.di;

import com.sb.woohoo.ui.main.fragments.alarm.addalarm.AddAlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.di.AlarmModule;

import dagger.Component;

@Component(modules = AddAlarmModule.class)
public interface AddAlarmComponent {
    void injectAddAlarmFragment(AddAlarmFragment addAlarmFragment);
}
