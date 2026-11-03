package com.sb.woohoo.ui.main.fragments.livefeed;

import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.LiveFeed.DROP_IN_ACCEPT;
import static com.sb.woohoo.utils.Constants.AUTO_CALL_DISCONNECT;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;
import com.twilio.video.AspectRatio;
import com.twilio.video.CameraCapturer;
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
import com.twilio.video.VideoConstraints;
import com.twilio.video.VideoDimensions;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;

import javax.inject.Inject;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by Satyaraj on 3/17/2018.
 */

public class LiveFeedPresenter extends BasePresenter implements LiveFeedContract.Action {

    private final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private final String LOCAL_VIDEO_TRACK_NAME = "camera";
    private int DELAY = 1000;
    @Inject
    MediaPlayerUtil mediaPlayerUtil;
    private Gson gson;
    private RemoteAudioTrack participantAudioTrack;
    private LiveFeedRepository liveFeedRepository;
    private LiveFeedFragment view;
    private AudioManager audioManager;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private LocalParticipant localParticipant;
    private Room room;
    //Time counter init for sync
    private int countDownTimer = 12;
    private int retryTimer = 10;
    private String participantIdentity;
    private boolean disconnectedFromOnDestroy;
    private CameraCapturerCompat cameraCapturerCompat;
    private Handler timerHandler = new Handler();
    private boolean initTimer = false;
    private boolean isUserConnected = false;
    /********************{@Timer Runnable}*********************/

    private Runnable timerRunnable = new Runnable() {
        public void run() {
            countDownTimer--;
            view.onUpdateCountDownTimer(String.valueOf(countDownTimer));
            if (countDownTimer == 0) {

                if (view.senderClientId != null && !view.senderClientId.equalsIgnoreCase("")) {
                    view.sendAction();
                    onConnectCall();
                } else {
                    view.onBackPressed();
                }
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(this, DELAY);
            }
        }
    };


    private Runnable initializeRunnable = new Runnable() {
        @Override
        public void run() {

            if (initTimer) {
                timerHandler.postDelayed(timerRunnable, DELAY);
            }
        }
    };
    private Runnable reconnectingRunnable = new Runnable() {
        @Override
        public void run() {
            retryTimer--;
            if (retryTimer == 0 && !isUserConnected) {
                view.onError(view.getString(R.string.internet_not_available));
                endCall();
            } else {
                timerHandler.postDelayed(this, DELAY);
            }
        }
    };

    @Inject
    public LiveFeedPresenter(LiveFeedFragment fragment, LiveFeedRepository liveFeedRepository,
                             AudioManager audioManager, CameraCapturerCompat cameraCapturerCompat, Gson gson) {
        this.liveFeedRepository = liveFeedRepository;
        this.view = fragment;
        this.audioManager = audioManager;
        this.cameraCapturerCompat = cameraCapturerCompat;
        liveFeedRepository.onAttach(this);
        this.cameraCapturerCompat.initializeCameraCapturerCompat(view.requireContext());
        this.gson = gson;
    }

