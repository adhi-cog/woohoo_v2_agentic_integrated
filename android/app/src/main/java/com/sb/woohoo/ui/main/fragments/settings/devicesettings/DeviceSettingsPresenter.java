package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.sb.woohoo.R;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_BLUETOOTH;
import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_LANGUAGE;
import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_NAME;
import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_RESET;
import static com.sb.woohoo.ui.main.fragments.settings.devicesettings.DeviceSettingsFragment.DEVICE_TIMEZONE;
import static com.sileria.android.Resource.getAssets;

/**
 * Presenter for {@link DeviceSettingsFragment}.
 *
 * @author Pankaj.
 */
public class DeviceSettingsPresenter extends BasePresenter implements DeviceSettingsContract.Actions {

    private static String timeZoneJson = loadJSONFromAsset();
    private DeviceSettingsRepository deviceSettingsRepository;
    private DeviceSettingsFragment mView;
    private List<DeviceSetting> deviceSettingList;
    private String deviceName = null;

    /*----------------------------------------------------- Update Device Name ------------------------------------------------*/

    @Inject
    public DeviceSettingsPresenter(DeviceSettingsRepository deviceSettingsRepository, DeviceSettingsFragment mView, List<DeviceSetting> deviceSettingList) {
        this.deviceSettingsRepository = deviceSettingsRepository;
        this.mView = mView;
        this.deviceSettingList = deviceSettingList;
        deviceSettingsRepository.onAttach(this);
    }

    /*----------------------------------------------------- Fetch Device Name -------------------------------------------------*/

    private static String getTimeZone() {
        TimeZone timeZoneText = TimeZone.getDefault();
        long hours = TimeUnit.MILLISECONDS.toHours(timeZoneText.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeZoneText.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;
        if (hours > 0) {
            result = String.format("GMT+%02d:%02d", hours, minutes);
        } else if (hours < 0) {
            result = String.format("GMT-%02d:%02d", Math.abs(hours), minutes);
        } else {
            result = "GMT+00:00";
        }

        String city = getCityName(result);
        if (city.equals("")) {
            result = String.format("%s ( %s )", result, timeZoneText.getID());
        } else {
            result = String.format("%s ( %s )", result, city);
        }
        return result;
    }

    private static String getCityName(String id) {
        try {
            JSONObject obj = new JSONObject(timeZoneJson);
            return obj.getString(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("timezone.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static String getTimeZoneUpdate() {
        TimeZone timeZoneText = TimeZone.getDefault();
        long hours = TimeUnit.MILLISECONDS.toHours(timeZoneText.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeZoneText.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;
        if (hours > 0) {
            result = String.format("GMT+%02d:%02d", hours, minutes);
        } else if (hours < 0) {
            result = String.format("GMT-%02d:%02d", Math.abs(hours), minutes);
        } else {
            result = "GMT+00:00";
        }

//        result = String.format("%s ( %s )", result);

        return result;
    }

    /**
     * Requests {@link DeviceSettingsFragment} to perform an API call for update hub name
     */
    void updateDeviceName(String deviceName) {
        this.deviceName = deviceName;
        for (int i = 0; i < deviceSettingList.size(); i++) {
            if (deviceSettingList.get(i).getAction() == DEVICE_NAME) {
                deviceSettingList.get(i).setDeviceSettingContent(deviceName);
                deviceSettingList.get(i).setLoading(true);
                deviceSettingList.get(i).setDeviceSettingContent(mView.getString(R.string.updating_time));
                break;
            }
        }
        mView.updateSettingList();

        deviceSettingsRepository.updateDeviceNameApiRequest(deviceName);
    }

    /**
     * Requests {@link DeviceSettingsRepository} from get fetching Device Name
     */
    @Override
    public void onDeviceNameSuccess(String deviceName) {
        this.deviceName = deviceName;
        for (int i = 0; i < deviceSettingList.size(); i++) {
            if (deviceSettingList.get(i).getAction() == DEVICE_NAME) {
                deviceSettingList.get(i).setDeviceSettingContent(deviceName);
                deviceSettingList.get(i).setLoading(false);
                break;
            }
        }
        mView.updateSettingList();
    }

    /**
     * Requests {@link DeviceSettingsRepository} from get fetching Device Name Status Success and Error
     */
    @Override
    public void onDeviceNameStatusError(String responseStatus) {
        for (int i = 0; i < deviceSettingList.size(); i++) {
            if (deviceSettingList.get(i).getAction() == DEVICE_NAME) {
                deviceSettingList.get(i).setDeviceSettingContent(mView.getString(R.string.not_fetch_name));
                deviceSettingList.get(i).setLoading(false);
                break;
            }
        }
        mView.updateSettingList();
    }

    void onDeviceNameStatusSuccess() {
        for (int i = 0; i < deviceSettingList.size(); i++) {
            if (deviceSettingList.get(i).getAction() == DEVICE_NAME) {
                deviceSettingList.get(i).setDeviceSettingContent(deviceName);
                deviceSettingList.get(i).setLoading(false);
                break;
            }
        }
        mView.updateSettingList();
    }

    @Override
    public void onTimezoneChanged() {
        for (int i = 0; i < deviceSettingList.size(); i++) {
            if (deviceSettingList.get(i).getAction() == DEVICE_TIMEZONE) {
                String timeZone = getTimeZoneUpdate();
                deviceSettingList.get(i).setDeviceSettingContent(getTimeZone());
                deviceSettingList.get(i).setLoading(false);
                deviceSettingsRepository.updateTimeZone(timeZone);
                break;
            }
        }
        mView.updateSettingList();
    }

    @Override
    public void onAPIError(String message) {
        mView.showMessage(message);
    }

    @Override
    public void onAPISuccess(String msg) {
        mView.showMessage(msg);
    }

    @Override
    public void factoryReset() {
        File cache = Objects.requireNonNull(mView.getContext()).getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    FileUtils.deleteDir(new File(appDir, s));

                    SharedPreferences.Editor editor =
                            mView.getContext().getSharedPreferences(mView.getContext().getPackageName(), Context.MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    // Setting up a Flag for Login UX tweak.
                    editor.putBoolean(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE, true);
                    editor.commit();
                    editor.apply();

                    mView.resetUI();
                }
            }
        }
    }

    String getUserDeviceName() {
        return deviceName;
    }

    @Override
    public void intializeView() {

        deviceSettingList.add(new DeviceSetting(mView.getString(R.string.name_of_device),mView.getString(R.string.fetching_name), DEVICE_NAME, true));
        deviceSettingList.add(new DeviceSetting(mView.getString(R.string.device_language),mView.getString(R.string.english_us), DEVICE_LANGUAGE));
        deviceSettingList.add(new DeviceSetting(mView.getString(R.string.device_timezone),getTimeZone(), DEVICE_TIMEZONE));
        deviceSettingList.add(new DeviceSetting(mView.getString(R.string.factory_reset),mView.getString(R.string.reset_woohoo_hub), DEVICE_RESET));
        deviceSettingList.add(new DeviceSetting(mView.getString(R.string.bluetooth),mView.getString(R.string.connect_to_other_devices), DEVICE_BLUETOOTH));

        mView.updateSettingList();
    }

    @Override
    public void getDeviceName() {
        deviceSettingsRepository.fetchDeviceNameApiRequest();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        deviceSettingsRepository.repositoryCleanUps();
    }

}
