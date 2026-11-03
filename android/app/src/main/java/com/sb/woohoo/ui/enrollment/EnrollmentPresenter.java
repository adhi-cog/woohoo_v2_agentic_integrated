package com.sb.woohoo.ui.enrollment;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.enrollment.sensory.EnrollHelper;
import com.sb.woohoo.ui.enrollment.sensory.EnrollListener;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sensory.speech.snsr.Snsr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

public class EnrollmentPresenter
        extends BasePresenter
        implements EnrollmentContract.Action,
        EnrollListener {
    // Constants.
    private final String ENROLL_UDT_MODEL = "assets/models/udt-enUS.snsr";
    private final String ENROLL_OUTPUT_FILE = "enrolled.snsr";
    private final String TRIGGER_PHRASE = "custom-trigger-phrase";
    // Other Components.
    private EnrollmentContract.View mView;
    private EnrollmentRepository mRepository;
    private Activity mActivity;
    private EnrollHelper mEnrollHelper;
    // Variables.
    private int mSuccessfulAudioCaptured;

    @Inject
    public EnrollmentPresenter(
            EnrollmentContract.View view,
            EnrollmentRepository repository,
            Activity activity) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(EnrollmentPresenter.this);
        this.mActivity = activity;
    }

    /********************************************* {@code private} Methods ********************************************/

    private void instantiateEnrollment() {
        mEnrollHelper = new EnrollHelper(
                EnrollmentPresenter.this,
                TRIGGER_PHRASE,
                ENROLL_UDT_MODEL,
                ENROLL_OUTPUT_FILE,
                mActivity
        );
    }

    public void stopEnrollment() {
        mActivity.runOnUiThread(() -> {
                    if (mEnrollHelper != null) {
                        mEnrollHelper.stop();
                        mEnrollHelper = null;
                    }
                }
        );
    }

    private boolean doesEnrollmentExists() {
        return mRepository.doesEnrollmentFileExists(mActivity);
    }

    /*********************************** {@link EnrollmentContract.Action} Callbacks **********************************/

    @Override
    public void initializeComponents() {
        mView.setToolbarTitleAsEmpty();
        mView.hideAudioTrainingCompleted();
        mView.hideAudioTrainingError();
        mView.showAudioTrainingStart();
        mView.showSayBarText();
        mView.showStartTraining();
        mView.hideVoiceTrainingProgress();
        //TODO uncomment below when there is the need for voice registration
        Toast.makeText(mActivity, String.valueOf(doesEnrollmentExists()), Toast.LENGTH_LONG).show();
        //if (doesEnrollmentExists()) {
//            mView.proceedToNextScreen();
//            mView.changeStartTrainingToSkip();
//            mView.showRegisteredAudio();

        //Below code is for Voice Genie
        // mView.proceedToNextScreen();
        //} else {
//            mView.changeSkipToStartTraining();
//            mView.hideRegisteredAudio();

        //Below code is for Voice Genie
        mView.performCopyOperation();
        //}
        Snsr.init(mActivity);  // Required for direct access to assets
        instantiateEnrollment();
    }

    @Override
    public void performEnrollment() {
        if (mEnrollHelper == null) {
            instantiateEnrollment();
        }

        mSuccessfulAudioCaptured = 0;

        mView.resetUIToStartTraining();
        mView.hideAudioTrainingError();
        mView.hideRegisteredAudio();
        mView.hideVoiceTrainingProgress();
        mView.hideStartTraining();
        mView.showAudioTrainingStart();
        mView.showAudioTraining();
        mView.show1stStageInitialize();

        mEnrollHelper.start();
    }

    /**************************************** {@link EnrollListener} Callbacks ****************************************/

    @Override
    public void onGoodAudioCaptured() {
        mActivity.runOnUiThread(() -> {
                    mSuccessfulAudioCaptured++;
                    switch (mSuccessfulAudioCaptured) {
                        case 1: {
                            mView.show1stStageCompleted();
                        }
                        break;

                        case 2: {
                            mView.show2ndStageCompleted();
                        }
                        break;

                        case 3: {
                            mView.show3rdStageCompleted();
                        }
                        break;

                        case 4: {
                            mView.hideSayBarText();
                            mView.hideStartTraining();
                            mView.show4thStageCompleted();
                            mView.showVoiceTrainingProgress();
                        }
                        break;
                    }
                }
        );
    }

    @Override
    public void promptNextAudioCapture() {
        mActivity.runOnUiThread(() -> {
                    switch (mSuccessfulAudioCaptured) {
                        case 0: {
                            mView.show1stStageInitialize();
                        }
                        break;

                        case 1: {
                            mView.show2nsStageInitialize();
                        }
                        break;

                        case 2: {
                            mView.show3rdStageInitialize();
                        }
                        break;

                        case 3: {
                            mView.show4thStageInitialize();
                        }
                        break;
                    }
                }
        );
    }

    @Override
    public void onAudioCaptureFailed(String reason, String fix) {
        mActivity.runOnUiThread(() -> {
                    switch (mSuccessfulAudioCaptured) {
                        case 0: {
                            mView.show1stStageFailed();
                        }
                        break;

                        case 1: {
                            mView.show2ndStageFailed();
                        }
                        break;

                        case 2: {
                            mView.show3rdStageFailed();
                        }
                        break;

                        case 3: {
                            mView.show4thStageFailed();
                        }
                        break;
                    }
                }
        );
    }

    @Override
    public void onAudioTrainingProgress(final int progress) {
        mActivity.runOnUiThread(() -> mView.setVoiceTrainingProgress(progress)
        );
    }

    @Override
    public void onAudioEnrolled() {
        mActivity.runOnUiThread(() -> {
                    mView.hideVoiceTrainingProgress();
                    mView.showSayBarText();
                    mView.hideAudioTraining();
                    mView.hideAudioTrainingStart();
                    mView.setToolbarTitleAsSuccess();
                    mView.showAudioTrainingCompleted();
                }
        );
    }

    @Override
    public void onPoorPhraseQuality(String fix) {
        stopEnrollment();
        mActivity.runOnUiThread(() -> {
                    mView.hideAudioTraining();
                    mView.hideStartTraining();
                    mView.hideAudioTrainingStart();
                    mView.hideAudioTrainingCompleted();
                    mView.hideVoiceTrainingProgress();
                    mView.showSayBarText();
                    mView.showAudioTrainingError(fix);
                }
        );
    }

    public boolean copyFileFromAsset(Context context, String pathToCopy, String fileName) throws IOException {
        InputStream myInput;
        OutputStream myOutput;
        byte[] buffer = new byte[1024];
        int length;
        try {
            myOutput = new FileOutputStream(pathToCopy);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            myInput = context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        while ((length = myInput.read(buffer)) > 0) {
            try {
                myOutput.write(buffer, 0, length);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    myInput.close();
                    myOutput.flush();
                    myOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}