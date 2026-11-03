package com.sb.woohoo.ui.main.fragments.devices.launcher.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.devices.launcher.DeviceLauncherFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class DeviceLauncherModule {
    private DeviceLauncherFragment mFragment;

    public DeviceLauncherModule(DeviceLauncherFragment deviceLauncherFragment) {
        this.mFragment = deviceLauncherFragment;
    }

    @Provides
    DeviceLauncherFragment getDeviceLauncherFragment() {
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
