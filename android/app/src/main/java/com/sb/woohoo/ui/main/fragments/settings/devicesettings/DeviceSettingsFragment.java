package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

import static com.sileria.android.Kit.getSystemService;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CustomDialogBox;
import com.sb.woohoo.databinding.FragmentDeviceNameSettingsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.di.DaggerDeviceSettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.di.DeviceSettingsComponent;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.di.DeviceSettingsModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.ui.onboarding.timezone.TimeZoneSelectionFragment;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Shows and Manages Device Setting of User.
 */
public class DeviceSettingsFragment extends FeatureFragment<MainActivity> implements DeviceSettingsContract.Views, OnClickListener, NonPopFragment {

    final static int DEVICE_NAME = 100;
    final static int DEVICE_LANGUAGE = 101;
    final static int DEVICE_TIMEZONE = 102;
    final static int DEVICE_RESET = 103;
    final static int DEVICE_CHANGE_PASSWORD = 104;
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_CAMERA;
    final static int DEVICE_BLUETOOTH =104;


    // Presenter
    @Inject
    DeviceSettingsPresenter deviceSettingsPresenter;



    @Inject
    DeviceSettingsAdapter deviceSettingsAdapter;

    @Inject
    AnalyticsHelper helper;

    private CustomDialogBox dialogBox;

    private String deviceName;
    private FragmentDeviceNameSettingsBinding binding;

    public static DeviceSettingsFragment newInstance() {
        return new DeviceSettingsFragment();
    }

    public static String getTimeZone(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;
        if (hours > 0) {
            result = String.format(Locale.ENGLISH, "GMT +%d:%02d", hours, minutes);
        } else if (hours < 0) {
            result = String.format(Locale.ENGLISH, "GMT %d:%02d", hours, minutes);
        } else {
            result = "GMT 00:00";
        }
        return result;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return DeviceSettingsFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.DeviceSettings(),  null);
        binding = FragmentDeviceNameSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        DeviceSettingsComponent deviceSettingsComponent = DaggerDeviceSettingsComponent.builder()
                .deviceSettingsModule(new DeviceSettingsModule(this))
                .build();

        deviceSettingsComponent.injectDeviceName(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());

        initViews();

        binding.commonToolbarLayout.commonToolbarTitle.setText(getString(R.string.fragment_device_settings_title));
        attachParent((MainActivity) getActivity());

        dialogBox = new CustomDialogBox(requireContext(), R.style.DialogTheme);

        binding.commonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackClick());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return deviceSettingsPresenter;
    }

    private void initViews() {

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(deviceSettingsAdapter);
        binding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        deviceSettingsPresenter.intializeView();
        deviceSettingsPresenter.getDeviceName();
    }

    @Override
    public void onDeviceNameStatusSuccessError(String errorMessage) {
        MethodUtils.showToastMessage(getContext(), errorMessage);
    }

    @Override
    public void resetUI() {
        WifiManager mWifiManager = (WifiManager) Objects
                .requireNonNull(getActivity())
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = null;
        if (mWifiManager != null) {
            list = mWifiManager.getConfiguredNetworks();
        }
        if (list != null) {
            for (WifiConfiguration i : list) {
                mWifiManager.removeNetwork(i.networkId);
                mWifiManager.saveConfiguration();
            }
        }

        Intent intent = new Intent(getActivity(), OnboardingActivity.class);
        intent.setAction("settings");
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void updateSettingList() {
        deviceSettingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getContext(), message);
    }

    private void onFactoryResetClicked() {
        dialogBox.show();
        dialogBox.binding.title.setText(R.string.fragment_device_settings_factory_reset_message);
        dialogBox.binding.editText.setVisibility(View.GONE);
        dialogBox.binding.buttonPositive.setText(R.string.reset);
        dialogBox.binding.buttonNegative.setText(R.string.cancel);

        dialogBox.binding.buttonPositive.setOnClickListener(v -> {
            deviceSettingsPresenter.factoryReset();
            dialogBox.dismiss();
        });

        dialogBox.binding.buttonNegative.setOnClickListener(v -> dialogBox.dismiss());
    }

    private void onLanguageClicked() {
        dialogBox.show();
        dialogBox.binding.editText.setVisibility(View.GONE);
        dialogBox.binding.buttonPositive.setText(R.string.ok);
        dialogBox.binding.buttonNegative.setVisibility(View.GONE);
        dialogBox.binding.title.setText(R.string.fragment_device_settings_support_text);

        dialogBox.binding.buttonPositive.setOnClickListener(v -> dialogBox.dismiss());
    }

    private void onTimeZoneClicked() {
        getParentActivity().switchFragmentUsingAdd(TimeZoneSelectionFragment.newInstance(TimeZoneSelectionFragment.MAINACTIVITY));
    }

    public void onTimezoneChanged() {
        deviceSettingsPresenter.onTimezoneChanged();
    }

    private void onNameClicked() {
        dialogBox.show();

        dialogBox.binding.title.setText(R.string.fragment_device_settings_edit_device_name);

        dialogBox.binding.editText.setText(deviceName);
        dialogBox.binding.editText.setVisibility(View.VISIBLE);
        dialogBox.binding.editText.setSelection(dialogBox.binding.editText.getText().toString().length());
        dialogBox.binding.editText.requestFocus();

        dialogBox.binding.buttonPositive.setText(R.string.save);
        if (dialogBox.binding.buttonNegative.getVisibility() == View.GONE) {
            // Its Gone
            dialogBox.binding.buttonNegative.setVisibility(View.VISIBLE);
        }
        dialogBox.binding.buttonNegative.setText(R.string.cancel);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        dialogBox.binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    dialogBox.binding.buttonPositive.setEnabled(true);
                    dialogBox.binding.buttonPositive.setAlpha(1.0f);
                } else {
                    dialogBox.binding.buttonPositive.setEnabled(false);
                    dialogBox.binding.buttonPositive.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialogBox.binding.buttonPositive.setOnClickListener(v -> {
            MethodUtils.hideSoftInputKeyboard(dialogBox.binding.editText, getContext());
            deviceName = dialogBox.binding.editText.getText().toString().trim();
            deviceSettingsPresenter.updateDeviceName(deviceName);
            dialogBox.dismiss();
        });

        dialogBox.binding.buttonNegative.setOnClickListener(v -> {
            MethodUtils.hideSoftInputKeyboard(dialogBox.binding.editText, getContext());
            dialogBox.dismiss();
        });
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        deviceSettingsPresenter.presenterCleanUps();
    }

    @Override
    public void onItemClicked(DeviceSetting setting) {
        switch (setting.getAction()) {
            case DEVICE_NAME:
                deviceName = deviceSettingsPresenter.getUserDeviceName();
                if (deviceName != null) {
                    if (BluetoothAdapter.getDefaultAdapter()!=null){
                        BluetoothAdapter.getDefaultAdapter().setName(deviceName);
                    }
                    onNameClicked();
                }
                break;
            case DEVICE_LANGUAGE:
                onLanguageClicked();
                break;
            case DEVICE_TIMEZONE:
                onTimeZoneClicked();
                break;
            case DEVICE_RESET:
                onFactoryResetClicked();
                break;
            case DEVICE_BLUETOOTH:
                onBluetoothClicked();
                break;
        }
    }

    private void onBluetoothClicked() {
        getParentActivity().goToBluetooth();
    }


    public void onBackClick() {
        getParentActivity().onBackPressed();
    }

    void onRootClicked() {

    }

}
