package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon;

import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoRenderer;

public interface SurveillanceContract {

    interface View {

        void onAPICallError(String message);

        void updateStatus();
    }

    interface Actions {
        void updateHubStatus(boolean status);

        void connectToRoom(String twilioToken, String roomName);

        void reCreateVideoStreamingResources(VideoRenderer videoView);

        void endCall();

        void createAudioAndVideoTracks();

        boolean checkCameraSource(CameraCapturer.CameraSource cameraSource);

        void releaseVideoStreamingResources();

        void onHubStatusUpdateSuccess();

        void onHubStatusUpdateError(String message);

        void onAPICallError(String errorMessage);

    }
}
