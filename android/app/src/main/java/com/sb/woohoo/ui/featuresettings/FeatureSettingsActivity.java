package com.sb.woohoo.ui.featuresettings;

import static com.sb.woohoo.ui.featuresettings.DeviceIdExtensionKt.getTestDevices;
import static com.sb.woohoo.utils.Constants.ENDING_POINT_TO_DISABLE_DARK_MODE;
import static com.sb.woohoo.utils.Constants.RESTART_DEFAULT_HOUR;
import static com.sb.woohoo.utils.Constants.RESTART_DEFAULT_MIN;
import static com.sb.woohoo.utils.Constants.DEFAULT_SENSORY_OPERATING_POINT;
import static com.sb.woohoo.utils.Constants.STARTING_POINT_TO_ENABLE_DARK_MODE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.sb.woohoo.R;
import android.view.LayoutInflater;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentFeatureSettingsBinding;
import com.sb.woohoo.ui.base.BaseActivity;
import com.sb.woohoo.ui.featuresettings.di.DaggerFeatureSettingsComponent;
import com.sb.woohoo.ui.featuresettings.di.FeatureSettingsComponent;
import com.sb.woohoo.ui.featuresettings.di.FeatureSettingsModule;
import com.sb.woohoo.utils.Constants;

import java.util.ArrayList;

public class FeatureSettingsActivity extends BaseActivity {

