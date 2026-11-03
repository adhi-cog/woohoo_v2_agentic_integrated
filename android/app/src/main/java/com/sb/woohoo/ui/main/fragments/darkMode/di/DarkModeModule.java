package com.sb.woohoo.ui.main.fragments.darkMode.di;

import android.content.Context;
import android.media.AudioManager;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.darkMode.DarkModeFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.utils.CameraCapturerCompat;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

@Module
public class DarkModeModule {
    DarkModeFragment surveillanceFragment;

    public DarkModeModule(DarkModeFragment surveillanceFragment) {
        this.surveillanceFragment = surveillanceFragment;
    }

    @Provides
    public AudioManager getAudioManager() {
        return (AudioManager) surveillanceFragment.getParentActivity().getSystemService(Context.AUDIO_SERVICE);
    }


    @Inject
    CameraCapturerCompat getCameraSource() {
        return WooHooApp.get(surveillanceFragment.getParentActivity()).getWooHooAppComponent().getCamerSource();
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(surveillanceFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(surveillanceFragment.getParentActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    DarkModeFragment getDarkModeFragment() {
        return surveillanceFragment;
    }

    @Provides
    Context getContext() {
        return surveillanceFragment.getContext();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(surveillanceFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
