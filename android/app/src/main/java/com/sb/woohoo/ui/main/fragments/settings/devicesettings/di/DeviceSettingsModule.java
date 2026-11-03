package com.sb.woohoo.ui.main.fragments.settings.devicesettings.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSetting;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class DeviceSettingsModule {

    private DeviceSettingsFragment deviceSettingsFragment;
    private List<DeviceSetting> deviceSettingsList;

    public DeviceSettingsModule(DeviceSettingsFragment deviceSettingsFragment) {
        this.deviceSettingsFragment = deviceSettingsFragment;
        this.deviceSettingsList = new ArrayList<>();
    }

    @Provides
    DeviceSettingsFragment getDeviceSettingsFragment() {
        return deviceSettingsFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(deviceSettingsFragment.getActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public Fragment getFragment() {
        return deviceSettingsFragment;
    }

    @Provides
    List<DeviceSetting> getDeviceSettingsList() {
        return deviceSettingsList;
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(deviceSettingsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
