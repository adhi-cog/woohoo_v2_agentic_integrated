package com.sb.woohoo.ui.main.fragments.interel.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.interel.InterelUtilityFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class InterelUtilityModule {

    private InterelUtilityFragment interelUtilityFragment;

    public InterelUtilityModule(InterelUtilityFragment interelUtilityFragment) {
        this.interelUtilityFragment = interelUtilityFragment;
    }

    @Provides
    InterelUtilityFragment provideInterelUtilityFragment() {
        return interelUtilityFragment;
    }

    @Provides
    Gson provideGson() {
        return WooHooApp.get(interelUtilityFragment.requireActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    WooHooApiCall provideWooHooApiCall() {
        return WooHooApp.get(interelUtilityFragment.requireActivity()).getWooHooApiCall();
    }

    @Provides
    SharedPrefsHelper provideSharedPrefHelper() {
        return WooHooApp.get(interelUtilityFragment.requireActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(interelUtilityFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
