package com.sb.woohoo.ui.main.fragments.livefeedhubaudio;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonObject;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static com.sb.woohoo.utils.Constants.AUTO_CALL_DISCONNECT;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

public class LiveFeedToHubAudioPresenter
        extends BasePresenter
        implements LiveFeedToHubAudioContract.Action {

    private final String CAMERA_ROTATE_EVENT = "clientCameraPanEvent";
    private int DELAY = 1000;
    //CONSTANTS
    private final int CAMERA_HANDLER_INTERVAL = 1000;
    @Inject
    MediaPlayerUtil mediaPlayerUtil;
    private LiveFeedToHubAudioRepository repository;
    private LiveFeedToHubAudioFragment view;
    private AudioManager audioManager;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private LocalParticipant localParticipant;
    private Room room;
    //Time counter init for sync
    private int countDownTimer = 12;
    private String participantIdentity;
    private boolean disconnectedFromOnDestroy;
    private CameraCapturerCompat cameraCapturerCompat;
    private Handler timerHandler = new Handler();
    private String deviceID;
    private String twilioToken;
    private RemoteAudioTrack participantAudioTrack;

    private Runnable timerRunnable = new Runnable() {
        public void run() {
            countDownTimer = countDownTimer - 1;
//            view.onUpdateCountDownTimer(String.valueOf(countDownTimer));
            if (countDownTimer == 0) {
                if (view != null && view.getActivity() != null && !view.isDetached())
                    view.showMessage(view.getString(R.string.call_disconnected));
                view.onBackPressed();
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(this, DELAY);
            }
        }
    };

    private Runnable initializeRunnable = new Runnable() {
        @Override
        public void run() {

            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.postDelayed(timerRunnable, DELAY);
        }
    };

    @Inject
    public LiveFeedToHubAudioPresenter(LiveFeedToHubAudioFragment fragment, LiveFeedToHubAudioRepository repository,
                                       AudioManager audioManager, CameraCapturerCompat cameraCapturerCompat) {
        this.repository = repository;
        this.view = fragment;
        this.audioManager = audioManager;
        this.cameraCapturerCompat = cameraCapturerCompat;
        this.repository.onAttach(this);
        this.cameraCapturerCompat.initializeCameraCapturerCompat(view.getContext());
    }

    @Override
    public void getHubStatus(String remoteHubId) {
        this.deviceID = remoteHubId;
        repository.requestHubStatus(remoteHubId);
    }

    @Override
    public void onAPICallError(Object message) {
        String msgString;
        if (message instanceof Integer)
            msgString = view.getString((Integer) message);
        else
            msgString = (String) message;
        view.onError(msgString);
    }

    @Override
    public void onHubStatusError(Object message) {
        String msgString;
        if (message instanceof Integer)
            msgString = view.getString((Integer) message);
        else
            msgString = (String) message;
        view.onError(msgString);
        endCall();
    }

    @Override
    public void getHubUserStatus(String remoteHubId, String userId) {
        this.deviceID = remoteHubId;
        repository.requestHubUserStatus(remoteHubId, userId);
    }

    @Override
    public void onHubAvailable() {
        repository.requestLiveFeedToken(deviceID);
    }

    @Override
    public void startLiveFeed() {
        if (deviceID != null && twilioToken != null) {
            if(IS_AGORA_ENABLED) {
                view.joinAgoraRoom(twilioToken);
            } else {
                view.joinRoom(twilioToken);
            }
        }
    }

    @Override
    public void onLiveFeedTokenSuccess(String token) {
        this.twilioToken = token;
        repository.updateHubStatus(true);
        view.requestLiveFeedParams();
    }

    void initiateLiveFeed(boolean contactLiveFeed, String contactUserId) {
        repository.startLiveFeed(deviceID, contactLiveFeed, contactUserId);
    }

    @Override
    public void onLiveFeedTokenError(String errorMessage) {
        view.onError(errorMessage);
    }

    @Override
    public void connectToRoom(String twilioToken, String roomName) {
        configureAudio(false);
        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(twilioToken)
                .roomName(roomName);
        /*
         * Add local audio track to connect options to share with participants.
         */
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }

        room = Video.connect(view.getParentActivity(), connectOptionsBuilder.build(), roomListener());
    }

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            view.enableView();
            timerHandler.postDelayed(initializeRunnable, AUTO_CALL_DISCONNECT);
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            view.onConnectedToRoom();
            view.enableView();
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            super.onUserOffline(uid, reason);
            view.getParentActivity().runOnUiThread(() -> {
                Log.i("AgoraCall", "AgoraCall::onUserOffline :" +  "for " + uid);
                endCall();
                view.getParentActivity().onEndLiveFeed();
                view.onBackPressed();
            });
        }
    };



    @Override
    public void connectToAgoraRoom(String agoraToken, String roomName, int uid) {

        initializeEngine();
        mRtcEngine.enableAudio();
        joinChannel(agoraToken, roomName, uid);
        view.getParentActivity().runOnUiThread(() -> view.enableView());

    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(view.getParentActivity(), view.getParentActivity().getString(R.string.agora_app_id), mRtcEventHandler);
            mRtcEngine.enableAudio();
            mRtcEngine.muteLocalAudioStream(false);
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        } catch (Exception e) {
            Log.e("LiveFeedPresenter", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void joinChannel(String agoraToken, String roomName, int uid) {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
      /*  String token = view.getParentActivity().getString(R.string.agora_temp_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }*/
        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        Log.i("AgoraCall", "AgoraCall::joinChannel");
        mRtcEngine.joinChannel(agoraToken, roomName, "Extra Optional Data", uid, option);
    }






    /*
     * Room events listener
     */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                localParticipant = room.getLocalParticipant();

                mediaPlayerUtil.setPlayerLooping(true);
                mediaPlayerUtil.startPlayer();

                // Publish local audio immediately once we have the participant so the other side hears us.
                changeMicState();

                timerHandler.postDelayed(initializeRunnable,AUTO_CALL_DISCONNECT);
                view.enableView();
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                configureAudio(false);
                endCall();
                view.onError(e.getMessage());
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                localParticipant = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false);
//                    view.onMoveLocalVideoToPrimaryView();
                }

                view.getParentActivity().onBackPressed();
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onReconnected(@NonNull Room room) {

            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
//                removeRemoteParticipant(remoteParticipant);
                endCall();
                view.onError(view.getString(R.string.call_reject));
            }

            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.i("Recording Started", "");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.i("RecordingStopped", "");
            }
        };
    }

    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        participantIdentity = remoteParticipant.getIdentity();
        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Only render video tracks that are subscribed to
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
//                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(remoteParticipantListener());
    }

    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {


            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {
                participantAudioTrack = remoteAudioTrack;
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, TwilioException twilioException) {

            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
//                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, TwilioException twilioException) {

            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
//                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, TwilioException twilioException) {

            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                if (remoteParticipant.isConnected()) {
//                    view.removeVideoPreview(false);
                }
            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {
                if (remoteParticipant.isConnected()) {
//                    view.removeVideoPreview(true);
                }
            }
        };
    }

    /*
     * Set primary view as renderer for participant video track
     */
