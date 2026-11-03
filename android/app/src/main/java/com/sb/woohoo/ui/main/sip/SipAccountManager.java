package com.sb.woohoo.ui.main.sip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.main.sip.call.models.SipCallData;
import com.sb.woohoo.ui.main.sip.service.LinphoneService;
import com.sb.woohoo.ui.main.sip.service.ServiceWaitThread;
import com.sb.woohoo.ui.main.sip.service.ServiceWaitThreadListener;
import com.sb.woohoo.ui.main.sip.settings.LinphonePreferences;
import com.sb.woohoo.ui.main.sip.state.SipCallActionState;
import com.sb.woohoo.ui.main.sip.state.SipCallState;
import com.sb.woohoo.ui.main.sip.state.SipRegistrationState;

import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Factory;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Reason;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;
import org.reactivestreams.Subscriber;

import java.util.Locale;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

@SuppressLint("CheckResult")
public class SipAccountManager {

    SharedPrefsHelper mSharedPrefsHelper;
    private SipAccountManager() {
        //private constructor
        mSharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
    }

    public SipCallState currentSipCallState = null;

    private static SipAccountManager sInstance;

    public static SipAccountManager getInstance() {

        if(sInstance == null) {
            sInstance = new SipAccountManager();
        }
        return sInstance;
    }

    public PublishSubject<SipRegistrationState> sipRegistrationSubject = PublishSubject.create();
    public PublishSubject<SipCallState> sipCallSubject = PublishSubject.create();
    public PublishSubject<SipCallActionState> sipCallActionSubject = PublishSubject.create();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SipCallData outgoingSipCallData;


    public void initLinphoneService(Context context) {



        if (LinphoneService.isReady()) {
            onLinphoneServiceReady(context);
        } else {
            try {
                ServiceWaitThreadListener listener = () -> onLinphoneServiceReady(context);
                android.util.Log.i("Linphone", "Starting service");

                context.startService(new Intent().setClass(context, LinphoneService.class));
                new ServiceWaitThread(listener).start();
            } catch (IllegalStateException ise) {
                android.util.Log.e("Linphone", "Exception raised while starting service: " + ise);
            }

        }
    }

