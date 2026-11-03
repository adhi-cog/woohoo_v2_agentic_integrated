package com.sb.woohoo.ui.main.fragments.settings.thingstosay.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.ThingsToSayAdapter;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.ThingsToSayFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ThingsToSayModule {

    private ThingsToSayFragment thingsToSayFragment;

    public ThingsToSayModule(ThingsToSayFragment thingsToSayFragment) {
        this.thingsToSayFragment = thingsToSayFragment;
    }

    @Provides
    ThingsToSayFragment getThingsToSayFragment() {
        return thingsToSayFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(thingsToSayFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public ThingsToSayAdapter getThingsToSayAdapter() {
        return new ThingsToSayAdapter();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(thingsToSayFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
