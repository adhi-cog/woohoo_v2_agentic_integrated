package com.sb.woohoo.ui.main.fragments.livefeedaudio;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.sip.LinphoneManager;
import com.sb.woohoo.utils.CameraCapturerCompat;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import org.webrtc.voiceengine.WebRtcAudioUtils;
import org.linphone.core.Core;

import java.util.Collections;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import static com.sb.woohoo.utils.Constants.AUTO_CALL_DISCONNECT;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by Satyaraj on 3/17/2018.
 */

public class LiveFeedAudioPresenter extends BasePresenter implements LiveFeedAudioContract.Action {

    private final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private final String LOCAL_VIDEO_TRACK_NAME = "camera";
    private int DELAY = 1000;
    @Inject
    MediaPlayerUtil mediaPlayerUtil;
    private LiveFeedAudioRepository liveFeedAudioRepository;
    private LiveFeedAudioFragment view;
    private AudioManager audioManager;
    //Time counter init for sync
    private int countDownTimer = 12;
    private String participantIdentity;
    private boolean disconnectedFromOnDestroy;
    private CameraCapturerCompat cameraCapturerCompat;
    private Handler timerHandler = new Handler();
    private boolean initTimer = false;
    public boolean isSipMode = false;
    /********************{@Timer Runnable}*********************/

    private Runnable timerRunnable = new Runnable() {
        public void run() {
            countDownTimer --;
//            view.onUpdateCountDownTimer(String.valueOf(countDownTimer));
            if (countDownTimer == 0) {
                view.onBackPressed();
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(this, DELAY);
            }
        }
    };

    private Runnable initializeRunnable = () -> {
        if (initTimer) {
            timerHandler.postDelayed(timerRunnable, DELAY);
        }
    };

    @Inject
    public LiveFeedAudioPresenter(LiveFeedAudioFragment fragment, LiveFeedAudioRepository liveFeedAudioRepository,
                                  AudioManager audioManager, CameraCapturerCompat cameraCapturerCompat) {
        this.liveFeedAudioRepository = liveFeedAudioRepository;
        this.view = fragment;
        this.audioManager = audioManager;
        this.cameraCapturerCompat = cameraCapturerCompat;
        liveFeedAudioRepository.onAttach(this);
        this.cameraCapturerCompat.initializeCameraCapturerCompat(view.requireContext());
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

    }

    @Override
    public void endCall() {

        liveFeedAudioRepository.updateHubStatus(false);


        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);

        }



        if(isSipMode){
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
        if (isSipMode) {
            Core core = LinphoneManager.getCore();
            if (core != null) {
                core.enableMic(enable);
            }
        } else if (mRtcEngine != null) {
            mRtcEngine.enableLocalAudio(enable);
        }
    }

    @Override
    public void onConnectCall() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(initializeRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }
        if(!IS_AGORA_ENABLED) {
           // mediaPlayerUtil.stopPlayer();
          //  configureAudio(true);
        }
        mediaPlayerUtil.stopPlayer();
        configureAudio(true);
        view.onConnectingToStream();
    }

    @Override
    public void enableSpeaker(boolean enable) {
        if (isSipMode) {
            if (LinphoneManager.getAudioManager() != null) {
                if (enable) {
                    LinphoneManager.getAudioManager().routeAudioToSpeaker();
                } else {
                    LinphoneManager.getAudioManager().routeAudioToEarPiece();
                }
            }
        } else if (mRtcEngine != null) {
            mRtcEngine.setEnableSpeakerphone(enable);
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void sendAcceptActionObj(String senderClientId, String senderHubId, String receiverHubId, String token, boolean contactLiveFeed, String contactUserId) {

        LiveFeedActions liveFeedActions = new LiveFeedActions();
        liveFeedActions.token = token;
        liveFeedActions.senderHubId = senderHubId;
        liveFeedActions.senderClientId = senderClientId;
        liveFeedActions.receiverHubId = receiverHubId;
        liveFeedActions.contactLiveFeed = contactLiveFeed;
        liveFeedActions.contactUserId = contactUserId;

        Gson gson = new Gson();
        String object = gson.toJson(liveFeedActions);
        JsonObject jsonObject = (new JsonParser()).parse(object).getAsJsonObject();
        LoggerWrapper.i("LiveFeed_Actions", jsonObject.toString());

        view.getParentActivity().emitEventToServer("dropInAccept", jsonObject);
    }

    public void setConnectingScreenTimer(boolean enable) {
        initTimer = enable;
    }

    /************************************/
    private RtcEngine mRtcEngine;

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
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.i("AgoraCall", "AgoraCall::onError : " + err);
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            Log.i("AgoraCall", "AgoraCall::onRemoteVideoStateChanged :" + state + "for " + uid);
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            Log.i("AgoraCall", "AgoraCall::onUserJoined :" + "for " + uid);
            //setupRemoteVideo(uid);
        }

        @Override
        public void onLocalAudioStateChanged(int state, int error) {
            super.onLocalAudioStateChanged(state, error);
            Log.i("AgoraCall", "AgoraCall::onLocalAudioStateChanged :" + state);
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

    public void initializeEngine(String agoraToken, String roomName, int uid) {
        try {
            mRtcEngine = RtcEngine.create(view.getParentActivity(), view.getParentActivity().getString(R.string.agora_app_id), mRtcEventHandler);
            mRtcEngine.disableAudio();
            //mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        } catch (Exception e) {
            Log.e("LiveFeedPresenter", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        joinChannel(agoraToken, roomName, uid);
    }

    public void connectToAgoraRoom(String agoraToken, String roomName, int uid) {
        //initializeEngine();
        mRtcEngine.enableAudio();
        //joinChannel(agoraToken, roomName, uid);
        setupLocalAudio();
        view.getParentActivity().runOnUiThread(() -> {
            view.onConnectedToRoom();
            view.enableView();
            //timerHandler.postDelayed(initializeRunnable, AUTO_CALL_DISCONNECT); --161020
        });
    }

    private void setupLocalAudio() {
        view.getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //view.thumbnailVideoViewAgora.setVisibility(View.VISIBLE);
                mRtcEngine.enableLocalAudio(true);
            }
        });
    }

    private void joinChannel(String agoraToken, String roomName, int uid) {
        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        mRtcEngine.joinChannel(agoraToken, roomName, null, uid, option);
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
}
