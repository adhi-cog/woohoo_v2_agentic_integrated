package com.sb.woohoo.ui.main.fragments.livefeedhubaudio;

public interface LiveFeedToHubAudioContract {

    interface View {
          void onConnectedToRoom();
        void onUpdateCountDownTimer(String countDown);

        void onConnectingToStream(String connectingText);

//        void onRemoveParticipantVideo(VideoTrack videoTrack);

//        void onMoveLocalVideoToThumbnailView();

//        void onAddPrimaryRenderer(VideoTrack videoTrack);

//        void onMoveLocalVideoToPrimaryView();

//        void onMirrorPrimaryVideoStream(boolean mirrorStatus);

//        void removeVideoPreview(boolean isPreviewOff);

        void onError(String message);

        void requestLiveFeedParams();

        void showMessage(String message);
    }

    interface Action {

        void connectToRoom(String twilioToken, String roomName);
        void connectToAgoraRoom(String twilioToken, String roomName, int uid);


//        void moveVideoStreamView(VideoView primaryVideoView, VideoView thumbnailVideoView);

//        void addRenderer(VideoTrack videoTrack, VideoView videoView);

        void endCall();

        void createAudioAndVideoTracks();

//        boolean checkCameraSource(CameraCapturer.CameraSource cameraSource);

//        void releaseVideoStreamingResources();

        void onConnectCall();

        void getHubStatus(String hub_ID);

        void onAPICallError(Object message);

        void onHubStatusError(Object message);

        void onHubAvailable();

        void startLiveFeed();

        void onLiveFeedTokenSuccess(String token);

        void onLiveFeedTokenError(String errorMessage);

        void enableSpeaker(boolean enable);

        //        void enableVideo(boolean enable);
        void getHubUserStatus(String hub_ID, String userId);


        void enableAudio(boolean enable);

        void onHubStatusUpdateError(String error_message);
    }
}
