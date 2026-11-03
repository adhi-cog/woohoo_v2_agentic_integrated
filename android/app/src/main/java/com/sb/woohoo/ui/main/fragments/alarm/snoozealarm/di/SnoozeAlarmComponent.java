package com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.di;

import com.sb.woohoo.ui.main.fragments.alarm.addalarm.AddAlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.SnoozeAlarmFragment;

import dagger.Component;

@Component(modules = SnoozeAlarmModule.class)
public interface SnoozeAlarmComponent {
    void injectSnoozeAlarmFragment(SnoozeAlarmFragment snoozeAlarmFragment);

}
