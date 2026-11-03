package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

/**
 * Created by Satyaraj on 3/13/2018.
 */

public interface ClickListener {

    void onDeleteAlarmClick(Alarm alarm, int position);
    void onUpdateAlarmClick(Alarm alarm, int position);

}