    private boolean hasCredentialChanged(ProxyConfig proxyConfig) {

        return !proxyConfig.getTransport().equalsIgnoreCase(mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_TRANSPORT, "")) ||
                !proxyConfig.getDomain().equalsIgnoreCase(mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_DOMAIN, ""))    ||
                !proxyConfig.getIdentityAddress().getUsername().equalsIgnoreCase(mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_ID, ""));



    }

    private Consumer getSipEventsSubscriber() {
        Consumer consumer = (sipCallState) -> {
            if (sipCallState instanceof SipCallActionState.Accept) {
                Call call = LinphoneContext.instance().getLinphoneManager().getCore().getCurrentCall();
                android.util.Log.i("Linphone::", "call : " + call.getRemoteAddressAsString());
                LinphoneContext.instance().getLinphoneManager().getCallManager().acceptCall(call);
                android.util.Log.i("Linphone::", "after accepting call mic: ");

            } else if (sipCallState instanceof SipCallActionState.End) {
                LinphoneContext.instance().getLinphoneManager().getCore().getCalls()[0].terminate();
            } else if (sipCallState instanceof SipCallActionState.Reject) {
                LinphoneContext.instance().getLinphoneManager().getCore().getCalls()[0].decline(Reason.Declined);
            } else if (sipCallState instanceof SipCallActionState.outgoingCallInit) {
                SipCallActionState.outgoingCallInit outgoingCallInit = (SipCallActionState.outgoingCallInit) sipCallState;
                String dialNumber = outgoingCallInit.getSipAddress();
                if (dialNumber == null || dialNumber.trim().isEmpty()) {
                    android.util.Log.e("Linphone::", "Ignoring outgoing SIP call without a valid number");
                    return;
                }
                if (isCurrentSipAccount(dialNumber)) {
                    Toast.makeText(
                            WooHooApp.getAppContext(),
                            WooHooApp.getAppContext().getString(R.string.sip_self_call_error),
                            Toast.LENGTH_LONG)
                            .show();
                    android.util.Log.i("Linphone::", "Ignoring outgoing SIP call to current account: " + dialNumber);
                    return;
                }
                outgoingSipCallData = new SipCallData(
                        dialNumber,
                        outgoingCallInit.getDisplayName(),
                        outgoingCallInit.getDisplayNumber(),
                        outgoingCallInit.getImageUrl());
                String address = "sip:" + dialNumber.replaceAll("-" , "") + "@" + mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_DOMAIN, "");
                android.util.Log.i("Linphone::", "calling : " + address);
                LinphoneContext.instance().getLinphoneManager().getCallManager().newOutgoingCall(address, null);

            } else {
                android.util.Log.i("Linphone::", "unknown sip state:");
            }
        };

            return consumer;
    }

    private boolean isCurrentSipAccount(String dialNumber) {
        String currentSipId = mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_ID, "");
        return !normalizeSipUser(dialNumber).isEmpty()
                && normalizeSipUser(dialNumber).equalsIgnoreCase(normalizeSipUser(currentSipId));
    }

    private String normalizeSipUser(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim();
        if (normalized.startsWith("sip:")) {
            normalized = normalized.substring(4);
        }
        int atIndex = normalized.indexOf('@');
        if (atIndex >= 0) {
            normalized = normalized.substring(0, atIndex);
        }
        int paramIndex = normalized.indexOf(';');
        if (paramIndex >= 0) {
            normalized = normalized.substring(0, paramIndex);
        }
        return normalized.replace("-", "")
                .replace(" ", "")
                .trim();
    }

    private void setSipSubcription() {
        android.util.Log.i("XXX::", "No sip status observers, so create now");
        Disposable d = sipCallActionSubject.subscribe(
                getSipEventsSubscriber(),
                throwable -> {
                    android.util.Log.i("Linphone::", "sip error:" + throwable);
                    setSipSubcription();
                });
        compositeDisposable.add(d);
    }

    @SuppressLint("CheckResult")
    private void onLinphoneServiceReady(Context context) {

        android.util.Log.i("LinphoneAccount", "onServiceReady");
        // Once the service is ready, we can move on in the application
        // We'll forward the intent action, type and extras so it can be handled
        // by the next activity if needed, it's not the launcher job to do that

        Core core = LinphoneContext.instance().getLinphoneManager().getCore();
        if (core != null) {
            if(core.getProxyConfigList() != null && core.getProxyConfigList().length > 1) {
                android.util.Log.i("LinphoneAccount::", String.format("getProxyConfigList size: (%d)", core.getProxyConfigList().length));
                delete();
            }
            if (core.getProxyConfigList() != null && core.getProxyConfigList().length ==1 && hasCredentialChanged(core.getProxyConfigList()[0])) {
                android.util.Log.i("LinphoneAccount::", "getProxyConfigList has changed");
                delete();
            }


            if (core.getProxyConfigList() == null || core.getProxyConfigList().length ==0) {
                android.util.Log.i("LinphoneAccount::", "getProxyConfigList is null");
                showNoAccountConfigured(context);
            } else {
                android.util.Log.i("LinphoneAccount::", String.format("getProxyConfigList (%d):", core.getProxyConfigList().length) + core.getProxyConfigList()[0].getIdentityAddress().getUsername() + "@" + core.getProxyConfigList()[0].getIdentityAddress().getDomain());
                // configureEchoCalibration();
                core.refreshRegisters();
            }
        } else {
            android.util.Log.e("LinphoneAccount::", "core is null");
        }

        if (!sipCallActionSubject.hasObservers()) {
            setSipSubcription();
            LinphoneContext.instance().getLinphoneManager().changeStatusToOnline();

        } else {
            android.util.Log.e("XXX::", "Already has observers");
        }


    }

    private void showNoAccountConfigured(Context context) {
       // LinphoneAddressForDevice addressForDevice = new LinphoneAddressForDevice("1011", "3bc4eb1e", "training", "core1-hk.netustay.com");
        compositeDisposable.add(sipRegistrationSubject.subscribe(state -> {
            if(state instanceof SipRegistrationState.RegistrationFailed) {
            delete();
        }}));

        LinphoneAddressForDevice addressForDevice = new LinphoneAddressForDevice(mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_ID, ""),
                mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_PASS, ""),
                mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_DOMAIN, ""),
                mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_PROXY, ""),
                mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_TRANSPORT, ""));

        Toast.makeText(context, "Trying to connect..", Toast.LENGTH_LONG).show();
       createProxyConfig(context, addressForDevice);

        //step-2 :
        configureEchoCalibration(context);

        //steps-3 :
        findProxy(addressForDevice.getProxy(), addressForDevice.getTransport());//updateProxyConfig();

    }

    public void configureEchoCalibration(Context context) {

        boolean needsEchoCalibration =
                LinphoneManager.getCore().isEchoCancellerCalibrationRequired();
        boolean echoCalibrationDone =
                LinphonePreferences.instance().isEchoCancellationCalibrationDone();
        android.util.Log.i("LinphoneAccount::",
                "[Assistant] Echo cancellation calibration required ? "
                        + needsEchoCalibration
                        + ", already done ? "
                        + echoCalibrationDone);

        if (needsEchoCalibration && !echoCalibrationDone) {
            LinphonePreferences.instance().setEchoCancellationCalibrationDone(true);
            startEchoCancellerCalibration(context);
        }

    }

    private void startEchoCancellerCalibration(Context context) {
        LinphoneManager.getCore()
                .addListener(
                        new CoreListenerStub() {
                            @Override
                            public void onEcCalibrationResult(
                                    Core core, EcCalibratorStatus status, int delayMs) {
                                if (status == EcCalibratorStatus.InProgress) return;
                                LinphoneManager.getAudioManager().routeAudioToEarPiece();
                                //done

                                ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                                        .setMode(AudioManager.MODE_NORMAL);
                            }
                        });
        LinphoneManager.getAudioManager().startEcCalibration();
    }

    public void clearCredentialsCache() {
        mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_SIP_ID);
                mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_SIP_PASS);
                mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_SIP_DOMAIN);
                mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_SIP_PROXY);
                mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_SIP_TRANSPORT);
                mSharedPrefsHelper.clear(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE);
    }


    public void delete() {

       Core core = LinphoneManager.getCore();
        ProxyConfig[] proxyConfigs = core.getProxyConfigList();

        ProxyConfig mProxyConfig = null;
        AuthInfo mAuthInfo = null;
        android.util.Log.i("LinphoneAccount::", "getProxyConfigList :delete" + core.getProxyConfigList().length);
        if (proxyConfigs.length > 0) {
            for (int i = 0; i < proxyConfigs.length; i++) {
                mProxyConfig = proxyConfigs[i];
                mAuthInfo = mProxyConfig.findAuthInfo();

                if (core != null) {
                    if (mProxyConfig != null) {
                        core.removeProxyConfig(mProxyConfig);
                    }
                    if (mAuthInfo != null) {
                        core.removeAuthInfo(mAuthInfo);
                    }
                    core.refreshRegisters();
                }
            }

        }

    }

    public SipCallData getOutgoingSipCallData() {
        return outgoingSipCallData;
    }

    public void clearOutgoingSipCallData() {
        outgoingSipCallData = null;
    }

    public void createProxyConfig(Context context, LinphoneAddressForDevice linphoneAddressForDevice) {

            Core core = LinphoneManager.getCore();

        if (core != null) {
            android.util.Log.i("LinphoneAccount::","[Generic Connection Assistant] Reloading configuration with default");
            reloadDefaultAccountCreatorConfig();
        }

        AccountCreator accountCreator = getAccountCreator();
        accountCreator.setUsername(linphoneAddressForDevice.getRoom());
        accountCreator.setDomain(linphoneAddressForDevice.getDomain());
        accountCreator.setPassword(linphoneAddressForDevice.getPassword());
        accountCreator.setDisplayName(linphoneAddressForDevice.getRoom());

        if(linphoneAddressForDevice.getTransport().equalsIgnoreCase(context.getResources().getString(R.string.udp)))
            accountCreator.setTransport(TransportType.Udp);
        else if(linphoneAddressForDevice.getTransport().equalsIgnoreCase(context.getResources().getString(R.string.tcp)))
            accountCreator.setTransport(TransportType.Tcp);
        else // TLS
            accountCreator.setTransport(TransportType.Tls);


        ProxyConfig proxyConfig = accountCreator.createProxyConfig();

        // If this isn't a sip.linphone.org account, disable push notifications and enable
        // service notification, otherwise incoming calls won't work (most probably)
        if (proxyConfig != null) {
            proxyConfig.setPushNotificationAllowed(false);
        }
        android.util.Log.i("LinphoneAcccount::",
                "[Assistant] Unknown domain used, push probably won't work, enable service mode");
        LinphonePreferences.instance().setServiceNotificationVisibility(true);
        LinphoneContext.instance().getNotificationManager().startForeground();

            if (proxyConfig == null) {
                android.util.Log.e("LinphoneAcccount::", "[Assistant] Account creator couldn't create proxy config");
                // TODO: display error message
            } else {
                LinphonePreferences.instance().firstLaunchSuccessful();
            }

    }

    public ProxyConfig findProxy(String proxyDomain, String transport) {

        boolean mIsNewlyCreatedAccount = false;
        ProxyConfig mProxyConfig = null;
        Core core = LinphoneManager.getCore();
        if(LinphoneManager.getCore().getProxyConfigList().length > 0) {
            if (LinphoneManager.getCore().getProxyConfigList().length > 1) {
                delete();
            } else if(hasCredentialChanged(LinphoneManager.getCore().getProxyConfigList()[0])) {
                delete();
            } else {
                mIsNewlyCreatedAccount = false;
                mProxyConfig = LinphoneManager.getCore().getProxyConfigList()[0];
            }
        }
        AuthInfo  mAuthInfo = null;
        // Create a proxy config if there is none
        if (mProxyConfig == null) {
            // Ensure the default configuration is loaded first
            String defaultConfig = LinphonePreferences.instance().getDefaultDynamicConfigFile();
            core.loadConfigFromXml(defaultConfig);
            mProxyConfig = core.createProxyConfig();
            mAuthInfo = Factory.instance().createAuthInfo(null, null, null, null, null, null);
            mIsNewlyCreatedAccount = true;
        }

        if (mProxyConfig != null) {
            mAuthInfo = mProxyConfig.findAuthInfo();
            if(!proxyDomain.isEmpty()) {
                mProxyConfig.edit();
                String proxyAddr = String.format("<sip:%s;transport=%s>", proxyDomain, transport);
                Address proxy = Factory.instance().createAddress(proxyAddr);
                if (proxy != null) {
                    Log.i(
                            "NETUSTAY",
                            "mProxy - proxy is not  null: " + proxy.asString());

                    mProxyConfig.setServerAddr(proxy.asString());
                    mProxyConfig.setRoute(proxy.asString());
                }
                mProxyConfig.done();
            }
        }


        if (mIsNewlyCreatedAccount) {
            if (core != null && mProxyConfig != null && mAuthInfo != null) {
                core.addAuthInfo(mAuthInfo);
                core.addProxyConfig(mProxyConfig);
                core.setDefaultProxyConfig(mProxyConfig);
            }
        }

        return mProxyConfig;
    }

    public void updateProxyConfig() {
        ProxyConfig mProxyConfig = LinphoneManager.getCore().getDefaultProxyConfig();
        if (mProxyConfig != null) {
            mProxyConfig.edit();
            String server = mProxyConfig.getServerAddr();
            Address serverAddr = Factory.instance().createAddress(server);
            if (serverAddr != null) {
                try {
                    serverAddr.setTransport(TransportType.Udp);
                    server = serverAddr.asString();
                    mProxyConfig.setServerAddr(server);
                    mProxyConfig.setRoute(server);
                } catch (NumberFormatException nfe) {
                    Log.e(nfe);
                }
            }
            mProxyConfig.done();
        } else {
            android.util.Log.e("LinphoneAccount::","[Account Settings] No proxy config !");
        }
    }

    public void deleteAccount() {
        Core core = LinphoneManager.getCore();
        ProxyConfig[] proxyConfigs = core.getProxyConfigList();

        ProxyConfig mProxyConfig = null;
        AuthInfo mAuthInfo = null;
        if (proxyConfigs.length > 0) {
            for (int i = 0; i < proxyConfigs.length; i++) {
                mProxyConfig = proxyConfigs[i];
                mAuthInfo = mProxyConfig.findAuthInfo();

                if (core != null) {
                    if (mProxyConfig != null) {
                        core.removeProxyConfig(mProxyConfig);
                    }
                    if (mAuthInfo != null) {
                        core.removeAuthInfo(mAuthInfo);
                    }
                    core.refreshRegisters();
                }
            }
        }


        // Set a new default proxy config if the current one has been removed
        if (core != null && core.getDefaultProxyConfig() == null) {
            if (proxyConfigs.length > 0) {
                core.setDefaultProxyConfig(proxyConfigs[0]);
            }
        }
    }

    public AccountCreator getAccountCreator() {
        return LinphoneManager.getInstance().getAccountCreator();
    }

    private void reloadAccountCreatorConfig(String path) {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.loadConfigFromXml(path);
            AccountCreator accountCreator = getAccountCreator();
            accountCreator.reset();
            accountCreator.setLanguage(Locale.getDefault().getLanguage());
        }
    }

    void reloadDefaultAccountCreatorConfig() {
        android.util.Log.i("LinphoneAccount::", "[Assistant] Reloading configuration with default");
        reloadAccountCreatorConfig(LinphonePreferences.instance().getDefaultDynamicConfigFile());
    }
}
