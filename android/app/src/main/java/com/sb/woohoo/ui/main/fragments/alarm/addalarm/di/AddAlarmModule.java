package com.sb.woohoo.ui.main.fragments.alarm.addalarm.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.alarm.addalarm.AddAlarmFragment;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class AddAlarmModule {

    private AddAlarmFragment addAlarmFragment;
    private List<Alarm> alarmList;

    public AddAlarmModule(AddAlarmFragment addAlarmFragment) {
        this.addAlarmFragment = addAlarmFragment;
        this.alarmList = new ArrayList<>();
    }

    @Provides
    Fragment getFragment() {
        return addAlarmFragment;
    }

    @Provides
    List<Alarm> getAlarmList() {
        return alarmList;
    }

    @Provides
    AddAlarmFragment getAlarmFragment() {
        return addAlarmFragment;
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(addAlarmFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(addAlarmFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

}
