package com.sb.woohoo.ui.main.fragments.alarm.alarmalart.di;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.AlarmAlertFragment;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class AlarmAlertModule {

    private AlarmAlertFragment alarmAlertFragment;

    public AlarmAlertModule(AlarmAlertFragment alarmAlertFragment) {
        this.alarmAlertFragment = alarmAlertFragment;
    }

    @Provides
    AlarmAlertFragment getAlarmAlertFragment() {
        return alarmAlertFragment;
    }

    @Provides
    Fragment getFragment() {
        return alarmAlertFragment;
    }

//    @Provides
//    MediaPlayer getRingtone(){
//        return MediaPlayer.create(alarmAlertFragment.getContext(), R.raw.alert_ringtone);
//    }

    @Provides
    MediaPlayerUtil getMediaPlayerUtil() {
        return new MediaPlayerUtil(alarmAlertFragment.getContext(), R.raw.alert_ringtone);
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(alarmAlertFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(alarmAlertFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
