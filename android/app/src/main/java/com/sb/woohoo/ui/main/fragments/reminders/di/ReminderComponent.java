package com.sb.woohoo.ui.main.fragments.reminders.di;

import com.sb.woohoo.ui.main.fragments.reminders.ReminderFragment;

import dagger.Component;

/**
 * Created by Satyaraj on 3/8/2018.
 */

@Component(modules = ReminderModule.class)
public interface ReminderComponent {

    void injectReminderFragment(ReminderFragment reminderFragment);

}
