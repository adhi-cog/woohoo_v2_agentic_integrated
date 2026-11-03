package com.sb.woohoo.utils.sensory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RequestPermissionHandler;
import com.sb.woohoo.databinding.ActivitySensoryBinding;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.update.InstallTask;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sensory.speech.snsr.Snsr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@SuppressWarnings("unused")
public class SensoryActivity extends AppCompatActivity implements WakeWordDetect {

    public static final String EnrollOutFile = "enrolled.snsr";
    private final  String TAG = "SensoryActivity";
    private final  String ENROLL_UDT_MODEL = "assets/models/udt-enUS.snsr";
    private final  String TriggerPhrase = "custom-trigger-phrase";
    private  final int REQUEST_PERMISSION = 600;

    private String mEnrollFile;
    private Enroll mEnroll;
    private PhraseSpot mPhraseSpot;
    private boolean wasLaunchedFromSettings = false;
    private ActivitySensoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensoryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        // Stay in portrait mode even if user tilts
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Snsr.init(this);  // Required for direct access to assets

        if (savedInstanceState == null) {
            // New application instance
            binding.textViewConsole.setText("");
        }

        requestPermissions();

        AppCloseTask();

        wasLaunchedFromSettings = checkForIntent();

        binding.doneTraining.setOnClickListener(view -> onDoneTrainingClick());
    }

    private void AppCloseTask() {
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
            LoggerWrapper.e(InstallTask.class.getSimpleName(), "------------2-------------");
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder msg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                msg.append(line);
            }
            LoggerWrapper.e(SensoryActivity.class.getSimpleName(), "Install error msg : " + msg);
//            if (!msg.toString().contains("Failure")) {
//
//            }
        } catch (Exception e) {
            LoggerWrapper.e(SensoryActivity.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                LoggerWrapper.e(SensoryActivity.class.getSimpleName(), e.getMessage());
            }
        }

//        ProcessBuilder pb = new ProcessBuilder("adb shell am force-stop com.troopr.app.loading\n");
//        Process pc = null;
//        try {
//            pc = pb.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (pc != null) {
//                pc.waitFor();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private boolean checkForIntent() {
        Intent intent = getIntent();
        return intent == null;
    }

    private void requestPermissions() {

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION};
        RequestPermissionHandler mRequestPermissionHandler = new RequestPermissionHandler();

        mRequestPermissionHandler.requestPermission(this, PERMISSIONS, REQUEST_PERMISSION, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), getString(R.string.request_permission_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDoneTrainingClick() {
        Intent intent;
        if (wasLaunchedFromSettings) {
            intent = new Intent(SensoryActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SensoryActivity.this, OnboardingActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onPause() {
        stopSpeechActivities();
        LoggerWrapper.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoggerWrapper.i(TAG, "onResume()");
        setUiState(UiState.BEFORE_ENROLL);
    }

    private void setUiState(UiState uiState) {
        switch (uiState) {
            case BEFORE_ENROLL:
                setEnabledLook(binding.buttonEnroll, true);
                setEnabledLook(binding.doneTraining, false);
                File enrollOutDir = Enroll.getOutDir(this);
                File enrollOutFile = new File(enrollOutDir, EnrollOutFile);
                boolean canTalk = enrollOutFile.exists();
                if (canTalk) {
                    mEnrollFile = enrollOutFile.toString();
                    setEnabledLook(binding.doneTraining, true);
                    binding.doneTraining.setText(getString(R.string.textview_text_skip));
                    binding.textViewConsole.setText(getString(R.string.voice_training_done));
                } else {
                    binding.textViewConsole.setText(getString(R.string.press_enroll_button));
                }
                setEnabledLook(binding.buttonStop, false);
                binding.textViewState.setText("");
                break;
            case ENROLLING:
                binding.textViewConsole.setText("");
                binding.doneTraining.setText(getString(R.string.done_training));
                setEnabledLook(binding.buttonEnroll, false);
                setEnabledLook(binding.doneTraining, false);
                setEnabledLook(binding.buttonStop, true);
                binding.textViewState.setText(getString(R.string.enrolling_trigger));
                break;
            case ENROLLED:
                setEnabledLook(binding.buttonEnroll, true);
                setEnabledLook(binding.buttonStop, false);
                setEnabledLook(binding.doneTraining, true);
                binding.textViewState.setText("");
                break;
            default:
                LoggerWrapper.e("TAG", "Unsupported UI state: " + uiState);
        }
    }

    private void setEnabledLook(Button button, boolean enabled) {
        button.setEnabled(enabled); // Button can be null here on startup?
        if (enabled)
            button.setAlpha(1.0f);
        else
            button.setAlpha(0.5f);
    }

    @SuppressWarnings("UnusedParameters")
    public void onButtonEnroll(View view) {
        mEnroll = new Enroll(this, TriggerPhrase, ENROLL_UDT_MODEL, EnrollOutFile);
        mEnroll.start();
        setUiState(UiState.ENROLLING);
    }

    @SuppressWarnings("UnusedParameters")
    public void onButtonStop(View view) {
        stopSpeechActivities();
        log("Stopped by user.");
        notify(UiState.BEFORE_ENROLL);
    }


    void log(final String text) {
        runOnUiThread(() -> appendToLog(text));
    }

    private void appendToLog(String text) {
        LoggerWrapper.i(TAG, "Console: " + text);
        binding.textViewConsole.append(text + "\n");
        binding.scrollView.post(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN));
    }

    void notify(final UiState uiState) {
        runOnUiThread(() -> handleNotify(uiState));
    }

    private void handleNotify(UiState uiNotify) {
        if (uiNotify == UiState.ENROLLED) {
            mEnrollFile = mEnroll.getOutPath();
            mEnroll = null;
            log("- - Done! - -");
        }
        setUiState(uiNotify);
    }

    public void stopSpeechActivities() {
        if (mEnroll != null) {
            mEnroll.stop();
            mEnroll = null;
        }
        if (mPhraseSpot != null) {
            mPhraseSpot.stop();
            mPhraseSpot = null;
        }
    }

    @Override
    public void onWakeWordDetect() {

    }

    @Override
    public void onWakeWordListening() {

    }

    @Override
    public void onWakeWordStopped() {

    }

    @Override
    public void onWakeWordError() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoggerWrapper.i("SensoryActivity", "onStart()");
    }


}
