package com.sb.woohoo.ui.main.fragments.livefeedhub;

public interface LiveFeedToHubContract {

    interface View {

        void onUpdateCountDownTimer(String countDown);

        void onConnectingToStream(String connectingText);


        void onMoveLocalVideoToThumbnailView();


        void onMoveLocalVideoToPrimaryView();

        void onMirrorPrimaryVideoStream(boolean mirrorStatus);

        void removeVideoPreview(boolean isPreviewOff);

        void onError(String message);

        void requestLiveFeedParams();

        void showMessage(String message);

        void onDisplayError(String errorMessage);
    }

    interface Action {

        void connectToAgoraRoom(String twilioToken, String roomName, int uid);

        void endCall();

        void onConnectCall();

        void getHubStatus(String hub_ID);

        void getHubUserStatus(String hub_ID, String userId);

        void onAPICallError(Object message);

        void onHubStatusError(String message);

        void onHubAvailable();

        void startLiveFeed();

        void onLiveFeedTokenSuccess(String token);

        void onLiveFeedTokenError(String errorMessage);

        void enableSpeaker(boolean enable);

        void enableVideo(boolean enable);

        void enableAudio(boolean enable);

        void onHubStatusUpdateError(String error_message);
    }
}
