package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.di;

import android.content.Context;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.SurveillanceInitialFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Retrofit;

@Module
public class SurveillanceInitialModule {

    private SurveillanceInitialFragment surveillanceInitialFragment;

    public SurveillanceInitialModule(SurveillanceInitialFragment surveillanceInitialFragment) {
        this.surveillanceInitialFragment = surveillanceInitialFragment;
    }

    @Provides
    Gson getGson() {
        return WooHooApp.get(surveillanceInitialFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    Retrofit getRetrofit() {
        return WooHooApp.get(surveillanceInitialFragment.getParentActivity()).getWooHooAppComponent().getRetrofit();
    }

    @Provides
    SurveillanceInitialFragment getSurveillanceInitialFragment() {
        return surveillanceInitialFragment;
    }

    @Provides
    Context getContext() {
        return surveillanceInitialFragment.getContext();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(surveillanceInitialFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
