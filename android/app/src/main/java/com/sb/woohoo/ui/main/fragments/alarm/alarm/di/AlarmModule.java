package com.sb.woohoo.ui.main.fragments.alarm.alarm.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class AlarmModule {

    private AlarmFragment alarmFragment;
    private List<Alarm> alarmList;

    public AlarmModule(AlarmFragment alarmFragment) {
        this.alarmFragment = alarmFragment;
        this.alarmList = new ArrayList<>();
    }

    @Provides
    Fragment getFragment() {
        return alarmFragment;
    }

    @Provides
    List<Alarm> getAlarmList() {
        return alarmList;
    }

    @Provides
    AlarmFragment getAlarmFragment() {
        return alarmFragment;
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(alarmFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(alarmFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
