package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.PinViewFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class PinViewModule {

    private PinViewFragment pinViewFragment;

    public PinViewModule(PinViewFragment pinViewFragment) {
        this.pinViewFragment = pinViewFragment;
    }

    @Provides
    PinViewFragment providePinViewFragment() {
        return pinViewFragment;
    }

    @Provides
    WooHooApiCall provideWooHooApiCall() {
        return WooHooApp.get(pinViewFragment.getParentActivity()).getWooHooApiCall();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(pinViewFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
