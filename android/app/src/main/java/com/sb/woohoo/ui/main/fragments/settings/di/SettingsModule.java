package com.sb.woohoo.ui.main.fragments.settings.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.settings.Setting;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.utils.others.SIMUtils;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link SettingsFragment}.
 */
@Module
public class SettingsModule {
    private SettingsFragment mFragment;
    private List<Setting> settingList;

    public SettingsModule(SettingsFragment mFragment) {
        this.mFragment = mFragment;
        this.settingList = new ArrayList<>();
    }

    @Provides
    SharedPrefsHelper providesSharedPrefsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }

    @Provides
    SIMUtils providesSimUtils() {
        return new SIMUtils(mFragment.getParentActivity());
    }

    @Provides
    SettingsFragment providesSettingsFragment() {
        return mFragment;
    }

    @Provides
    WooHooApiCall getWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    ConnectivityHelper providesConnectivityHelper() {
        return new ConnectivityHelper();
    }

    @Provides
    List<Setting> provideList() {
        return settingList;
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}