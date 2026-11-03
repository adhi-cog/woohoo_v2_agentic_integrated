package com.sb.woohoo.ui.main.helpers;

import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.Hardware.CAMERA_PAN;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.QuestionRequest;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.socketdata.Music;
import com.sb.woohoo.ui.main.pojo.socketevent.CameraRotationResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.LightSocketResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedParticipantModel;
import com.sb.woohoo.ui.main.pojo.socketevent.SurveillanceModeSocketResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Handles all the event callback made by {@link Socket}.
 *
 * @author Ritwik Jamuar.
 */
@SuppressWarnings("DanglingJavadoc")
public class SocketHelper extends BaseHelper {

    // Create a Handler that uses the Main Looper to run in
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mStatusChecker = null;
    private Runnable mSocketPinger = null;

    private int SOCKET_CHECK_INTERVAL = 60 * 2000;

    private int SOCKET_PING_INTERVAL = 20 * 60 * 1000;


    private enum SocketStatus {
        UNINTIALIZED,
        CONNECTING,
        CONNECT_SUCCESS,
        CONNECT_FAILED,
        DISCONNECT,
        ERROR,
        CONNECT_ERROR, CONNECT_TIMEOUT,
        RECONNECT, RECONNECT_ATTEMPT, RECONNECT_ERROR, RECONNECT_FAILED,
        CLOSE
    }

    private SocketStatus lastSocketStatus = SocketStatus.UNINTIALIZED;
    private int count = 1;

    private void trackSocketEventChange(SocketStatus socketEvent) {
        if (socketEvent == lastSocketStatus) {
            count++;
        } else {
            LoggerWrapper.i(TAG, "Socket check::" + lastSocketStatus + "::" + count);
            count = 1;
            LoggerWrapper.i(TAG, "Socket check::" + socketEvent + " received");
            lastSocketStatus = socketEvent;
        }
    }

    interface SocketConnectionStatusListener {

        void socketConnectionUpdate(boolean isConnected);
    }

