package com.sb.woohoo.ui.main.fragments.bluetoothSigned.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.BluetoothSignedSettingsFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class BluetoothSignedSettingsModule {

    private BluetoothSignedSettingsFragment bluetoothSignedSettingsFragment;


    public BluetoothSignedSettingsModule(BluetoothSignedSettingsFragment bluetoothSignedSettingsFragment) {
         this.bluetoothSignedSettingsFragment = bluetoothSignedSettingsFragment;

    }

    @Provides
    BluetoothSignedSettingsFragment provideBluetoothSignedSettingsFragment() {
        return bluetoothSignedSettingsFragment;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    WooHooApiCall provideWooHooApiCall(){
        return WooHooApp.get(bluetoothSignedSettingsFragment.requireActivity()).getWooHooApiCall();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(bluetoothSignedSettingsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
