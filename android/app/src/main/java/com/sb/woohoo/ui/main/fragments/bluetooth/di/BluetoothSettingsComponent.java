package com.sb.woohoo.ui.main.fragments.bluetooth.di;

import com.sb.woohoo.ui.main.fragments.bluetooth.BluetoothUnSignedSettingsFragment;

import dagger.Component;

@Component(modules = BluetoothSettingsModule.class)
public interface BluetoothSettingsComponent {

    void injectBluetoothUnSignedSettingsFragment(BluetoothUnSignedSettingsFragment bluetoothUnSignedSettingsFragment);

}
