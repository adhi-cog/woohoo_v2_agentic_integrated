package com.sb.woohoo.ui.main.fragments.settings;

import static com.sb.woohoo.ui.main.fragments.settings.SettingsFragment.PERFORM_ENROLLMENT;
import static com.sb.woohoo.ui.main.fragments.settings.SettingsFragment.PERFORM_LOGOUT;
import static com.sb.woohoo.ui.main.fragments.settings.SettingsFragment.PERFORM_SCREENCAST;
import static com.sb.woohoo.ui.main.fragments.settings.SettingsFragment.SETTINGS_ACTION;
import static com.sb.woohoo.ui.main.fragments.settings.SettingsFragment.SETTINGS_SWITCH;
import static com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment.DIALOGFLOW_ENGINE;
import static com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment.SMARTFLOW_ENGINE;
import static com.sb.woohoo.ui.main.fragments.settings.wakewordswitch.WakeWordSwitchFragment.GLOBAL_WAKEWORD;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment;
import com.sb.woohoo.ui.main.fragments.settings.engineswitch.EngineSwitchFragment;
import com.sb.woohoo.ui.main.fragments.settings.language.LanguageChangeFragment;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.ThingsToSayFragment;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateFragment;
import com.sb.woohoo.ui.main.fragments.settings.wakewordswitch.WakeWordSwitchFragment;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.SIMUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Presenter for {@link SettingsFragment}.
 */
public class SettingsPresenter
        extends BasePresenter
        implements SettingsContract.Action {

    public static int SETTINGS_WIFI = 100;
    public static int SETTINGS_BLUETOOTH = 101;
    public static int SETTINGS_DISPLAY = 102;
    public static int SETTINGS_VOLUME = 103;
    public static int SETTINGS_DEVICE = 104;
    public static int SETTINGS_VOICE_ENROll = 105;
    public static int SETTINGS_THINGS_TO_SAY = 106;
    public static int SETTINGS_UPDATE = 107;
    public static int SETTINGS_HELP = 108;
    public static int SETTINGS_LOGOUT = 109;
    public static int SETTINGS_SIM = 110;
    public static int SETTINGS_ENGINE = 111;
    public static int SETTINGS_WAKEWORD = 112;
    public static int SETTINGS_LANGUAGE = 113;
    public static int SETTINGS_SCREENCAST = 114;

    private SettingsFragment mView;
    private SettingsRepository mRepository;
    private SharedPrefsHelper mPrefsHelper;
    private List<Setting> settingList;
    private SIMUtils simUtils;

    /*@BindString(R.string.fragment_settings_text_view_wifi)
    String wifi;
    @BindString(R.string.engine)
    String engine;
    @BindString(R.string.wakeword)
    String wakeword;
    @BindString(R.string.device_language)
    String device_language;
    @BindString(R.string.device_settings)
    String device_settings;
    @BindString(R.string.voice_model)
    String voice_model;
    @BindString(R.string.things_to_say)
    String things_to_say;
    @BindString(R.string.system_update)
    String system_update;
    @BindString(R.string.logout)
    String logout;
    @BindString(R.string.no_update_available)
    String no_update_available;
    @BindString(R.string.update_available)
    String update_available;
    @BindString(R.string.fragment_update_string_version)
    String fragment_update_string_version;
    @BindString(R.string.could_not_fetch)
    String could_not_fetch;
    @BindString(R.string.cellular_data_off)
    String cellular_data_off;
    @BindString(R.string.cellular_data_on)
    String cellular_data_on;
    @BindString(R.string.updating)
    String updating;
    @BindString(R.string.checking_update)
    String checking_update;
    @BindString(R.string.suggestions_to_speak)
    String suggestions_to_speak;
    @BindString(R.string.enroll_voice)
    String enroll_voice;
    @BindString(R.string.name_device_lang)
    String name_device_lang;*/

    @Inject
    SettingsPresenter(SettingsFragment view, List<Setting> settingList, SettingsRepository repository, SIMUtils simUtils) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(SettingsPresenter.this);
        this.settingList = settingList;
        mPrefsHelper = WooHooApp.getSharedPrefsHelper();
        this.simUtils = simUtils;
    }

    /********************************************************** Log Out **************************************************************/

    void logOut() {
        mRepository.performLogOut();
    }

    @Override
    public void onLoggedOutSuccessfully() {
        // mView.onLogOutSuccess();
        mView.closeMainActivity();
//        DbHandler db = new DbHandler(mView.getContext());
//        db.DeleteUserTable();

    }

    @Override
    public void onCheckUpdateSuccess(UpdateDataModel updateDataModel) {
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int versionCodeReceived = updateDataModel.getVersionCode();

        if (versionCodeReceived > currentVersionCode) {
            for (int i = 0; i < settingList.size(); i++) {
                if (settingList.get(i).getFragmentType() == SETTINGS_UPDATE) {
                    settingList.get(i).setUpdateAvailable(true);
                    settingList.get(i).setLoading(false);
                    settingList.get(i).setVersionName(updateDataModel.getVersionName());

                    Spannable wordtoSpan1 = new SpannableString(mView.getString(R.string.fragment_update_string_version)+" " + updateDataModel.getVersionName() + " · ");
                    Spannable wordtoSpan2 = new SpannableString(mView.getString(R.string.update_available));
                    wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#50e3c2")), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    settingList.get(i).setSettingsContent(TextUtils.concat(wordtoSpan1, wordtoSpan2));

                    settingList.get(i).setFeatureFragment(UpdateFragment.newInstance(updateDataModel));
                    break;
                }
            }
        } else {
            for (int i = 0; i < settingList.size(); i++) {
                if (settingList.get(i).getFragmentType() == SETTINGS_UPDATE) {
                    settingList.get(i).setUpdateAvailable(false);
                    settingList.get(i).setLoading(false);
                    settingList.get(i).setSettingsContent(mView.getString(R.string.no_update_available));
                    settingList.get(i).setFeatureFragment(UpdateFragment.newInstance());
                    break;
                }
            }
        }

        mView.updateSettingsList();
    }

    @Override
    public void onCheckUpdateFailed() {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_UPDATE) {
                settingList.get(i).setUpdateAvailable(false);
                settingList.get(i).setLoading(false);

                Spannable wordtoSpan2 = new SpannableString(mView.getString(R.string.could_not_fetch));
                wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#FFE44640")), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                settingList.get(i).setSettingsContent(wordtoSpan2);
                settingList.get(i).setFeatureFragment(UpdateFragment.newInstance());
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void intializeViews() {

        int engine = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);

        int wakeword = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD);

        settingList.add(new Setting(R.drawable.ic_wifi_settings, mView.getString(R.string.fragment_settings_text_view_wifi), getConnectedWifi(), WifiFragment.newInstance(WifiFragment.MAINACTIVITY), SETTINGS_SWITCH, SETTINGS_WIFI));
