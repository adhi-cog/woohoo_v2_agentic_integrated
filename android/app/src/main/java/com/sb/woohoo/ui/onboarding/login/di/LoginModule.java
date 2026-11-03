package com.sb.woohoo.ui.onboarding.login.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.ui.onboarding.login.LoginFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Created by admin on 2/20/18.
 */

@Module
public class LoginModule {

    private LoginFragment loginFragment;

    public LoginModule(LoginFragment loginFragment) {
        this.loginFragment = loginFragment;
    }

    @Provides
    WooHooApiCall getWooHooApiCall() {
        return WooHooApp.get(loginFragment.getParentActivity()).getWoohooApiNoAuth();
    }

    @Provides
    LoginFragment loginFragment() {
        return loginFragment;
    }

    @Provides
    public Gson getGson() {
        return WooHooApp.get(loginFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    WifiFragment getWifiFragment() {
        return loginFragment.getParentActivity().getWifiSelectionFragment();
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
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(loginFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
