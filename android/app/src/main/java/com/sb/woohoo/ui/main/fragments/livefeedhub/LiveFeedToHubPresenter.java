package com.sb.woohoo.ui.main.fragments.livefeedhub;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonObject;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;

import javax.inject.Inject;

import static com.sb.woohoo.utils.Constants.AUTO_CALL_DISCONNECT;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

import androidx.annotation.NonNull;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class LiveFeedToHubPresenter
        extends BasePresenter
        implements LiveFeedToHubContract.Action {

    private final String CAMERA_ROTATE_EVENT = "clientCameraPanEvent";
    private static int DELAY = 1000;
    //CONSTANTS
    private final int CAMERA_HANDLER_INTERVAL = 1000;
    @Inject
    MediaPlayerUtil mediaPlayerUtil;
    private LiveFeedToHubRepository repository;
    private LiveFeedToHubFragment view;
    private AudioManager audioManager;
    //Time counter init for sync
    private int countDownTimer = 12;
    private String participantIdentity;
    private boolean disconnectedFromOnDestroy;
    private CameraCapturerCompat cameraCapturerCompat;
    private Handler timerHandler = new Handler();
    private String deviceID;
    private String twilioToken;
    private int retryTimer = 10;

    private Runnable timerRunnable = new Runnable() {
        public void run() {
            countDownTimer = countDownTimer - 1;
//            view.onUpdateCountDownTimer(String.valueOf(countDownTimer));
            if (countDownTimer == 0) {
                if (view != null && view.getActivity() != null && !view.isDetached())
                    view.showMessage(view.getString(R.string.user_not_pick_call));
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
    private RtcEngine mRtcEngine = null;

    @Inject
    public LiveFeedToHubPresenter(LiveFeedToHubFragment fragment, LiveFeedToHubRepository repository,
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
    public void getHubUserStatus(String remoteHubId, String userId) {
        this.deviceID = remoteHubId;
        repository.requestHubUserStatus(remoteHubId, userId);
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
    public void onHubStatusError(String message) {
        view.onError(message);
        Log.i("AgoraCall", "onHubStatusError");
        endCall();
    }

    @Override
    public void onHubAvailable() {
        repository.requestLiveFeedToken(deviceID);
    }

    @Override
    public void startLiveFeed() {
        if (deviceID != null && twilioToken != null) {
            view.joinAgoraRoom(twilioToken);
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


    IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.i("AgoraCall", "AgoraCall::onJoinChannelSuccess : "  + twilioToken + ", roomName: " + channel + ", uid: " + uid);

            view.getParentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mRtcEngine!=null)
                        view.setupLocalVideo(mRtcEngine);
                }
            });

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i("AgoraCall", "AgoraCall::onUserJoined : " + uid);
            if(mRtcEngine!=null)
                view.setupRemoteVideo(uid, mRtcEngine);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            //view.onRemoteUserLeft(uid, mRtcEngine);
            //endCall();
            view.getParentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("AgoraCall", "AgoraCall::onUserOffline :" +  "for " + uid);
                    endCall();
                    view.getParentActivity().onEndLiveFeed();
                    view.onBackPressed();
                }
            });
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.i("AgoraCall", "AgoraCall::onError : "  + err);

        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            Log.i("AgoraCall", "AgoraCall::onConnectionStateChanged state: "  + state +" , reason:" + reason);

        }

        @Override
        public void onLocalVideoStateChanged(int localVideoState, int error) {
            super.onLocalVideoStateChanged(localVideoState, error);
            Log.i("AgoraCall", "AgoraCall::onLocalVideoStateChanged : "  + localVideoState + ", error:" + error);

        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            Log.i("AgoraCall", "AgoraCall::onRemoteVideoStateChanged : uid:" + uid + ", state:"  + state + ", error:" + reason);

        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
            Log.i("AgoraCall", "AgoraCall::onFirstRemoteVideoFrame : uid:" + uid);

        }

        @Override
        public void onFirstLocalVideoFramePublished(int elapsed) {
            super.onFirstLocalVideoFramePublished(elapsed);
            Log.i("AgoraCall", "AgoraCall::onFirstLocalVideoFramePublished");

        }

        @Override
        public void onLocalAudioStateChanged(int state, int error) {
            super.onLocalAudioStateChanged(state, error);
            Log.i("AgoraCall", "AgoraCall::onLocalAudioStateChanged: state: " +state + ", error: " + error);

        }

        @Override
        public void onConnectionLost() {
            super.onConnectionLost();
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
            Log.i("AgoraCall", "AgoraCall::onRemoteAudioStateChanged: uid: " + uid + ", state:" +state + ", reason: " + reason);

        }

        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);
            Log.i("AgoraCall", "AgoraCall::onRejoinChannelSuccess: uid: " + uid + ", channel:" +channel);

        }
    };


    @Override
    public void connectToAgoraRoom(String twilioToken, String roomName, int uid) {
        initializeEngine();
        Log.i("AgoraCall", "AgoraCall::connectToAgoraRoom : "  + twilioToken + ", roomName: " + roomName + ", uid: " + uid);
        joinChannel(twilioToken, roomName, uid);
        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.enableView();
                //timerHandler.postDelayed(initializeRunnable, AUTO_CALL_DISCONNECT); --161020
            }
        });
    }


    private void initializeEngine() {
        Log.i("AgoraCall", "AgoraCall::initializeEngine");

        try {
            mRtcEngine = RtcEngine.create(view.getParentActivity(), view.getParentActivity().getString(R.string.agora_app_id), mRtcEventHandler);
            //mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            //mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER);
            mRtcEngine.enableVideo();
            mRtcEngine.enableAudio();
            mRtcEngine.muteLocalVideoStream(false);
            mRtcEngine.muteLocalAudioStream(false);
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
            /*mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));*/
        } catch (Exception e) {
            Log.e("LiveFeedPresenter", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }



    private void joinChannel(String token, String hubId, int uid) {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        Log.i("AgoraCall", "AgoraCall::joinChannel");
        mRtcEngine.joinChannel(token, hubId, null, uid, option);
    }

    private void configureAudio(boolean enable) {

        /*
         * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
         * to be in this mode when playout and/or recording starts for the best
         * possible VoIP performance. Some devices have difficulties with
         * speaker mode if this is not set.
         */

        //Set Speaker
        audioManager.setSpeakerphoneOn(enable);
    }

    private void leaveChannel() {
        Log.i("AgoraCall", "AgoraCall::leaveChannel");
        if(mRtcEngine != null) {
            mRtcEngine.muteLocalAudioStream(true);
            mRtcEngine.muteLocalVideoStream(true);
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
        }
    }

    @Override
    public void endCall() {
        Log.i("AgoraCall", "AgoraCall::endCall");

         /*
        Update The Hub Status
         */
        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.removeVideo();
            }
        });
        leaveChannel();

        repository.updateHubStatus(false);
        //leaveChannel();
        disconnectedFromOnDestroy = true;
        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }


    }

    @Override
    public void enableAudio(boolean enable) {
        if(mRtcEngine != null) {
            mRtcEngine.muteLocalVideoStream(enable);
        }
    }

    @Override
    public void onHubStatusUpdateError(String error_message) {
        view.onError(error_message);
    }

    @Override
    public void onConnectCall() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }
        view.showMessage(view.getString(R.string.call_accept));

        configureAudio(true);
        mediaPlayerUtil.stopPlayer();
        view.onConnectingToStream(view.getString(R.string.connecting));
    }

    @Override
    public void enableSpeaker(boolean enable) {
        if(mRtcEngine != null) {
            mRtcEngine.setEnableSpeakerphone(enable);
        }
    }

    @Override
    public void enableVideo(boolean enable) {
        if(mRtcEngine != null) {
            mRtcEngine.muteLocalVideoStream(!enable);
        }
    }

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
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        repository.repositoryCleanUps();
    }
}