    /**
     * Starts the periodical update routine (mStatusChecker
     * adds the callback to the handler).
     */
    public synchronized void startUpdates(SocketConnectionStatusListener listener) {
        if (mStatusChecker == null) {
            mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    // Run the passed runnable
                    try {
                        if (mSocket.connected()) {
                            trackSocketEventChange(SocketStatus.CONNECT_SUCCESS);
                        } else {
                            trackSocketEventChange(SocketStatus.CONNECT_FAILED);
                        }
                        listener.socketConnectionUpdate(mSocket.connected());
                    } catch (Exception e) {
                        trackSocketEventChange(SocketStatus.ERROR);
                        listener.socketConnectionUpdate(false);
                    }
                    // Re-run it after the update interval
                    mHandler.postDelayed(this, SOCKET_CHECK_INTERVAL);
                }
            };
            mStatusChecker.run();
        }
        if (mSocketPinger == null) {
            mSocketPinger = new Runnable() {
                @Override
                public void run() {
                    // Run the passed runnable
                    try {
                        if (mSocket != null) {
                            mSocket.emit("ping", "server");
                            LoggerWrapper.d(TAG, "SocketHelper: pinging");
                        } else {
                            LoggerWrapper.d(TAG, "SocketHelper: Socket is null for pinging");
                        }
                    } catch (Exception e) {
                        LoggerWrapper.e(TAG, "SocketHelper: pinging error");
                    }
                    // Re-run it after the update interval
                    mHandler.postDelayed(this, SOCKET_PING_INTERVAL);
                }
            };
            mSocketPinger.run();
        }
    }

    /**
     * Stops the periodical update routine from running,
     * by removing the callback.
     */
    public synchronized void stopUpdates() {
        LoggerWrapper.i(TAG, "SocketHelper::stopUpdates::");
        mHandler.removeCallbacks(mStatusChecker);
        mStatusChecker = null;
    }

    public synchronized void stopSocketPing() {
        LoggerWrapper.i(TAG, "SocketHelper::stopSocketPing::");
        mHandler.removeCallbacks(mSocketPinger);
        mSocketPinger = null;
    }


    // Constants.
    private static final String TAG = SocketHelper.class.getSimpleName();

    // Components.
    private Socket mSocket;
    private SocketListener mListener;
    private Gson gson;
    private JsonParser jsonParsor;
    private AnalyticsHelper analyticsHelper;
    private long lastUpdatedSystemAlertSocketTime = 0;
    /**************************************** {@link Socket} Event's Callbacks ****************************************/

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onConnect", args);
            StringBuilder sb = new StringBuilder();
            for (Object obj : args) {
                sb.append(obj);
                sb.append(":::");
            }
            printLog("onConnect ()");
            LoggerWrapper.d(TAG, " Socket Event Receive - onConnect :" + sb.toString() + "socket:id: " + mSocket.id());
            mListener.onTestAcknowledged();
            mListener.onConnect();

            startUpdates(isConnected -> LoggerWrapper.i(TAG, "SocketHelper::socketConnectionUpdate::" + isConnected));

        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onDisconnect", args);
            printLog("oDisconnect ()");
            LoggerWrapper.d(TAG, " Socket Event Receive - onDisconnect ");
            trackSocketEventChange(SocketStatus.DISCONNECT);
            stopUpdates();
            mListener.onDisconnect();

        }
    };
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onError", args);
            printLog("onError ()");

            if (args != null && args.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (Object obj : args) {
                    sb.append(obj);
                    sb.append(":::");
                }
                LoggerWrapper.e(TAG, "Socket Event Receive - onError :" + sb.toString() + "socket:id: " + mSocket.id());
                mListener.onError("Socket Event Receive - onError :" + sb.toString() + "socket:id: " + mSocket.id());
            } else {
                LoggerWrapper.e(TAG, "Error while connecting to Room");
                mListener.onError("Error while connecting to Room");
            }
        }
    };
    private Emitter.Listener onReConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onReConnect", args);
            printLog("oReconnect ()");
            LoggerWrapper.d(TAG, " Socket Event Receive - oReconnect ");
            mListener.onReconnect();
        }
    };
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onMessage", args);
            printLog("onMessage () : ");
            LoggerWrapper.d(TAG, " Socket Event Receive - onMessage ");
            mListener.onMessage();
        }
    };
    private Emitter.Listener onTest = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onTest", args);
            printLog("onTest()");
            LoggerWrapper.d(TAG, " Socket Event Receive - onTest ");
            mListener.onTestAcknowledged();
        }
    };
    private Emitter.Listener onServicesUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onServicesUpdated", args);
            printLog(args[0].toString());
            LoggerWrapper.d(TAG, " Socket Event Receive - onServicesUpdated ");
            // TODO : Implement the functionality to update Contact List.
        }
    };
    private Emitter.Listener onCameraPan = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onCameraPan", args);
            printLog("woohoo_camera_pan");
            mListener.onCameraPan(gson.fromJson(args[0].toString(), CameraRotationResponse.class));
        }
    };
    private Emitter.Listener onDeviceStateChanged = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onDeviceStateChanged", args);
            printLog("changeStateOfDevice");
            printLog(args[0].toString());
            JsonElement object = jsonParsor.parse(args[0].toString());
            LightSocketResponse lightSocketResponse = gson.fromJson(object.getAsJsonObject().get("data").toString(), LightSocketResponse.class);
            if (lightSocketResponse.getFrom().equalsIgnoreCase("client")) {
                mListener.onLightDeviceStateChanged(lightSocketResponse.getLightData());
            }
        }
    };
    private Emitter.Listener onUpdateHomeScreen = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onUpdateHomeScreen", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "Home screen update: Initialize" + args[i].toString());
            }
            LoggerWrapper.d(TAG, "Socket Event Receive - onUpdateHomeScreen");
            mListener.onUpdateHomeScreen();
        }
    };

    private Emitter.Listener onHotelCheckIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onHotelCheckIn", args);
            printLog(args[0].toString());
            LoggerWrapper.e(TAG, "Hotel checkin : Initialize");
            JsonElement object = jsonParsor.parse(args[0].toString());
            HotelCheckInResponse hotelCheckInResponse = gson.fromJson(object.getAsJsonObject().get("data").toString(), HotelCheckInResponse.class);

            mListener.onCheckIn(hotelCheckInResponse);
        }
    };

    private Emitter.Listener onFeedbackCallInit = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onFeedbackCallInit", args);
            printLog(args[0].toString());
            LoggerWrapper.e(TAG, "Feedback : Initialize");
            QuestionRequest lFeedbackData = gson.fromJson(args[0].toString(), QuestionRequest.class);
            ArrayList<Question>  lQuestions = new ArrayList<>();
            if(lFeedbackData != null && lFeedbackData.getData() != null &&
                    lFeedbackData.getData().getQuestion() != null)
            {
                LoggerWrapper.e(TAG, "Feedback : lFeedbackData : "
                        +lFeedbackData.getData().getQuestion());
                lQuestions.addAll(lFeedbackData.getData().getQuestion());
            }
            mListener.onFeedbackScreenOpen(lQuestions);
        }
    };

    private Emitter.Listener onCovidCallInit = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onCovidCallInit", args);
            printLog(args[0].toString());
            LoggerWrapper.e(TAG, "Feedback : Initialize");
            JsonElement object = jsonParsor.parse(args[0].toString());

            mListener.onCovidScreenOpen();
        }
    };

    private Emitter.Listener onHotelCheckOut = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onHotelCheckOut", "");
            printLog(args[0].toString());
            LoggerWrapper.e(TAG, "Hotel checkout: Initialize");
            JsonElement object = jsonParsor.parse(args[0].toString());
            HotelCheckOutResponse hotelCheckOutResponse = gson.fromJson(object.getAsJsonObject().get("data").toString(), HotelCheckOutResponse.class);

            mListener.onCheckOut(hotelCheckOutResponse);
        }
    };

    private Emitter.Listener onBluetoothDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onBluetoothDisconnect", "");
            printLog(args[0].toString());
            LoggerWrapper.e(TAG, "onBluetoothDisconnect: Initialize");
            mListener.onDisconnectAllBluetooth();

