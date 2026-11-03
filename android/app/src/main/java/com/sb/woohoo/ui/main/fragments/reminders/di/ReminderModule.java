package com.sb.woohoo.ui.main.fragments.reminders.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.reminders.ReminderFragment;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Created by Satyaraj on 3/8/2018.
 */

@Module
public class ReminderModule {

    private ReminderFragment reminderFragment;
    private List<Reminder> remindersList;

    public ReminderModule(ReminderFragment reminderFragment) {
        this.reminderFragment = reminderFragment;
        this.remindersList = new ArrayList<>();
    }

    @Provides
    List<Reminder> getRemindersList() {
        return remindersList;
    }

    @Provides
    ReminderFragment getReminderFragment() {
        return reminderFragment;
    }

    @Provides
    Fragment getFragment() {
        return reminderFragment;
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(reminderFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(reminderFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
