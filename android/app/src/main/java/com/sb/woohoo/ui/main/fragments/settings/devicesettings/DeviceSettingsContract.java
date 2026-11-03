package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

public interface DeviceSettingsContract {

    interface Views {

        void onDeviceNameStatusSuccessError(String errorMessage);

        void resetUI();

        void updateSettingList();

        void showMessage(String message);
    }

    interface Actions {

        void onDeviceNameSuccess(String deviceName);

        void onDeviceNameStatusError(String error);

        void factoryReset();

        void intializeView();

        void getDeviceName();

        void onTimezoneChanged();

        void onAPIError(String message);

        void onAPISuccess(String message);

    }
}
