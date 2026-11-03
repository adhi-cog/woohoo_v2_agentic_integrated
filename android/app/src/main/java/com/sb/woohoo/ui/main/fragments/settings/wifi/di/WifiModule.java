package com.sb.woohoo.ui.main.fragments.settings.wifi.di;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Shrey on 27/02/18.
 */

@Module
public class WifiModule {

    private Context context;
    private LayoutInflater layoutInflater;
    private WifiFragment wifiFragment;

    public WifiModule(LayoutInflater layoutInflater, Context context, WifiFragment wifiFragment) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.wifiFragment = wifiFragment;
    }

    @Provides
    public WifiManager getWifiManager() {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    WifiFragment getWifiFragment() {
        return wifiFragment;
    }

    @Provides
    public WifiPresenter getWifiSelectionPresenter(WifiManager wifiManager) {
        return new WifiPresenter(wifiManager, wifiFragment);
    }

    @Provides
    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(wifiFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