//    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
//        view.onMoveLocalVideoToThumbnailView();
//        view.onAddPrimaryRenderer(videoTrack);
//    }

    /*
     * Called when remote participant leaves the room
     */
//    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
//
//        if (!remoteParticipant.getIdentity().equals(participantIdentity)) {
//            return;
//        }
//
//        /*
//         * Remove remote participant renderer
//         */
//        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
//            RemoteVideoTrackPublication remoteVideoTrackPublication =
//                    remoteParticipant.getRemoteVideoTracks().get(0);
//
//            /*
//             * Remove video only if subscribed to participant track
//             */
//            if (remoteVideoTrackPublication.isTrackSubscribed()) {
//                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
//            }
//        }
//        view.onMoveLocalVideoToPrimaryView();
//    }

//    private void removeParticipantVideo(VideoTrack videoTrack) {
//        view.onRemoveParticipantVideo(videoTrack);
//    }

    private void configureAudio(boolean enable) {

        /*
         * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
         * to be in this mode when playout and/or recording starts for the best
         * possible VoIP performance. Some devices have difficulties with
         * speaker mode if this is not set.
         */

        //Set Speaker
        audioManager.setSpeakerphoneOn(enable);
        //Local Audio Track
        if (localAudioTrack != null) {
            localAudioTrack.enable(enable);
        }
    }

