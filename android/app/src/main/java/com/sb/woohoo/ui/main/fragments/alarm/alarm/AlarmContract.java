package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

public interface AlarmContract {

    interface View {
        void displayAlarms();

        void alarmAdded();

        void alarmDeleted();

        void alarmsDeleted();

        void alarmUpdated();
    }

    interface Action {
        void addAlarm(int action, Alarm alarm);

        void showAlarm(int action);

        void deleteAlarm(int action, int index);

        void deleteAll();
    }
}
