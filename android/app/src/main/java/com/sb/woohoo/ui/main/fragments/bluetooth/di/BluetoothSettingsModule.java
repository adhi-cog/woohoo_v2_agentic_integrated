package com.sb.woohoo.ui.main.fragments.bluetooth.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.bluetooth.BluetoothUnSignedSettingsFragment;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.BluetoothSignedSettingsFragment;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class BluetoothSettingsModule {

    private BluetoothUnSignedSettingsFragment bluetoothUnSignedSettingsFragment;


    public BluetoothSettingsModule(BluetoothUnSignedSettingsFragment bluetoothUnSignedSettingsFragment) {
         this.bluetoothUnSignedSettingsFragment = bluetoothUnSignedSettingsFragment;

    }

    @Provides
    BluetoothUnSignedSettingsFragment provideBluetoothUnSignedSettingsFragment() {
        return bluetoothUnSignedSettingsFragment;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    WooHooApiCall provideWooHooApiCall(){
        return WooHooApp.get(bluetoothUnSignedSettingsFragment.requireActivity()).getWooHooApiCall();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(bluetoothUnSignedSettingsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