//        settingList.add(new Setting(R.drawable.ic_bluetooth_settings, "Bluetooth", "Off", SETTINGS_SWITCH, SETTINGS_BLUETOOTH));
//        if (simUtils.isSimPresent()) {
//            if (simUtils.isMobileDataOn()) {
//                settingList.add(new Setting(R.drawable.ic_setting_sim, "SIM Settings", "Cellular Data: On", SimSettingsFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_SIM));
//            } else {
//                settingList.add(new Setting(R.drawable.ic_setting_sim, "SIM Settings", "Cellular Data: Off", SimSettingsFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_SIM));
//            }
//        } else {
//            settingList.add(new Setting(R.drawable.ic_setting_sim, "SIM Settings", "No SIM found", SimSettingsFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_SIM));
//        }

        settingList.add(new Setting(R.drawable.ic_screencast, mView.getString(R.string.screen_cast), mView.getString(R.string.mirror_your_device), SETTINGS_ACTION, PERFORM_SCREENCAST,
                SETTINGS_SCREENCAST));

        if (engine == DIALOGFLOW_ENGINE) {
            settingList.add(new Setting(R.drawable.ic_setting_engine, mView.getString(R.string.engine), mView.getString(R.string.consumer_smartflow), EngineSwitchFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_ENGINE));
        } else {
            settingList.add(new Setting(R.drawable.ic_setting_engine,  mView.getString(R.string.engine), mView.getString(R.string.enterprise_smartflow), EngineSwitchFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_ENGINE));
        }

        if (wakeword == GLOBAL_WAKEWORD) {
            settingList.add(new Setting(R.drawable.ic_enroll_settings, mView.getString(R.string.wakeword), mView.getString(R.string.global_wakeword), WakeWordSwitchFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_WAKEWORD));
        } else {
            settingList.add(new Setting(R.drawable.ic_enroll_settings, mView.getString(R.string.wakeword), mView.getString(R.string.private_wakeword), WakeWordSwitchFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_WAKEWORD));
        }

      /*  if (lang == LANGUAGE_CHINESE) {
            settingList.add(new Setting(R.drawable.ic_display_settings, mView.getString(R.string.device_language), mView.getString(R.string.chinese_lang), LanguageChangeFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_LANGUAGE));
        } else if (lang == LANGUAGE_ARABIC) {
            settingList.add(new Setting(R.drawable.ic_display_settings, mView.getString(R.string.device_language), mView.getString(R.string.arabic_lang), LanguageChangeFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_LANGUAGE));
        }else if (lang == LANGUAGE_DUTCH) {
            settingList.add(new Setting(R.drawable.ic_display_settings, mView.getString(R.string.device_language), mView.getString(R.string.dutch_lang), LanguageChangeFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_LANGUAGE));
        } else {
            settingList.add(new Setting(R.drawable.ic_display_settings, mView.getString(R.string.device_language), mView.getString(R.string.english_lang), LanguageChangeFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_LANGUAGE));

        }*/
        //settingList.add(new Setting(R.drawable.ic_display_settings, "Display", "Device Brightness", SETTINGS_SWITCH, SETTINGS_DISPLAY));

        //settingList.add(new Setting(R.drawable.ic_volume_settings, "Sound", "Media, notification, alarm volume", SETTINGS_SWITCH, SETTINGS_VOLUME));
        settingList.add(new Setting(R.drawable.ic_device_settings, mView.getString(R.string.device_settings), mView.getString(R.string.name_device_lang), DeviceSettingsFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_DEVICE));
        settingList.add(new Setting(R.drawable.ic_enroll_settings, mView.getString(R.string.voice_model), mView.getString(R.string.enroll_voice), SETTINGS_ACTION, PERFORM_ENROLLMENT, SETTINGS_VOICE_ENROll));
        settingList.add(new Setting(R.drawable.ic_voice_setting, mView.getString(R.string.things_to_say), mView.getString(R.string.suggestions_to_speak), ThingsToSayFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_THINGS_TO_SAY));
        settingList.add(new Setting(R.drawable.ic_update_settings, mView.getString(R.string.system_update), mView.getString(R.string.checking_update), UpdateFragment.newInstance(), SETTINGS_SWITCH, SETTINGS_UPDATE, true));
