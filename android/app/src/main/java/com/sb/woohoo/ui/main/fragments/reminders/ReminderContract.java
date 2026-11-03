package com.sb.woohoo.ui.main.fragments.reminders;

import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Satyaraj on 2/26/2018.
 */

public interface ReminderContract {

    interface View {
        void allReminders(List<Reminder> listItems);

        void scrollList(int position);

        void respondVoice(String response);

    }

    interface Action {

        void addReminder(Reminder newReminder, int reminderAction);

        void showReminders(int reminderAction);

        void setAlarm(Reminder newReminder);

        void deleteReminders();

        void cancelAlarm(int reminderID);

        void deleteReminder(Reminder reminder);

        void editReminder(Reminder reminder, HashMap<String, String> parameters);

    }
}
