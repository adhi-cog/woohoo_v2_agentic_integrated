package com.sb.woohoo.ui.main;

import static android.media.AudioManager.STREAM_SYSTEM;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_INTRO_VIDEO_KEY;
import static com.sb.woohoo.analytics.CustomEvents.category_name_fnb;
import static com.sb.woohoo.analytics.CustomEvents.event_name_add_to_cart;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_SURVEILLANCE_MODE;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_ID;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_MUTE_STATE;
import static com.sb.woohoo.ui.base.IHintBarState.HINT_NO_CHANGE;
import static com.sb.woohoo.ui.base.IHintBarState.HINT_RESET;
import static com.sb.woohoo.ui.main.helpers.ConnectivityHelper.GOOGLE_ADDRESS;
import static com.sb.woohoo.ui.main.helpers.ConnectivityHelper.GOOGLE_PORT;
import static com.sb.woohoo.utils.Constants.DARK_MODE;
import static com.sb.woohoo.utils.Constants.DEFAULT_VALUE_FOR_ORDER;
import static com.sb.woohoo.utils.Constants.ENDING_POINT_TO_DISABLE_DARK_MODE;
import static com.sb.woohoo.utils.Constants.HUB_DETAILS;
import static com.sb.woohoo.utils.Constants.RESTART_DEFAULT_HOUR;
import static com.sb.woohoo.utils.Constants.RESTART_DEFAULT_MIN;
import static com.sb.woohoo.utils.Constants.STARTING_POINT_TO_ENABLE_DARK_MODE;
import static com.sb.woohoo.utils.others.LangUtils.DEFAULT_LANG;
import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;
import static com.sb.woohoo.utils.others.MethodUtils.getVersionNameAndVersionCode;
import static com.sb.woohoo.utils.others.MethodUtils.goToSleep;
import static com.sb.woohoo.utils.others.MethodUtils.hasSystemPermission;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;
import static com.sileria.android.Kit.getSystemService;
import static com.sileria.android.Resource.getString;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.HomeFragmentViewModel;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.FnbEvent;
import com.sb.woohoo.analytics.SipStatusConstant;
import com.sb.woohoo.custom.CustomDialogAlert;
import com.sb.woohoo.custom.CustomDialogBoxWithSubTitle;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.WoohooExceptionHandler;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.ActivityHomeBinding;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.base.IHintBarState;
import com.sb.woohoo.ui.enrollment.EnrollmentActivity;
import com.sb.woohoo.ui.main.di.DaggerMainComponent;
import com.sb.woohoo.ui.main.di.MainComponent;
import com.sb.woohoo.ui.main.di.MainModule;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmFragment;
import com.sb.woohoo.ui.main.fragments.amadeusServices.AmadeusServicesFragment;
import com.sb.woohoo.ui.main.fragments.bluetooth.BluetoothUnSignedSettingsFragment;
import com.sb.woohoo.ui.main.fragments.bluetoothSigned.BluetoothSignedSettingsFragment;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.covid.CovidFragment;
import com.sb.woohoo.ui.main.fragments.darkMode.DarkModeFragment;
import com.sb.woohoo.ui.main.fragments.devices.discovery.DeviceDiscoveryFragment;
import com.sb.woohoo.ui.main.fragments.dialer.DialerFragment;
import com.sb.woohoo.ui.main.fragments.calllog.CallLogStorage;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.DiningRestaurantsFragment;
import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.feedback.FeedbackFragment;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.TaxData;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.home.HomeLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.home.IntroVideoPlayerDialog;
import com.sb.woohoo.ui.main.fragments.interel.InterelUtilityFragment;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.LiveFeedAudioFragment;
import com.sb.woohoo.ui.main.fragments.livefeedhub.LiveFeedToHubFragment;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.LiveFeedOwnDeviceFragment;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.music.MusicFragment;
import com.sb.woohoo.ui.main.fragments.newstext.headline.NewsHeadlineFragment;
import com.sb.woohoo.ui.main.fragments.overview.categories.OverviewCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.qrcode.QRCodeFragment;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.RelaxationMusicCache;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.RelaxationMusicFragment;
import com.sb.woohoo.ui.main.fragments.reminders.ReminderFragment;
import com.sb.woohoo.ui.main.fragments.remote.TVRemoteFragment;
import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;
import com.sb.woohoo.ui.main.fragments.screensaver.ScreenSaverFragment;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;
import com.sb.woohoo.ui.main.fragments.settings.update.InstallCallback;
import com.sb.woohoo.ui.main.fragments.settings.update.InstallTask;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.settings.update.test.UpdateService;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.fragments.translation.TranslationFragment;
import com.sb.woohoo.ui.main.fragments.video.VideoFragment;
import com.sb.woohoo.ui.main.fragments.video.VideoListFragment;
import com.sb.woohoo.ui.main.fragments.weather.WeatherFragment;
import com.sb.woohoo.ui.main.fragments.weather.di.LatLngByIPModel;
import com.sb.woohoo.ui.main.fragments.webview.WebViewFragment;
import com.sb.woohoo.ui.main.helpers.TimerHelper;
import com.sb.woohoo.ui.main.helpers.UpdateHelper;
import com.sb.woohoo.ui.main.pojo.EndSession;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.ui.main.pojo.RemoteSystemUpdateModel;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckInResponse;
import com.sb.woohoo.ui.main.pojo.socketevent.HotelCheckOutResponse;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayImage;
import com.sb.woohoo.ui.main.secondarydisplay.offers.SecondDisplay;
import com.sb.woohoo.ui.main.secondarydisplay.offers.SecondDisplayDataListener;
import com.sb.woohoo.ui.main.secondarydisplay.offers.SecondDisplayStateListener;
import com.sb.woohoo.ui.main.sip.LinphoneContext;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.ui.main.sip.call.models.SipCallData;
import com.sb.woohoo.ui.main.sip.state.SipCallState;
import com.sb.woohoo.ui.main.sip.state.SipRegistrationState;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.WorkerUtil;
import com.sb.woohoo.utils.gps.GPSLocation;
import com.sb.woohoo.utils.others.AssetUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.spotify.sdk.android.player.Metadata;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MainContract.Views,
        UpdateService.UpdateListener, InstallCallback, SecondDisplayStateListener,
        IntroVideoPlayerDialog.DialogCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_MODIFY_SETTINGS = 200;
    private final static int WIFI_NOT_CONNECTED = 100;
    private final static int NO_INTERNET = 101;
    private final static int SERVER_ERROR = 102;
    private final static int UNKNOWN_ERROR = 103;
    private final static int BLUETOOTH_CODE = 1234;
    private static final int MIC_PERMISSION_REQUEST_CODE = 9001;
    private static int connectionRetry = 0;

    private final Boolean ENABLE_PERIODIC_RESTART = false;
    private final String ALERT_ORDER = "order";
    private final String ALERT_DISPLAY_TYPE = "Fullscreen";
    private final String ALERT_MEDIA_TYPE_IMAGE = "Image";
    private final String ALERT_MEDIA_TYPE_VIDEO = "video";

    private CompositeDisposable sipCompositeDisposable = new CompositeDisposable();
    private boolean isSipCallScreenVisibleOrOpening = false;


    public ActivityHomeBinding binding;


    // Presenter.
    @Inject
    MainPresenter mainPresenterObject;
    @Inject
    GPSLocation mGPSLocation;
    Dialog dialog, dialogCheckInOut;

    private CustomDialogBoxWithSubTitle alertOrderNow;

    private Dialog dialogScreen;
    ConstraintLayout settingUpLayout;
    ConstraintLayout errorLayout;
    ConstraintLayout dialogBackground;
    TextView errorMessage;
    TextView versioncodeText;
    TextView errorAction;
    boolean setupSuccessful = false;
    MainContract.Actions mainPresenter;
    @Inject
    UpdateHelper updateHelper;
    private SharedPrefsHelper mPrefsHelper;
    // Variables.
    private boolean isHintVisibility = true;
    private double[] latLng = new double[2];
    private CustomDialogAlert dialogAlert;
    private UpdateService updateService;
    private UpdateDataModel updateDataModel;

    private Dialog settingsRequestAlertDialog = null;
    private Dialog sipDetailsDialog = null;

    private ArrayList<Product> selectedProductsCart = new ArrayList<>();
    private double totalPrice = 0;

    private ArrayList<TaxData> mTaxList = new ArrayList<>();

    private String currentLang;

    private CompositeDisposable disposable = new CompositeDisposable();

    // Display / Presentation ....

    private SecondDisplayDataListener secondDisplayDataListener;
    private SecondDisplay secondDisplay;
    private String hotelLogo;
    private boolean settingsMode = false;
    public boolean isTransparentActivityEnabled = false;

    HomeFragmentViewModel model;
    public BluetoothAdapter bluetoothAdapter;

    private final boolean ENABLE_NIGHT_MODE = false;
    @Inject
    AnalyticsHelper helper;

    private Handler bottomAppsGestureHandler = new Handler();
    private Runnable hideGestureRunnable = new Runnable() {
        @Override
        public void run() {
            findViewById(R.id.rl_lottie_swipe_up).setVisibility(GONE);
        }
    };
    private long timeLeft;

    /********************************** {@link Activity} Callback Methods
     *  *********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configureRotation();
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(MainActivity.this));
        showLoading();
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        injectActivity();
        initializeViews();
        if (ENABLE_PERIODIC_RESTART) {
            startPeriodicRestart();
        }
        showFirstTimeIntroGesture();
        setClickEvents();
        setTopDrawerVisibility();

        BluetoothManager lBluetoothManager =
                (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (lBluetoothManager != null) {
            bluetoothAdapter = lBluetoothManager.getAdapter();
        }

        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        model = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        if (bluetoothAdapter != null) {
            updateBluetoothIconInHomePage(bluetoothAdapter.isEnabled());
        }

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            drawerItemEnable();
        } else if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            drawerItemDisEnable();
        }

        mainPresenter = mainPresenterObject;

        animationComplete();

        if (isGpsSupported()) {
            mGPSLocation.requestLocationUpdate();
        }

        new TimerHelper(MainActivity.this);
        new UpdateHelper(getApplicationContext());

        connectServer();

        updateService = updateHelper.getUpdateService();
        if (updateService != null) {
            updateService.registerListener(this);
        }

        mPrefsHelper = WooHooApp.getSharedPrefsHelper();
        ensureMicPermissionAtStartup();

        currentLang = LocaleHelper.getLanguage(MainActivity.this);
//        if (currentLang.equalsIgnoreCase(Constants.LANGUAGE.CHINESH)) {
//            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_LANGUAGE_CHANGE,
//            LANGUAGE_CHINESE);
//        } else if (currentLang.equalsIgnoreCase(Constants.LANGUAGE.ARABIC)) {
//            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_LANGUAGE_CHANGE,
//            LANGUAGE_ARABIC);
//        } else if (currentLang.equalsIgnoreCase(Constants.LANGUAGE.DUTCH)) {
//            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_LANGUAGE_CHANGE,
//            LANGUAGE_DUTCH);
//        } else {
//            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_LANGUAGE_CHANGE,
//            LANGUAGE_ENGLISH);
//        }

        mainPresenter.callUpdateAppVersion();

//        if (BuildConfig.DEBUG && mPrefsHelper.get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE,
//        false)) {
//            if (mPrefsHelper.get(SharedPrefsKey.PREF_KEY_SIP_ID, "").isEmpty()) {
//                showGetSipDetailsDialog();
//            } else {
//                initSipRegistration();
//            }
//        }

//        disable top bar call options for RC_DOMAIN_ID
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID) || domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID) || domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID) || domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID)) {
            binding.content.drawerTopLayout2.setVisibility(GONE);
        }

        //TODO Disable Night Mode AS It was Crashing , need to resolve in future
        if (ENABLE_NIGHT_MODE) {
            createAlarmToDisableSurveillance();
            createAlarmToEnableSurveillance();
        }

        if (domainId.equalsIgnoreCase(Constants.GBTB_DOMAIN_ID))
            loadSecondDisplayIfExists(); // Secondary Display

        mainPresenter.getLanguageRequest();


        // Start periodic updates to store SIP connection status in Firestore
        handler.post(updateSipStatusRunnable);
    }

//    private void enableLinphoneConfig() {
//        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
//        if(domainId.equalsIgnoreCase(HOLIDAY_INN_DOMAIN_ID)){
//            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, true);
//        }
//    }


    /**
     * it will set a timer for restart
     * periodic work manager is used {@link WorkerUtil}
     */
    private void startPeriodicRestart() {
        int hours =
                WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_HR,
          RESTART_DEFAULT_HOUR);
        int minutes =
         WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_MIN,
          RESTART_DEFAULT_MIN);
        new WorkerUtil().workFun(MainActivity.this, (hours * 60) + minutes);
    }

    public void drawerItemDisEnable() {

        binding.content.llBluetoothImg.setColorFilter(ContextCompat.getColor(this, R.color.black)
                , PorterDuff.Mode.SRC_IN);
        binding.content.llBluetoothText.setTextColor(getResources().getColor(R.color.black));
        binding.content.llBluetoothText.setTypeface(null, Typeface.NORMAL);
    }

    public void drawerItemEnable() {
        binding.content.llBluetoothText.setTextColor(getResources().getColor(R.color.purple_theme_tile_color_tertiary));
        binding.content.llBluetoothText.setTypeface(null, Typeface.BOLD);
        binding.content.llBluetoothImg.setColorFilter(ContextCompat.getColor(this,
R.color.purple_theme_tile_color_tertiary), PorterDuff.Mode.SRC_IN);
    }

    private boolean isBluetoothPermission() {
        boolean lPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lPermission = ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return lPermission;
    }

    private void showFirstTimeIntroGesture() {
        findViewById(R.id.rl_lottie_swipe_up).setVisibility(VISIBLE);
        bottomAppsGestureHandler.postDelayed(hideGestureRunnable, 7000);
    }

    public void enableFullWidthMode() {
        findViewById(R.id.container_left).setVisibility(GONE);
    }

    public void disableFullWidthMode() {
        findViewById(R.id.container_left).setVisibility(VISIBLE);
    }

    private void showGetSipDetailsDialog() {
        if (sipDetailsDialog != null && sipDetailsDialog.isShowing()) {
            sipDetailsDialog.dismiss();
            sipDetailsDialog = null;
        }
        sipDetailsDialog = new Dialog(this, R.style.DialogTheme);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(sipDetailsDialog);
        }

        sipDetailsDialog.setContentView(R.layout.dialog_sip_setup);

        Button cancel = sipDetailsDialog.findViewById(R.id.button_cancel);
        Button submit = sipDetailsDialog.findViewById(R.id.button_submit);

        RadioGroup radioButtonGroup = sipDetailsDialog.findViewById(R.id.radio_group_transport);


        cancel.setOnClickListener(v -> sipDetailsDialog.dismiss());

        submit.setOnClickListener(v -> {
            mPrefsHelper.put(SharedPrefsKey.PREF_KEY_SIP_ID,
             ((EditText) sipDetailsDialog.findViewById(R.id.edt_sip_id)).getEditableText().toString());
            mPrefsHelper.put(SharedPrefsKey.PREF_KEY_SIP_PASS,
             ((EditText) sipDetailsDialog.findViewById(R.id.edt_sip_pass)).getEditableText().toString());
            mPrefsHelper.put(SharedPrefsKey.PREF_KEY_SIP_DOMAIN,
             ((EditText) sipDetailsDialog.findViewById(R.id.edt_sip_domain)).getEditableText().toString());
            mPrefsHelper.put(SharedPrefsKey.PREF_KEY_SIP_PROXY,
             ((EditText) sipDetailsDialog.findViewById(R.id.edt_sip_proxy)).getEditableText().toString());

            int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
            View radioButton = radioButtonGroup.findViewById(radioButtonID);
            int idx = radioButtonGroup.indexOfChild(radioButton);
            RadioButton r = (RadioButton) radioButtonGroup.getChildAt(idx);
            if (r != null) {
                mPrefsHelper.put(SharedPrefsKey.PREF_KEY_SIP_TRANSPORT,
                 r.getText().toString().trim());
                initSipRegistration();
                sipDetailsDialog.dismiss();
            }
        });

        sipDetailsDialog.setCancelable(false);

        if (sipDetailsDialog != null && !sipDetailsDialog.isShowing()) {
            sipDetailsDialog.show();
        }
    }

    public void initSipRegistration() {
        if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
            LoggerWrapper.i(TAG, "Skipping SIP initialization because Linphone feature is disabled");
            return;
        }

        String sipId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SIP_ID, "");
        String sipPassword = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SIP_PASS, "");
        String sipDomain = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SIP_DOMAIN, "");
        if (TextUtils.isEmpty(sipId) || TextUtils.isEmpty(sipPassword) || TextUtils.isEmpty(sipDomain)) {
            LoggerWrapper.e(TAG, "Skipping SIP initialization because SIP credentials are incomplete");
            return;
        }

        if (SipAccountManager.getInstance().sipRegistrationSubject.hasObservers()) {
            sipCompositeDisposable.dispose();
        }

        if (!SipAccountManager.getInstance().sipRegistrationSubject.hasObservers()) {
            sipCompositeDisposable.add((Disposable) SipAccountManager.getInstance().sipRegistrationSubject.subscribe(state -> onSipRegistrationState(state), throwable -> onSipError(throwable)));
            SipAccountManager.getInstance().initLinphoneService(this);
        } else {
            LoggerWrapper.e("XXX", "sipRegistrationSubject already has observers");
        }
        if (!SipAccountManager.getInstance().sipCallSubject.hasObservers()) {
            sipCompositeDisposable.add((Disposable) SipAccountManager.getInstance().sipCallSubject.subscribe(state -> onSipCallStateChanged(state), throwable -> onSipError(throwable)));
        } else {
            LoggerWrapper.e("XXX", "sipCallSubject already has observers");
        }



       /* SipAccountManager.getInstance().sipCallSubject.subscribe(sipCallState -> {
            if (sipCallState instanceof SipCallState.End || sipCallState instanceof SipCallState
            .Released) {
                mainPresenter.runWakeWord();
            } else if (sipCallState instanceof SipCallState.IncomingReceived || sipCallState
            instanceof SipCallState.OutgoingInit) {
                try {
                   // mainPresenter.disableWakeWord();
                } catch (Exception e ) {
                    LoggerWrapper.e("sipCallSubject > " +  sipCallState + " but disableWakeWord
                    failed" , e.getMessage());
                }
                LinphoneContext.instance().getLinphoneManager().getAudioManager()
                .routeAudioToSpeaker();
                LinphoneContext.instance().getLinphoneManager().getCallManager()
                .refreshInCallActions();
            }
        }, throwable -> {
            LoggerWrapper.e("sipCallSubject receieved error", throwable.getMessage());
            Toast.makeText(this, "Sip error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onSipCallStateChanged(SipCallState sipCallState) {
        LoggerWrapper.i("XXX", "Received sip state: " + sipCallState);
        if (sipCallState instanceof SipCallState.IncomingReceived) {
            String address = ((SipCallState.IncomingReceived) sipCallState).getAddress();
            CallLogStorage.logIncoming(address);
            String number = normalizeSipAddressNumber(address);
            showSipCallScreen(new SipCallData(address, number, number, null));
        } else if (sipCallState instanceof SipCallState.OutgoingInit) {
            String address = ((SipCallState.OutgoingInit) sipCallState).getAddress();
            CallLogStorage.logOutgoing(address);
            showSipCallScreen(buildSipCallData(address));
        } else if (sipCallState instanceof SipCallState.OutgoingProgress
                || sipCallState instanceof SipCallState.OutgoingRinging) {
            showSipCallScreen(buildSipCallData(null));
        } else if (sipCallState instanceof SipCallState.End
                || sipCallState instanceof SipCallState.Released
                || sipCallState instanceof SipCallState.Error) {
            isSipCallScreenVisibleOrOpening = false;
            SipAccountManager.getInstance().clearOutgoingSipCallData();
            mainPresenter.runWakeWord();
        } else if (sipCallState instanceof SipCallState.Running || sipCallState instanceof SipCallState.Connected) {
            showSipCallScreen(buildSipCallData(null));
        } else {
            Log.e("XXX", "Unhandled sip state: " + sipCallState);
        }
    }

    private void showSipCallScreen(@Nullable SipCallData sipCallData) {
        if (sipCallData == null) {
            return;
        }
        if (getCurrentFragment() instanceof LiveFeedAudioFragment) {
            isSipCallScreenVisibleOrOpening = true;
            return;
        }
        if (isSipCallScreenVisibleOrOpening) {
            return;
        }
        isSipCallScreenVisibleOrOpening = true;
        mainPresenter.switchFragmentUsingAdd(LiveFeedAudioFragment.newInstance(sipCallData));
        mainPresenter.disableWakeWord();
    }

    @Nullable
    private SipCallData buildSipCallData(@Nullable String stateAddress) {
        SipCallData outgoingSipCallData = SipAccountManager.getInstance().getOutgoingSipCallData();
        String address = !TextUtils.isEmpty(stateAddress) ? stateAddress : null;

        if (TextUtils.isEmpty(address) && outgoingSipCallData != null) {
            address = outgoingSipCallData.getAddress();
        }

        if (TextUtils.isEmpty(address)) {
            address = getCurrentSipCallAddress();
        }

        String number = normalizeSipAddressNumber(address);
        if (TextUtils.isEmpty(address) && !TextUtils.isEmpty(number)) {
            address = number;
        }
        if (TextUtils.isEmpty(address)) {
            return null;
        }

        if (outgoingSipCallData != null) {
            String displayNumber = TextUtils.isEmpty(outgoingSipCallData.getDisplayNumber())
                    ? number : outgoingSipCallData.getDisplayNumber();
            String displayName = TextUtils.isEmpty(outgoingSipCallData.getDisplayName())
                    ? displayNumber : outgoingSipCallData.getDisplayName();
            return new SipCallData(
                    address,
                    displayName,
                    displayNumber,
                    outgoingSipCallData.getImageUrl());
        }

        return new SipCallData(address, number, number, null);
    }

    @Nullable
    private String getCurrentSipCallAddress() {
        try {
            org.linphone.core.Call currentCall = LinphoneContext.instance()
                    .getLinphoneManager()
                    .getCore()
                    .getCurrentCall();
            if (currentCall != null && currentCall.getRemoteAddress() != null) {
                return currentCall.getRemoteAddress().asStringUriOnly();
            }
        } catch (Exception exception) {
            LoggerWrapper.e("XXX", "Unable to resolve current SIP call address: " + exception.getMessage());
        }
        return null;
    }

    private String normalizeSipAddressNumber(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String value = address.trim();
        if (value.startsWith("sip:")) {
            value = value.substring(4);
        }
        int atIndex = value.indexOf('@');
        if (atIndex >= 0) {
            value = value.substring(0, atIndex);
        }
        int paramIndex = value.indexOf(';');
        if (paramIndex >= 0) {
            value = value.substring(0, paramIndex);
        }
        return value.trim();
    }

    public void disableWakeWord() {
        mainPresenter.disableWakeWord();
    }

    public void runWakeWord() {
        mainPresenter.runWakeWord();
    }

    private void onSipRegistrationState(SipRegistrationState sipRegistrationState) {
        if (sipRegistrationState instanceof SipRegistrationState.RegistrationSuccess) {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, true);
            Toast.makeText(this, "Hello! this device is enabled for calls now!",
             Toast.LENGTH_LONG).show();
        } else if (sipRegistrationState instanceof SipRegistrationState.RegistrationFailed) {
            Toast.makeText(this, "Credentials seems to be invalid. Please try again.",
             Toast.LENGTH_LONG).show();
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false);
            //TODO in future if we want to show dialog for SIP to set the credentials
//            showGetSipDetailsDialog();
        } else if (sipRegistrationState instanceof SipRegistrationState.RegistrationInProgress) {
            //do-nothing
        } else if (sipRegistrationState instanceof SipRegistrationState.RegistrationInCleared) {
            Toast.makeText(this, "This device will be disabled for calls now. Please contact " +
             "admin.", Toast.LENGTH_LONG).show();
        } else if (sipRegistrationState instanceof SipRegistrationState.RegistrationInNone) {
            //do-nothing
        }
    }

    private void onSipError(Throwable throwable) {
        Toast.makeText(this, "This device is disabled for calls.", Toast.LENGTH_LONG).show();
    }

    private void showLoading() {
        dialog = new Dialog(this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);

        versioncodeText = dialog.findViewById(R.id.versioncode_text);
        if (BuildConfig.DEBUG) {
            versioncodeText.setVisibility(VISIBLE);
            versioncodeText.setText(getVersionNameAndVersionCode());
        }

        settingUpLayout = dialog.findViewById(R.id.setting_up_screen);
        errorLayout = dialog.findViewById(R.id.error_layout);
        errorMessage = dialog.findViewById(R.id.error_message);
        errorAction = dialog.findViewById(R.id.error_action);
        dialogBackground = dialog.findViewById(R.id.dialog_background);
        dialogBackground.setClickable(false);
        dialogBackground.setEnabled(false);

        settingUpLayout.setVisibility(VISIBLE);
        errorLayout.setVisibility(GONE);
        setActivityFullScreen(MainActivity.this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.gravity = Gravity.TOP;
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            setActivityFullScreen(window);
        }

        dialog.setCancelable(false);
        //FIXME android.view.WindowLeaked: Activity com.sb.woohoo.ui.main.MainActivity has leaked
        // window com.android.internal.policy.impl.PhoneWindow$DecorView{2b8f1b3c V.ED.... R.....
        // .. 0,0-800,1280} that was originally added here
        //at android.view.ViewRootImpl.<init>(ViewRootImpl.java:398)
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialogBackground.setOnClickListener(view -> {
            switch ((int) view.getTag()) {
                case SERVER_ERROR:
                    dialogBackground.setClickable(false);
                    dialogBackground.setEnabled(false);
                    settingUpLayout.setVisibility(VISIBLE);
                    errorLayout.setVisibility(GONE);
                    if (connectionRetry > 2) {
                        mainPresenter.switchFragmentOnError(WifiFragment.newInstance(WifiFragment.ERROR_LOADING));
                        changeHintBarState(HINT_RESET, HINT_NO_CHANGE, GONE);
                        dialog.dismiss();
                    } else {
                        connectServer();
                    }
                    break;
                case NO_INTERNET:
                case WIFI_NOT_CONNECTED:
                    mainPresenter.switchFragmentOnError(WifiFragment.newInstance(WifiFragment.ERROR_LOADING));
                    changeHintBarState(HINT_RESET, HINT_NO_CHANGE, GONE);
                    dialog.dismiss();
                    break;
                case UNKNOWN_ERROR:
                    break;
            }

        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment currentFragment =
         FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
        if (currentFragment instanceof VideoFragment
                || currentFragment instanceof MusicFragment
                || currentFragment instanceof RelaxationMusicFragment) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        } else {
            setVolumeControlStream(STREAM_SYSTEM);
        }

        if (event.getRepeatCount() == 0 && event.getKeyCode() != 282) {
            mainPresenter.volumeUpPressed();
        }
        if (event.getKeyCode() == 282) {
            mainPresenter.runWakeWord();
        }

        // If the device is in ScreenSaverFragment we need to hide the wakeWordHint bar.
        // other than ScreenSaverFragment we need to show wakeWordHint bar.
       /* if (event.getKeyCode() == KeyEvent.KEYCODE_MUTE)
        {
            mainPresenter.closeDrawer();
            onMutePressedAction();
            if (currentFragment instanceof ScreenSaverFragment) {
                setMute(GONE);
            }else {
                setMute(VISIBLE);
            }
        }*/
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGpsSupported()) {
            mGPSLocation.cancelLocationUpdate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sipCompositeDisposable.dispose();
        Log.i("SocketHelper", "setupSuccessful:" + setupSuccessful);
        if (setupSuccessful) {
            mainPresenter.onDestroy(MainActivity.this);
        }
        disposable.dispose();
        if (secondDisplay != null) {
            secondDisplayDataListener = null;
            secondDisplay.dismiss();
            secondDisplay = null;
        }
//        LinphoneContext.instance().getLinphoneManager().destroy();
        try {
            //TODO Disable Night Mode AS It was Crashing , need to resolve in future
            if (ENABLE_NIGHT_MODE) {
                if (darkModeDisableBroadcastReceiver != null) {
                    unregisterReceiver(darkModeDisableBroadcastReceiver);
                }
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
        mTaxList.clear();
        mTaxList = null;

        // Stop the periodic updates when the activity is destroyed
        handler.removeCallbacks(updateSipStatusRunnable);
    }

    @Override
    public void onBackPressed() {
        setActivityFullScreen(MainActivity.this);
        if (isTransparentActivityEnabled) {
            isTransparentActivityEnabled = false;
        } else {
            stopTextToSpeech(false);
        }
        super.onBackPressed();
    }


    /****************************************** LIVEFEED METHODS
     *  ******************************************************/

    public void startLiveFeedToOwnHub(Hub hub) {
        if (hub.getConnected() != null && hub.getConnected()) {
            binding.wakeWordAnimationBackground.setVisibility(GONE);
            mainPresenter.closeDrawer();
            mainPresenter.disableOtherSkills();
            Bundle bundle = new Bundle();
            bundle.putParcelable(HUB_DETAILS, hub);
            bundle.putBoolean(Constants.CONTACT_LIVE_FEED, false);
            mainPresenter.switchFragmentUsingAdd(LiveFeedToHubFragment.newInstance(bundle));
        } else {
            MethodUtils.showToastMessage(getApplicationContext(), "Hub is offline");
        }
    }

    public void onEndLiveFeed() {
        mainPresenter.endLiveFeed();
    }

    @Override
    public void setVolumeControlStream() {
        setVolumeControlStream(STREAM_SYSTEM);
    }

    @Override
    public void changeView(boolean state) {
        if (state) {
            if (binding.wakeWordAnimationBackground != null) {
                binding.wakeWordAnimationBackground.setVisibility(GONE);
            }
        } else {
            Fragment currentFragment =
             FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
            while (!(currentFragment instanceof HomeFragment)) {
                onBackPressed();
                currentFragment =
                FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
            }
        }
    }

    /****************************************** {@link BaseActivity} Methods
     *  ******************************************/

    @Override
    public void onLocationChanged(double lat, double lon) {
        super.onLocationChanged(lat, lat);
        latLng[0] = lat;
        latLng[1] = lon;
        LoggerWrapper.i(MainActivity.class.getSimpleName(), "LatitudeonL : " + latLng[0]);
        LoggerWrapper.i(MainActivity.class.getSimpleName(), "LatitudeonL : " + latLng[1]);
    }

    @Override
    public void onIpAdressFound(String wifiIP) {
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_IP, wifiIP);
        mainPresenter.callForLatLngbyIP();
        LoggerWrapper.i(MainActivity.class.getSimpleName(), "wifi IP : " + wifiIP);
    }

    @Override
    public void onRespondToUser(String message) {
        super.onRespondToUser(message);
        mainPresenter.respondToUser(message);
    }

    @Override
    public void showSpeechResponse(String message) {
        runOnUiThread(() -> {
            if (message != null && !message.trim().isEmpty()) {
                binding.wakewordHint.setVisibility(VISIBLE);
                binding.wakewordHint.setText(message);
            }
        });
    }

    /********************************************* {@code public} Methods
     *  *********************************************/

    public double[] getLocation() {
        return latLng;
    } // Used by Weather Fragment

    public void resumePlayingVideo() {
        mainPresenter.resumePlayingVideo();
    } // Used by AlarmAlert and ReminderAlert Fragment

    public void stopTextToSpeech(boolean callbackNeeded) {
        mainPresenter.stopTextToSpeech(callbackNeeded);
    } // Used by NewsHeadline and QnA Fragment

    public void readNewsHeadline(String newsHeadline, boolean isDetailedNews) {
        mainPresenter.readNewsHeadline(newsHeadline, isDetailedNews);
    } // Used by NewsHeadline Fragment

    public void askFollowUpQuery(String query) {
        mainPresenter.askFollowUpQuery(query);
    } // Used by DeviceDiscovery Fragment

    public void openOnBoardingActivity() {
        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    } // Used by Settings Fragment

    public void closeSlidingTray() {
        mainPresenter.closeDrawer();
    } // Used by HomeFragment, Settings Fragment

    public void seekMusic(int position) {
        mainPresenter.seekMusic(position);
    } // Used by MusicFragment.

    public void stopPlayingMusic() {
        mainPresenter.stopPlayingMusic();
    } // Used by MusicFragment.

    public void setMusicRepeat(boolean repeat) {
        mainPresenter.setMusicRepeat(repeat);
    }

    public void pauseMusicPlaying() {
        mainPresenter.pausePlayingMusic();
    } // Used by MusicFragment.

    public void resumeMusicPlaying() {
        mainPresenter.resumePlayingMusic();
    } // Used by MusicFragment.

    public void nextSong() {
        mainPresenter.nextSong();
    } // Used by MusicFragment.

    public void previousSong() {
        mainPresenter.previousSong();
    } // Used by MusicFragment.

    public void terminateMusicPlayer() {
        mainPresenter.terminateMusicPlayer();
    } // Used by MusicFragment.

    public void playMusic(String spotifyURI) {
        mainPresenter.playMusic(spotifyURI);
    } // Used by MusicFragment.

    public void setSpotifyPlayer(String token) {
        mainPresenter.setSpotifyPlayer(MainActivity.this, token);
    } // Used by MusicFragment.

    public void rotateCamera(boolean rotate, String hubId, String receverUserId, int angle,
 int isRotating) {
        mainPresenter.rotateCamera(rotate, hubId, receverUserId, angle, isRotating);
    }

    public void emitEventToServer(String socketEvent, JsonObject jsonObject) {
        mainPresenter.emitEventToServer(socketEvent, jsonObject);
    }

    public void setMute(int aSetVisibility) {
        LoggerWrapper.d("setMute",
         String.valueOf(WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)));
        if (WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
            binding.content.muteImage.setImageResource(R.drawable.ic_drawable_unmute);
            binding.content.muteText.setText(getString(R.string.unmute));
            /*muteImage.setColorFilter(ContextCompat.getColor(
                    getApplicationContext(), R.color.red_brick), android.graphics.PorterDuff.Mode
                    .SRC_IN);*/
            binding.content.muteText.setTextColor(getResources().getColor(R.color.red_brick));
            mainPresenter.disableWakeWord();
        } else {
            binding.content.muteImage.setImageResource(R.drawable.ic_mute);
           /* muteImage.setColorFilter(ContextCompat.getColor(
                    getApplicationContext(), R.color.black), android.graphics.PorterDuff.Mode
                    .SRC_IN);*/
            binding.content.muteText.setText(getString(R.string.mute));
            binding.content.muteText.setTextColor(getResources().getColor(R.color.black));
            enableSkillsAfterLogOutError();
        }
        changeHintBarState(HINT_RESET, HINT_RESET, aSetVisibility);
    }

    public void startSurveillanceMode(boolean enable) {
        changeView(enable);
        mainPresenter.enableSurveillanceMode(enable);
        WooHooApp.getSharedPrefsHelper().setHubMode(PREF_KEY_SURVEILLANCE_MODE, enable);
    }

    @Override
    protected void onResume() {
        setActivityFullScreen(MainActivity.this);
        super.onResume();
    }

    public void onLightStateEmitSocket(JsonObject jsonObject) {
        mainPresenter.onLightStateEmitSocket(jsonObject);
    } // Used by PhilipsHuePresenter

    public int getMusicCurrentPosition() {
        return mainPresenter.getMusicCurrentPosition();
    } // Used by MusicFragment.

    public boolean isFragmentStacked() {
        return mainPresenter.isFragmentStacked();
    } // Used by QnA Fragment

    public boolean isMusicPlayerInitialized() {
        return mainPresenter.isMusicPlayerInitialized();
    } // Used by MusicFragment.

    public boolean isMusicPlaying() {
        return mainPresenter.isMusicPlaying();
    } // Used by MusicFragment.

    public boolean isSpotifyUserLoggedIn() {
        return mainPresenter.isSpotifyUserLoggedIn();
    } // Used by MusicFragment.

    public boolean isMusicPlayerTerminated() {
        return mainPresenter.isMusicPlayerTerminated();
    } // Used by MusicFragment.

    public boolean isMusicRepeating() {
        return mainPresenter.isMusicRepeating();
    } // Used by MusicFragment.

    public void switchFragmentUsingAdd(FeatureFragment featureFragmentLeft,
     FeatureFragment featureFragmentRight) {
        mainPresenter.switchFragmentUsingAdd(featureFragmentLeft, featureFragmentRight);
    }

    public void switchFragmentUsingAdd(FeatureFragment featureFragment) {
        mainPresenter.switchFragmentUsingAdd(featureFragment);
    } // Used by every other Fragment

    public void switchFragmentUsingReplace(FeatureFragment featureFragment) {
        mainPresenter.switchFragmentUsingReplace(featureFragment);
    } // Used by every other Fragment

    public Metadata getMusicMetaData() {
        return mainPresenter.getMusicMetaData();
    } // Used by MusicFragment.

    public void onTimeZoneChanged() {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
            if (homeFragment instanceof HomeFragment) {
                ((HomeFragment) homeFragment).setDate();
            }
        } else {
            Fragment leftFragment =
             getSupportFragmentManager().findFragmentByTag(HomeLeftPaneFragment.class.getSimpleName());
            if (leftFragment instanceof HomeLeftPaneFragment) {
                ((HomeLeftPaneFragment) leftFragment).setDate();
            }
        }
    }

    /**
     * @return
     *****************************************************************************************************************/

    public Fragment getCurrentFragment() {

        return FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
    }

    @Override
    public void switchFragmentUsingAdd(String fragmentName) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentSwitch(fragmentName);
            }
        });


    }

    @Override
    public void setHintBarClickListener(View.OnClickListener hintBarClickListener) {
        binding.wakewordHint.setOnClickListener(hintBarClickListener);
    }

    @Override
    public void resetHintBarClickListener() {
        binding.wakewordHint.setOnClickListener(null);
    }

    @Override
    public void changeHintBarState(int hintText, int backgroundColour, int visibility) {
        if (hintText != HINT_NO_CHANGE) {
            if (hintText == IHintBarState.Text.HINT_EMPTY) {
                hintText = R.string.empty;
            }
            if (hintText == HINT_RESET) {
                hintText = R.string.activity_main_bottom_text;
            }
            if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "")
                    .equalsIgnoreCase(Constants.GBTB_DOMAIN_ID) ||
                    (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID,
                            "").equalsIgnoreCase(Constants.RC_DOMAIN_ID))) {
                binding.wakewordHint.setText("");
            } else {
                if (WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
                    binding.wakewordHint.setText(R.string.mute_text);
                    mainPresenter.showMuteBar();
                } else {
                    mainPresenter.hideMuteBar();
                    binding.wakewordHint.setText(getString(hintText));
                }
            }
        }

        if (backgroundColour != HINT_NO_CHANGE) {
            if (backgroundColour == IHintBarState.Background.HINT_TRANSPARENT) {
                backgroundColour = android.R.color.transparent;
            }
            if (backgroundColour == HINT_RESET) {
                backgroundColour = R.color.bottom_hint_background;
            }
            binding.wakewordHint.setBackgroundResource(backgroundColour);
        }

        isHintVisibility = visibility != GONE;
        binding.wakewordHint.setVisibility(visibility);
    } // Used by FeatureFragment

    /********************************************* {@code private} Methods
     *  ********************************************/

    private void initializeViews() {
        setActivityFullScreen(MainActivity.this);
        requestModifySettingsPermission();
    }


    private void injectActivity() {
        MainComponent mainComponent =
         DaggerMainComponent.builder().wooHooAppComponent(((WooHooApp) getApplication()).getWooHooAppComponent()).mainModule(new MainModule(this)).build();
        mainComponent.injectMainActivity(this);
    }

    private void animationComplete() {
        binding.wakeWordAnimationBackground.setVisibility(GONE);
        if (isHintVisibility) {
            binding.wakewordHint.setVisibility(VISIBLE);
        }
    }

    /************************************************ InputInterceptor Methods
     *  ***********************************************/

    public void onPushAutoClose() {
        mainPresenter.snoozePop();
    } // Used by InputInterceptorRelativeLayout

    /************************************************** Error methods
     *  *****************************************************/

    @Override
    public void onError(@StringRes int msgId) {
        //its on UI thread because the error message sometimes comes from camera which is on
        // background thread.
        runOnUiThread(() -> {
            MethodUtils.showToastMessage(getApplicationContext(), getString(msgId));
        });
    } // Used by MainPresenter

    @Override
    public void onServerError(String msg) {
        showErrorMessage(SERVER_ERROR);
    }

    /******************************************** Listeners
     *  *******************************************/

    private void setTopDrawerVisibility(){
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equals(Constants.KHALIDIA_PALACE_DOMAIN_ID)){
            binding.content.llBlutooth.setVisibility(View.INVISIBLE);
        }else{
            binding.content.llBlutooth.setVisibility(VISIBLE);
        }
    }
    private void setClickEvents() {
        binding.errorLayout.viewNetworkIssue.setOnClickListener(view -> onNetworkIssueLayoutClicked());
        binding.wakewordHint.setOnClickListener(view -> onWakeWordPressed());
        binding.content.llHome.setOnClickListener(view -> onHomePressed());
        binding.content.mute.setOnClickListener(view -> onMutePressed());
        binding.clear.setOnClickListener(view -> onClearPressed());
        binding.content.llSurveillance.setOnClickListener(view -> onSurveillancePressed());
        binding.content.llDropIn.setOnClickListener(view -> onLiveFeedPressed());
        binding.content.llCall.setOnClickListener(view -> onCallPressed());
        binding.content.llSetting.setOnClickListener(view -> onSettingsPressed());
        binding.contentHome.setOnClickListener(view -> onOtherContentClick());
        binding.screenOverlay.setOnClickListener(view -> onTouch());
        //binding.content.llBlutooth.setOnClickListener(view -> ll_Blutooth());
        binding.content.llBlutooth.setOnClickListener(view -> goToBluetooth());
        binding.errorLayout.muteViewHolder.setOnClickListener(view -> onMuteIssueLayoutClicked());
        binding.content.volSetting.setOnClickListener(view -> onVolumeSettingClick());
    }

    private void onVolumeSettingClick() {
        if (binding.content.topDrawerItems.getVisibility() == VISIBLE) {
            mainPresenter.hideTopDrawerItem();
        } else {
            mainPresenter.showAllSoundBar();
        }
    }

    void onNetworkIssueLayoutClicked() {
        switchFragmentUsingAdd(WifiFragment.newInstance(WifiFragment.MAINACTIVITY));
    }

    void onMuteIssueLayoutClicked() {
        //If the screen is in ScreensaverFrag and user um-mute's need to unlock the screen and
        // un-mute
        Fragment lFragment =
         getSupportFragmentManager().findFragmentByTag(ScreenSaverFragment.class.getSimpleName());
        if (lFragment instanceof ScreenSaverFragment) {
            getSupportFragmentManager().popBackStack();
        }
        onMutePressed();
    }

    void onWakeWordPressed() {
        if ((WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) || WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.GBTB_DOMAIN_ID)) {
            return;
        }
        if (mainPresenter != null) {
            mainPresenter.stopWakeWordDetection();
            mainPresenter.onWakeWordDetected();
        } else {
            LoggerWrapper.e(MainActivity.class.getSimpleName(), "onWakeWordPressed failed " +
             "mainPresenter is null");
        }
    }

    public void onWakeWordChanged() {
        File file = AssetUtils.getModelExists(MainActivity.this);
        if (file != null) {
            mainPresenter.stopWakeWord();
            mainPresenter.runWakeWord();
            mainPresenter.onWakeWordChanged();
        } else {
            Intent intent = new Intent(MainActivity.this, EnrollmentActivity.class);
            intent.setAction("settings");
            startActivity(intent);
            finish();
        }
    }

    public void onHomePressed() {
        mainPresenter.closeDrawer();
        mainPresenter.onHomePressed();
    }

    void onMutePressed() {
        mainPresenter.closeDrawer();
        onMutePressedAction();
        setMute(VISIBLE);
        //To update quick link icon in homeFragment
        Fragment lFragment =
         getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
        if (lFragment instanceof HomeFragment) {
            ((HomeFragment) lFragment).setMuteForQuickLink();
        }
    }

    public void onMutePressedAction() {
        LoggerWrapper.d("setMute-onMutePressed",
         String.valueOf(WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)));
        if (WooHooApp.getSharedPrefsHelper().getMuteMode(PREF_MUTE_STATE)) {
            WooHooApp.getSharedPrefsHelper().setMuteMode(PREF_MUTE_STATE, false);
            mainPresenter.enableSkillsForLogOut();
        } else {
            WooHooApp.getSharedPrefsHelper().setMuteMode(PREF_MUTE_STATE, true);
            mainPresenter.disableWakeWord();
        }
    }

    void onClearPressed() {
        // mainPresenter.closeDrawer();
        LoggerWrapper.d("onClear", "onClear");
        mainPresenter.stopWakeWord();
        mainPresenter.runWakeWord();
    }

    void onSurveillancePressed() {
        mainPresenter.closeDrawer();
        darkModeOptions();
    }

    public void darkModeOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_Dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_dark_mode_option, null);

        builder.setCancelable(true);
        Button display = dialogLayout.findViewById(R.id.display);
        Button clock = dialogLayout.findViewById(R.id.clock);
        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        setActivityFullScreen(window);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        clock.setOnClickListener(view -> {
                    dialog.dismiss();
                    switchFragmentUsingAdd(DarkModeFragment.newInstance());
                }
        );
        display.setOnClickListener(view -> {
                    dialog.dismiss();
                    setDarkMode();
                }
        );
    }

    void setDarkMode() {
        if (hasSystemPermission(getApplicationContext())) {
            goToSleep(getApplicationContext());
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DARK_MODE, true);
            DarkModeFragment darkModeFragment = DarkModeFragment.newInstance();
            darkModeFragment.setArguments(bundle);
            switchFragmentUsingAdd(darkModeFragment);
        }
    }

    @Override
    public void setNightModeFromVoice(boolean enabled) {
        mainPresenter.closeDrawer();
        if (enabled) {
            setDarkMode();
        } else if (getCurrentFragment() instanceof DarkModeFragment) {
            onBackPressed();
        }
    }

    void ll_Blutooth() {
        bluetoothClickAction();
//        model.checkBluetoothStatus(bluetoothAdapter.isEnabled());
    }

    public void bluetoothClickAction() {
        mainPresenter.closeDrawer();
        boolean lPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lPermission = ActivityCompat.checkSelfPermission(getApplicationContext(),
             Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device Not Support Bluetooth", Toast.LENGTH_SHORT).show();
            updateBluetoothIconInHomePage(false);
        } else if (lPermission && bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth disabled!", Toast.LENGTH_SHORT).show();
            updateBluetoothIconInHomePage(false);
            bluetoothAdapter.disable();
            drawerItemDisEnable();
        } else if (lPermission && !bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, BLUETOOTH_CODE);
        }
    }

    @Override
    public void setBluetoothFromVoice(boolean enabled) {
        mainPresenter.closeDrawer();
        boolean lPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lPermission = ActivityCompat.checkSelfPermission(getApplicationContext(),
             Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device Not Support Bluetooth", Toast.LENGTH_SHORT).show();
            updateBluetoothIconInHomePage(false);
        } else if (!lPermission) {
            goToBluetooth();
        } else if (enabled && !bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, BLUETOOTH_CODE);
        } else if (!enabled && bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth disabled!", Toast.LENGTH_SHORT).show();
            updateBluetoothIconInHomePage(false);
            bluetoothAdapter.disable();
            drawerItemDisEnable();
        }
    }

    @Override
    public void openBluetoothFromVoice() {
        goToBluetooth();
    }

    public boolean goToBluetooth() {
        mainPresenter.closeDrawer();
        if (hasSystemPermission(getApplicationContext())) {
            switchFragmentUsingAdd(BluetoothSignedSettingsFragment.newInstance());
        } else {
            switchFragmentUsingAdd(BluetoothUnSignedSettingsFragment.newInstance());
        }
        return true;
    }

    //    if (mBluetoothAdapter == null) {
