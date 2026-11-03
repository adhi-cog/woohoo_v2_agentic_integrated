package com.sb.woohoo.ui.main.fragments.settings.update;

import android.os.Environment;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.FileUtils;

import java.io.File;

import javax.inject.Inject;

public class UpdatePresenter extends BasePresenter implements UpdateContract.Action, InstallCallback {

    static final String APK_FOLDER = Environment.getExternalStorageDirectory() + "/sb/";
    private UpdateContract.View mView;
    private UpdateRepository mRepository;

    @Inject
    public UpdatePresenter(UpdateFragment view, UpdateRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(UpdatePresenter.this);
    }

    /*************************** {@link UpdateContract.Action} Callbacks ***************************/

    @Override
    public void performUpdate() {
        mRepository.checkForUpdates();
    }

    @Override
    public void onCheckUpdateSuccess(UpdateDataModel updateDataModel) {
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int versionCodeReceived = updateDataModel.getVersionCode();
        if (versionCodeReceived > currentVersionCode) {
            if (checkApk(versionCodeReceived)) {
                mView.showInstallUI();
            } else {
                FileUtils.deleteFolder(UpdatePresenter.APK_FOLDER);
                mView.toggleUpdateUI(updateDataModel);
            }
        } else {
            FileUtils.deleteFolder(UpdatePresenter.APK_FOLDER);
            mView.showToastMessage(R.string.already_have_latest_version);
        }

        mView.hideProgress();
        mView.resetCheckForUpdateButton();
    }

    @Override
    public void onCheckUpdateFailed() {
        mView.hideProgress();
        mView.resetCheckForUpdateButton();
        mView.showToastMessage(R.string.no_data_fetched);
    }

    /********************************** {@link UpdateCallback}'s **********************************/

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    public void installApk(String fileLocation) {

        mView.installUpdate(fileLocation);
    }

    @Override
    public void onInstall(boolean isSuccess) {
        if (isSuccess) {
            // mView.showToastMessage("Successfully installed");
        } else {
            mView.showToastMessage(R.string.installation_failed);
        }
        mView.enableTextViewButton();
    }

    @Override
    public void startInstallation() {

    }

    public boolean checkApk(Integer versionCode) {

        SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
        if (helper.get(SharedPrefsKey.PREF_DOWNLOAD_STATUS, false)) {
            if (FileUtils.dirExists(APK_FOLDER)) {
                String fileName = versionCode + ".apk";
                String APK_PATH = APK_FOLDER + fileName;
                File file = new File(APK_PATH);
                if (FileUtils.checkIfFileExists(file)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }else{
            return false;
        }

    }
}
