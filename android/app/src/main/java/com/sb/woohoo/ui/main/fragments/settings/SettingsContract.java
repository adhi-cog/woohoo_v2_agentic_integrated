package com.sb.woohoo.ui.main.fragments.settings;

import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link SettingsFragment}.
 */
public interface SettingsContract {
    interface View {
        void showError(String errorMessage);

        void closeMainActivity();

        void resetUI();

        void onLogOutSuccess();

        void updateSettingsList();

    }

    interface Action {
        void onAPIError(Object errorCause);

        void onLoggedOutSuccessfully();

        void onCheckUpdateSuccess(UpdateDataModel updateDataModel);

        void onCheckUpdateFailed();

        void intializeViews();

        void onUpdateClicked();

        void onWifiNetworkChanged();

        void onMobileNetworkChanged(boolean isChecked);

        void onEngineChanged(String text);

        void onWakeWordChanged(String text);

        void onLangChanged(String text);
    }
}