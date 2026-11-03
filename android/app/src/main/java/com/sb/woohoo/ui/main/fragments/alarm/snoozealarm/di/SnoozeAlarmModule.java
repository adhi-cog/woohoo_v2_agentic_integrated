package com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.AddAlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.SnoozeAlarmFragment;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SnoozeAlarmModule {

    private SnoozeAlarmFragment snoozeAlarmFragment;
    private List<Alarm> alarmList;

    public SnoozeAlarmModule(SnoozeAlarmFragment snoozeAlarmFragment) {
        this.snoozeAlarmFragment = snoozeAlarmFragment;
        this.alarmList = new ArrayList<>();
    }

    @Provides
    Fragment getFragment() {
        return snoozeAlarmFragment;
    }

    @Provides
    List<Alarm> getAlarmList() {
        return alarmList;
    }

    @Provides
    SnoozeAlarmFragment getAlarmFragment() {
        return snoozeAlarmFragment;
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(snoozeAlarmFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(snoozeAlarmFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
