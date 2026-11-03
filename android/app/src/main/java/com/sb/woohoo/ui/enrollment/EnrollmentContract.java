package com.sb.woohoo.ui.enrollment;

public interface EnrollmentContract {
    interface View {
        void showAudioTrainingStart();

        void hideAudioTrainingStart();

        void showAudioTraining();

        void hideAudioTraining();

        void showAudioTrainingCompleted();

        void hideAudioTrainingCompleted();

        void showAudioTrainingError(String fix);

        void hideAudioTrainingError();

        void setToolbarTitleAsEmpty();

        void setToolbarTitleAsSuccess();

        void showSayBarText();

        void hideSayBarText();

        void showStartTraining();

        void hideStartTraining();

        void showVoiceTrainingProgress();

        void hideVoiceTrainingProgress();

        void setVoiceTrainingProgress(int progress);

        void resetUIToStartTraining();

        void showRegisteredAudio();

        void hideRegisteredAudio();

        void changeStartTrainingToSkip();

        void changeSkipToStartTraining();

        void show1stStageInitialize();

        void show1stStageCompleted();

        void show1stStageFailed();

        void show2nsStageInitialize();

        void show2ndStageCompleted();

        void show2ndStageFailed();

        void show3rdStageInitialize();

        void show3rdStageCompleted();

        void show3rdStageFailed();

        void show4thStageInitialize();

        void show4thStageCompleted();

        void show4thStageFailed();

        void proceedToNextScreen();

        void performCopyOperation();
    }

    interface Action {
        void initializeComponents();

        void performEnrollment();
    }
}