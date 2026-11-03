package com.sb.woohoo.ui.enrollment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RequestPermissionHandler;
import com.sb.woohoo.databinding.ActivityEnrollmentBinding;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.ui.enrollment.di.DaggerEnrollmentComponent;
import com.sb.woohoo.ui.enrollment.di.EnrollmentComponent;
import com.sb.woohoo.ui.enrollment.di.EnrollmentModule;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.utils.others.MethodUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.inject.Inject;


public class EnrollmentActivity
        extends BaseActivity
        implements EnrollmentContract.View {
    // Constants.
    private final int REQUEST_PERMISSION = 600;
    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // Presenter.
    @Inject
    EnrollmentPresenter mPresenter;

    // Variables.
    private boolean wasLaunchedFromSettings = false;
    @Inject
    AnalyticsHelper helper;
    /************************************************* Listeners ******************************************************/

    private RequestPermissionHandler.RequestPermissionListener mRequestPermissionListener =
            new RequestPermissionHandler.RequestPermissionListener() {
                @Override
                public void onSuccess() {
                    MethodUtils.showToastMessage(EnrollmentActivity.this, "Permission granted");
                }

                @Override
                public void onFailed() {
                    MethodUtils.showToastMessage(EnrollmentActivity.this, "Request permission failed");
                }
            };

    /************************************* {@link android.app.Activity} Callbacks *************************************/

    private ActivityEnrollmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configureRotation();
        super.onCreate(savedInstanceState);
        binding = ActivityEnrollmentBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        injectActivity();
        helper.track(new Screen.Enrollment(),  null);
        initializeViews();
        initializeOtherComponents();
        requestPermissions();
        closeApp();
        wasLaunchedFromSettings = checkForIntent();
        setInitialState();
        setClickEvents();
    }



    /******************************************** {@code private} Methods *********************************************/

    private void injectActivity() {
        EnrollmentComponent component =
                DaggerEnrollmentComponent
                        .builder()
                        .wooHooAppComponent(((WooHooApp) getApplication()).getWooHooAppComponent())
                        .enrollmentModule(new EnrollmentModule(EnrollmentActivity.this))
                        .build();
        component.injectEnrollmentActivity(EnrollmentActivity.this);
    }

    private void initializeViews() {
    }

    private void initializeOtherComponents() {
        mPresenter.initializeComponents();
    }

    private void requestPermissions() {
        RequestPermissionHandler mRequestPermissionHandler = new RequestPermissionHandler();
        mRequestPermissionHandler.requestPermission(
                EnrollmentActivity.this,
                PERMISSIONS,
                REQUEST_PERMISSION,
                mRequestPermissionListener);
    }

    private void closeApp() {
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            String command = "am force-stop installer.woohoo.sb.com.woohoohubinstaller" + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            LoggerWrapper.i(EnrollmentActivity.class.getSimpleName(), "------------2-------------");
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            while ((line = errorStream.readLine()) != null) {
                /*msg += line;*/
                msg = msg.concat(line);
            }
            LoggerWrapper.i(EnrollmentActivity.class.getSimpleName(), "Install msg : " + msg);
            if (!msg.contains("Failure")) {
                LoggerWrapper.i(EnrollmentActivity.class.getSimpleName(), "Success");
            } else {
                LoggerWrapper.e(EnrollmentActivity.class.getSimpleName(), "Failure : " + msg);
            }
        } catch (Exception e) {
            LoggerWrapper.e(EnrollmentActivity.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                LoggerWrapper.e(EnrollmentActivity.class.getSimpleName(), e.getMessage());
            }
        }
    }

    private boolean checkForIntent() {
        Intent intent = getIntent();
        return intent == null;
    }

    private void setInitialState() {
        binding.activityEnrollmentRelativeLayoutEnrollmentStart.setVisibility(View.VISIBLE);
        binding.activityEnrollmentRelativeLayoutEnrollmentCompleted.setVisibility(View.GONE);
        binding.activityEnrollmentLinearLayoutTraining.setVisibility(View.GONE);
    }

    @Override
    public void performCopyOperation() {
        String packageName = getPackageName();
        String dataFolderPath = getFilesDir().getPath();

        LoggerWrapper.i(EnrollmentActivity.class.getSimpleName(), "performCopyOperation - " + dataFolderPath + packageName + "files/enroll");
        try {
            if (mPresenter.copyFileFromAsset(getApplicationContext(),
                    dataFolderPath + packageName + "files/enroll", "enrolled.snsr")) {
                proceedToNextScreen();
            }
        } catch (IOException e) {
            LoggerWrapper.e(EnrollmentActivity.class.getSimpleName(), e.getMessage());
        }
        //TODO will be optimised later..

    }

    @Override
    public void proceedToNextScreen() {
        LoggerWrapper.i(EnrollmentActivity.class.getSimpleName(), "onComplete called!");
        Intent intent;
        LoggerWrapper.i("proceedToNextScreen", String.valueOf(wasLaunchedFromSettings));
        if (wasLaunchedFromSettings) {
            intent = new Intent(EnrollmentActivity.this, MainActivity.class);
        } else {
            intent = new Intent(EnrollmentActivity.this, OnboardingActivity.class);
        }
        mPresenter.stopEnrollment();
        startActivity(intent);
        finish();
    }

    /************************************ {@link EnrollmentContract.View} Callbacks ***********************************/

    @Override
    public void showAudioTrainingStart() {
        binding.activityEnrollmentRelativeLayoutEnrollmentStart.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAudioTrainingStart() {
        binding.activityEnrollmentRelativeLayoutEnrollmentStart.setVisibility(View.GONE);
    }

    @Override
    public void showAudioTraining() {
        binding.activityEnrollmentLinearLayoutTraining.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAudioTraining() {
        binding.activityEnrollmentLinearLayoutTraining.setVisibility(View.GONE);
    }

    @Override
    public void showAudioTrainingCompleted() {
        binding.activityEnrollmentRelativeLayoutEnrollmentCompleted.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAudioTrainingCompleted() {
        binding.activityEnrollmentRelativeLayoutEnrollmentCompleted.setVisibility(View.GONE);
    }

    @Override
    public void showAudioTrainingError(String fix) {
        binding.activityEnrollmentRelativeLayoutEnrollmentError.setVisibility(View.VISIBLE);
        binding.trainingFix.setText(fix);
    }

    @Override
    public void hideAudioTrainingError() {
        binding.activityEnrollmentRelativeLayoutEnrollmentError.setVisibility(View.GONE);
    }

    @Override
    public void setToolbarTitleAsEmpty() {
        binding.activityEnrollmentToolbar.commonToolbarTitle.setText("");
    }

    @Override
    public void setToolbarTitleAsSuccess() {
        binding.activityEnrollmentToolbar.commonToolbarTitle.setText(getString(R.string.activity_enrollment_text_success));
    }

    @Override
    public void showSayBarText() {
        binding.activityEnrollmentTextViewSayBarText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSayBarText() {
        binding.activityEnrollmentTextViewSayBarText.setVisibility(View.GONE);
    }

    @Override
    public void showStartTraining() {
        binding.activityEnrollmentTextViewStartVoiceTraining.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideStartTraining() {
        binding.activityEnrollmentTextViewStartVoiceTraining.setVisibility(View.GONE);
    }

    @Override
    public void showVoiceTrainingProgress() {
        binding.activityEnrollmentProgressBarProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVoiceTrainingProgress() {
        binding.activityEnrollmentProgressBarProgress.setVisibility(View.GONE);
    }

    @Override
    public void setVoiceTrainingProgress(int progress) {
        binding.activityEnrollmentProgressBarProgress.setProgress(progress);
    }

    @Override
    public void resetUIToStartTraining() {
        binding.activityEnrollmentTextViewStageOne.setText(getString(R.string.activity_enrollment_text_voice_training_stage_one));
        binding.activityEnrollmentTextViewStageTwo.setText("");
        binding.activityEnrollmentTextViewStageThree.setText("");
        binding.activityEnrollmentTextViewStageFour.setText("");

        binding.activityEnrollmentImageViewIconStageOne.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_spinner_white));
        binding.activityEnrollmentImageViewIconStageTwo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_circle_peach_16dp));
        binding.activityEnrollmentImageViewIconStageThree.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_circle_peach_16dp));
        binding.activityEnrollmentImageViewIconStageFour.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_circle_peach_16dp));
    }

    @Override
    public void showRegisteredAudio() {
        binding.activityEnrollmentLinearLayoutRegistered.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRegisteredAudio() {
        binding.activityEnrollmentLinearLayoutRegistered.setVisibility(View.GONE);
    }

    @Override
    public void changeStartTrainingToSkip() {
        binding.activityEnrollmentTextViewStartVoiceTraining.setText(getString(R.string.activity_enrollment_text_skip));
    }

    @Override
    public void changeSkipToStartTraining() {
        binding.activityEnrollmentTextViewStartVoiceTraining.setText(getString(R.string.activity_enrollment_text_start_voice_training));
    }

    @Override
    public void show1stStageInitialize() {
        binding.activityEnrollmentTextViewStageOne.setText(getString(R.string.activity_enrollment_text_voice_training_stage_one));
        binding.activityEnrollmentImageViewIconStageOne.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_spinner_white));
    }

    @Override
    public void show1stStageCompleted() {
        binding.activityEnrollmentTextViewStageOne.setText(getString(R.string.activity_enrollment_text_audio_enrollment_completed));
        binding.activityEnrollmentImageViewIconStageOne.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tick_28dp));
    }

    @Override
    public void show1stStageFailed() {
        binding.activityEnrollmentTextViewStageOne.setText(getString(R.string.activity_enrollment_text_audio_enrollment_failed));
    }

    @Override
    public void show2nsStageInitialize() {
        binding.activityEnrollmentTextViewStageTwo.setText(getString(R.string.activity_enrollment_text_voice_training_stage_two));
        binding.activityEnrollmentImageViewIconStageTwo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_spinner_white));
    }

    @Override
    public void show2ndStageCompleted() {
        binding.activityEnrollmentTextViewStageTwo.setText(getString(R.string.activity_enrollment_text_audio_enrollment_completed));
        binding.activityEnrollmentImageViewIconStageTwo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tick_28dp));
    }

    @Override
    public void show2ndStageFailed() {
        binding.activityEnrollmentTextViewStageTwo.setText(getString(R.string.activity_enrollment_text_audio_enrollment_failed));
    }

    @Override
    public void show3rdStageInitialize() {
        binding.activityEnrollmentTextViewStageThree.setText(getString(R.string.activity_enrollment_text_voice_training_stage_three));
        binding.activityEnrollmentImageViewIconStageThree.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_spinner_white));
    }

    @Override
    public void show3rdStageCompleted() {
        binding.activityEnrollmentTextViewStageThree.setText(getString(R.string.activity_enrollment_text_audio_enrollment_completed));
        binding.activityEnrollmentImageViewIconStageThree.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tick_28dp));
    }

    @Override
    public void show3rdStageFailed() {
        binding.activityEnrollmentTextViewStageThree.setText(getString(R.string.activity_enrollment_text_audio_enrollment_failed));
    }

    @Override
    public void show4thStageInitialize() {
        binding.activityEnrollmentTextViewStageFour.setText(getString(R.string.activity_enrollment_text_voice_training_stage_four));
        binding.activityEnrollmentImageViewIconStageFour.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_spinner_white));
    }

    @Override
    public void show4thStageCompleted() {
        binding.activityEnrollmentTextViewStageFour.setText(getString(R.string.activity_enrollment_text_audio_enrollment_completed));
        binding.activityEnrollmentImageViewIconStageFour.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tick_28dp));
    }

    @Override
    public void show4thStageFailed() {
        binding.activityEnrollmentTextViewStageFour.setText(getString(R.string.activity_enrollment_text_audio_enrollment_failed));
    }

    /************************************** onClick Listeners *************************************/

    private void setClickEvents() {
        binding.activityEnrollmentTextViewStartVoiceTraining.setOnClickListener(view -> onStartTrainingClicked());
        binding.activityEnrollmentTextViewFinishVoiceTraining.setOnClickListener(view -> onFinishTrainingClicked());
        binding.activityEnrollmentTextViewRetryVoiceTraining.setOnClickListener(view -> onTryAgainClicked());
        binding.activityEnrollmentLinearLayoutRegistered.setOnClickListener(view -> onRetrainClicked());
        binding.activityEnrollmentToolbar.commonToolbarBack.setOnClickListener(view -> proceedToNextScreen());
    }

    void onStartTrainingClicked() {
        if (getString(R.string.activity_enrollment_text_skip).equals(binding.activityEnrollmentTextViewStartVoiceTraining.getText().toString())) {
            proceedToNextScreen();
        } else {
            mPresenter.performEnrollment();
        }
    }

    void onFinishTrainingClicked() {
        proceedToNextScreen();
    }

    void onTryAgainClicked() {
        mPresenter.performEnrollment();
    }

    void onRetrainClicked() {
        mPresenter.performEnrollment();
    }
}