//        // Device does not support Bluetooth
//    } else if (!mBluetoothAdapter.isEnabled()) {
//        // Bluetooth is not enabled :)
//    } else {
//        // Bluetooth is enabled
//    }
    void onLiveFeedPressed() {
        mainPresenter.closeDrawer();
        mainPresenter.stopPlayingVideo();
        mainPresenter.switchFragmentUsingAdd(LiveFeedOwnDeviceFragment.newInstance());
    }

    void onCallPressed() {
        mainPresenter.closeDrawer();
        mainPresenter.stopPlayingVideo();
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
            mainPresenter.switchFragmentUsingAdd(DialerFragment.newInstance());
        } else {
            Toast.makeText(this, getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    public void onHomeCallClicked(View view) {
        onCallPressed();
    }

    public void onSettingsPressed() {
        mainPresenter.closeDrawer();
        mainPresenter.stopPlayingVideo();
        changeHintBarState(HINT_RESET, HINT_RESET, VISIBLE);
        if (!settingsMode) {
            Toast.makeText(MainActivity.this, getString(R.string.settings_not_enabled),
             Toast.LENGTH_SHORT).show();
        } else {
            mainPresenter.switchFragmentUsingAdd(SettingsFragment.newInstance());
        }
    }

    void onOtherContentClick() {
        mainPresenter.closeDrawer();
    }

    void onTouch() {
        mainPresenter.closeDrawer();
    }

    /*********************************** LOGOUT ***************************************************/

    public void disableSkillsBeforeLogOutInitiate() {
        mainPresenter.disableSkillsForLogOut();
    }

    public void enableSkillsAfterLogOutError() {
        mainPresenter.enableSkillsForLogOut();
    }

    /********************************* Loading Dialog
     *  **********************************************/

    private final Handler retryHandler = new Handler();
    private int retryCount = 0;


    private void showErrorMessage(int errorStatus) {
        settingUpLayout.setVisibility(GONE);
        errorLayout.setVisibility(VISIBLE);
        dialogBackground.setTag(errorStatus);
        dialogBackground.setClickable(true);
        dialogBackground.setEnabled(true);
        LoggerWrapper.i(TAG, "Sync error status " + errorStatus);

        if (retryCount < 5) {
            retryCount++;
            retryHandler.postDelayed(() -> {
                connectServer();
            }, 5000);
        } else {
            retryCount = 0;
        }

        switch (errorStatus) {
            case SERVER_ERROR:
                errorMessage.setText(String.format(getString(R.string.server_sync_error),
                 errorStatus));
                if (connectionRetry > 2) {
                    errorAction.setText(getString(R.string.tap_to_connect_wifi));
                    connectionRetry = 0;
                }
                connectionRetry++;
                break;
            case NO_INTERNET:
                errorMessage.setText(getString(R.string.no_internet_text));
                errorAction.setText(getString(R.string.tap_to_connect_wifi));
                break;
            case WIFI_NOT_CONNECTED:
                errorMessage.setText(getString(R.string.wifi_disabled));
                errorAction.setText(getString(R.string.tap_to_connect_wifi));
                break;
            case UNKNOWN_ERROR:
                errorMessage.setText(getString(R.string.unknown_error));
                break;
        }
    }

    public static Completable hasInternetConnection() {
        return Completable.create(emitter -> {
            int maxRetries = 5;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    // Connect to Google DNS to check for connection
                    int timeoutMs = 1500;
                    Socket socket = new Socket();
                    InetSocketAddress socketAddress = new InetSocketAddress(GOOGLE_ADDRESS,
                     GOOGLE_PORT);

                    socket.connect(socketAddress, timeoutMs);
                    socket.close();

                    // Connection successful, complete the emitter
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }

                    return; // Exit the loop

                } catch (IOException e) {
                    Log.i("XXX", "Connection attempt failed. Retry count: " + (retryCount + 1));
                    e.printStackTrace();

                    retryCount++;

                    // Wait for some time before the next retry
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // All retries failed, emit an error
            if (!emitter.isDisposed()) {
                emitter.onError(new RuntimeException("Failed to establish a connection after " +
                 "multiple retries."));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    private void connectServer() {
        if (MethodUtils.isNetworkAvailable(this)) {
            disposable.add(hasInternetConnection().subscribe(() -> {
                        mainPresenter.getUserDetails();
                        mainPresenter.checkForHubMode();
                        mainPresenter.getScreenSaverTime();
                        retryHandler.removeCallbacksAndMessages(null);

                    },
                    error -> {
//                        showErrorMessage(NO_INTERNET);
                        Toast.makeText(this, "Internet not found on this device. Please check " +
                         "your network settings.", Toast.LENGTH_LONG).show();

                    }));
        } else {
            showErrorMessage(WIFI_NOT_CONNECTED);
        }
    }

    private void ensureMicPermissionAtStartup() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                && !mPrefsHelper.get(SharedPrefsKey.PREF_KEY_MIC_PERMISSION_ASKED, false)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
            mPrefsHelper.put(SharedPrefsKey.PREF_KEY_MIC_PERMISSION_ASKED, true);
        }
    }

    @Override
    public void onServerSetup() {
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, !domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID) ? 2000 : 5000);
        mainPresenter.onCreate(MainActivity.this);
        setupSuccessful = true;
    }

    @Override
    public void onNoInternet() {
        showErrorMessage(NO_INTERNET);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIC_PERMISSION_REQUEST_CODE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                Toast.makeText(this, getString(R.string.permission_needed), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void updateLatLng(LatLngByIPModel latLngByIPModel) {
        latLng[0] = latLngByIPModel.data.lat;
        latLng[1] = latLngByIPModel.data.lon;
        LoggerWrapper.i(MainActivity.class.getSimpleName(), "LatitudeuL : " + latLng[0]);
        LoggerWrapper.i(MainActivity.class.getSimpleName(), "LatitudeuL : " + latLng[1]);

        mPrefsHelper.put(SharedPrefsKey.PREF_KEY_COUNTRY_CODE, latLngByIPModel.data.countryCode);
    }

    @Override
    public String getLang() {
        return currentLang;
    }

    @Override
    public void setLeftPane(FeatureFragment fragment) {
        FragmentTransactionManager.setLeftPane(getSupportFragmentManager(), fragment);
    }

    @Override
    public void loadDataInSecondDisplay(SecondaryDisplayData data) {
        if (secondDisplayDataListener != null) {
            Log.i("SecondDisplay", "MainActivity::loadDataInSecondDisplay");
            secondDisplayDataListener.showOffers(data);
        }
    }

    @Override
    public void onSettingsMode(boolean settingsMode) {
        this.settingsMode = settingsMode;
    }

    @Override
    public void updateFeedbackList(ArrayList<Question> aQuestions) {
        MainActivity.this.runOnUiThread(() -> {
            switchFragmentUsingAdd(FeedbackFragment.newInstance(aQuestions));
        });
    }

    @Override
    public void sessionStarted(StartSession appstoreRes) {

        closeSlidingTray();
        WebViewFragment fragment = WebViewFragment.newInstance(appstoreRes);
        switchFragmentUsingAdd(fragment);

    }

    @Override
    public void sessionEndSucess(EndSession appstoreRes) {

        Fragment currentFragment =
         FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
        while (!(currentFragment instanceof HomeFragment)) {
            onBackPressed();
            currentFragment =
             FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
        }
    }

    @Override
    public void onAllLocaleFetch(ArrayList<LanguageData> data) {
        String selectedLang = LocaleHelper.getLanguage(this);
        for (int i=0; i<data.size(); i++){
            if(data.get(i).getLanguageCode().equals(selectedLang))
            {
                WooHooApp.getSharedPrefsHelper().setIntroVideo(PREF_INTRO_VIDEO_KEY,
                 data.get(i).getIntroVideo());
                return;
            }
        }
    }

    //    Handler handler = new Handler();
//    Runnable runnable = null;
    CountDownTimer Ordertimer = null;
//    Boolean isCloseClicked = false;

    private void showSystemAlert(MainActivity context, RemoteAlertModel remoteAlertModel) {
        boolean isSurveillanceMode =
         WooHooApp.getSharedPrefsHelper().getHubMode(PREF_KEY_SURVEILLANCE_MODE);
        if (isSurveillanceMode) {
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                isCloseClicked = false;
                closeAllDialog();

                if ((TextUtils.isEmpty(remoteAlertModel.data.voiceOutput))) {
                    playDingSound();
                } else if (!TextUtils.isEmpty(remoteAlertModel.data.voiceOutput) && !remoteAlertModel.data.voiceOutput.equalsIgnoreCase("null")) {
                    onRespondToUser(remoteAlertModel.data.voiceOutput);
                } else {
                    playDefaultNotificationSound();
                }

                if (remoteAlertModel.data.displayType.equalsIgnoreCase(ALERT_DISPLAY_TYPE)) {
                    dialogScreen = new Dialog(MainActivity.this, R.style.DialogTheme);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        MethodUtils.setDialogMargins(dialogScreen);
                    }

                    dialogScreen.setContentView(R.layout.fullscreen_image);
                    ImageView image = dialogScreen.findViewById(R.id.image);
                    TextView title = dialogScreen.findViewById(R.id.title);
                    TextView timer = dialogScreen.findViewById(R.id.timer);
                    TextView displaytext = dialogScreen.findViewById(R.id.displaytext);
                    VideoView videoView = dialogScreen.findViewById(R.id.videoView);
                    ImageView close = dialogScreen.findViewById(R.id.close);
                    Button order = dialogScreen.findViewById(R.id.order);
                    TextView timerText = dialogScreen.findViewById(R.id.timerText);

                    dialogScreen.setCancelable(true);
                    if (dialogScreen != null && !dialogScreen.isShowing()) {
                        dialogScreen.show();
                    }

                    if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
                        setActivityFullScreen(MainActivity.this);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    dialogScreen.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            Window window = getWindow();
                            if (window != null) {
                                lp.copyFrom(window.getAttributes());
                                lp.gravity = Gravity.TOP;
                                //This makes the dialog take up the full width
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                window.setAttributes(lp);
                                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                setActivityFullScreen(window);

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            closeAllDialog();

                        }
                    });
                    if (remoteAlertModel.data.alertCategory.equalsIgnoreCase(ALERT_ORDER)) {
                        order.setVisibility(VISIBLE);
                    } else {
                        order.setVisibility(GONE);
                    }


                    if (remoteAlertModel.data.timerOnScreen.equalsIgnoreCase("true")) {
                        int interval = Integer.parseInt(remoteAlertModel.data.timerValue);
                        interval = interval * 60 * 1000;


                        final int[] time = {interval};
                        timerText.setVisibility(VISIBLE);
                        timer.setVisibility(VISIBLE);
                        Ordertimer = new CountDownTimer(time[0], 1000) {

                            public void onTick(long millisUntilFinished) {

                                long remainedSecs = millisUntilFinished / 1000;
                                timer.setText(checkDigit((int) (remainedSecs / 60)) + ":" + checkDigit((int) (remainedSecs % 60)));// manage it accordign to you

                            }

                            public void onFinish() {
                                timer.setText("00:00:00");
                                Toast.makeText(MainActivity.this, "Finish", Toast.LENGTH_SHORT).show();

                                cancel();
                                closeAllDialog();

                            }
                        }.start();


                        order.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    alertOrderNow = new CustomDialogBoxWithSubTitle(MainActivity.this
                                            , R.style.DialogTheme);
                                    if (!alertOrderNow.isShowing()) {
                                        alertOrderNow.show();
                                    }
                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    Window window = getWindow();
                                    if (window != null) {
                                        lp.copyFrom(window.getAttributes());
                                        lp.gravity = Gravity.TOP;
                                        //This makes the dialog take up the full width
                                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                        window.setAttributes(lp);
                                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                        setActivityFullScreen(window);
                                    }

                                    alertOrderNow.binding.title.setText(getString(R.string.order));
                                    alertOrderNow.binding.subTitle.setText(remoteAlertModel.data.itemName + "-" + remoteAlertModel.data.price);
                                    alertOrderNow.binding.editText.setHint(getString(R.string.enter_quantity));
                                    InputMethodManager imm =
                                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(alertOrderNow.binding.editText.getWindowToken(), 0);
                                    alertOrderNow.binding.editText.setText(DEFAULT_VALUE_FOR_ORDER);
                                    alertOrderNow.binding.editText.setRawInputType(Configuration.KEYBOARD_12KEY);
                                    alertOrderNow.binding.buttonPositive.setText(R.string.confirm);
                                    alertOrderNow.binding.buttonNegative.setText(R.string.cancelText);
                                    alertOrderNow.binding.buttonPositive.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                String quantity = alertOrderNow.binding.editText.getText().toString();
                                                if(quantity.isEmpty() || quantity.isBlank() || Integer.parseInt(quantity)<1){
                                                    alertOrderNow.binding.editText.setText(DEFAULT_VALUE_FOR_ORDER);
                                                    quantity = DEFAULT_VALUE_FOR_ORDER;
                                                }

                                                JSONObject body = new JSONObject();
                                                body.put("hubId",
                                                        mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID,
                                                                ""));
                                                body.put("deviceId",
                                                        mPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID,
                                                                ""));
                                                body.put("price", remoteAlertModel.data.price);
                                                body.put("itemName", remoteAlertModel.data.itemName);
                                                body.put("quantity", quantity);
                                                body.put("userId",
                                                        mPrefsHelper.get(SharedPrefsKey.PREF_KEY_USER_ID, ""));
                                                body.put("alertId", remoteAlertModel.data.alertId);
                                                LoggerWrapper.d("MainActivity", " Click on orderNow :" +
                                                        " alertId = " + remoteAlertModel.data.alertId);
                                                hideSoftKeyboard(MainActivity.this,
                                                        alertOrderNow.binding.editText);
                                                mainPresenter.placeOrder(body);
                                                closeAllDialog();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                    alertOrderNow.binding.buttonNegative.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (alertOrderNow != null && alertOrderNow.isShowing()) {
                                                alertOrderNow.dismiss();
                                            }
                                            hideSoftKeyboard(MainActivity.this,
                                                    alertOrderNow.binding.editText);
                                        }
                                    });
                                }
                        });

                    }

                    if (remoteAlertModel.data.mediaType.equalsIgnoreCase(ALERT_MEDIA_TYPE_IMAGE)) {
                        image.setVisibility(VISIBLE);
                        videoView.setVisibility(GONE);

                        Picasso.get().load(remoteAlertModel.data.mediaUrl).fit().into(image);

                        title.setText("" + remoteAlertModel.data.title);
                        displaytext.setText("" + remoteAlertModel.data.displayText);

                    } else if (remoteAlertModel.data.mediaType.equalsIgnoreCase(ALERT_MEDIA_TYPE_VIDEO)) {
                        image.setVisibility(GONE);
                        videoView.setVisibility(VISIBLE);

                        MediaController mediaController = new MediaController(MainActivity.this);
                        mediaController.setAnchorView(videoView);
                        Uri video = Uri.parse(remoteAlertModel.data.mediaUrl);
                        videoView.setMediaController(mediaController);
                        videoView.setVideoURI(video);
                        videoView.requestFocus();
                        videoView.start();

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setLooping(false);
                            }
                        });

                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Toast.makeText(getApplicationContext(),
                                 getString(R.string.video_over), Toast.LENGTH_SHORT).show();
                                dialogScreen.dismiss();
                            }
                        });

                        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                LoggerWrapper.d("API123", "What " + what + " extra " + extra);
                                return false;
                            }
                        });

                    } else if (remoteAlertModel.data.mediaType.equalsIgnoreCase("file")) {
                        image.setVisibility(GONE);
                        videoView.setVisibility(GONE);
                        Uri myUri = Uri.parse(remoteAlertModel.data.mediaUrl);

                    } else {

                        dialogScreen.dismiss();

                    }

                } else {

                    dialogAlert = new CustomDialogAlert(MainActivity.this, R.style.DialogTheme,
                     remoteAlertModel.data);

                    if (!dialogAlert.isShowing()) {
                        dialogAlert.show();
                    }
                    dialogAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            Window window = getWindow();
                            if (window != null) {
                                lp.copyFrom(window.getAttributes());
                                lp.gravity = Gravity.TOP;
                                //This makes the dialog take up the full width
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                window.setAttributes(lp);
                                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                setActivityFullScreen(window);

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                        }
                    });
                }
            }
        });

    }

    private void closeAllDialog() {

        if (Ordertimer != null) {
            Ordertimer.cancel();
        }
        if (alertOrderNow != null && alertOrderNow.isShowing()) {
            alertOrderNow.dismiss();
        }
        if (dialogAlert != null && dialogAlert.isShowing()) {
            dialogAlert.dismiss();
        }
        if (dialogScreen != null && dialogScreen.isShowing()) {
            dialogScreen.dismiss();
        }
    }

    private void playDingSound() {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        Uri mediaPath =
         Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ding_sound);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mediaPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playDefaultNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSoftKeyboard(Context mContext, EditText username) {
        closeKeyboard();
        closeKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (((Activity) mContext).getCurrentFocus() != null && ((Activity) mContext).getCurrentFocus() instanceof EditText) {
            InputMethodManager imm =
             (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
             (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private char[] readTextFile() {
        return new char[0];
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void systemAlert(RemoteAlertModel remoteAlertModel) {

        showSystemAlert(MainActivity.this, remoteAlertModel);
    }

    @Override
    public void systemUpdate(RemoteSystemUpdateModel remoteSystemUpdateModel) {

        LoggerWrapper.i(MainActivity.class.getSimpleName(), "****remote call receve sucessfully");
        mainPresenter.doCheckUpdate();
    }

    @Override
    public void updateAvail(UpdateDataModel updateDataModelTemp) {

        updateDataModel = updateDataModelTemp;
        if (updateDataModel != null) {
            String dir_path = Environment.getExternalStorageDirectory() + "/sb";
            String fileName = String.valueOf(updateDataModel.getVersionCode()) + ".apk";
            String path = dir_path + "/" + fileName;

            File file = new File(path);

            if (updateService != null && (!file.exists())) {
                LoggerWrapper.i(MainActivity.class.getSimpleName(),
                 "**** start downloading " + file.exists());
                updateService.startDownload(updateDataModel);
            } else {
                LoggerWrapper.i(MainActivity.class.getSimpleName(), "**** server is null " + path);
                installUpdate(path);

            }
        } else {
            LoggerWrapper.e(MainActivity.class.getSimpleName(), "**** UpdateDataModel is null");
        }
    }

    @Override
    public void installUpdate(String apkFilePath) {
        try {
            LoggerWrapper.i(MainActivity.class.getSimpleName(), "**** installaion start ");

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                new InstallTask(MainActivity.this, this, apkFilePath).execute();
            } else {

                File toInstall = new File(apkFilePath);
                Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +
                ".provider", toInstall);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(contentUri);
                    startActivity(install);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    startActivity(install);
                }
            }

        } catch (Exception e) {
            LoggerWrapper.e(MainActivity.class.getSimpleName(), e.getMessage());
        }

    }

    @Override
    public void updateHomeScreenFromServer() {

        Fragment homeFragment =
         getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
        if (homeFragment instanceof HomeFragment) {
            LoggerWrapper.d(TAG, "Socket Event - callHotelDetailsAPI()");
            ((HomeFragment) homeFragment).callHotelDetailsAPI();
        }
    }

    public void updateScreenSaverTime(int time) {
        mainPresenter.updateScreenSaverTime(time);
    }

    public void checkDateRangeForDarkMode(Date date) {
        Fragment currentFragment =
         FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());

        boolean featureMode =
         WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE, false);
        String hrStart, hrEnd;

        if (featureMode) {
            hrStart =
             WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_HR,
              STARTING_POINT_TO_ENABLE_DARK_MODE);
            hrEnd =
             WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_MIN,
              ENDING_POINT_TO_DISABLE_DARK_MODE);
        } else {
            hrStart =
             WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DARK_MODE_START_POINT,
              STARTING_POINT_TO_ENABLE_DARK_MODE);
            hrEnd =
             WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DARK_MODE_END_POINT,
              ENDING_POINT_TO_DISABLE_DARK_MODE);
        }

        if (darkModeTargetTime(date, hrStart)) {
            if (!(currentFragment instanceof DarkModeFragment)) {
                setDarkMode();
            }
        } else if (darkModeTargetTime(date, hrEnd) && currentFragment instanceof DarkModeFragment) {
            new WoohooExceptionHandler(this).restart();
            startSurveillanceMode(false);
            onBackPressed();
        }
    }

    private boolean darkModeTargetTime(Date date, String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int targetHour = Integer.parseInt(parts[0]);
                int targetMinute = Integer.parseInt(parts[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                return (hour == targetHour && minute == targetMinute);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void updateScreenSaverFromServerMulti(ScreenSaverMulti screenSaverMulti) {
        new Handler(this.getMainLooper()).post(() -> {
            if (screenSaverMulti != null) {
                Fragment screenSaverFragment =
                 getSupportFragmentManager().findFragmentByTag(ScreenSaverFragment.class.getSimpleName());
                if (screenSaverFragment instanceof ScreenSaverFragment) {
                    ((ScreenSaverFragment) screenSaverFragment).onScreenSaverUpdate(screenSaverMulti);
                } else {
                    Log.i("ScreenSaverMulti", "MainActivity::updateScreenSaverTime just update " +
                    "time");
                    updateScreenSaverTime(screenSaverMulti.getScreenSaverTime());
                }

                if (secondDisplayDataListener != null) {
                    Log.i("SecondDisplay", "MainActivity::loadDataInSecondDisplay");
                    secondDisplayDataListener.loadScreenSaversAll(screenSaverMulti);
                }
            }
        });
    }

    public void updateSecondDisplay(SecondaryDisplayImage data) {
        if (secondDisplay != null && secondDisplay.isShowing()) {
            secondDisplay.syncSecondDisplayWithActiveScreenSavers(data);
        }
    }

    public void changeAudioStateSecondDisplay(boolean audioState) {
        if (secondDisplay != null && secondDisplay.isShowing()) {
            secondDisplay.changeAudioState(audioState);
        }
    }

    public void updateHomeScreenContactsFromServer() {
        Fragment homeFragment =
         getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
        if (homeFragment instanceof HomeFragment) {
            ((HomeFragment) homeFragment).getHotelStaffMembers();
        }
    }


    public void setVoiceGenieLangChange(String lang) {

        mainPresenter.setLangChange(lang);
    }

    public void fragmentSwitch(String tittle) {
        if(getString(R.string.buy_sim).equalsIgnoreCase(tittle)){
            switchFragmentUsingAdd(BuySimFragment.newInstance());
        } else if(getString(R.string.tv_remote).equalsIgnoreCase(tittle)){
            switchFragmentUsingAdd(TVRemoteFragment.newInstance());
        } else if (getString(R.string.honeywell).equalsIgnoreCase(tittle) || getString(R.string.Interel).equalsIgnoreCase(tittle) || getString(R.string.control).equalsIgnoreCase(tittle) || getString(R.string.smart_room_control).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(InterelUtilityFragment.newInstance());
        } else if (getString(R.string.spotify).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(MusicFragment.newInstance(null));
        } else if (getString(R.string.relaxation_music).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(RelaxationMusicFragment.newInstance());
        } else if (getString(R.string.reservation_app_tile).equalsIgnoreCase(tittle) || getString(R.string.your_bookings).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(ReservationFragment.newInstance(null));
        } else if (getString(R.string.amadeus_services_hotsos).equalsIgnoreCase(tittle) || getString(R.string.amadeus_service_request).equalsIgnoreCase(tittle) || getString(R.string.amenities).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(AmadeusServicesFragment.newInstance());
        } else if (getString(R.string.contacts).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(ContactsFragment.newInstance(null));
        } else if (getString(R.string.devices).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(DeviceDiscoveryFragment.newInstance());
        } else if (getString(R.string.weather).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(WeatherFragment.newInstance(null));
        } else if (getString(R.string.youtube).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(VideoListFragment.newInstance(null));
        } else if (getString(R.string.fragment_news_text_view_title).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(NewsHeadlineFragment.newInstance(null));
        } else if (getString(R.string.alarms).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(AlarmFragment.newInstance(null));
        } else if (getString(R.string.call).equalsIgnoreCase(tittle)) {
            if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
                switchFragmentUsingAdd(DialerFragment.newInstance());
            } else {
                Toast.makeText(this, getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();
            }
        } else if (getString(R.string.fragment_reminder_text_view_title).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(ReminderFragment.newInstance(null));
        } else if (getString(R.string.Translation).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(TranslationFragment.newInstance(null));
        } else if (getString(R.string.drawer_menu_feedback).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(FeedbackFragment.newInstance());
        } else if (getString(R.string.drawer_menu_covid).equalsIgnoreCase(tittle)) {
            switchFragmentUsingAdd(CovidFragment.newInstance());
        } else if (getString(R.string.drawer_menu_ordering).equalsIgnoreCase(tittle)) {
            if (isWebFnb()) {
//                mainPresenter.switchFragmentUsingAdd(WebViewFragment.newInstance(WooHooApp
//                .getSharedPrefsHelper().get(SharedPrefsKey.PREF_INFRA_SYS_URL, ""),
//                WebViewFragment.WebviewLaunchMode.InfraSys));
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    mainPresenter.switchFragmentUsingAdd(FnBLeftPaneFragment.getInstance(FnBCategoriesFragment.class.getSimpleName()), FnBCategoriesFragment.newInstance());
                else mainPresenter.switchFragmentUsingReplace(FnBCategoriesFragment.newInstance());
            }
        } else if (getString(R.string.faq).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(FaqCategoriesFragment.newInstance());
        } else if (getString(R.string.in_room_dining).equalsIgnoreCase(tittle)) {
            if (isWebFnb()) {
//                mainPresenter.switchFragmentUsingAdd(WebViewFragment.newInstance(WooHooApp
//                .getSharedPrefsHelper().get(SharedPrefsKey.PREF_INFRA_SYS_URL, ""),
//                WebViewFragment.WebviewLaunchMode.InfraSys));
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    mainPresenter.switchFragmentUsingAdd(FnBLeftPaneFragment.getInstance(FnBCategoriesFragment.class.getSimpleName()), FnBCategoriesFragment.newInstance());
                else mainPresenter.switchFragmentUsingReplace(FnBCategoriesFragment.newInstance());
            }
        } else if (getString(R.string.overview).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (getString(R.string.ticket_purchase).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(QRCodeFragment.newInstance(getString(R.string.ticket_purchase_quote), getString(R.string.scan_qr_code_to_purchase_ticket), R.drawable.purchase_ticket_qr_new));
        } else if (getString(R.string.time_slot_booking).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(QRCodeFragment.newInstance(getString(R.string.time_slot_booking_quote), getString(R.string.scan_qr_code_to_book_slot), R.drawable.book_time_slot_qr_new));
        } else if (getString(R.string.information).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(OverviewCategoriesFragment.newInstance());
        } else if (getString(R.string.fragment_restaurant_title).equalsIgnoreCase(tittle)) {
            mainPresenter.switchFragmentUsingAdd(DiningRestaurantsFragment.newInstance());
        } else if (getString(R.string.h_care_spa).equalsIgnoreCase(tittle)) {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", "148");
            bundle.putBoolean("category_sub_type", true);
            helper.trackFnb(new FnbEvent.FnbCategoriesSelected(bundle));
            FnBSubCategoriesFragment fnBSubCategoriesFragment =
                    FnBSubCategoriesFragment.newInstance();
            fnBSubCategoriesFragment.setArguments(bundle);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FnBLeftPaneFragment fnBLeftPaneFragment =
                 FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
                switchFragmentUsingAdd(fnBLeftPaneFragment, fnBSubCategoriesFragment);
            } else {
                switchFragmentUsingReplace(fnBSubCategoriesFragment);
            }
        }
    }

    private boolean isWebFnb() {
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        return (Constants.INFRASYS_SUPPORTED_DOMAINS.contains(domainId) && !TextUtils.isEmpty(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_INFRA_SYS_URL, "")));
    }

    public void onNetworkConnected() {
        showLoading();
        connectServer();
    }

    public void initLiveFeedToContactHub() {
        mainPresenter.disableOtherSkills();
    }

    @Override
    public void startInstallation() {

    }

    public void orderPlaced() {
        closeAllDialog();
        Toast.makeText(MainActivity.this, getString(R.string.your_order_is_placed),
         Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInstall(boolean isSuccess) {

    }

    @Override
    public void onProgressUpdate(int value) {

    }

    @Override
    public void onCancelDownload() {

    }

    @Override
    public void onUpdateDownloadFailed() {
        LoggerWrapper.e("TAG", "**** downloaded fail");
        Toast.makeText(MainActivity.this, getString(R.string.download_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateDownloadSuccess(String fileLocation) {

        LoggerWrapper.d(MainActivity.class.getSimpleName(), "**** downloaded sucess ");

        mainPresenter.installApk(MainPresenter.APK_FOLDER + updateDataModel.getVersionCode() +
        ".apk");
    }

    public void calltoVoiceCommand(String toString) {
        mainPresenter.calltoVoiceCommand(toString);
    }

    public void stopWakeWordAnimationFromFragment() {
        mainPresenter.stopWakeWordDetection();
        mainPresenter.onWakeWordDetected();
    }

    @Override
    public void onSecondDisplayCreated(SecondDisplayDataListener secondDisplayDataListener) {
        this.secondDisplayDataListener = secondDisplayDataListener;
        mainPresenter.secondDisplayCreated();
    }

    public boolean isAvailableInStock(Product product) {
        if (product.getStock() > getSelectedProductCount(product)) return true;
        return false;
    }

    private int getSelectedProductCount(Product product) {
        int count = 0;
        for (Product p : getSelectedProductsList()) {
            if (p.getId() == product.getId()) count++;
        }
        return count;
    }

    public void callStatsDumpAPI(StatsDumpRequest statsDumpRequest) {
        mainPresenter.callStatsDumpAPI(statsDumpRequest);

    }

    public void addProductToCart(Product product) {
        StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
        statsDumpRequest.setCategoryName(category_name_fnb);
        statsDumpRequest.setEventName(event_name_add_to_cart);
        statsDumpRequest.setIntentIdentifier("" + product.getId());
        statsDumpRequest.setIntentIdentifierName(product.getName());
        callStatsDumpAPI(statsDumpRequest);

        boolean isFound = false;
        String productStr = new Gson().toJson(product.getModifiers());

        for (Product p : selectedProductsCart) {
            String pStr = new Gson().toJson(p.getModifiers());
            if (p.getId() == product.getId() && productStr.equalsIgnoreCase(pStr)) {
                p.setSelectedQuantity(p.getSelectedQuantity() + 1);
                addInTotalPrice(p);
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            product.setSelectedQuantity(1);
            selectedProductsCart.add(new Product(product));
            if (product.getAmount() == null) // First time add without selecting any modifier
                product.setAmount(product.getPrice());
            addInTotalPrice(product);
        }
    }

    public void removeProductToCart(Product product) {
        for (Product p : selectedProductsCart)
            if (product.getId() == p.getId()) {
                reduceInTotalPrice(p);
                if (p.getSelectedQuantity() > 1) p.setSelectedQuantity(p.getSelectedQuantity() - 1);
                else selectedProductsCart.remove(p);
                break;
            }
    }

    public void addProductToCartFromOrderFrag(Product product) {
        StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
        statsDumpRequest.setCategoryName(category_name_fnb);
        statsDumpRequest.setEventName(event_name_add_to_cart);
        statsDumpRequest.setIntentIdentifier("" + product.getId());
        statsDumpRequest.setIntentIdentifierName(product.getName());
        callStatsDumpAPI(statsDumpRequest);

        product.setSelectedQuantity(product.getSelectedQuantity() + 1);
        for (Product p : selectedProductsCart)
            if (product.getId() == p.getId()) {
                addInTotalPrice(p);
                break;
            }
    }

    public void removeProductToCartFromOrderFrag(Product product) {
        String productStr = new Gson().toJson(product.getModifiers());
        for (Product p : selectedProductsCart) {
            String pStr = new Gson().toJson(p.getModifiers());
            if (product.getId() == p.getId() && productStr.equalsIgnoreCase(pStr)) {
                reduceInTotalPrice(p);
                if (p.getSelectedQuantity() > 1) p.setSelectedQuantity(p.getSelectedQuantity() - 1);
                else selectedProductsCart.remove(p);
                break;
            }
        }
    }

    public boolean isProductAvailableInCart(Product product) {
        for (Product p : selectedProductsCart)
            if (product.getId() == p.getId()) return true;
        return false;
    }

    public ArrayList<TaxData> getTaxList() {
        return mTaxList;
    }

    public void setTaxData(TaxData taxData) {
        if (mTaxList == null) {
            mTaxList = new ArrayList<>();
        }
        if (mTaxList.contains(taxData)) {
            mTaxList.remove(taxData);
            LoggerWrapper.d("FNBOrderFrag", "remove Tax : " + taxData.getCategoryId());
        }
        //Add everytime bcz the tax values can be changed(updated) from server. so need to update
        // in list
        mTaxList.add(taxData);
    }

    public TaxData getTaxDataFromList(Product aProduct) {
        LoggerWrapper.d("FNBOrderFrag", "mTaxList size = : " + mTaxList.size());
        LoggerWrapper.d("FNBOrderFrag",
         "---aProduct getCategoryId  = : " + aProduct.getCategoryId());
        LoggerWrapper.d("FNBOrderFrag", "---aProduct getCategoryId  = : " + aProduct.getId());
        if (mTaxList != null && !mTaxList.isEmpty()) {
            for (int i = 0; i < mTaxList.size(); i++) {
                TaxData lData = mTaxList.get(i);
                LoggerWrapper.d("FNBOrderFrag",
                 "-----lData getCategoryId  = : " + lData.getCategoryId());
                if (lData.getCategoryId() == aProduct.getCategoryId()) {
                    LoggerWrapper.d("FNBOrderFrag", "Found Tax : " + lData);
                    return lData;
                }
            }
        }
        //Sending default data
        LoggerWrapper.d("FNBOrderFrag", "get getDefaultTax ");

        return TaxData.getDefaultTax();
    }

    @Override
    public void onDialogOpened() {
        mainPresenter.disableWakeWord();
    }

    @Override
    public void onDialogClosed() {
        mainPresenter.runWakeWord();
    }


    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }
    }

    public void callSessionEndApi(StartSession value) {
        mainPresenter.callSessionEnd(value);
    }


    @Override
    public void updateCheckIn(HotelCheckInResponse hotelCheckInResponse) {

        checkInDialog(hotelCheckInResponse);
    }

    public void setDefault() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                LocaleHelper.setLocale(MainActivity.this, DEFAULT_LANG);
                if (bluetoothAdapter != null) {
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    }
                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }, 60000);

    }

    @Override
    public void updateCheckOut(HotelCheckOutResponse hotelCheckOutResponse) {
        Fragment currentFragment =
         FragmentTransactionManager.getCurrentFragment(getSupportFragmentManager());
        if (currentFragment instanceof RelaxationMusicFragment) {
            ((RelaxationMusicFragment) currentFragment).stopPlaybackAndClearCache();
        } else {
            RelaxationMusicCache.clear(this);
        }
        setDefault();
        checkOutDialog(hotelCheckOutResponse);
    }

    @SuppressLint("StringFormatInvalid")
    public void checkInDialog(HotelCheckInResponse hotelCheckInResponse) {

        MainActivity.this.runOnUiThread(() -> {

            if (dialogCheckInOut != null && dialogCheckInOut.isShowing()) {
                dialogCheckInOut.dismiss();
            }

            dialogCheckInOut = new Dialog(MainActivity.this, R.style.DialogTheme);

            dialogCheckInOut.setContentView(R.layout.dialog_checkin);
            ImageView image = dialogCheckInOut.findViewById(R.id.image);
            TextView title = dialogCheckInOut.findViewById(R.id.title);
            TextView stay = dialogCheckInOut.findViewById(R.id.stay);
            TextView welcome = dialogCheckInOut.findViewById(R.id.welcome);
            ImageView close = dialogCheckInOut.findViewById(R.id.close);

            Picasso picasso = WooHooApp.get(MainActivity.this).getWooHooAppComponent().getPicasso();
            picasso.load(hotelCheckInResponse.getImage()).centerCrop().fit().into(image);
            title.setText(String.format(getString(R.string.hello_guestname),
hotelCheckInResponse.getGuestName()));
            welcome.setText(String.format(getString(R.string.welcome_to_hotelname),
             hotelCheckInResponse.getHotelName()));

            dialogCheckInOut.setCancelable(true);
            if (dialogCheckInOut != null && !dialogCheckInOut.isShowing()) {
                dialogCheckInOut.show();
            }
            setActivityFullScreen(MainActivity.this);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialogCheckInOut.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = getWindow();
                    if (window != null) {
                        lp.copyFrom(window.getAttributes());
                        lp.gravity = Gravity.TOP;
                        //This makes the dialog take up the full width
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        window.setAttributes(lp);
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                         WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        setActivityFullScreen(window);

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCheckInOut.dismiss();
                }
            });
        });
    }

    public void checkOutDialog(HotelCheckOutResponse hotelCheckOutResponse) {

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogCheckInOut != null && dialogCheckInOut.isShowing()) {
                    dialogCheckInOut.dismiss();
                }

                dialogCheckInOut = new Dialog(MainActivity.this, R.style.DialogTheme);
                dialogCheckInOut.setContentView(R.layout.dialog_checkout);
                ImageView image = dialogCheckInOut.findViewById(R.id.image);
                TextView title = dialogCheckInOut.findViewById(R.id.title);
                ImageView close = dialogCheckInOut.findViewById(R.id.close);

                Picasso picasso =
                 WooHooApp.get(MainActivity.this).getWooHooAppComponent().getPicasso();
                picasso.load(hotelCheckOutResponse.getImage()).centerCrop().fit().into(image);
                dialogCheckInOut.setCancelable(true);
                if (dialogCheckInOut != null && !dialogCheckInOut.isShowing()) {
                    dialogCheckInOut.show();
                }
                setActivityFullScreen(MainActivity.this);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialogCheckInOut.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        Window window = getWindow();
                        if (window != null) {
                            lp.copyFrom(window.getAttributes());
                            lp.gravity = Gravity.TOP;
                            //This makes the dialog take up the full width
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            window.setAttributes(lp);
                            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            setActivityFullScreen(window);

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        }
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCheckInOut.dismiss();
                    }
                });
            }
        });
    }

    /**********************************************************************************************************************/

    private void requestModifySettingsPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                if (settingsRequestAlertDialog != null && settingsRequestAlertDialog.isShowing()) {
                    settingsRequestAlertDialog.dismiss();
                    settingsRequestAlertDialog = null;
                }
                settingsRequestAlertDialog = new Dialog(this, R.style.DialogTheme);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    MethodUtils.setDialogMargins(settingsRequestAlertDialog);
                }
                settingsRequestAlertDialog.setContentView(R.layout.dialog_setting_access);

                Button cancel = settingsRequestAlertDialog.findViewById(R.id.button_negative);
                Button ok = settingsRequestAlertDialog.findViewById(R.id.button_positive);

                cancel.setOnClickListener(v -> this.finish());

                ok.setOnClickListener(v -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse(
                            "package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_MODIFY_SETTINGS);
                });

                settingsRequestAlertDialog.setCancelable(false);
                if (settingsRequestAlertDialog != null && !settingsRequestAlertDialog.isShowing()) {
                    settingsRequestAlertDialog.show();
                }

            } else {
                Settings.System.putInt(getContentResolver(),
                 Settings.System.SCREEN_BRIGHTNESS_MODE,
                  Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MODIFY_SETTINGS) {
            if (settingsRequestAlertDialog != null && Settings.System.canWrite(this)) {
                settingsRequestAlertDialog.dismiss();
                Settings.System.putInt(getContentResolver(),
                 Settings.System.SCREEN_BRIGHTNESS_MODE,
                  Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        }
        if (requestCode == BLUETOOTH_CODE) {
            Toast.makeText(this, "Long press on bluetooth option to see bluetooth devices!",
             Toast.LENGTH_LONG).show();
            if (resultCode == RESULT_OK) {
                updateBluetoothIconInHomePage(true);
                drawerItemEnable();
            } else if (resultCode == RESULT_CANCELED) {
                updateBluetoothIconInHomePage(false);
                drawerItemDisEnable();
            }
        }
    }

    public void updateBluetoothIconInHomePage(boolean status) {
        model.checkBluetoothStatus(status);
    }

    public ArrayList<Product> getSelectedProductsList() {
        return selectedProductsCart;
    }

    public void reduceInTotalPrice(Product product) {
        if (product.getAmount() != null) {
            totalPrice = totalPrice - Double.parseDouble(product.getAmount());
        } else {
            totalPrice = totalPrice - Double.parseDouble(product.getPrice());
        }
    }

    public void addInTotalPrice(Product product) {
        if (product.getAmount() != null) {
            totalPrice = totalPrice + Double.parseDouble(product.getAmount());
        } else { // Comes whena new item get added with no modifiers
            totalPrice = totalPrice + Double.parseDouble(product.getPrice());
        }
        updateCart();
    }

    private void updateCart() {
        Fragment lFragment = getSupportFragmentManager().
                findFragmentByTag(FnBSubCategoriesFragment.class.getSimpleName());
        if (lFragment instanceof FnBSubCategoriesFragment) {
            ((FnBSubCategoriesFragment) lFragment).updateCart();
        }

        lFragment = getSupportFragmentManager().
                findFragmentByTag(FnBCategoriesFragment.class.getSimpleName());
        if (lFragment instanceof FnBCategoriesFragment) {
            ((FnBCategoriesFragment) lFragment).updateCart();
        }
    }

    public void removeProductFromList(Product product) {
        selectedProductsCart.remove(product.getId());
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getTotalCartCount() {
        int totalCartCount = 0;
        for (int i = 0; i < getSelectedProductsList().size(); i++) {
            Product product = getSelectedProductsList().get(i);
            totalCartCount = totalCartCount + product.getSelectedQuantity();
        }
        return totalCartCount;
    }

    public void resetSelectedData() {
        selectedProductsCart.clear();
        totalPrice = 0;
    }

    public void updateProductInstructions(Product product) {
        /*if (selectedProductsCart.containsKey(product.getId())){
            selectedProductsCart.put(product.getId(),product);
        }*/
    }

    public String getProductInstructions(Product product) {
        /*if (selectedProductsCart.containsKey(product.getId())){
            return selectedProductsCart.get(product.getId()).getInstructions();
        }*/
        return "";
    }

    public void loadSecondDisplayIfExists() {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if (displays.length > 1) {
            showSecondaryDisplay(displays[1]);
        }
    }

    private void showSecondaryDisplay(Display display) {
        secondDisplay = new SecondDisplay(MainActivity.this, display, this);
        secondDisplay.show();
    }

    public void setHotelLogo(String hotelLogo) {
        this.hotelLogo = hotelLogo;
    }

    public String getHotelLogo() {
        return hotelLogo;
    }

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int REQUEST_CODE_DISABLE_DARKMODE = 10;
    private int REQUEST_CODE_ENABLE_DARKMODE = 11;

    private void createAlarmToDisableSurveillance() {
        //Register AlarmManager Broadcast receive.
        Calendar calendar = Calendar.getInstance();
        int hourofDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourofDay < 7) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
//        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0); // and alarm second
        long intendedTime = calendar.getTimeInMillis();

        registerMyAlarmBroadcast(REQUEST_CODE_DISABLE_DARKMODE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime,
         AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    BroadcastReceiver darkModeDisableBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int requestCode = 0;
            if (intent != null && intent.getExtras() != null) {
                requestCode = intent.getExtras().getInt("broadcast_id");
//                onRespondToUser("Your Alarm is there " + requestCode);
                boolean isSurveillanceMode =
                 WooHooApp.getSharedPrefsHelper().getHubMode(PREF_KEY_SURVEILLANCE_MODE);
                if (requestCode == REQUEST_CODE_DISABLE_DARKMODE && isSurveillanceMode) {
                    startSurveillanceMode(false);
                    onBackPressed();
                } else if (requestCode == REQUEST_CODE_ENABLE_DARKMODE && !isSurveillanceMode) {
                    //TODO Disable Night Mode AS It was Crashing , need to resolve in future
                    if (ENABLE_NIGHT_MODE) {
                        showCountdownPromptToEnableDarkMode();
                    }
                }
            }
        }
    };

    private void showCountdownPromptToEnableDarkMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FullScreenDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogLayout = inflater.inflate(R.layout.dialog_enable_darkmode, null);
        builder.setCancelable(true);
        builder.setView(dialogLayout);
        AlertDialog dialogNightMode = builder.create();
        Window window = dialogNightMode.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNightMode.show();

        TextView txtTitleCountdown = dialogLayout.findViewById(R.id.txt_title_countdown);
        TextView txtCancel = dialogLayout.findViewById(R.id.txt_cancel);
        final int[] count = {5};
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                count[0]--;
                txtTitleCountdown.setText("" + count[0]);
            }

            @Override
            public void onFinish() {
                mainPresenter.switchFragmentUsingAdd(DarkModeFragment.newInstance());
                dialogNightMode.dismiss();
            }
        };
        countDownTimer.start();

        txtCancel.setOnClickListener(v -> {
            countDownTimer.cancel();
            dialogNightMode.dismiss();
        });

    }

    private void registerMyAlarmBroadcast(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(darkModeDisableBroadcastReceiver,
             new IntentFilter("com.woohoo.DarkMode"), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(darkModeDisableBroadcastReceiver,
                    new IntentFilter("com.woohoo.DarkMode"));
        }
        Intent intent = new Intent("com.woohoo.DarkMode");
        intent.putExtra("broadcast_id", requestCode);
        pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                flagForPendingIntent());
        alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
    }

    private void createAlarmToEnableSurveillance() {
        //Register AlarmManager Broadcast receive.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
//        calendar.add(Calendar.MINUTE, 2);
        calendar.set(Calendar.SECOND, 0); // and alarm second
        long intendedTime = calendar.getTimeInMillis();

        registerMyAlarmBroadcast(REQUEST_CODE_ENABLE_DARKMODE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime,
         AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void showAlarmList() {
        Fragment lCurrentFragment = getCurrentFragment();
        if ((lCurrentFragment instanceof AlarmFragment)) {
            ((AlarmFragment) lCurrentFragment).showAlarm();
        }
    }

    private boolean previousSipConnectedState = false;

    private final Handler handler = new Handler();
    private final Runnable updateSipStatusRunnable = new Runnable() {
        @Override
        public void run() {
            boolean isSipConnected = false;

            if (LinphoneContext.isReady()) {
                isSipConnected = LinphoneContext.instance()
                        .getLinphoneManager()
                        .isSipConnected();
            }

            if (isSipConnected != previousSipConnectedState) {
                // Only call updateSipStatusOnFirestore if the state has changed
                updateSipStatusOnFirestore(isSipConnected);
                previousSipConnectedState = isSipConnected;
            }


            // Schedule the next execution after a minute (60,000 milliseconds)
            handler.postDelayed(this, 5000);
        }
    };

    private void updateSipStatusOnFirestore(boolean isConnected) {
        try {
            String sipStatus = SipStatusConstant.SIP_STATUS;
            String deviceId = SipStatusConstant.DEVICE_ID;
            String userId = SipStatusConstant.USER_ID;
            String deviceName = SipStatusConstant.DEVICE_NAME;
            String connected = SipStatusConstant.CONNECTED;

            JSONObject obj = new JSONObject();
            obj.put(deviceId, WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""));
            obj.put(userId, WooHooApp.getSharedPrefsHelper().get(PREF_KEY_USER_ID, null));
            obj.put(deviceName,
             WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DEVICE_NAME, ""));
            obj.put(connected, isConnected);

            Map<String, Object> user = new HashMap<>();
            user.put(Long.toString(System.currentTimeMillis()), obj.toString());

            // Firestore event entry
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(sipStatus)
                    .document(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""))
                    .set(user, SetOptions.merge());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
