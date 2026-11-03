package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon;

import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.CameraCapturerCompat;
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
import com.twilio.video.VideoRenderer;

import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.Collections;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class SurveillancePresenter
        extends BasePresenter
        implements SurveillanceContract.Actions {

    //Constants
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";

    private SurveillanceRepository surveillanceRepository;
    private AudioManager audioManager;
    private SurveillanceFragment view;
    private CameraCapturerCompat cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private Room room;
    private LocalParticipant localParticipant;
    private boolean disconnectedFromOnDestroy = false;
    private String participantIdentity;
    private RemoteAudioTrack participantAudioTrack;

    @Inject
    public SurveillancePresenter(SurveillanceFragment fragment, SurveillanceRepository surveillanceRepository,
                                 AudioManager audioManager, CameraCapturerCompat cameraCapturerCompat) {
        this.surveillanceRepository = surveillanceRepository;
        this.view = fragment;
        this.audioManager = audioManager;
        this.cameraCapturerCompat = cameraCapturerCompat;
        surveillanceRepository.onAttach(this);
        this.cameraCapturerCompat.initializeCameraCapturerCompat(view.getContext());
    }


    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void updateHubStatus(boolean status) {
        surveillanceRepository.updateHubStatus(status);
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
            connectOptionsBuilder
                    .videoTracks(Collections.singletonList(localVideoTrack));
        }

        room = Video.connect(view.getParentActivity(), connectOptionsBuilder.build(), roomListener());
    }

    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(@NonNull Room room) {

                localParticipant = room.getLocalParticipant();
                new Handler().postDelayed(() -> configureAudio(true), 8000);

                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException e) {
                configureAudio(false);
                endCall();
            }

            @Override
            public void onDisconnected(@NonNull Room room, TwilioException e) {
                localParticipant = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false);
                }

                if (e != null) {
                    view.onAPICallError(e.getMessage());
                }
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                configureAudio(false);
                removeRemoteParticipant(remoteParticipant);
                endCall();
                view.updateStatus();
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                view.onAPICallError(view.getString(R.string.Reconnecting));
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                view.onAPICallError("Reconnected");
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.i("Recording Started", "");
            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                LoggerWrapper.i("RecordingStopped", "");
            }
        };
    }

    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        participantIdentity = remoteParticipant.getIdentity();

        if (localParticipant != null) {
            localParticipant.getAudioTracks();
            if (!localParticipant.getAudioTracks().isEmpty()) {
                if (localAudioTrack != null) {
                    localParticipant.publishTrack(localAudioTrack);
                }
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                LoggerWrapper.i("OnAudioTrackPublished", "");
            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                LoggerWrapper.i("onAudioTrackUnpublished", "");
            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                             @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
                LoggerWrapper.i("onDataTrackPublished", "");
            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
                LoggerWrapper.i("onDataTrackUnpublished", "");
            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                LoggerWrapper.i("onVideoTrackPublished", "");
            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                LoggerWrapper.i("onVideoTrackUnpublished", "");
            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               @NonNull RemoteAudioTrack remoteAudioTrack) {
                LoggerWrapper.i("onAudioTrackSubscribed", "");
                remoteAudioTrack.enablePlayback(false);
            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                 @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 @NonNull RemoteAudioTrack remoteAudioTrack) {
                LoggerWrapper.i("onAudioTrckUnsubscribed", "");
            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       @NonNull TwilioException twilioException) {
                LoggerWrapper.i("AudioTrackSubFailed", "");
            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                              @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                              @NonNull RemoteDataTrack remoteDataTrack) {
            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                                @NonNull RemoteDataTrack remoteDataTrack) {
            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                      @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                                      @NonNull TwilioException twilioException) {
            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               @NonNull RemoteVideoTrack remoteVideoTrack) {
                LoggerWrapper.i("onVideoTrackSubscribed", "");
            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                                 @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 @NonNull RemoteVideoTrack remoteVideoTrack) {
                LoggerWrapper.i("onVideoTrackUnsub", "");
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       @NonNull TwilioException twilioException) {
                LoggerWrapper.i("VideoTrackSubFailed", "");
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

            }

            @Override
            public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant,
                                             @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        });

    }

    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getIdentity().equals(participantIdentity)) {
            return;
        }
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
        //Local Audio Track
        if (localAudioTrack != null) {
            localAudioTrack.enable(enable);
        }
    }

    @Override
    public void reCreateVideoStreamingResources(VideoRenderer videoView) {

    }

    @Override
    public void endCall() {

        LoggerWrapper.d("endcall", "endcall");
        updateHubStatus(false);
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
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
        if (participantAudioTrack != null) {
            participantAudioTrack = null;
        }

    }

    @Override
    public void createAudioAndVideoTracks() {

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

//        localVideoTrack.addRenderer(videoView);
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
    public void onHubStatusUpdateSuccess() {
        LoggerWrapper.i("HubStatusUpdateSuccess", "true");

    }

    @Override
    public void onHubStatusUpdateError(String message) {
        LoggerWrapper.i("HubStatusUpdateError", "Could not update Hub Status");
        view.onAPICallError(message);

    }

    @Override
    public void onAPICallError(String errorMessage) {
        view.onAPICallError(errorMessage);
    }

}
