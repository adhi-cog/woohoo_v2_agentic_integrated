package com.sb.woohoo.ui.main.Interactors;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_ID;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_MUTE_STATE;
import static com.sb.woohoo.ui.main.HandlerManager.CAMERA_ROTATION;
import static com.sb.woohoo.ui.main.HandlerManager.WAKE_WORD_DETECTED;
import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.Hardware.CAMERA_PAN_EVENT_TO_HUB;
import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.System.SET_HUB_DETAILS;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_FOLLOWUP;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_NEWS;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.CALLBACK_NEWS_DETAILS;
import static com.sb.woohoo.utils.voice.PollyTextToSpeech.STREAM_MUSIC;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonObject;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.HandlerManager;
import com.sb.woohoo.ui.main.Interactors.listener.HelperListener;
import com.sb.woohoo.ui.main.Interactors.listener.PresenterListener;
import com.sb.woohoo.ui.main.MainPresenter;
import com.sb.woohoo.ui.main.callbacks.BrightnessCallback;
import com.sb.woohoo.ui.main.callbacks.CompletionCallback;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.music.MusicCallbackListener;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.helpers.AnimationsHelper;
import com.sb.woohoo.ui.main.helpers.AudioHelper;
import com.sb.woohoo.ui.main.helpers.BroadcastHelper;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.ui.main.helpers.DeviceHelper;
import com.sb.woohoo.ui.main.helpers.DrawerHelper;
import com.sb.woohoo.ui.main.helpers.GSpeechRecognizerHelper;
import com.sb.woohoo.ui.main.helpers.HandlerHelper;
import com.sb.woohoo.ui.main.helpers.HardwareHelper;
import com.sb.woohoo.ui.main.helpers.LiveFeedHelper;
import com.sb.woohoo.ui.main.helpers.MusicHelper;
import com.sb.woohoo.ui.main.helpers.PollyHelper;
import com.sb.woohoo.ui.main.helpers.SocketHelper;
import com.sb.woohoo.ui.main.helpers.TimerHelper;
import com.sb.woohoo.ui.main.helpers.WakeWordHelper;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.socketdata.Music;
import com.sb.woohoo.ui.main.pojo.socketevent.CameraRotationData;
import com.sb.woohoo.ui.main.pojo.socketevent.CameraRotationModel;
import com.sb.woohoo.ui.main.pojo.socketevent.CameraRotationResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedParticipantModel;
import com.sb.woohoo.utils.VolumeControl;
import com.sb.woohoo.utils.others.CameraUtils;
import com.sb.woohoo.utils.speech.SoundUtil;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.twilio.video.Camera2Capturer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class HelperInteractor implements PresenterListener.Helper {
    private static final long ASR_START_DELAY_MS = 700L;

    // Helpers.
    private AnimationsHelper mAnimationsHelper;
    private AudioHelper mAudioHelper;
    private BroadcastHelper mBroadcastHelper;
    private ConnectivityHelper mConnectivityHelper;
    private DeviceHelper mDeviceHelper;
    private DrawerHelper mDrawerHelper;
    private HandlerHelper mHandlerHelper;
    private HardwareHelper mHardwareHelper;
    private MusicHelper mMusicHelper;
    private TimerHelper mTimerHelper;
    private PollyHelper mPollyHelper;
    private SocketHelper mSocketHelper;
    private GSpeechRecognizerHelper mGSpeechRecognizerHelper;
    private LiveFeedHelper mLiveFeedHelper;
    private WakeWordHelper mWakeWordHelper;
    private Context mContext;
    private final Handler wakeWordRearmHandler = new Handler();
    private Runnable wakeWordRearmRunnable;
    private int wakeWordRearmAttempts = 0;
    private static final int WAKEWORD_REARM_DELAY_MS = 3000;
    private static final int WAKEWORD_REARM_RETRY_MS = 1000;
    private static final int WAKEWORD_REARM_MAX_ATTEMPTS = 6;

    // Listeners.
    private HelperListener mListener;

    private long lastUpdatedHubDetailsSocketTime = 0;


    // Others.
    private AtomicBoolean internetStatus;
    private BroadcastHelper.BroadcastListener mBroadcastListener = new BroadcastHelper.BroadcastListener() {

        @Override
        public void onBroadcastTrigger(Intent intent, String key) {
            try {
                mListener.onAlert(intent, key);
            } catch (IOException | ClassNotFoundException e) {
                LoggerWrapper.e(MainPresenter.class.getSimpleName(), e.getMessage());
            }
        }

        @Override
        public void onBatteryStateReceived(Intent intent) {
        }

        @Override
        public void onDateChangeReceived(Intent intent) {
        }

        @Override
        public void onIntentNotFound(Intent intent) {
        }

        @Override
        public void onVolumeListener() {
        }

        @Override
        public void onActionNotFoundForBroadcast(Intent intent) {
        }
    };
    private WakeWordHelper.WakeWordCallback mWakeWordListener = new WakeWordHelper.WakeWordCallback() {

        @Override
        public void onWakeWordDetected() {
            mHandlerHelper.getForegroundHandler().sendEmptyMessage(WAKE_WORD_DETECTED);
            Log.e("VoiceGeneyWakrWprd", "Genny");
        }
    };
    private ConnectivityHelper.ConnectivityHandlerCallback mConnectivityListener = new ConnectivityHelper.ConnectivityHandlerCallback() {
        @Override
        public void internetDown() {
            internetStatus.compareAndSet(true, false);
            mAnimationsHelper.getNetworkIssuesHandler().showError(R.string.no_internet);
            stopAsr();
            stopWakeWordAnimation(false);
            updateStatusOnFirestore(false);
        }

        @Override
        public void serverDown() {
            internetStatus.compareAndSet(true, false);
            mAnimationsHelper.getNetworkIssuesHandler().showError(R.string.network_issues);

            mHandlerHelper.getForegroundHandler().postDelayed(() -> mConnectivityHelper.checkInternet(), 1000 * 10);
        }

        @Override
        public void internetUp() {
            internetStatus.compareAndSet(false, true);
            if (WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
                mAnimationsHelper.getNetworkIssuesHandler().showMuteError(internetStatus.get());
            } else {
                mAnimationsHelper.getNetworkIssuesHandler().dismissError();
            }
            mListener.checkAndUpdateStatus();
            updateStatusOnFirestore(true);
        }
    };

    private void updateStatusOnFirestore(boolean isConnected) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("deviceId", WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""));
            obj.put("userId", WooHooApp.getSharedPrefsHelper().get(PREF_KEY_USER_ID, null));
            obj.put("connected", isConnected);

            Map<String, Object> user = new HashMap<>();
            user.put(Long.toString(System.currentTimeMillis()), obj.toString());

            // Firestore event entry
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("hub_status")
                    .document(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""))
                    .set(user, SetOptions.merge());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private DeviceHelper.DeviceListener mDeviceListener = new DeviceHelper.DeviceListener() {
        @Override
        public void emitLightState(JsonObject object) {
            mSocketHelper.emitEventToServer(SocketHelper.SocketEvents.Lights.CHANGE_DEVICE_STATE, object);
        }
    };
    private DrawerHelper.DrawerCallbacks mDrawerListener = new DrawerHelper.DrawerCallbacks() {
        @Override
        public void onVolumeSeek(int volume) {
            mHardwareHelper.setVolume(volume);
        }

        @Override
        public void onBrightnessSeek(int brightness) {
            mHardwareHelper.setBrightness(brightness);
        }

        @Override
        public void onDrawerOpen() {
            mAnimationsHelper.getDrawerAnimationsHandler().onDrawerOpen();
        }

        @Override
        public void onDrawerClose() {
            mAnimationsHelper.getDrawerAnimationsHandler().onDrawerClose();
        }
    };
    private VolumeControl.VolumeControlCallbacks mVolumeControlListener = new VolumeControl.VolumeControlCallbacks() {
        @Override
        public void onVolumeUpdate(int streamVolume, int maxVolume) {
            if (streamVolume > 0)
                emitEventToServer(SocketHelper.SocketEvents.Hardware.MUTE_UNMUTE, getMuteData(false));
            else
                emitEventToServer(SocketHelper.SocketEvents.Hardware.MUTE_UNMUTE, getMuteData(true));
            mDrawerHelper.updateSeekBar(AudioManager.STREAM_MUSIC, streamVolume, maxVolume);
        }

        @Override
        public void onVolumeChanged(int streamType, int streamVolume) {
            if (streamVolume > 0)
                emitEventToServer(SocketHelper.SocketEvents.Hardware.MUTE_UNMUTE, getMuteData(false));
            else
                emitEventToServer(SocketHelper.SocketEvents.Hardware.MUTE_UNMUTE, getMuteData(true));
            mDrawerHelper.updateSeekBar(streamType, streamVolume, mHardwareHelper.getMaxVolume());
        }

        @Override
        public void onVolumeMuted(int streamType, boolean streamMuteStatus) {
            emitEventToServer(SocketHelper.SocketEvents.Hardware.MUTE_UNMUTE, getMuteData(true));
        }

        @Override
        public void onVolumeError(int error, String message) {
        }

        @Override
        public void onVolumeResponse(String response) {
        }

        @Override
        public void onStreamChange(MediaPlayer mediaPlayer, int streamType) {
            // FIXME on Stream change??
        }

        @Override
        public void updateDrawer(int volume) {
            mDrawerHelper.updateSeekBar(AudioManager.STREAM_MUSIC, volume, mHardwareHelper.getMaxVolume());
        }
    };
    private Camera2Capturer.Listener mCameraCapturerListener = new Camera2Capturer.Listener() {
        @Override
        public void onFirstFrameAvailable() {
        }

        @Override
        public void onCameraSwitched(String newCameraId) {
        }

        @Override
        public void onError(Camera2Capturer.Exception camera2CapturerException) {
        }
    };
    private HandlerManager.ForegroundHandlerCallbacks mForegroundHandlerListener = new HandlerManager.ForegroundHandlerCallbacks() {
        @Override
        public void onWakeWord() {
            if (internetStatus.get()) {
                if (mGSpeechRecognizerHelper.getASRProcessingStatus()) {
                    Log.d("ASR_FLOW", "Wake word ignored because ASR session is already active");
                    return;
                }
                LoggerWrapper.d(HelperInteractor.class.getSimpleName(), "onWakeWord");
                Log.d("ASR_FLOW", "Wake word detected -> starting ASR");
                cancelWakeWordRearm();
                mListener.onActivityDetected();
                mHardwareHelper.duckVolume(AudioManager.STREAM_MUSIC);
                mAnimationsHelper.getWakeWordHandler().resetAnimation();
                mDrawerHelper.closeSlidingTray();
                mDrawerHelper.closeSlidingTrayBottom();
                mAnimationsHelper.getWakeWordHandler().startVoiceAnimation(null);
                mWakeWordHelper.stopPhraseSpotRecognizer();
                SoundUtil.playSound(mContext, R.raw.asr_wakeup_sound);
                mHandlerHelper.getForegroundHandler().postDelayed(() -> {
                    Log.d("ASR_FLOW", "Starting ASR after wakeup tone delay=" + ASR_START_DELAY_MS + "ms");
                    mGSpeechRecognizerHelper.startSpeechToText();
                }, ASR_START_DELAY_MS);
            } else {
                restoreVolume(AudioManager.STREAM_MUSIC);
                mAudioHelper.playAudioNotificationResource(R.raw.error_speech);
                mWakeWordHelper.runWakeWordRecognition();
            }
        }

        @Override
        public void onCameraRotation() {
            if (!mLiveFeedHelper.isRotating()) {
                mSocketHelper.emitEventToServer(CAMERA_PAN_EVENT_TO_HUB,
                        mLiveFeedHelper.toRotateCamera(1, 0, ""));
                mHandlerHelper.removeForegroundHandler(CAMERA_ROTATION);
            } else {
                mHandlerHelper.postForegroundMessage(CAMERA_ROTATION, 1000);
            }
        }
    };
    private HandlerManager.BackgroundHandlerCallbacks mBackgroundHandlerListener = new HandlerManager.BackgroundHandlerCallbacks() {

    };
    private BrightnessCallback mBrightnessListener = new BrightnessCallback() {
        @Override
        public void onBrightnessChange() {
            int brightness = mHardwareHelper.getBrightness();
            mDrawerHelper.setUIOnBrightnessChange(brightness);
        }
    };
    private MusicCallbackListener mMusicCallbackListener = new MusicCallbackListener() {
        @Override
        public void onLoggedIn() {
            mListener.onSpotifyUserLoggedIn();
        }

        @Override
        public void onLoginFailed() {
            mListener.onSpotifyUserLoginFailed();
        }

        @Override
        public void onLoggedOut() {
            mListener.onSpotifyUserLoggedOut();
        }

        @Override
        public void onPlaybackError(Error error) {
            mListener.onSpotifyPlayerPlaybackError(error);
        }

        @Override
        public void onPlaying() {
            mListener.onSpotifyMusicPlaying();
        }

        @Override
        public void onPaused() {
            mListener.onSpotifyMusicPaused();
        }

        @Override
        public void onNextTrack() {
            mListener.onSpotifyMusicNextTrack();
        }

        @Override
        public void onPreviousTrack() {
            mListener.onSpotifyMusicPreviousTrack();
        }

        @Override
        public void onShuffleOn() {
            mListener.onSpotifyMusicShuffleOn();
        }

        @Override
        public void onShuffleOff() {
            mListener.onSpotifyMusicShuffleOff();
        }

        @Override
        public void onRepeatOn() {
            mListener.onSpotifyMusicRepeatOn();
        }

        @Override
        public void onRepeatOff() {
            mListener.onSpotifyMusicRepeatOff();
        }

        @Override
        public void onStopped() {
            mListener.onSpotifyMusicStopped();
        }

        @Override
        public void onTrackChanged() {
            mListener.onSpotifyMusicTrackChanged();
        }

        @Override
        public void onTrackDelivered() {
            mListener.onSpotifyMusicTrackDelivered();
        }

        @Override
        public void onMetadataChanged() {
            mListener.onSpotifyMusicMetaDataChanged();
        }

        @Override
        public void takeDataToRespondUser(Metadata metadata) {
            mListener.onTakeMetaData(metadata);
        }
    };
    private CompletionCallback mMusicCompletionCallback = new CompletionCallback() {
        @Override
        public void onActionComplete() {
            mListener.onSpotifyMusicActionCompleted();
        }

        @Override
        public void onActionCancelled() {
            mListener.onSpotifyMusicActionCancelled();
        }
    };
    private PollyHelper.PollyCallback mPollyListener = new PollyHelper.PollyCallback() {
        @Override
        public void onSpeechStart(int type) {
            LoggerWrapper.d(PollyHelper.class.getSimpleName(), "HelperInteractor: onSpeechStart type=" + type);
            mListener.onActivityDetected();
        }

        @Override
        public void onSpeechComplete(int type) {
            LoggerWrapper.d(PollyHelper.class.getSimpleName(), "HelperInteractor: onSpeechComplete type=" + type);
            switch (type) {
                case CALLBACK_FOLLOWUP:
                    startAsrRecording();
                    break;
                case CALLBACK_NEWS:
                    mListener.onSpeechNewsCallback(false);
                    break;
                case CALLBACK_NEWS_DETAILS:
                    mListener.onBackNavigation();
                    mListener.onSpeechNewsCallback(true);
                    break;

                default:
                    mListener.onActivityIdle();
                    mAnimationsHelper.getWakeWordHandler().setFollowUp(false);
            }
        }

        @Override
        public void onSpeechError() {
            LoggerWrapper.e(PollyHelper.class.getSimpleName(), "HelperInteractor: onSpeechError");
        }
    };
    private SocketHelper.SocketListener mSocketListener = new SocketHelper.SocketListener() {
        @Override
        public void onTestAcknowledged() {
            LoggerWrapper.i("SOCKET_ACKNOWLEDGEMENT", "onTest()");

            if ((System.currentTimeMillis() - lastUpdatedHubDetailsSocketTime > 2000)) {
                lastUpdatedHubDetailsSocketTime = System.currentTimeMillis();
                String authToken = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, "");
                String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
                JsonObject obj = new JsonObject();
                obj.addProperty("token", authToken);
                obj.addProperty("hubId", deviceId);
                mSocketHelper.emitEventToServer(SET_HUB_DETAILS, obj);
            }
        }

        @Override
        public void onDisconnectAllBluetooth() {
            BluetoothAdapter mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (mBlueToothAdapter != null && mBlueToothAdapter.isEnabled()) {
//                mSwitchCompat.setChecked(true);
//            } else {
//                mSwitchCompat.setChecked(false);
//            }
            Set<BluetoothDevice> pairedDevices = mBlueToothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    try {
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                        LoggerWrapper.e("TAG_Bluetooth_Unpair", "Successfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerWrapper.e("TAG_Bluetooth_Unpair", "Failed Due to--" + e.getMessage().toString());
                    }
                }
            }
        }

        @Override
        public void onCameraPan(final CameraRotationResponse cameraRotationResponse) {
            mHandlerHelper.runThread(HandlerHelper.THREAD_FOREGROUND, () -> {
                CameraRotationModel cameraRotationModel = cameraRotationResponse.getData();
                CameraRotationData data = cameraRotationModel.getData();
                int direction = data.getDirection();
                int rotationStatus = data.getRotationStatus();
                boolean isContinuous = rotationStatus == 1;
                if (direction == CameraUtils.DIR_LEFT) {
                    mHardwareHelper.rotateCamera(CameraUtils.DIR_LEFT, CameraUtils.MOTOR_SPEED, true, isContinuous);
                } else {
                    mHardwareHelper.rotateCamera(CameraUtils.DIR_RIGHT, CameraUtils.MOTOR_SPEED, true, isContinuous);
                }
            });
        }

        @Override
        public void onLightDeviceStateChanged(final Light light) {
            if (light != null) {
                mHandlerHelper.runThread(HandlerHelper.THREAD_FOREGROUND, () -> mListener.onLightSocketEvent(light));
            } else {
                LoggerWrapper.d(HelperInteractor.class.getSimpleName(), "No light data");
            }
        }

        @Override
        public void onLiveFeedInitialized(LiveFeedParticipantModel liveFeedParticipantModel) {
            mListener.getTwilioToken(liveFeedParticipantModel);
        }

        @Override
        public void onAcceptDropIn() {

            mHandlerHelper.runThread(HandlerHelper.THREAD_FOREGROUND, () -> mListener.dropInCallAccepted());
        }

        @Override
        public void onRejectDropIn() {
            mHandlerHelper.runThread(HandlerHelper.THREAD_FOREGROUND, () -> mListener.dropInCallRejected());
        }

        @Override
        public void onMusicPlayPause(Music.PlayPauseData playPauseData) {
            if (playPauseData != null) {
                Music.PlayPauseData.PlayStateData data = playPauseData.getData();
                if (data != null) {
                    if (data.isPlaying()) {
                        resumePlayingMusic();
                    } else {
                        pausePlayingMusic();
                    }
                }
            }
        }

        @Override
        public void onMusicTrackChanged(Music.ChangeTrackData changeTrackData) {
            if (changeTrackData != null) {
                Music.ChangeTrackData.TrackChangeData trackChangeData = changeTrackData.getData();
                if (trackChangeData != null) {
                    String trackDirection = trackChangeData.getChangeTo();
                    switch (trackDirection) {
                        case SocketHelper.SocketEvents.Music.MUSIC_PREVIOUS: {
                            previousSong();
                        }
                        break;

                        case SocketHelper.SocketEvents.Music.MUSIC_NEXT: {
                            nextSong();
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void onMusicMuteToggle(Music.MuteToggleData muteToggleData) {
            if (muteToggleData != null) {
                Music.MuteToggleData.MuteData muteData = muteToggleData.getData();
                if (muteData != null) {
                    boolean isVolume = muteData.isVolume();
                    if (isVolume) {
                        mHardwareHelper.muteCurrentStream(false);
                    } else {
                        mHardwareHelper.muteCurrentStream(true);
                    }
                }
            }
        }

        @Override
        public void onConnect() {
            mConnectivityHelper.checkInternet();
        }

        @Override
        public void onDisconnect() {
            mConnectivityHelper.checkInternet();
        }

        @Override
        public void onError(String errorCause) {
            mConnectivityHelper.checkInternet();
        }

        @Override
        public void onReconnect() {
            mConnectivityHelper.checkInternet();
        }

        @Override
        public void onMessage() {
        }

        @Override
        public void onSurveillanceModeChanged(boolean state) {
            mHandlerHelper.runThread(HandlerHelper.THREAD_FOREGROUND, () -> mListener.onSurveillanceModeChanged(state));
        }

        @Override
        public void onUpdateHomeScreen() {
            mListener.updateHomeScreen();
        }


        @Override
        public void onUpdateScreenSaverMulti(ScreenSaverMulti screenSaverMulti) {
            mListener.updateScreenSaverMulti(screenSaverMulti);
        }

        @Override
        public void onCheckIn(HotelCheckInResponse hotelCheckInResponse) {

            mListener.checkIn(hotelCheckInResponse);
        }

        @Override
        public void onCheckOut(HotelCheckOutResponse hotelCheckOutResponse) {

            mListener.checkOut(hotelCheckOutResponse);
        }

        @Override
        public void onFeedbackScreenOpen(ArrayList<Question> aQuestions) {
            mListener.feedback(aQuestions);
        }

        @Override
        public void onCovidScreenOpen() {
            mListener.covid();
        }

        @Override
        public void onUpdateHomeScreenContacts() {
            mListener.updateHomeScreenContacts();
        }


        public void onSystemCallAlert(RemoteAlertModel remoteAlertModel) {
            mListener.onSystemAlert(remoteAlertModel);
        }

        @Override
        public void onSystemUpdateCall(RemoteSystemUpdateModel remoteSystemUpdateModel) {
            mListener.onSystemUpdate(remoteSystemUpdateModel);

        }
    };
    private LiveFeedHelper.LiveFeedListener mLiveFeedListener = new LiveFeedHelper.LiveFeedListener() {
        @Override
        public void rotateCamera(boolean rotate, JsonObject jsonObject) {
            if (rotate) {
                mLiveFeedHelper.setRotation(true);
                mSocketHelper.emitEventToServer(CAMERA_PAN_EVENT_TO_HUB, jsonObject);
                mHandlerHelper.postForegroundMessage(CAMERA_ROTATION, 1000);
            } else {
                mLiveFeedHelper.setRotation(false);
                mSocketHelper.emitEventToServer(CAMERA_PAN_EVENT_TO_HUB, jsonObject);
            }
        }
    };
    private GSpeechRecognizerHelper.GSpeechRecognizerCallback gSpeechRecognizerCallback = new GSpeechRecognizerHelper.GSpeechRecognizerCallback() {
        @Override
        public void onSpeechRecognized(String text, String languageCode) {
            mHardwareHelper.restoreVolume(AudioManager.STREAM_MUSIC);
            GSpeechRecognizerHelper.listeningCount = 0;
            mAnimationsHelper.getWakeWordHandler().animateText(text, mListener, languageCode);
            Log.e("GetSpeechData", text.toString());
            scheduleWakeWordRearm();
//            mListener.processQuery(text);
        }

        @Override
        public void handleSpeechRecognizerTimeOutError() {
            stopWakeWordAnimation(true);
//            mWakeWordHelper.runWakeWordRecognition();
        }

        @Override
        public void handleSpeechRecognizerError() {

//            stopWakeWordAnimation(true);
//            mWakeWordHelper.runWakeWordRecognition();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    stopWakeWordRecognition();
                    startAsrRecording();
                }
            }, 200);
        }

        @Override
        public void onGoogleVoiceNotInstalled() {
            //stopWakeWordAnimation(false);
        }

        @Override
        public void asrActive() {
            mListener.onActivityDetected();
        }

        @Override
        public void asrInActive() {
            mListener.onActivityIdle();
        }

        @Override
        public void asrNoResponse() {
            GSpeechRecognizerHelper.listeningCount = 0;
            mHardwareHelper.restoreVolume(AudioManager.STREAM_MUSIC);
            mListener.onActivityIdle();
        }

        @Override
        public void asrRmsChanged(float modulationValue) {
            mAnimationsHelper.getWakeWordHandler().modulateVoice(modulationValue);
        }

        @Override
        public void onAsrProcessing(boolean processing) {
            mAnimationsHelper.getWakeWordHandler().setProcessing(processing);
        }

        @Override
        public void onAsrRecording(boolean recording) {
            mAnimationsHelper.getWakeWordHandler().setRecording(recording);
        }
    };

    private void scheduleWakeWordRearm() {
        if (mAnimationsHelper == null || mWakeWordHelper == null) {
            return;
        }
        if (mAnimationsHelper.getWakeWordHandler().isFollowUp()) {
            return;
        }
        wakeWordRearmAttempts = 0;
        cancelWakeWordRearm();
        wakeWordRearmRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAnimationsHelper.getWakeWordHandler().isFollowUp()) {
                    return;
                }
                if (!mAnimationsHelper.getWakeWordHandler().isActive()) {
                    mWakeWordHelper.runWakeWordRecognition();
                    return;
                }
                wakeWordRearmAttempts++;
                if (wakeWordRearmAttempts < WAKEWORD_REARM_MAX_ATTEMPTS) {
                    wakeWordRearmHandler.postDelayed(this, WAKEWORD_REARM_RETRY_MS);
                }
            }
        };
        wakeWordRearmHandler.postDelayed(wakeWordRearmRunnable, WAKEWORD_REARM_DELAY_MS);
    }

    private void cancelWakeWordRearm() {
        if (wakeWordRearmRunnable != null) {
            wakeWordRearmHandler.removeCallbacks(wakeWordRearmRunnable);
            wakeWordRearmRunnable = null;
        }
    }

    @Inject
    public HelperInteractor(
            AnimationsHelper animationsHelper,
            AudioHelper audioHelper,
            BroadcastHelper broadcastHelper,
            ConnectivityHelper connectivityHelper,
            DeviceHelper deviceHelper,
            DrawerHelper drawerHelper,
            HandlerHelper handlerHelper,
            HardwareHelper hardwareHelper,
            MusicHelper musicHelper,
            TimerHelper mTimerHelper,
            PollyHelper pollyHelper,
            SocketHelper socketHelper,
            GSpeechRecognizerHelper gSpeechRecognizerHelper,
            LiveFeedHelper liveFeedHelper,
            WakeWordHelper wakeWordHelper) {
        this.mAnimationsHelper = animationsHelper;
        this.mAudioHelper = audioHelper;
        this.mBroadcastHelper = broadcastHelper;
        this.mConnectivityHelper = connectivityHelper;
        this.mDeviceHelper = deviceHelper;
        this.mDrawerHelper = drawerHelper;
        this.mHandlerHelper = handlerHelper;
        this.mHardwareHelper = hardwareHelper;
        this.mMusicHelper = musicHelper;
        this.mTimerHelper = mTimerHelper;
        this.mPollyHelper = pollyHelper;
        this.mSocketHelper = socketHelper;
        this.mGSpeechRecognizerHelper = gSpeechRecognizerHelper;
        this.mLiveFeedHelper = liveFeedHelper;
        this.mWakeWordHelper = wakeWordHelper;
        internetStatus = new AtomicBoolean(false);
    }

    public void setListener(MainPresenter listener) {
        this.mListener = listener;
    }

    public void registerCallbacks(String lang) {

        mGSpeechRecognizerHelper.setLang(lang);
        mBroadcastHelper.setListener(mBroadcastListener);
        mSocketHelper.setListener(mSocketListener);
        mConnectivityHelper.setListener(mConnectivityListener);
        mDeviceHelper.setListener(mDeviceListener);
        mDrawerHelper.setListener(mDrawerListener);
        mGSpeechRecognizerHelper.setSpeechRecognizerListener(gSpeechRecognizerCallback);
        mHardwareHelper.setVolumeControlCallbacks(mVolumeControlListener);
        mHardwareHelper.setCameraCaptureCompatListener(mCameraCapturerListener);
        mHardwareHelper.setBrightnessChangeObserver(mBrightnessListener);
        mMusicHelper.setListener(mMusicCallbackListener, mMusicCompletionCallback);
        mPollyHelper.setVolumeListener(mVolumeControlListener);
        mPollyHelper.setListener(mPollyListener);
        mHandlerHelper.setForegroundHandlerCallback(mForegroundHandlerListener);
        mHandlerHelper.setBackgroundHandlerCallback(mBackgroundHandlerListener);
        mLiveFeedHelper.setListener(mLiveFeedListener);
        mWakeWordHelper.setListener(mWakeWordListener);
    }

    public void initializeComponents(Context context) {
        mContext = context;
        mHardwareHelper.initializeComponents(context);

        mPollyHelper.initializeComponent();

        // Initialize the Drawer.
        int currentBrightness = mHardwareHelper.getBrightness();

        mDrawerHelper.initAudioManager();

        mDrawerHelper.initDrawer(currentBrightness);

        mDrawerHelper.initListener();

        mDrawerHelper.setDefaultSeekBarValue();

        mGSpeechRecognizerHelper.warmUpCloudAsr();
        mWakeWordHelper.runWakeWordRecognition();

        mBroadcastHelper.registerBroadcasts(context);

        mHardwareHelper.registerVolumeObserver(AudioManager.STREAM_MUSIC);

        // Check for Internet Connectivity.
        mConnectivityHelper.checkInternet();

        // Connect Socket and Register the Callbacks.
        mSocketHelper.connectSocket();
        mSocketHelper.registerSocketCallbacks();

        mAnimationsHelper.getDrawerAnimationsHandler().toggleVisibility(false);
    }

    public void onWakeWordChanged() {
        mWakeWordHelper.onWakeWordChanged();
    }

    public void cleanUp(Context context) {
        mAnimationsHelper.onHelperCleanUp();
        mAudioHelper.onHelperCleanUp();
        mBroadcastHelper.onHelperCleanUp(context);
        mConnectivityHelper.onHelperCleanUp();
        mDeviceHelper.onHelperCleanUp();
        mDrawerHelper.onHelperCleanUp();
        mHardwareHelper.onHelperCleanUp();
        mPollyHelper.onHelperCleanUp();
        mSocketHelper.unRegisterSocketCallbacks();
        mSocketHelper.onHelperCleanUp();
        mGSpeechRecognizerHelper.onHelperCleanUp();
        mLiveFeedHelper.onHelperCleanUp();
        mWakeWordHelper.onHelperCleanUp();
        mMusicHelper.onHelperCleanUp(context);
        mTimerHelper.onHelperCleanUp();
        mHandlerHelper.removeForegroundHandler(WAKE_WORD_DETECTED);
        cancelWakeWordRearm();
        SoundUtil.releaseMediaPlayer();
        internetStatus = null;
    }

    private JsonObject getMuteData(boolean isMute) {
        String authToken = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, "");
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        JsonObject muteDataJson = new JsonObject();
        muteDataJson.addProperty(SocketHelper.SocketEvents.System.DeviceData.TOKEN, deviceId); // Use Session Token.
        muteDataJson.addProperty(SocketHelper.SocketEvents.System.DeviceData.HUB_ID, authToken); // Use Hub ID provided by Server.
        muteDataJson.addProperty(SocketHelper.SocketEvents.Hardware.Volume.VOLUME, !isMute);
        return muteDataJson;
    }

    @Override
    public void startAsrRecording() {
        mHandlerHelper.getForegroundHandler().sendEmptyMessage(WAKE_WORD_DETECTED);
    }

    @Override
    public void onVolumeUpPressed() {
        mHardwareHelper.onVolumeUpPressed();
    }

    @Override
    public void onVolumeDownPressed() {
        mHardwareHelper.onVolumeDownPressed();
    }

    @Override
    public void closeDrawer() {
        mDrawerHelper.closeSlidingTray();
        mDrawerHelper.closeSlidingTrayBottom();
    }

    @Override
    public void toggleSlidingTrayVisibility(boolean isVisible) {
        mDrawerHelper.setSlidingTrayVisibility(isVisible);
    }

    @Override
    public void toggleSlidingTrayBottomVisibility(boolean isVisible) {
//        mDrawerHelper.setSlidingTrayBottomVisibility(isVisible);
    }

    @Override
    public void overlayAnimation(boolean b) {
        mAnimationsHelper.getDrawerAnimationsHandler().toggleVisibility(b);
    }

    @Override
    public void stopTimer() {
        mTimerHelper.stopTimer();
    }

    @Override
    public void showMuteBar(boolean aIsNetworkPresent) {
        mAnimationsHelper.showMuteBar(aIsNetworkPresent);
    }

    @Override
    public void hideMuteBar(boolean aIsNetworkPresent) {
        mAnimationsHelper.hideMuteBar(aIsNetworkPresent);
    }

    @Override
    public void showAllSoundBar() {
        mDrawerHelper.showAllSoundBar();
    }

    @Override
    public void hideTopDrawerItem() {
        mDrawerHelper.hideTopDrawerItem();

    }

    @Override
    public void runWakeWordRecognition() {
        mHardwareHelper.restoreVolume(AudioManager.STREAM_MUSIC);
        mGSpeechRecognizerHelper.stopAsrListeningThread();
        SoundUtil.releaseMediaPlayer();
        mWakeWordHelper.runWakeWordRecognition();
    }

    @Override
    public void stopWakeWordRecognition() {
        cancelWakeWordRearm();
        mWakeWordHelper.stopPhraseSpotRecognizer();
    }

    @Override
    public void setFollowUp(boolean isFollowUp) {
        mAnimationsHelper.getWakeWordHandler().setFollowUp(isFollowUp);
        if (!isFollowUp) {
            mWakeWordHelper.runWakeWordRecognition();
            stopWakeWordAnimation(false);
        }
    }

    @Override
    public void stopWakeWordAnimation(boolean needTorunWakeWordRecobnition) {
        //need to run runWakeWordRecognition
        if (mAnimationsHelper != null && mAnimationsHelper.getWakeWordHandler() != null) {
            mAnimationsHelper.getWakeWordHandler().setFollowUp(false);
            mAnimationsHelper.getWakeWordHandler().stopVoiceAnimation(needTorunWakeWordRecobnition, mWakeWordHelper);
        }
    }

    @Override
    public void removeWakeWordHandler() {
        mHandlerHelper.removeForegroundHandler(WAKE_WORD_DETECTED);
    }

    @Override
    public void startSpeaking(String text, int streamType, int callbackType, String lang) {
        mPollyHelper.startSpeaking(text, streamType, callbackType, lang);
    }

    @Override
    public void stopSpeaking(boolean needCallback) {
        mPollyHelper.stopSpeaking(needCallback);
    }

    @Override
    public void pausePlayingMusic() {
        mMusicHelper.pauseMusic();
    }

    @Override
    public void stopPlayingMusic() {
        mMusicHelper.stopMusic();
    }

    @Override
    public void resumePlayingMusic() {
        mMusicHelper.resumeMusic();
    }

    public void resumeMusicPlayingBefore() {
        mMusicHelper.resumeMusicWasPlaying();
    }

    @Override
    public void nextSong() {
        mMusicHelper.skipToNextMusic();
    }

    @Override
    public void previousSong() {
        mMusicHelper.skipToPreviousMusic();
    }

    public boolean getWasMusicPlayingStatus() {
        return mMusicHelper.isMusicPlaying();
    }

    @Override
    public void controlSystemVolume(IntentInfo intentInfo) {
        mHardwareHelper.controlSystemVolume(intentInfo);
    }

    @Override
    public int getBrightness() {
        return mHardwareHelper.getBrightness();
    }

    @Override
    public void setBrightness(int brightness) {
        mHardwareHelper.setBrightness(brightness);
    }

    @Override
    public void rotateCamera(boolean rotate, String hubId, String receverUserId, int angle, int isRotating) {
        mLiveFeedHelper.setRemoteDeviceID(hubId);
        mLiveFeedHelper.rotateCamera(rotate, mLiveFeedHelper.toRotateCamera(angle, isRotating, receverUserId));
    }

    @Override
    public boolean isMusicPlayerInitialized() {
        return mMusicHelper.isPlayerInitialized();
    }

    @Override
    public boolean isMusicPlaying() {
        return mMusicHelper.isPlaying();
    }

    @Override
    public int getMusicCurrentPosition() {
        return mMusicHelper.getMusicCurrentPosition();
    }

    @Override
    public void seekMusic(int position) {
        mMusicHelper.seekMusic(position);
    }

    @Override
    public boolean isMusicRepeating() {
        return mMusicHelper.isRepeating();
    }

    @Override
    public void setMusicRepeat(boolean repeat) {
        mMusicHelper.setRepeat(repeat);
    }

    @Override
    public boolean isMusicPlayerTerminated() {
        return mMusicHelper.isPlayerTerminated();
    }

    @Override
    public Metadata getMusicMetaData() {
        return mMusicHelper.getMetaData();
    }

    @Override
    public void terminateMusicPlayer() {
        mMusicHelper.terminateSpotifyPlayer();
    }

    @Override
    public void playMusic(String spotifyURI) {
        mMusicHelper.playMusic(spotifyURI);
    }

    @Override
    public boolean isSpotifyUserLoggedIn() {
        return mMusicHelper.isLoggedIn();
    }

    @Override
    public void setSpotifyPlayer(Context context, String token) {
        mMusicHelper.setSpotifyPlayer(context, token);
    }

    @Override
    public boolean getCameraStatus() {
        return mHardwareHelper.getVideoCapturer() != null;
    }

    @Override
    public void emitEventToServer(String event, JsonObject object) {
        mSocketHelper.emitEventToServer(event, object);
    }

    @Override
    public void restoreVolume(int audioStream) {
        mHardwareHelper.restoreVolume(audioStream);
    }

    @Override
    public void stopAsr() {
        mHardwareHelper.restoreVolume(AudioManager.STREAM_MUSIC);
        mGSpeechRecognizerHelper.stopAsrListeningThread();
        SoundUtil.releaseMediaPlayer();
    }
}
