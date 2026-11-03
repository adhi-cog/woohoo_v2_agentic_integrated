package com.sb.woohoo.ui.main.fragments.reminders.di;

import android.media.MediaPlayer;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.reminders.ReminderAlertFragment;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Created by Satyaraj on 3/13/2018.
 */
@Module
public class ReminderAlertModule {

    private ReminderAlertFragment reminderAlertFragment;

    public ReminderAlertModule(ReminderAlertFragment reminderAlertFragment) {
        this.reminderAlertFragment = reminderAlertFragment;
    }

    @Provides
    ReminderAlertFragment getReminderAlertFragment() {
        return reminderAlertFragment;
    }

    @Provides
    Fragment getFragment() {
        return reminderAlertFragment;
    }

    @Provides
    MediaPlayer getRingtone() {
        return MediaPlayer.create(reminderAlertFragment.getContext(), R.raw.what_friends_are_for);
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(reminderAlertFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(reminderAlertFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

}