//            JsonElement object = jsonParsor.parse(args[0].toString());
//            HotelCheckOutResponse hotelCheckOutResponse = gson.fromJson(object.getAsJsonObject().get("data").toString(), HotelCheckOutResponse.class);

//            mListener.onCheckOut(hotelCheckOutResponse);
        }
    };

    private Emitter.Listener onUpdateScreenSaver = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onUpdateScreenSaver", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "Screen saver update: Initialize" + args[i].toString());
            }
            LoggerWrapper.d(TAG, "Socket Event Receive - onUpdateScreenSaver");

            JsonElement object = jsonParsor.parse(args[0].toString());
            ScreenSaverMulti screenSaverMulti = gson.fromJson(object.getAsJsonObject().get("data").toString(), ScreenSaverMulti.class);

            mListener.onUpdateScreenSaverMulti(screenSaverMulti);

        }
    };

    private Emitter.Listener onUpdateHomeScreenContacts = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onUpdateHomeScreenContacts", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "Home screen update: Initialize" + args[i].toString());
            }
            mListener.onUpdateHomeScreenContacts();
        }
    };
    private Emitter.Listener onInitializingLiveFeed = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onInitializingLiveFeed", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "call: Initialize" + args[i].toString());
            }
            LiveFeedParticipantModel liveFeedParticipantModel = gson.fromJson(args[0].toString(), LiveFeedParticipantModel.class);
            mListener.onLiveFeedInitialized(liveFeedParticipantModel);
        }
    };
    private Emitter.Listener onInitializingSystemUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onInitializingSystemUpdate", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "system update: Initialize" + args[i].toString());
            }

            RemoteSystemUpdateModel remoteSystemUpdateModel = gson.fromJson(args[0].toString(), RemoteSystemUpdateModel.class);
            mListener.onSystemUpdateCall(remoteSystemUpdateModel);

        }
    };
    private Emitter.Listener onInitializingSystemAlert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            trackEvent("onInitializingSystemAlert", args);
            if ((System.currentTimeMillis() - lastUpdatedSystemAlertSocketTime > 2000))
            {
                lastUpdatedSystemAlertSocketTime = System.currentTimeMillis();
                for (int i = 0; i < args.length; i++) {
                    LoggerWrapper.e(TAG, "system RemoteAlertModel: Initialize" + args[i].toString());
                }
                RemoteAlertModel remoteAlertModel = gson.fromJson(args[0].toString(), RemoteAlertModel.class);
                if (remoteAlertModel != null)
                    mListener.onSystemCallAlert(remoteAlertModel);
            }
        }
    };
    private Emitter.Listener onParticipantActionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onParticipantActionListener", args);
            for (int i = 0; i < args.length; i++) {
                LoggerWrapper.e(TAG, "call: Accepted" + args[i].toString());
            }
            mListener.onAcceptDropIn();
        }
    };

    private Emitter.Listener onParticipantRejectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onParticipantRejectListener", args);
            LoggerWrapper.i(TAG, "call: " + args[0].toString());
            if (mListener != null) {
                mListener.onRejectDropIn();
            }

        }
    };
    private Emitter.Listener onMusicPlayPause = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onMusicPlayPause", args);
            Music.PlayPauseData playPauseData = gson.fromJson(
                    args[0].toString(),
                    Music.PlayPauseData.class
            );
            mListener.onMusicPlayPause(playPauseData);
        }
    };
    private Emitter.Listener onMusicTrackChanged = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onMusicTrackChanged", args);
            trackEvent("", args);
            Music.ChangeTrackData changeTrackData = gson.fromJson(
                    args[0].toString(),
                    Music.ChangeTrackData.class
            );
            mListener.onMusicTrackChanged(changeTrackData);
        }
    };
    private Emitter.Listener onMusicMuteToggle = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onMusicMuteToggle", args);
            Music.MuteToggleData muteToggleData = gson.fromJson(
                    args[0].toString(),
                    Music.MuteToggleData.class
            );

            mListener.onMusicMuteToggle(muteToggleData);
        }
    };
    private Emitter.Listener onUpdateSurveillanceListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onUpdateSurveillanceListener", args);
            printLog("Surveillance_Mode_Changed");
            SurveillanceModeSocketResponse surveillanceModeSocketResponse = gson.fromJson(args[0].toString(), SurveillanceModeSocketResponse.class);
            mListener.onSurveillanceModeChanged(surveillanceModeSocketResponse.getData().getSurveillanceMode());
        }
    };

    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onReconnecting", args);
        }
    };

    private Emitter.Listener onConnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onConnecting", args);
        }
    };

    private Emitter.Listener onConnectError = (args) -> {
        trackSocketEventChange(SocketStatus.CONNECT_ERROR);
        trackEvent("onConnectError", args);
    };


    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackEvent("onConnectTimeout", args);
            trackSocketEventChange(SocketStatus.CONNECT_TIMEOUT);

        }
    };

    private Emitter.Listener onReconnectAttempt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackSocketEventChange(SocketStatus.RECONNECT_ATTEMPT);

            trackEvent("onReconnectAttempt", args);
        }
    };


    private Emitter.Listener onReconnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackSocketEventChange(SocketStatus.RECONNECT_ERROR);

            trackEvent("onReconnectError", args);
        }
    };

    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            trackSocketEventChange(SocketStatus.RECONNECT_FAILED);

            trackEvent("onReconnectFailed", args);
        }
    };

    private void trackEvent(String event, Object... args) {
        this.analyticsHelper.track(event, new Bundle());
    }

    @Inject
    public SocketHelper(Socket socket, Gson gson, JsonParser jsonParser, AnalyticsHelper analyticsHelper) {
        this.mSocket = socket;
        this.gson = gson;
        this.jsonParsor = jsonParser;
        this.analyticsHelper = analyticsHelper;
    }

    public SocketListener getListener() {
        return mListener;
    }

    /********************************************* {@code public} Methods *********************************************/

    public void setListener(SocketListener listener) {
        this.mListener = listener;
    }

    /**
     * Tells {@link Socket} to connect to URL given during initialization.
     */
    public void connectSocket() {
        if (mSocket == null) {
            mListener.onError("Socket not initialized");
            LoggerWrapper.e(TAG, "SocketHelper::connectSocket but socket is null");

        } else {
            Socket result = mSocket.connect();

            if (result != null) {
                LoggerWrapper.i(TAG, "SocketHelper::connectSocket::" + result.connected() + "::" + result.id());
            }
        }
    }

    /**
     * Tells {@link Socket} to disconnect to URL given during initialization.
     */
    public void disconnectSocket() {
        if (mSocket == null) mListener.onError("Socket not initialized");
        else {
            mSocket.disconnect();
            trackSocketEventChange(SocketStatus.CLOSE);
            stopUpdates();
            stopSocketPing();
        }
    }

    /**
     * Registers {@link Socket} Callbacks.
     */
    public void registerSocketCallbacks() {
        // Register basic Socket connection callbacks.
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_ERROR, onError);
        mSocket.on(Socket.EVENT_RECONNECT, onReConnect);

        mSocket.on(Socket.EVENT_CONNECTING, onConnecting);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        mSocket.on(Socket.EVENT_RECONNECTING, onReconnecting);
        mSocket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt);
        mSocket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
        mSocket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);

        mSocket.on(Socket.EVENT_MESSAGE, onMessage);

        // Register System Events callbacks.
        mSocket.on(SocketEvents.System.TEST_ACKNOWLEDGE, onTest);
        mSocket.on(SocketEvents.System.UPDATE_SERVICES, onServicesUpdated);

        // Register Hardware Events callbacks.
        mSocket.on(CAMERA_PAN, onCameraPan);

        // Register Submit Events callbacks.
        mSocket.on(SocketEvents.SubmitForm.FEEDBACK_IN, onFeedbackCallInit);
        mSocket.on(SocketEvents.SubmitForm.COVID_IN, onCovidCallInit);

        //Register check event callback
        mSocket.on(SocketEvents.Hotel.CHECK_IN, onHotelCheckIn);
        mSocket.on(SocketEvents.Hotel.CHECK_OUT, onHotelCheckOut);

        //todo bluetooth socket Event
        mSocket.on(SocketEvents.Bluetooth.DISCONNECT_ALL, onBluetoothDisconnect);

        // Register Philips Hue Events callbacks.
        mSocket.on(SocketEvents.Lights.CHANGE_DEVICE_STATE, onDeviceStateChanged);
        mSocket.on(SocketEvents.HomeFragment.UpdateHomeScreen, onUpdateHomeScreen);
        mSocket.on(SocketEvents.HomeFragment.UpdateHomeScreenContacts, onUpdateHomeScreenContacts);

        mSocket.on(SocketEvents.ScreenSaverFragment.UpdateScreenSaver, onUpdateScreenSaver);

        mSocket.on(SocketEvents.ServerCall.INIT_SYSYTEM_UPDATE, onInitializingSystemUpdate);
        mSocket.on(SocketEvents.ServerCall.INIT_SYSYTEM_ALERT, onInitializingSystemAlert);


        // Register Live Feed event to listen when client/hub is dropping into this hub.
        mSocket.on(SocketEvents.LiveFeed.INIT_LIVE_FEED, onInitializingLiveFeed);
        mSocket.on(SocketEvents.LiveFeed.DROP_IN_ACCEPT, onParticipantActionListener);
        mSocket.on(SocketEvents.LiveFeed.DROP_IN_REJECT, onParticipantRejectListener);
        mSocket.on(SocketEvents.LiveFeed.DROP_IN_REJECT_HUB, onParticipantRejectListener);
        //this is fired only when the client disconnects the live-feed
        mSocket.on(SocketEvents.LiveFeed.HUB_CONNECTOR, onParticipantRejectListener);

        // Register Music Events callbacks.
        mSocket.on(SocketEvents.Music.MUSIC_PLAY_PAUSE, onMusicPlayPause);
        mSocket.on(SocketEvents.Music.MUSIC_TRACK_CHANGE, onMusicTrackChanged);
        mSocket.on(SocketEvents.Music.MUSIC_MUTE_TOGGLE, onMusicMuteToggle);

        //Register Update for Surveillance
        mSocket.on(SocketEvents.Surveillance.UPDATE_SURVEILLANCE, onUpdateSurveillanceListener);
    }

    /**
     * Unregisters {@link Socket} Callbacks.
     */
    public void unRegisterSocketCallbacks() {
        // Unregister basic Socket connection callbacks.
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_ERROR, onError);
        mSocket.off(Socket.EVENT_RECONNECT, onReConnect);
        mSocket.off(Socket.EVENT_MESSAGE, onMessage);

        mSocket.off(Socket.EVENT_CONNECTING, onConnecting);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);

        mSocket.off(Socket.EVENT_RECONNECTING, onReconnecting);
        mSocket.off(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt);
        mSocket.off(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
        mSocket.off(Socket.EVENT_RECONNECT_FAILED, onReconnectFailed);

        // Unregister System Events callbacks.
        mSocket.off(SocketEvents.System.TEST_ACKNOWLEDGE, onTest);
        mSocket.off(SocketEvents.System.UPDATE_SERVICES, onServicesUpdated);

        // Unregister Hardware Events callbacks.
        mSocket.off(CAMERA_PAN, onCameraPan);

        // Unregister Philips Hue Events callbacks.
        mSocket.off(SocketEvents.Lights.CHANGE_DEVICE_STATE, onDeviceStateChanged);

        // Register Submit Events callbacks.
        mSocket.off(SocketEvents.SubmitForm.FEEDBACK_IN, onFeedbackCallInit);
        mSocket.off(SocketEvents.SubmitForm.COVID_IN, onCovidCallInit);

        //UnRegister check event callback
        mSocket.off(SocketEvents.Hotel.CHECK_IN, onHotelCheckIn);
        mSocket.off(SocketEvents.Hotel.CHECK_OUT, onHotelCheckOut);


        mSocket.off(SocketEvents.HomeFragment.UpdateHomeScreen, onUpdateHomeScreen);
        mSocket.off(SocketEvents.HomeFragment.UpdateHomeScreenContacts, onUpdateHomeScreenContacts);

        mSocket.off(SocketEvents.ScreenSaverFragment.UpdateScreenSaver, onUpdateScreenSaver);

        mSocket.off(SocketEvents.ServerCall.INIT_SYSYTEM_UPDATE, onInitializingSystemUpdate);
        mSocket.off(SocketEvents.ServerCall.INIT_SYSYTEM_ALERT, onInitializingSystemAlert);


        // Unregister Live Feed event to listen when client/hub is dropping into this hub.
        mSocket.off(SocketEvents.LiveFeed.INIT_LIVE_FEED, onInitializingLiveFeed);
        mSocket.off(SocketEvents.LiveFeed.DROP_IN_ACCEPT, onParticipantActionListener);
        mSocket.off(SocketEvents.LiveFeed.DROP_IN_REJECT, onParticipantRejectListener);
        mSocket.off(SocketEvents.LiveFeed.DROP_IN_REJECT_HUB, onParticipantRejectListener);
        //this is fired only when the client disconnects the live-feed
        mSocket.on(SocketEvents.LiveFeed.HUB_CONNECTOR, onParticipantRejectListener);

        // Unregister Music Events callbacks.
        mSocket.off(SocketEvents.Music.MUSIC_PLAY_PAUSE, onMusicPlayPause);
        mSocket.off(SocketEvents.Music.MUSIC_TRACK_CHANGE, onMusicTrackChanged);
        mSocket.off(SocketEvents.Music.MUSIC_MUTE_TOGGLE, onMusicMuteToggle);

        //Unregister Surveillance Update
        mSocket.off(SocketEvents.Surveillance.UPDATE_SURVEILLANCE, onUpdateSurveillanceListener);
    }

    /**
     * Emits an event from Socket.
     *
     * @param event Name of Event available in {@link SocketEvents}.
     */
    public void emitEventToServer(String event, JsonObject jsonObject) {

        LoggerWrapper.i(TAG, "**** socket for camera move ");
        if (mSocket != null) {
            mSocket.emit(event, jsonObject);
        }
    }

    private void printLog(String message) {
        LoggerWrapper.e(TAG, message);
    }

    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        Log.i("SocketHelper", "onHelperCleanUp:");

        disconnectSocket();
        mSocket = null;
        mListener = null;
    }

    /*********************************************** {@code interface}s ***********************************************/

    public interface SocketListener {
        /**
         * Invokes when Test Event has been triggered by the Server.
         */
        void onTestAcknowledged();

        void onDisconnectAllBluetooth();

        void onCameraPan(CameraRotationResponse cameraRotationResponse);

        void onLightDeviceStateChanged(Light light);

        void onLiveFeedInitialized(LiveFeedParticipantModel model);

        void onAcceptDropIn();

        void onRejectDropIn();

        void onMusicPlayPause(Music.PlayPauseData object);

        void onMusicTrackChanged(Music.ChangeTrackData object);

        void onMusicMuteToggle(Music.MuteToggleData object);

        void onConnect();

        void onDisconnect();

        void onError(String errorCause);

        void onReconnect();

        void onMessage();

        void onSurveillanceModeChanged(boolean state);

        void onUpdateHomeScreenContacts();

        void onUpdateHomeScreen();

        void onSystemCallAlert(RemoteAlertModel remoteAlertModel);

        void onSystemUpdateCall(RemoteSystemUpdateModel remoteSystemUpdateModel);

        void onUpdateScreenSaverMulti(ScreenSaverMulti screenSaverMulti);

        void onCheckIn(HotelCheckInResponse hotelCheckInResponse);

        void onCheckOut(HotelCheckOutResponse hotelCheckOutResponse);

        void onFeedbackScreenOpen(ArrayList<Question> aQuestions);

        void onCovidScreenOpen();
    }

    /**
     * Contains all the Constant Values used in Socket connection.
     */
    public interface SocketEvents {
        @SuppressWarnings("SpellCheckingInspection")
        interface Music {
            String MUSIC_SETTER = "spotifyMusicSetter";
            String MUSIC_PLAY_PAUSE = "spotifyMusicPlayPause";
            String MUSIC_TRACK_CHANGE = "spotifyMusicChangeTrack";
            String MUSIC_MUTE_TOGGLE = "hubMuteUnmute";
            String MUSIC_PREVIOUS = "prev";
            String MUSIC_NEXT = "next";
        }

        @SuppressWarnings("SpellCheckingInspection")
        interface System {
            String TEST_ACKNOWLEDGE = "test_ack";
            String UPDATE_SERVICES = "updateServices";
            String SET_HUB_DETAILS = "setDetailsHub";

            interface DeviceData {
                String ID = "id";
                String TOKEN = "token";
                String HUB_ID = "hubId";
            }
        }

        @SuppressWarnings("SpellCheckingInspection")
        interface Hardware {
            String CAMERA_PAN = "woohoo_camera_pan";
            String CAMERA_PAN_EVENT_TO_HUB = "clientCameraPanEvent";
            String MUTE_UNMUTE = "hubMuteUnmute";

            interface CameraData {
                String CAMERA_DATA = "data";
                String DIRECTION = "direction";
                String ROTATION_STATUS = "rotation_status";
            }

            interface Volume {
                String VOLUME = "volume";
            }
        }

        @SuppressWarnings("SpellCheckingInspection")
        interface Lights {
            String CHANGE_DEVICE_STATE = "changeDeviceState";

            interface ChangeDeviceState {
                String DEVICE_DATA = "data";
            }
        }

        @SuppressWarnings("SpellCheckingInspection")
        interface LiveFeed {
            String INIT_LIVE_FEED = "initLiveFeed";
            String DROP_IN_ACCEPT = "dropInAccept";
            String DROP_IN_REJECT = "dropInReject";
            String DROP_IN_REJECT_HUB = "deviceDropInReject";
            String HUB_CONNECTOR = "hub_connector";
        }

        interface Hotel {
            String CHECK_IN = "checkIn";
            String CHECK_OUT = "checkOut";
        }

        //todo bluetooth interface
        interface Bluetooth {
            String DISCONNECT_ALL = "disconnectAll";
        }

        interface SubmitForm {
            String FEEDBACK_IN = "feedback";
            String COVID_IN = "covid";
        }

        interface HomeFragment {
            String UpdateHomeScreen = "updateHomeScreen";
            String UpdateHomeScreenContacts = "updateHomeContactsScreen";
        }

        interface ScreenSaverFragment {
            String UpdateScreenSaver = "updateScreenSaver";
        }

        interface Surveillance {
            String UPDATE_SURVEILLANCE = "updateSurveillanceMode";
        }

        interface ServerCall {
            String INIT_SYSYTEM_UPDATE = "systemUpdate";
            String INIT_SYSYTEM_ALERT = "deviceAlert";

        }

    }
}