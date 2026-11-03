package com.sb.woohoo.ui.main.fragments.livefeed;

import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

public interface LiveFeedContract {

    interface View {

        void onUpdateCountDownTimer(String countDown);

        void onConnectingToStream();

        void onRemoveParticipantVideo(VideoTrack videoTrack);

        void onMoveLocalVideoToThumbnailView();

        void onAddPrimaryRenderer(VideoTrack videoTrack);

        void onMoveLocalVideoToPrimaryView();

        void onMirrorPrimaryVideoStream(boolean mirrorStatus);

        void removeVideoPreview(boolean isPreviewOff);

        void onConnectedToRoom();

        void onError(String message);

    }

    interface Action {

        void connectToRoom(String twilioToken, String roomName);

        void connectToAgoraRoom(String twilioToken, String roomName, int uid);

        void moveVideoStreamView(VideoView primaryVideoView, VideoView thumbnailVideoView);

        void addRenderer(VideoTrack videoTrack, VideoView videoView);

        void endCall();

        void enableAudio(boolean enable);

        void createAudioAndVideoTracks(VideoView videoView);

        boolean checkCameraSource(CameraCapturer.CameraSource cameraSource);

        void releaseVideoStreamingResources();

        void onConnectCall();

        void enableSpeaker(boolean isEnabled);

        void sendAcceptActionObj(String senderClientId, String senderHubId, String receiverHubId, String token,
                                 boolean contactLiveFeed, String contactUserId);

        void enableVideo(boolean enable);

        void setConnectingScreenTimer(boolean enable);
    }
}
