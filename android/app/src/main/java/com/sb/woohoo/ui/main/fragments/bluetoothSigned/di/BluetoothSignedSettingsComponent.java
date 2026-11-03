package com.sb.woohoo.ui.main.fragments.bluetoothSigned.di;


import com.sb.woohoo.ui.main.fragments.bluetoothSigned.BluetoothSignedSettingsFragment;

import dagger.Component;

@Component(modules = BluetoothSignedSettingsModule.class)
public interface BluetoothSignedSettingsComponent {

    void injectBluetoothSignedSettingsFragment(BluetoothSignedSettingsFragment bluetoothUnSignedSettingsFragment);

}
