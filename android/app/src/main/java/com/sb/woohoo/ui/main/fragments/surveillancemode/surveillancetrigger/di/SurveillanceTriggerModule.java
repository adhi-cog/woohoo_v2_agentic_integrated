package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.di;

import android.content.Context;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.SurveillanceTriggerFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class SurveillanceTriggerModule {
    private SurveillanceTriggerFragment surveillanceTriggerFragment;

    public SurveillanceTriggerModule(SurveillanceTriggerFragment surveillanceTriggerFragment) {
        this.surveillanceTriggerFragment = surveillanceTriggerFragment;
    }

    @Provides
    SurveillanceTriggerFragment provideSurveillanceTriggerFragment() {
        return surveillanceTriggerFragment;
    }

    @Provides
    WooHooApiCall provideWooHooApiCall() {
        return WooHooApp.get(surveillanceTriggerFragment.getParentActivity()).getWooHooApiCall();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    Context getContext() {
        return surveillanceTriggerFragment.getContext();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(surveillanceTriggerFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