    private FragmentFeatureSettingsBinding binding;
    private int MIN_OP = 1;
    private int MAX_OP = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentFeatureSettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        injectActivity();
        initializeViews();
    }

    public void injectActivity() {
        FeatureSettingsComponent component =
                DaggerFeatureSettingsComponent
                        .builder()
                        .featureSettingsModule(new FeatureSettingsModule(FeatureSettingsActivity.this))
                        .build();
        component.injectFeatureSettings(FeatureSettingsActivity.this);
    }

    public void initializeViews() {
        configureLinphoneConfig();
        configureNetworklogsConfig();
        configureAgoraConfig();
        configureRestartTimeConfig();
        configureTestDeviceId();
        configureOperatingPoint();
        configureDarkModeTimeConfig();
        binding.activityEnrollmentToolbar.commonToolbarBack.setOnClickListener(view -> onBackImagePressed());
    }

    private void configureLinphoneConfig() {
        boolean value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false);
        binding.linphoneSwitch.setChecked(value);

        binding.linphoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.linphoneSwitch.setChecked(isChecked);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, isChecked);
        });
    }

    private void configureNetworklogsConfig() {
        boolean value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_NETWORK_LOG, false);
        binding.networkLogSwitch.setChecked(value);

        binding.networkLogSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.networkLogSwitch.setChecked(isChecked);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_NETWORK_LOG, isChecked);
        });
    }

    private void configureAgoraConfig() {
        boolean value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_AGORA, false);
        binding.agoraCallSwitch.setChecked(value);

        binding.agoraCallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.agoraCallSwitch.setChecked(isChecked);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_AGORA, isChecked);
        });
    }

    private void configureOperatingPoint() {
        ArrayList<String> items = new ArrayList<>();
        items.add("Select");
        for (int i = MIN_OP; i < MAX_OP; i++) {
            items.add("" + i);
        }
        int operatingPoint = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_TEST_OPERATING_POINT, DEFAULT_SENSORY_OPERATING_POINT);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerOperatingPoint.setAdapter(aa);

        binding.spinnerOperatingPoint.setSelection(operatingPoint);
        binding.spinnerOperatingPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    ((TextView) view).setPadding(20, 0, 20, 0);
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds
                            (null, null,
                                    AppCompatResources.getDrawable(FeatureSettingsActivity.this, R.drawable.ic_spinner),
                                    null);
                    ((TextView) view).setCompoundDrawablePadding(20);
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                }

                if (i != 0) {
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_TEST_OPERATING_POINT, i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void configureTestDeviceId() {
        ArrayList<DeviceModel> deviceList = getTestDevices();
        final DeviceListAdapter adapter = new DeviceListAdapter(this, deviceList);
        binding.spinnerDevice.setAdapter(adapter);

        String value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_TEST_DEVICE_ID, Constants.USE_SYSTEM_ID);
        binding.spinnerDevice.setSelection(adapter.getPosition(value));

        binding.spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceModel deviceModel = (DeviceModel) adapterView.getAdapter().getItem(i);
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_TEST_DEVICE_ID, deviceModel.getTestDeviceId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void configureRestartTimeConfig() {
        boolean value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_RESTART, false);
        setRestartTime();
        setRestartSwitch(value);
        binding.restartSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setRestartSwitch(isChecked);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_RESTART, isChecked);
            if (!isChecked) {
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_HR, RESTART_DEFAULT_HOUR);
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_MIN, RESTART_DEFAULT_MIN);
                setRestartTime();
            }
        });
        binding.editRestartHr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence chr, int i, int i1, int i2) {
                int hr = chr.toString().isEmpty() ? RESTART_DEFAULT_HOUR : Integer.parseInt(chr.toString());
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_HR, hr);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editRestartMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence chr, int i, int i1, int i2) {
                int min = chr.toString().isEmpty() ? RESTART_DEFAULT_MIN : Integer.parseInt(chr.toString());
                if (min > 59) {
                    min = RESTART_DEFAULT_MIN;
                }
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_MIN, min);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setRestartSwitch(boolean value) {
        binding.restartSwitch.setChecked(value);
        binding.editRestartHr.setEnabled(value);
        binding.editRestartMin.setEnabled(value);
    }

    private void setRestartTime() {
        int hour = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_HR, RESTART_DEFAULT_HOUR);
        int min = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_RESTART_MIN, RESTART_DEFAULT_MIN);
        binding.editRestartHr.setText(String.format("%d", hour));
        binding.editRestartMin.setText(String.format("%d", min));

    }
    private void configureDarkModeTimeConfig() {
        boolean value = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE, false);
        setDarkModeTime();
        setDarkModeSwitch(value);
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkModeSwitch(isChecked);
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE, isChecked);
            if (!isChecked) {
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_HR, STARTING_POINT_TO_ENABLE_DARK_MODE);
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_MIN, ENDING_POINT_TO_DISABLE_DARK_MODE);
                setDarkModeTime();
            }
        });
        binding.darkModeRestartHr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence chr, int i, int i1, int i2) {
                String inputTime = chr.toString();

                // Check if the input matches the HH:mm format
                if (!isValidTime(inputTime)) {
                    binding.darkModeRestartHr.setError("Invalid time format (HH:mm)");
                } else {
                    // Clear the error if the input is valid
                    binding.darkModeRestartHr.setError(null);
                }
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_HR, inputTime);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.darkModeRestartMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence chr, int i, int i1, int i2) {
                String inputTime = chr.toString();

                // Check if the input matches the HH:mm format
                if (!isValidTime(inputTime)) {
                    binding.darkModeRestartMin.setError("Invalid time format (HH:mm)");
                } else {
                    // Clear the error if the input is valid
                    binding.darkModeRestartMin.setError(null);
                }
                WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_MIN, inputTime);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setDarkModeSwitch(boolean value) {
        binding.darkModeSwitch.setChecked(value);
        binding.darkModeRestartHr.setEnabled(value);
        binding.darkModeRestartMin.setEnabled(value);
    }

    private void setDarkModeTime() {
        String enableHour = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_HR, STARTING_POINT_TO_ENABLE_DARK_MODE);
        String disableHour = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_DARK_MODE_MIN, ENDING_POINT_TO_DISABLE_DARK_MODE);
        binding.darkModeRestartHr.setText( enableHour);
        binding.darkModeRestartMin.setText(disableHour);

    }

    // Function to validate the input time format (HH:mm)
    private boolean isValidTime(String inputTime) {
        return inputTime.matches("([01][0-9]|2[0-3]):[0-5][0-9]");
    }

    void onBackImagePressed() {
        onBackPressed();
    }
}
