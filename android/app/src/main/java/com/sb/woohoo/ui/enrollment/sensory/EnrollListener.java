package com.sb.woohoo.ui.enrollment.sensory;

public interface EnrollListener {
    void onGoodAudioCaptured();

    void promptNextAudioCapture();

    void onAudioCaptureFailed(String reason, String fix);

    void onAudioTrainingProgress(int progress);

    void onAudioEnrolled();

    void onPoorPhraseQuality(String fix);

}