/*
 * Copyright (c) 2010-2019 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sb.woohoo.ui.main.sip;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;

import com.sb.woohoo.ui.main.sip.compatibility.Compatibility;

import org.linphone.core.Call;
import org.linphone.core.ConfiguringState;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Factory;
import org.linphone.core.GlobalState;
import org.linphone.core.LogLevel;
import org.linphone.core.LoggingService;
import org.linphone.core.LoggingServiceListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;
import org.linphone.mediastream.Version;

import com.sb.woohoo.ui.main.sip.contacts.ContactsManager;
import com.sb.woohoo.ui.main.sip.notifications.NotificationsManager;
import com.sb.woohoo.ui.main.sip.service.LinphoneService;
import com.sb.woohoo.ui.main.sip.settings.LinphonePreferences;
import com.sb.woohoo.ui.main.sip.state.SipCallState;
import com.sb.woohoo.ui.main.sip.state.SipRegistrationState;

import java.util.ArrayList;


import static android.content.Intent.ACTION_MAIN;
import static com.sb.woohoo.ui.main.sip.WoohooSipSettings.enable_call_notification;

public class LinphoneContext {

    private static LinphoneContext sInstance = null;

    private Context mContext;

    private final LoggingServiceListener mJavaLoggingService =
            new LoggingServiceListener() {
                @Override
                public void onLogMessageWritten(
                        LoggingService logService, String domain, LogLevel lev, String message) {
                    switch (lev) {
                        case Debug:
                            android.util.Log.d(domain, message);
                            break;
                        case Message:
                            android.util.Log.i(domain, message);
                            break;
                        case Warning:
                            android.util.Log.w(domain, message);
                            break;
                        case Error:
                            android.util.Log.e(domain, message);
                            break;
                        case Fatal:
                        default:
                            android.util.Log.wtf(domain, message);
                            break;
                    }
                }
            };
    private CoreListenerStub mListener;
    private NotificationsManager mNotificationManager;
    private LinphoneManager mLinphoneManager;
    private ContactsManager mContactsManager;
    private final ArrayList<CoreStartedListener> mCoreStartedListeners;

    public static boolean isReady() {
        return sInstance != null;
    }

    public static LinphoneContext instance() {
        if (sInstance == null) {
            throw new RuntimeException("[Context] Linphone Context not available!");
        }
        return sInstance;
    }

    public LinphoneContext(Context context) {
        mContext = context;
        mCoreStartedListeners = new ArrayList<>();

        LinphonePreferences.instance().setContext(context);
        Factory.instance().setLogCollectionPath(context.getFilesDir().getAbsolutePath());
        boolean isDebugEnabled = LinphonePreferences.instance().isDebugEnabled();

        // Dump some debugging information to the logs
        dumpDeviceInformation();

        sInstance = this;
        Log.i("[Context] Ready");

        mListener =
                new CoreListenerStub() {
                    @Override
                    public void onGlobalStateChanged(Core core, GlobalState state, String message) {
                        Log.i("[Context] Global state is [", state, "]");

                        if (state == GlobalState.On) {
                            for (CoreStartedListener listener : mCoreStartedListeners) {
                                listener.onCoreStarted();
                            }
                        }
                    }

                    @Override
                    public void onConfiguringStatus(
                            Core core, ConfiguringState status, String message) {
                        Log.i("[Context] Configuring state is [", status, "]");

                        if (status == ConfiguringState.Successful) {
                            LinphonePreferences.instance()
                                    .setPushNotificationEnabled(
                                            LinphonePreferences.instance()
                                                    .isPushNotificationEnabled());
                        }
                    }

                    @Override
                    public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {

                        android.util.Log.i("LinphoneAccount::", "onRegistrationStateChanged :" + message + " -> " + state.name()+ " for " + core.getIdentity());
                        if ((core.getDefaultProxyConfig() != null
                                && core.getDefaultProxyConfig().equals(cfg))
                                || core.getDefaultProxyConfig() == null) {
                            android.util.Log.i("LinphoneAudio::", "state - if :" + state.name()  + " for " + core.getIdentity());

                        } else {
                            android.util.Log.i("LinphoneAudio::", "state - else :" + state.name()+ " for " + core.getIdentity());
                        }

                        if (state == RegistrationState.Ok) {
                            SipAccountManager.getInstance().sipRegistrationSubject.onNext(SipRegistrationState.RegistrationSuccess.INSTANCE);
                        } else if (state == RegistrationState.Failed) {
                            SipAccountManager.getInstance().sipRegistrationSubject.onNext(SipRegistrationState.RegistrationFailed.INSTANCE);
                        }  else if (state == RegistrationState.Progress) {
                            SipAccountManager.getInstance().sipRegistrationSubject.onNext(SipRegistrationState.RegistrationInProgress.INSTANCE);
                        } else if (state == RegistrationState.Cleared) {
                            SipAccountManager.getInstance().sipRegistrationSubject.onNext(SipRegistrationState.RegistrationInCleared.INSTANCE);
                        } else if (state == RegistrationState.None) {
                            SipAccountManager.getInstance().sipRegistrationSubject.onNext(SipRegistrationState.RegistrationInNone.INSTANCE);
                        }
                    }


                    @Override
                    public void onCallCreated(Core core, Call call) {
                        super.onCallCreated(core, call);
                      //  SipAccountManager.getInstance().currentSipCallState = new SipCallState.IncomingReceived(call.getRemoteAddress().asStringUriOnly());
                      //  SipAccountManager.getInstance().sipCallSubject.onNext(SipAccountManager.getInstance().currentSipCallState);
                        android.util.Log.i("LinphoneAccount::", "onCallCreated:"  + call.toString());

                    }

                    @Override
                    public void onEcCalibrationResult(Core core, EcCalibratorStatus status, int delayMs) {
                        super.onEcCalibrationResult(core, status, delayMs);
                        getLinphoneManager().getAudioManager().routeAudioToSpeaker();
                    }

                    @Override
                    public void onCallStateChanged(
                            Core core, Call call, Call.State state, String message) {


                        android.util.Log.i("Linphone::", "[Context] Call state is [" +  state +  "]");

                        //String jsonString = rottenTomatoesSearch(intent.getStringExtra("query"));

                        /*Intent i = new Intent(context, SipCallActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);*/


                        android.util.Log.i("LinphoneAccount::", "onCallStateChanged:"  + state.toString());

                        if (state == Call.State.IncomingReceived) {
                            core.enableMic(true);
                            getLinphoneManager().getAudioManager().routeAudioToSpeaker();
                            LinphoneManager.getCallManager().refreshInCallActions();
                            SipAccountManager.getInstance().currentSipCallState = new SipCallState.IncomingReceived(call.getRemoteAddress().asStringUriOnly());
                        } else if (state == Call.State.OutgoingInit) {
                            core.enableMic(true);
                            getLinphoneManager().getAudioManager().routeAudioToSpeaker();
                            LinphoneManager.getCallManager().refreshInCallActions();
                            SipAccountManager.getInstance().currentSipCallState = new SipCallState.OutgoingInit(call.getRemoteAddress().asStringUriOnly());
                        }
                        else if (state == Call.State.OutgoingProgress) {
                           // getLinphoneManager().getAudioManager().routeAudioToEarPiece();
                           // LinphoneManager.getCallManager().refreshInCallActions();
                            SipAccountManager.getInstance().currentSipCallState =  SipCallState.OutgoingProgress.INSTANCE;
                        } else if (state == Call.State.Connected) {
                            SipAccountManager.getInstance().currentSipCallState = SipCallState.Connected.INSTANCE;
                        } else if (state == Call.State.StreamsRunning) {
                            // Ensure mic is unmuted once media starts flowing.
                            core.enableMic(true);
                            SipAccountManager.getInstance().currentSipCallState = SipCallState.Running.INSTANCE;
                        }
                        else if (state == Call.State.Released || state == Call.State.End) {
                            SipAccountManager.getInstance().currentSipCallState = SipCallState.End.INSTANCE;
                        } else {
                            SipAccountManager.getInstance().currentSipCallState = null;
                        }

                        if(SipAccountManager.getInstance().currentSipCallState != null) {
                            SipAccountManager.getInstance().sipCallSubject.onNext(SipAccountManager.getInstance().currentSipCallState);
                        }

                        if (enable_call_notification) {
                            //mNotificationManager.displayCallNotification(call);
                        }

                        if (state == Call.State.IncomingReceived
                                || state == Call.State.IncomingEarlyMedia) {

                            // Starting SDK 24 (Android 7.0) we rely on the fullscreen intent of the
                            // call incoming notification
                            if (Version.sdkStrictlyBelow(Version.API24_NOUGAT_70)) {
                                //if (!mLinphoneManager.getCallGsmON()) onIncomingReceived();
                            }

                            // In case of push notification Service won't be started until here
                            if (!LinphoneService.isReady()) {
                                Log.i("[Context] Service not running, starting it");
                                Intent intent = new Intent(ACTION_MAIN);
                                intent.setClass(mContext, LinphoneService.class);
                                Compatibility.startService(mContext,intent);
                            }
                        } else if (state == Call.State.OutgoingInit) {
                            //onOutgoingStarted();
                        } else if (state == Call.State.Connected) {
                           // onCallStarted();
                        } else if (state == Call.State.End
                                || state == Call.State.Released
                                || state == Call.State.Error) {
                            if (LinphoneService.isReady()) {
                                LinphoneService.instance().destroyOverlay();
                            }

                            if (state == Call.State.Released
                                    && call.getCallLog().getStatus() == Call.Status.Missed) {
                                //mNotificationManager.displayMissedCallNotification(call);
                            }
                        }
                    }
                };

        mLinphoneManager = new LinphoneManager(context);
        mNotificationManager = new NotificationsManager(context);

        //Device settings
        /*if (DeviceUtils.isAppUserRestricted(mContext)) {
            // See https://firebase.google.com/docs/cloud-messaging/android/receive#restricted
            Log.w(
                    "[Context] Device has been restricted by user (Android 9+), push notifications won't work !");
        }

        int bucket = DeviceUtils.getAppStandbyBucket(mContext);
        if (bucket > 0) {
            Log.w(
                    "[Context] Device is in bucket "
                            + Compatibility.getAppStandbyBucketNameFromValue(bucket));
        }

        if (!PushNotificationUtils.isAvailable(mContext)) {
            Log.w("[Context] Push notifications won't work !");
        }*/
    }

    public void start(boolean isPush) {
        Log.i("[Context] Starting, push status is ", isPush);
        mLinphoneManager.startLibLinphone(isPush, mListener);

        mNotificationManager.onCoreReady();

        mContactsManager = new ContactsManager(mContext);
        if (!Version.sdkAboveOrEqual(Version.API26_O_80)
                || (mContactsManager.hasReadContactsAccess())) {
            mContext.getContentResolver()
                    .registerContentObserver(
                            ContactsContract.Contacts.CONTENT_URI, true, mContactsManager);
        }
        if (mContactsManager.hasReadContactsAccess()) {
            mContactsManager.enableContactsAccess();
        }
        mContactsManager.initializeContactManager();
    }

    public void destroy() {
        Log.i("[Context] Destroying");
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
            core = null; // To allow the gc calls below to free the Core
        }

        // Make sure our notification is gone.
        if (mNotificationManager != null) {
            mNotificationManager.destroy();
        }

        // Destroy the LinphoneManager second to last to ensure any getCore() call will work
        if (mLinphoneManager != null) {
            mLinphoneManager.destroy();
        }

        // Wait for every other object to be destroyed to make LinphoneService.instance() invalid
        sInstance = null;

        if (LinphonePreferences.instance().useJavaLogger()) {
            Factory.instance().getLoggingService().removeListener(mJavaLoggingService);
        }
        LinphonePreferences.instance().destroy();
    }

    public void updateContext(Context context) {
        mContext = context;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    /* Managers accessors */

    public LoggingServiceListener getJavaLoggingService() {
        return mJavaLoggingService;
    }

    public NotificationsManager getNotificationManager() {
        return mNotificationManager;
    }

    public LinphoneManager getLinphoneManager() {
        return mLinphoneManager;
    }

    public void addCoreStartedListener(CoreStartedListener listener) {
        mCoreStartedListeners.add(listener);
    }

    public void removeCoreStartedListener(CoreStartedListener listener) {
        mCoreStartedListeners.remove(listener);
    }

    public ContactsManager getContactsManager() {
        return mContactsManager;
    }

    /* Log device related information */

    private void dumpDeviceInformation() {
        Log.i("==== Phone information dump ====");
        Log.i("DISPLAY NAME=" + Compatibility.getDeviceName(mContext));
        Log.i("DEVICE=" + Build.DEVICE);
        Log.i("MODEL=" + Build.MODEL);
        Log.i("MANUFACTURER=" + Build.MANUFACTURER);
        Log.i("ANDROID SDK=" + Build.VERSION.SDK_INT);
        StringBuilder sb = new StringBuilder();
        sb.append("ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi).append(", ");
        }
        Log.i(sb.substring(0, sb.length() - 2));
    }

    public interface CoreStartedListener {
        void onCoreStarted();
    }
}