//    @Override
//    public void moveVideoStreamView(VideoView primaryVideoView, VideoView thumbnailVideoView) {
//        if (localVideoTrack != null) {
//            localVideoTrack.removeRenderer(primaryVideoView);
//            localVideoTrack.addRenderer(thumbnailVideoView);
//        }
//    }
//
//    @Override
//    public void addRenderer(VideoTrack videoTrack, VideoView videoView) {
//        videoTrack.addRenderer(videoView);
//    }

    @Override
    public void endCall() {

        /*
        Update The Hub Status
         */
        repository.updateHubStatus(false);
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */

        if (!IS_AGORA_ENABLED && room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }

        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
//            timerHandler.removeCallbacks(timerRunnable);
            if(!IS_AGORA_ENABLED)
                mediaPlayerUtil.stopPlayer();
        }
        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        if (!IS_AGORA_ENABLED && localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }

        if(IS_AGORA_ENABLED){
            Log.i("AgoraCall", "AgoraCall::leaveChannel");
            if (mRtcEngine != null) {
                mRtcEngine.muteLocalAudioStream(true);
                mRtcEngine.leaveChannel();
                mRtcEngine = null;
            }
        }
    }

    @Override
    public void enableAudio(boolean enable) {
        if (IS_AGORA_ENABLED) {
            if (mRtcEngine != null) {
                mRtcEngine.enableLocalAudio(enable);
            }
        } else {
            if (localAudioTrack != null) {
                localAudioTrack.enable(enable);
            }
        }
    }

    @Override
    public void onHubStatusUpdateError(String error_message) {
        view.onError(error_message);
    }

    @Override
    public void createAudioAndVideoTracks() {

//        //Aspect Ratio for UpStream Video
//        AspectRatio aspectRatio = new AspectRatio(9, 16);
//        VideoDimensions minVideoDimensions = new VideoDimensions(288, 352);
//        VideoDimensions maxVideoDimensions = new VideoDimensions(640, 480);
//
//        VideoConstraints videoConstraints = new VideoConstraints.Builder()
//                .aspectRatio(aspectRatio)
//                .minVideoDimensions(minVideoDimensions)
//                .maxVideoDimensions(maxVideoDimensions)
//                .minFps(5)
//                .maxFps(24)
//                .build();

        // Use software Acoustic Echo Cancellation
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);

        // Use software Noise Suppression
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);

        // Use software Auto Gain Control
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);

        localAudioTrack = LocalAudioTrack.create(view.getParentActivity(), true);

        // Share your camera
        //FIXME getVideoCapturer() NPE
//        localVideoTrack = LocalVideoTrack.create(view.getParentActivity(), true, cameraCapturerCompat.getVideoCapturer(), videoConstraints);
//
//        view.onMirrorPrimaryVideoStream(true);
//
//        localVideoTrack.addRenderer(videoView);
    }

//    @Override
//    public boolean checkCameraSource(CameraCapturer.CameraSource cameraSource) {
//        return cameraCapturerCompat.getCameraSource() == cameraSource;
//    }
//
//    @Override
//    public void releaseVideoStreamingResources() {
//        /*
//         * Release the local video track before going in the background. This ensures that the
//         * camera can be used by other applications while this app is in the background.
//         */
//        if (localVideoTrack != null) {
//            /*
//             * If this local video track is being shared in a Room, remove from local
//             * participant before releasing the video track. Participants will be notified that
//             * the track has been removed.
//             */
//            try {
//                if (localParticipant != null && localParticipant.getVideoTracks().size() > 0) {
//                    for (int index = 0; index < localParticipant.getVideoTracks().size(); index++) {
//                        localParticipant.getVideoTracks().remove(index);
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//            localVideoTrack.release();
//            localVideoTrack = null;
//        }
//    }

    @Override
    public void onConnectCall() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }
        view.showMessage(view.getString(R.string.call_accept));
        configureAudio(true);
        mediaPlayerUtil.stopPlayer();
        changeMicState();
        view.onConnectingToStream(view.getString(R.string.connecting));
    }

    private void changeMicState() {
        if (localParticipant != null && localAudioTrack != null) {
            localParticipant.publishTrack(localAudioTrack);
        }
    }

    @Override
    public void enableSpeaker(boolean enable) {
        if (IS_AGORA_ENABLED) {
            if (mRtcEngine != null) {
                mRtcEngine.setEnableSpeakerphone(enable);
            }
        } else {
            if (participantAudioTrack != null) {
                participantAudioTrack.enablePlayback(enable);
            }
        }
    }

//    @Override
//    public void enableVideo(boolean enable) {
//        if (localVideoTrack != null) {
//            localVideoTrack.enable(enable);
//        }
//    }

    public JsonObject toRotateCamera(String hubId, int angle, int isRotating) {
        JsonObject hubCameraPanObj = new JsonObject();
        JsonObject direction = new JsonObject();

        direction.addProperty("direction", angle);
        direction.addProperty("rotation_status", isRotating);
        hubCameraPanObj.addProperty("receiver_hub_id", hubId);
        hubCameraPanObj.add("data", direction);

        LoggerWrapper.i("Rotation", hubCameraPanObj.toString());
        return hubCameraPanObj;
    }

    @Override
    public void processActions(IntentInfo mIntentInfo) {
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        repository.repositoryCleanUps();
    }
}
