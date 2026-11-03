package com.sb.woohoo.ui.main.fragments.settings.update;

import android.content.pm.PackageManager;

public interface UpdateContract {
    interface View {
        void showToastMessage(int messageResId);

        boolean installUpdate(String path);

        boolean isPackageInstalled(String packageName, PackageManager packageManager);

        void toggleUpdateUI(UpdateDataModel updateDataModel);

        void showInstallUI();

        void hideProgress();

        void resetCheckForUpdateButton();

        void enableTextViewButton();
    }

    interface Action {
        void performUpdate();

        void onCheckUpdateSuccess(UpdateDataModel updateDataModel);

        void onCheckUpdateFailed();
    }
}
