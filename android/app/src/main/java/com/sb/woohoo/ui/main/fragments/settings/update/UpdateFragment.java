package com.sb.woohoo.ui.main.fragments.settings.update;

import static com.sb.woohoo.utils.others.MethodUtils.getVersionNameAndVersionCode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentUpdateBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.settings.update.di.DaggerUpdateComponent;
import com.sb.woohoo.ui.main.fragments.settings.update.di.UpdateComponent;
import com.sb.woohoo.ui.main.fragments.settings.update.di.UpdateModule;
import com.sb.woohoo.ui.main.fragments.settings.update.test.UpdateService;
import com.sb.woohoo.ui.main.helpers.UpdateHelper;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.FileUtils;
import com.sb.woohoo.utils.others.MethodUtils;

import java.io.File;

import javax.inject.Inject;

/**
 * Shows Update Version and UI to Update.
 *
 * @author Ashish Yadav
 */
public class UpdateFragment extends FeatureFragment<MainActivity>
        implements UpdateContract.View, NonPopFragment, UpdateService.UpdateListener, InstallCallback {

    private final Integer BTN_UPDATE_TAG = 1;
    private final Integer BTN_INSTALL_TAG = 2;
    // Constants.
    private static final String UPDATE_KEY = "updateData";


    // Presenter.
    @Inject
    UpdatePresenter mPresenter;
    @Inject
    UpdateHelper updateHelper;
    @Inject
    AnalyticsHelper analyticsHelper;


    private UpdateService updateService;
    private UpdateDataModel updateDataModel;
    private SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper();
    private FragmentUpdateBinding binding;

    public UpdateFragment() { /*Required empty public constructor*/ }

    public static UpdateFragment newInstance() {
        return new UpdateFragment();
    }

    public static UpdateFragment newInstance(UpdateDataModel updateDataModel) {
        UpdateFragment updateFragment = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UPDATE_KEY, updateDataModel);
        updateFragment.setArguments(bundle);
        return updateFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!helper.get(SharedPrefsKey.PREF_DOWNLOAD_STATUS, false)) {
            FileUtils.deleteFolder(UpdatePresenter.APK_FOLDER);
        }
        updateService = updateHelper.getUpdateService();
        if (updateService != null) {
            updateService.registerListener(this);
        }
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        analyticsHelper.track(new Screen.Update(),  null);
        binding = FragmentUpdateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO check for "checking the update" in the RecyclerView
        if (updateService != null) {
            if (updateService.getStatus() == AsyncTask.Status.RUNNING) {
                showUpdateProgressUI();
            } else if (updateService.getStatus() == AsyncTask.Status.FINISHED) {
                showUpdateFinishedUI();
            } else {
                checkArguments();
            }
        } else {
            showToastMessage(R.string.no_updates_available);
        }
    }

    private void checkArguments() {
        if (getArguments() != null) {
            UpdateDataModel updateDataModel = getArguments().getParcelable(UPDATE_KEY);
            if (updateDataModel != null) {
                this.updateDataModel = updateDataModel;
                int currentVersionCode = BuildConfig.VERSION_CODE;
                int versionCodeReceived = updateDataModel.getVersionCode();
                if (versionCodeReceived > currentVersionCode) {
                    if (mPresenter.checkApk(versionCodeReceived)) {
                        showInstallUI();
                    } else {
                        toggleUpdateUI(updateDataModel);
                    }
                } else {
                    toggleNoUpdateUI();
                }
            }
        } else {
            toggleNoUpdateUI();
        }
    }

    private void showUpdateFinishedUI() {
        if (updateService.isUpdateCancelled()) {
            checkArguments();
        } else {
            if (getArguments() != null) {
                UpdateDataModel updateDataModel = getArguments().getParcelable(UPDATE_KEY);
                //TODO check if the downloaded apk is old...
                if (updateDataModel != null) {
                    this.updateDataModel = updateDataModel;
                    if (mPresenter.checkApk(updateDataModel.getVersionCode())) {
                        showInstallUI();
                    } else {
                        FileUtils.deleteFolder(UpdatePresenter.APK_FOLDER);
                        toggleUpdateUI(updateDataModel);
                    }
                }
            }
        }
    }

    private void showUpdateProgressUI() {
        binding.fragmentUpdateRelativeLayoutUpdateProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void toggleUpdateUI(UpdateDataModel updateDataModel) {

        this.updateDataModel = updateDataModel;

        binding.fragmentUpdateRelativeLayoutCheckForUpdate.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateProgress.setVisibility(View.GONE);

        binding.fragmentUpdateRelativeLayoutNewUpdate.setVisibility(View.VISIBLE);
        binding.fragmentUpdateTextViewVersion.setText(String.format(getString(R.string.version),updateDataModel.getVersionName()));
        binding.fragmentUpdateTextViewChangeLog.setText(String.format(getString(R.string.change_log),updateDataModel.getDescription()));
        binding.fragmentUpdateText.setText(getString(R.string.fragment_update_string_new_updates));
        binding.fragmentUpdateButton.setText(getString(R.string.UPDATE));
        binding.fragmentUpdateButton.setTag(BTN_UPDATE_TAG);

        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
    }

    @Override
    public void showInstallUI() {
        binding.fragmentUpdateRelativeLayoutCheckForUpdate.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateProgress.setVisibility(View.GONE);

        binding.fragmentUpdateRelativeLayoutNewUpdate.setVisibility(View.VISIBLE);

        if (updateDataModel != null) {
            binding.fragmentUpdateTextViewVersion.setText(String.format(getString(R.string.version),updateDataModel.getVersionName()));
            binding.fragmentUpdateTextViewChangeLog.setText(String.format(getString(R.string.change_log),updateDataModel.getDescription()));
        }

        binding.fragmentUpdateText.setText(getString(R.string.download_complete));
        binding.fragmentUpdateButton.setText(getString(R.string.install_update));
        binding.fragmentUpdateButton.setTag(BTN_INSTALL_TAG);

        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.fragmentUpdateProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void resetCheckForUpdateButton() {
        binding.fragmentUpdateRelativeLayoutUpdateProgress.setClickable(true);
        binding.fragmentUpdateTextViewUpdate.setText(R.string.fragment_update_string_update_check_1);
    }

    private void toggleNoUpdateUI() {
        binding.fragmentUpdateRelativeLayoutNewUpdate.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutUpdateProgress.setVisibility(View.GONE);
        binding.fragmentUpdateRelativeLayoutCheckForUpdate.setVisibility(View.VISIBLE);
        binding.fragmentUpdateTextViewVersionNumber.setText(getString(R.string.version, getVersionNameAndVersionCode()));
        binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.VISIBLE);
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        UpdateComponent component =
                DaggerUpdateComponent
                        .builder()
                        .updateModule(new UpdateModule(UpdateFragment.this))
                        .build();
        component.injectUpdateFragment(UpdateFragment.this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentUpdateCommonToolbarLayout.commonToolbarTitle.setText(R.string.fragment_update_string_toolbar_title);
        binding.fragmentUpdateInputInterceptorRelativeLayout.setContext(getParentActivity());
        binding.fragmentUpdateCommonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.fragmentUpdateButton.setOnClickListener(this::onStartUpdateClicked);
        binding.fragmentUpdateCancelDownload.setOnClickListener(view1 ->onCancelUpdateClicked());
        binding.fragmentUpdateRelativeLayoutUpdateCheck.setOnClickListener(view1 ->onUpdateClicked());
    }

    /*********************************** {@code private} Methods **********************************/

    private void uiStateCheckingForUpdate() {
        binding.fragmentUpdateTextViewUpdate.setText(R.string.fragment_update_string_update_check_2);
        binding.fragmentUpdateProgressBar.setVisibility(View.VISIBLE);
    }

    /**************************** {@link UpdateContract.View} Callbacks ***************************/

    @Override
    public void showToastMessage(int msgStringId) {
        MethodUtils.showToastMessage(getParentActivity(), getString(msgStringId));
    }

    @Override
    public boolean installUpdate(String apkPath) {
        boolean result = false;

        try {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                new InstallTask(getContext(), this, apkPath).execute();
            } else {

                File toInstall = new File(Environment.getExternalStorageDirectory() + "/woohoo.apk");
                Uri contentUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", toInstall);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(contentUri);
                    startActivity(install);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    startActivity(install);
                }
            }

        } catch (Exception e) {
            LoggerWrapper.e(InstallTask.class.getSimpleName(), e.getMessage());
        }

        return result;
    }

    public void enableTextViewButton() {
        binding.fragmentUpdateButton.setEnabled(true);
    }

    private void disableTextViewButton() {
        binding.fragmentUpdateButton.setEnabled(false);
    }

    @Override
    public boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**************************** Listeners ***************************/

    void onRootClicked() {
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onStartUpdateClicked(View view) {
        if (view.getTag() == BTN_UPDATE_TAG) {
            doActionForUpdate();
        }

        if (view.getTag() == BTN_INSTALL_TAG) {
            disableTextViewButton();
            doActionForInstall();
        }
    }

    private void doActionForInstall() {
        if (getArguments() != null) {
            UpdateDataModel updateDataModel = getArguments().getParcelable(UPDATE_KEY);
            if (updateDataModel != null) {
                this.updateDataModel = updateDataModel;
                //TODO perform install action
                mPresenter.installApk(
                        UpdatePresenter.APK_FOLDER + updateDataModel.getVersionCode() + ".apk"
                );
            }
        }
    }

    private void doActionForUpdate() {
        if (updateDataModel != null) {
            if (updateService != null) {
                updateService.startDownload(updateDataModel);

                binding.fragmentUpdateRelativeLayoutNewUpdate.setVisibility(View.GONE);
                binding.fragmentUpdateRelativeLayoutUpdateCheck.setVisibility(View.GONE);
                binding.fragmentUpdateRelativeLayoutCheckForUpdate.setVisibility(View.GONE);

                binding.fragmentUpdateRelativeLayoutUpdateProgress.setVisibility(View.VISIBLE);

                binding.fragmentUpdateRelativeLayoutUpdateProgress.setClickable(false);
            }
        }
    }

    void onCancelUpdateClicked() {
        updateService.cancelDownload();
    }

    void onUpdateClicked() {
        if (View.VISIBLE == binding.fragmentUpdateRelativeLayoutNewUpdate.getVisibility()) {
            mPresenter.performUpdate();
        } else {
            uiStateCheckingForUpdate();
            mPresenter.performUpdate();
        }
    }


    /*********************************** {@link InstallCallback}s ***********************************/

    @Override
    public void onProgressUpdate(int value) {
        //TODO hide mRlCurrentAppVersion,mLlNewUpdate,mCheckForUpdateLayout,mUpdateProgressLayout
        binding.fragmentUpdateDownloadProgressText.setText(getString(R.string.downloading_update) + " " + "(" + value + "%" + ")");
        binding.fragmentUpdateDownloadProgressBar.setProgress(value);
    }

    @Override
    public void onCancelDownload() {
        toggleNoUpdateUI();
        showToastMessage(R.string.update_download_cancelled);
    }

    @Override
    public void onUpdateDownloadFailed() {
        toggleNoUpdateUI();
        showToastMessage(R.string.update_download_failed);
    }

    @Override
    public void onUpdateDownloadSuccess(String fileLocation) {
        showInstallUI();
    }

    @Override
    public void startInstallation() {
    }

    @Override
    public void onInstall(boolean isSuccess) {
        if (!isSuccess) {
            // showToastMessage("Try again");
        }
    }
}


//    fun install(context: Context, packageName: String, apkPath: String) {
//
//        // PackageManager provides an instance of PackageInstaller
//        val packageInstaller = context.packageManager.packageInstaller
//
//        // Prepare params for installing one APK file with MODE_FULL_INSTALL
//        // We could use MODE_INHERIT_EXISTING to install multiple split APKs
//        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
//        params.setAppPackageName(packageName)
//
//        // Get a PackageInstaller.Session for performing the actual update
//        val sessionId = packageInstaller.createSession(params)
//        val session = packageInstaller.openSession(sessionId)
//
//        // Copy APK file bytes into OutputStream provided by install Session
//        val out = session.openWrite(packageName, 0, -1)
//        val fis = File(apkPath).inputStream()
//        fis.copyTo(out)
//        session.fsync(out)
//        out.close()
//
//        // The app gets killed after installation session commit
//        session.commit(PendingIntent.getBroadcast(context, sessionId,
//                Intent("android.intent.action.MAIN"), 0).intentSender)
//    }