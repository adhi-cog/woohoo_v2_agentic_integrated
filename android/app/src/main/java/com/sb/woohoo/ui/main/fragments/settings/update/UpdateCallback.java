package com.sb.woohoo.ui.main.fragments.settings.update;

public interface UpdateCallback {
    void onPreExecute();

    void onProgressUpdate(int value, int integer);

    void onUpdateSuccess(String fileLocation);

    void onUpdateFailed();

    void onUpdateCancelled();
}