    @Override
    public void setConnectingScreenTimer(boolean enable) {
        initTimer = enable;
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

    /*
     * Room events listener
     */

    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(@NonNull Room room) {
                localParticipant = room.getLocalParticipant();
                mediaPlayerUtil.setPlayerLooping(true);
                mediaPlayerUtil.startPlayer();
                // Publish local audio as soon as the participant is available so the far end hears us.
                changeMicState();
                view.onConnectedToRoom();
                view.enableView();
                timerHandler.postDelayed(initializeRunnable, AUTO_CALL_DISCONNECT);


                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException e) {
                configureAudio(false);
                view.onBackPressed();
            }

            @Override
            public void onDisconnected(@NonNull Room room, TwilioException e) {
                localParticipant = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false);
                    view.onMoveLocalVideoToPrimaryView();
                }
                view.onBackPressed();
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);
                endCall();
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                view.removeVideoPreview(true);
                isUserConnected = false;
                timerHandler.postDelayed(reconnectingRunnable, DELAY);
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                isUserConnected = true;
                timerHandler.removeCallbacks(reconnectingRunnable);
                view.removeVideoPreview(false);
                if (room.getState() == Room.State.CONNECTED && room.getRemoteParticipants().size() <= 1) {
                    view.onError(view.getString(R.string.call_disconnected_by_user));
                    endCall();
                }
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.d("Recording Started", "");
            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.d("RecordingStopped", "");
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
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
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
            public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               @NonNull RemoteAudioTrack remoteAudioTrack) {
                participantAudioTrack = remoteAudioTrack;
            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       @NonNull TwilioException twilioException) {

            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                 @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               @NonNull RemoteVideoTrack remoteVideoTrack) {
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       @NonNull TwilioException twilioException) {

                view.onError(String.valueOf(remoteVideoTrackPublication.isTrackSubscribed()));

            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                 @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 @NonNull RemoteVideoTrack remoteVideoTrack) {
                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                             @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                              @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                      @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                                      @NonNull TwilioException twilioException) {

            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                                @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant,
                                            @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant,
                                             @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant,
                                            @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                if (remoteParticipant.isConnected()) {
                    view.removeVideoPreview(false);
                }
            }

            @Override
            public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant,
                                             @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                if (remoteParticipant.isConnected()) {
                    view.removeVideoPreview(true);
                }
            }
        };
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        view.onMoveLocalVideoToThumbnailView();
        view.onAddPrimaryRenderer(videoTrack);
    }

    /*
     * Called when remote participant leaves the room
     */
    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {

        if (!remoteParticipant.getIdentity().equals(participantIdentity)) {
            return;
        }

        /*
         * Remove remote participant renderer
         */
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        view.onMoveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        view.onRemoveParticipantVideo(videoTrack);
    }

    private void configureAudio(boolean enable) {

        /*
         * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
         * to be in this mode when playout and/or recording starts for the best
         * possible VoIP performance. Some devices have difficulties with
         * speaker mode if this is not set.
         */

//        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        //Set Speaker
        audioManager.setSpeakerphoneOn(enable);

        //Local Audio Track
        if (localAudioTrack != null) {
            localAudioTrack.enable(enable);
        }
    }

    @Override
    public void moveVideoStreamView(VideoView primaryVideoView, VideoView thumbnailVideoView) {
        if (localVideoTrack != null) {
            localVideoTrack.removeRenderer(primaryVideoView);
            localVideoTrack.addRenderer(thumbnailVideoView);
        }
    }

    @Override
    public void addRenderer(VideoTrack videoTrack, VideoView videoView) {
        videoTrack.addRenderer(videoView);
    }

    @Override
    public void endCall() {

        liveFeedRepository.updateHubStatus(false);

        if (com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED) {
            removeFromParent(mLocalVideo);
            mLocalVideo = null;
            removeFromParent(mRemoteVideo);
            mRemoteVideo = null;
            leaveChannel();
        } else {
            /*
             * Always disconnect from the room before leaving the Activity to
             * ensure any memory allocated to the Room resource is freed.
             */
            if (room != null && room.getState() != Room.State.DISCONNECTED) {
                room.disconnect();
                disconnectedFromOnDestroy = true;
            }

            if (timerHandler != null) {
                timerHandler.removeCallbacks(initializeRunnable);
                timerHandler.removeCallbacks(timerRunnable);
                mediaPlayerUtil.stopPlayer();
            }
        }

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void leaveChannel() {
        Log.i("AgoraCall", "AgoraCall::leaveChannel");
        if (mRtcEngine != null) {
            mRtcEngine.muteLocalAudioStream(true);
            mRtcEngine.muteLocalVideoStream(true);
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
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
    public void createAudioAndVideoTracks(VideoView videoView) {

        //Aspect Ratio for UpStream Video
        AspectRatio aspectRatio = new AspectRatio(9, 16);
        VideoDimensions minVideoDimensions = new VideoDimensions(288, 352);
        VideoDimensions maxVideoDimensions = new VideoDimensions(720, 1280);

        VideoConstraints videoConstraints = new VideoConstraints.Builder()
                .aspectRatio(aspectRatio)
                .minVideoDimensions(minVideoDimensions)
                .maxVideoDimensions(maxVideoDimensions)
                .minFps(5)
                .maxFps(24)
                .build();

        // Use software Acoustic Echo Cancellation
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);

        // Use software Noise Suppression
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);

        // Use software Auto Gain Control
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);

        localAudioTrack = LocalAudioTrack.create(view.getParentActivity(), true, LOCAL_AUDIO_TRACK_NAME);

        // Share your camera
        localVideoTrack = LocalVideoTrack.create(view.getParentActivity(), true, cameraCapturerCompat.getVideoCapturer(), videoConstraints, LOCAL_VIDEO_TRACK_NAME);

        view.onMirrorPrimaryVideoStream(true);

        localVideoTrack.addRenderer(videoView);
    }

    @Override
    public boolean checkCameraSource(CameraCapturer.CameraSource cameraSource) {
        return cameraCapturerCompat.getCameraSource() == cameraSource;
    }

    @Override
    public void releaseVideoStreamingResources() {
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        if (localVideoTrack != null) {
            /*
             * If this local video track is being shared in a Room, remove from local
             * participant before releasing the video track. Participants will be notified that
             * the track has been removed.
             */
            try {
                if (localParticipant != null && localParticipant.getVideoTracks().size() > 0) {
                    for (int index = 0; index < localParticipant.getVideoTracks().size(); index++) {
                        localParticipant.getVideoTracks().remove(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            localVideoTrack.release();
            localVideoTrack = null;
        }
    }

    @Override
    public void onConnectCall() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }
        if (IS_AGORA_ENABLED) {


        } else {
            mediaPlayerUtil.stopPlayer();
            configureAudio(true);
            changeMicState();
        }

//        mediaPlayerUtil.stopPlayer();
        view.onConnectingToStream();

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

    @Override
    public void enableVideo(boolean enable) {
        if (IS_AGORA_ENABLED) {
            if (mRtcEngine != null) {
                mRtcEngine.muteLocalVideoStream(!enable);
            }
        } else {
            if (localVideoTrack != null) {
                localVideoTrack.enable(enable);
            }
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void sendAcceptActionObj(String senderClientId, String senderHubId, String receiverHubId, String token,
                                    boolean contactLiveFeed, String contactUserId) {

        LiveFeedActions liveFeedActions = new LiveFeedActions();
        liveFeedActions.token = token;
        liveFeedActions.senderHubId = senderHubId;
        liveFeedActions.senderClientId = senderClientId;
        liveFeedActions.receiverHubId = receiverHubId;

        if (contactLiveFeed) {
            liveFeedActions.contactLiveFeed = contactLiveFeed;
            liveFeedActions.contactUserId = contactUserId;
        }

        String object = gson.toJson(liveFeedActions);
        JsonObject jsonObject = (new JsonParser()).parse(object).getAsJsonObject();
        LoggerWrapper.i("LiveFeed_Actions", jsonObject.toString());

        view.getParentActivity().emitEventToServer(DROP_IN_ACCEPT, jsonObject);
    }

    /********************************{ @JSON Emit User Action }**************************************/
    private class LiveFeedActions {
        @Expose
        String token;
        @Expose
        String senderHubId;
        @Expose
        String receiverHubId;
        @Expose
        String senderClientId;
        @Expose
        String contactUserId;
        @Expose
        boolean contactLiveFeed;

    }


    /************************************/
    private RtcEngine mRtcEngine;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
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
            Log.i("AgoraCall", "AgoraCall::onJoinChannelSuccess : channel " + channel + "for " + uid);

            //mediaPlayerUtil.setPlayerLooping(true);
            //mediaPlayerUtil.startPlayer();
            //setupLocalVideo();
        }

        @Override
        public void onError(int err) {
            super.onError(err);

            Log.i("AgoraCall", "AgoraCall::onError : " + err);
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            Log.i("AgoraCall", "AgoraCall::onRemoteVideoStateChanged :" + state + "for " + uid);
        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
            Log.i("AgoraCall", "AgoraCall::onFirstRemoteVideoFrame for " + uid);
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            Log.i("AgoraCall", "AgoraCall::onUserJoined :" + "for " + uid);
            setupRemoteVideo(uid);
        }

        @Override
        public void onLocalVideoStateChanged(int localVideoState, int error) {
            super.onLocalVideoStateChanged(localVideoState, error);
            Log.i("AgoraCall", "AgoraCall::onLocalVideoStateChanged :" + "for " + localVideoState);
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
            view.getParentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("AgoraCall", "AgoraCall::onUserOffline :" + "for " + uid);
                    endCall();
                    view.getParentActivity().onEndLiveFeed();
                    view.onBackPressed();
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = view.binding.fragmentContentLiveFeed.primaryVideoViewAgora;

                if (mLocalVideo != null) {
                    if (parent.indexOfChild(mLocalVideo.view) > -1) {
                        parent = view.binding.fragmentContentLiveFeed.thumbnailVideoViewAgora;

                    }
                } else {
                    setupLocalVideo();
                }

                if (mRemoteVideo != null) {
                    return;
                }

                final SurfaceView sView = RtcEngine.CreateRendererView(view.getContext());
                if(sView != null) {
                    sView.setZOrderMediaOverlay(parent == view.binding.fragmentContentLiveFeed.thumbnailVideoViewAgora);
                    parent.addView(sView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    mRemoteVideo = new VideoCanvas(sView, VideoCanvas.RENDER_MODE_FIT, uid);
                    mRtcEngine.setupRemoteVideo(mRemoteVideo);
                }
            }
        });
    }

    @Override
    public void connectToAgoraRoom(String agoraToken, String roomName, int uid) {

        //initializeEngine();
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio();
        //joinChannel(agoraToken, roomName        mRtcEngine.enableAudio();, uid);
        setupLocalVideo();
        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.onConnectedToRoom();
                view.enableView();
                //timerHandler.postDelayed(initializeRunnable, AUTO_CALL_DISCONNECT); --161020
            }
        });
    }

    public void initializeEngine(String agoraToken, String roomName, int uid) {
        try {
            mRtcEngine = RtcEngine.create(view.getParentActivity(), view.getParentActivity().getString(R.string.agora_app_id), mRtcEventHandler);
            //mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.disableVideo();
            mRtcEngine.disableAudio();
            //mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
         /*   mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));*/
        } catch (Exception e) {
            Log.e("LiveFeedPresenter", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        joinChannel(agoraToken, roomName, uid);
    }

    private void setupLocalVideo() {

        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.binding.fragmentContentLiveFeed.thumbnailVideoViewAgora.setVisibility(View.VISIBLE);
                final SurfaceView localView = RtcEngine.CreateRendererView(view.getContext());
                if (localView != null) {
                    localView.setZOrderMediaOverlay(true);
                    view.binding.fragmentContentLiveFeed.thumbnailVideoViewAgora.addView(localView);

                    mLocalVideo = new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(view.sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
                    mRtcEngine.setupLocalVideo(mLocalVideo);
                    mRtcEngine.enableLocalVideo(true);
                    mRtcEngine.enableLocalAudio(true);
                }
            }
        });
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
        option.autoSubscribeVideo = true;
        mRtcEngine.joinChannel(agoraToken, roomName, null, uid, option);
    }
}
