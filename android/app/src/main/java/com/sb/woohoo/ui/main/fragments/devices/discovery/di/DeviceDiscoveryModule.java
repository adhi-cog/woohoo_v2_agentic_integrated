package com.sb.woohoo.ui.main.fragments.devices.discovery.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.devices.discovery.DeviceDiscoveryFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class DeviceDiscoveryModule {

    private DeviceDiscoveryFragment mFragment;

    public DeviceDiscoveryModule(DeviceDiscoveryFragment deviceDiscoveryFragment) {
        this.mFragment = deviceDiscoveryFragment;
    }

    @Provides
    DeviceDiscoveryFragment getDeviceDiscoveryFragment() {
        return mFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