//        settingList.add(new Setting(R.drawable.ic_help_settings, "Help", "Get the most out of WooHoo", SETTINGS_SWITCH, SETTINGS_HELP));
        settingList.add(new Setting(R.drawable.ic_logout_settings, mView.getString(R.string.logout), getLoginID(), SETTINGS_ACTION, PERFORM_LOGOUT, SETTINGS_LOGOUT));

        mView.updateSettingsList();
    }

    @Override
    public void onUpdateClicked() {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_UPDATE) {
                settingList.get(i).setUpdateAvailable(false);
                settingList.get(i).setLoading(true);

                Spannable wordtoSpan2 = new SpannableString(mView.getString(R.string.updating));
                wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#FFE44640")), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                settingList.get(i).setSettingsContent(wordtoSpan2);
                settingList.get(i).setFeatureFragment(UpdateFragment.newInstance());
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void onWifiNetworkChanged() {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_WIFI) {
                settingList.get(i).setSettingsContent(getConnectedWifi());
                break;
            }
        }
        mView.updateSettingsList();
    }

    /****************************************************** Common Methods ***********************************************************/

    private String getConnectedWifi() {
        ConnectivityManager connManager = (ConnectivityManager) mView.getParentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final WifiManager wifiManager = (WifiManager) mView.getParentActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = null;
        if (connManager != null) {
            networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                return mView.getString(R.string.fan_mode_off);
            } else if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                        return connectionInfo.getSSID();
                    }
                } else {
                    return mView.getString(R.string.not_connected);
                }
            }
        }
        return "";
    }

    private String getLoginID() {
        return mPrefsHelper.get(SharedPrefsKey.PREF_KEY_EMAIL, "");
    }

    @Override
    public void onAPIError(Object errorCause) {
        String msgString;
        if (errorCause instanceof Integer)
            msgString = mView.getString((Integer) errorCause);
        else
            msgString = (String) errorCause;
        mView.showError(msgString);
    }

    void doCheckUpdate() {
        mRepository.preformCheckUpdate();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onMobileNetworkChanged(boolean isChecked) {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_SIM) {
                String text = isChecked ? mView.getString(R.string.cellular_data_on) : mView.getString(R.string.cellular_data_off);
                settingList.get(i).setSettingsContent(text);
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void onEngineChanged(String text) {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_ENGINE) {
                settingList.get(i).setSettingsContent(text);
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void onWakeWordChanged(String text) {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_WAKEWORD) {
                settingList.get(i).setSettingsContent(text);
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void onLangChanged(String text) {
        for (int i = 0; i < settingList.size(); i++) {
            if (settingList.get(i).getFragmentType() == SETTINGS_LANGUAGE) {
                settingList.get(i).setSettingsContent(text);
                break;
            }
        }
        mView.updateSettingsList();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}


