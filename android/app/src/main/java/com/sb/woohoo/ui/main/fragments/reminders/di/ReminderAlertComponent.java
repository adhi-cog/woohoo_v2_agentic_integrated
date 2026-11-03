package com.sb.woohoo.ui.main.fragments.reminders.di;

import com.sb.woohoo.ui.main.fragments.reminders.ReminderAlertFragment;

import dagger.Component;

/**
 * Created by Satyaraj on 3/13/2018.
 */
@Component(modules = ReminderAlertModule.class)
public interface ReminderAlertComponent {
    void injectReminderAlertFragment(ReminderAlertFragment reminderAlertFragment);
}
