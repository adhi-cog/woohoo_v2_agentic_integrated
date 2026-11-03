package com.sb.woohoo.ui.main.fragments.settings.update;

public interface InstallCallback {
    void startInstallation();

    void onInstall(boolean isSuccess);
}