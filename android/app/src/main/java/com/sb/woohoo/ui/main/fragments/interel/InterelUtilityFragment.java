package com.sb.woohoo.ui.main.fragments.interel;

import static com.sb.woohoo.utils.others.MethodUtils.isFloat;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION.FAN_MODE;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.AUTO;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.ECO;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.HIGH;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.LOW;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.MEDIUM;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION_FAN_MODE.OFF;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.SpaceItemDecoration;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentInterelUtilityBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.interel.di.DaggerInterelUtilityComponent;
import com.sb.woohoo.ui.main.fragments.interel.di.InterelUtilityComponent;
import com.sb.woohoo.ui.main.fragments.interel.di.InterelUtilityModule;
import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtility;
import com.sb.woohoo.ui.main.fragments.interel.pojo.InterelUtilityData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class InterelUtilityFragment
        extends FeatureFragment<MainActivity>
        implements InterelUtilityCallBackListener, InterelUtilityContract.Views {

    private DynamicUtility temperatureObject;
    private DynamicUtility fanmodeObject;

    private int MIN_TEMP = 18;
    private int MAX_TEMP = 28;

    @Inject
    InterelUtilityPresenter interelUtilityPresenter;
    @Inject
    InterelUtilityRVAdapter interelUtilityRVAdapter;
    @Inject
    AnalyticsHelper helper;

    private float tempPoint = 0;
    private FragmentInterelUtilityBinding binding;

    /**********************{@FEATURE_FRAGMENT_CALLBACK}********************************************/
    public static InterelUtilityFragment newInstance() {
        return new InterelUtilityFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        InterelUtilityComponent interelUtilityComponent = DaggerInterelUtilityComponent
                .builder()
                .interelUtilityModule(new InterelUtilityModule(this))
                .build();
        interelUtilityComponent.injectInterelUtilityComponent(this);
    }

    @Override
    public void bindViews(View view) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (!domainId.equals(Constants.CKS_DOMAIN_USER_ID) && !domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID) && !domainId.equals(Constants.REGENT_DOMAIN_ID))
            binding.commonToolbar.commonToolbarTitle.setText(getString(R.string.smart_room_control));
        else
            binding.commonToolbar.commonToolbarTitle.setText(getString(R.string.control));
        setClickEvents();
        init();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return interelUtilityPresenter;
    }

    /**************************************{@FRAGMENT_LIFECYCLE_CALLBACK}**************************/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Interel(),  null);
        binding = FragmentInterelUtilityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    private void init() {
        binding.rvInterelUtility.setAdapter(interelUtilityRVAdapter);
        binding.rvInterelUtility.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        binding.rvInterelUtility.addItemDecoration(new SpaceItemDecoration(2, 30, false));
        interelUtilityRVAdapter.updateList(getParentActivity(), createStubbedList());
//        interelUtilityPresenter.getRoomState();
    }


    private List<DynamicUtility> createStubbedList() {
        binding.layoutTemperature.setVisibility(View.GONE);
        getDynamicUtilities();
        return new ArrayList<>();
    }

    private DynamicUtility getOfflineDynamicUtility(int drawable, String utilityName, String mode, boolean isClicked) {
        DynamicUtility dynamicUtility = new DynamicUtility();
        dynamicUtility.setDrawableImageIcon(drawable);
        dynamicUtility.setCurrentState(String.valueOf(isClicked));
        dynamicUtility.setItemName(utilityName);
        dynamicUtility.setType(mode);
        return dynamicUtility;


    }

    @Override
    public void showProgressLayout(boolean show) {
        if (show) {
            if (binding.rlProgress != null) {
                binding.rlProgress.setVisibility(View.VISIBLE);
            }
            if (binding.progressBar != null)
                binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            if (binding.rlProgress != null) {
                binding.rlProgress.setVisibility(View.GONE);
            }
            if (binding.progressBar != null)
                binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUtilityFetched(InterelUtilityData interelUtilityData) {
    }

    private void getDynamicUtilities() {
        interelUtilityPresenter.getDynamicUtilities();
    }

    private void setFanMode(String string) {
        if (string.equalsIgnoreCase(AUTO)) {
            binding.rbFanAuto.setChecked(true);
            setTextcolor(binding.rbFanAuto, R.color.cyan_light);
        } else if (string.equalsIgnoreCase(OFF)) {
            binding.rbFanOff.setChecked(true);
            setTextcolor(binding.rbFanOff, R.color.cyan_light);
        } else if (string.equalsIgnoreCase(ECO)) {
            binding.rbFanEco.setChecked(true);
            setTextcolor(binding.rbFanEco, R.color.cyan_light);
        } else if (string.equalsIgnoreCase(LOW)) {
            binding.rbLow.setChecked(true);
            setTintForFanMode(binding.rbLow, R.color.cyan_light);
        } else if (string.equalsIgnoreCase(HIGH)) {
            binding.rbHigh.setChecked(true);
            setTintForFanMode(binding.rbHigh, R.color.cyan_light);
        } else if (string.equalsIgnoreCase(MEDIUM)) {
            binding.rbMedium.setChecked(true);
            setTintForFanMode(binding.rbMedium, R.color.cyan_light);
        }
    }

    private void setTextcolor(RadioButton radioButton, int color) {
        radioButton.setTextColor(ContextCompat.getColor(requireActivity(), color));

    }

    private void setClickEvents() {
        binding.rbLow.setOnClickListener(view -> onRadioButtonClicked(binding.rbLow));
        binding.rbFanAuto.setOnClickListener(view -> onRadioButtonClicked(binding.rbFanAuto));
        binding.rbFanEco.setOnClickListener(view -> onRadioButtonClicked(binding.rbFanEco));
        binding.rbFanOff.setOnClickListener(view -> onRadioButtonClicked(binding.rbFanOff));
        binding.rbMedium.setOnClickListener(view -> onRadioButtonClicked(binding.rbMedium));
        binding.rbHigh.setOnClickListener(view -> onRadioButtonClicked(binding.rbHigh));
        binding.commonToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.layoutThermostatMode.ivDecrease.setOnClickListener(this::onViewClicked);
        binding.layoutThermostatMode.ivIncrease.setOnClickListener(this::onViewClicked);
    }

    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();
        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.rb_fan_auto:
                interelUtilityPresenter.setFanMode(AUTO, stateId(AUTO));
                handleRadioButtons(true, false, false, false, false, false);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
            case R.id.rb_fan_Eco:
                interelUtilityPresenter.setFanMode(ECO, stateId(ECO));
                handleRadioButtons(false, true, false, false, false, false);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
            case R.id.rb_fan_off:
                interelUtilityPresenter.setFanMode(OFF, stateId(OFF));
                handleRadioButtons(false, false, true, false, false, false);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
            case R.id.rb_low:
                interelUtilityPresenter.setFanMode(LOW, stateId(LOW));
                handleRadioButtons(false, false, false, true, false, false);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
            case R.id.rb_medium:
                interelUtilityPresenter.setFanMode(MEDIUM, stateId(MEDIUM));
                handleRadioButtons(false, false, false, false, true, false);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
            case R.id.rb_high:
                interelUtilityPresenter.setFanMode(HIGH, stateId(HIGH));
                handleRadioButtons(false, false, false, false, false, true);
                changeRadioButtonUI(binding.rbFanAuto, binding.rbFanEco, binding.rbFanOff, binding.rbLow, binding.rbMedium, binding.rbHigh);
                break;
        }
    }

    private int stateId(String fanMode) {
        int id = 0;
        if (fanmodeObject != null && fanmodeObject.getStates() != null) {
            id = fanmodeObject.getStates().get(0).getId();
            for (int i = 0; i < fanmodeObject.getStates().size(); i++) {
                if (fanmodeObject.getStates().get(i).getStateValue().equalsIgnoreCase(fanMode)) {
                    return fanmodeObject.getStates().get(i).getId();
                }
            }
        }
        return id;
    }

    private void handleRadioButtons(boolean isAuto, boolean isEco, boolean isOff, boolean isLow, boolean isMedium, boolean isHigh) {
        binding.rbFanAuto.setChecked(isAuto);
        binding.rbFanEco.setChecked(isEco);
        binding.rbFanOff.setChecked(isOff);
        binding.rbLow.setChecked(isLow);
        binding.rbMedium.setChecked(isMedium);
        binding.rbHigh.setChecked(isHigh);
    }

    private void changeRadioButtonUI(RadioButton rbAuto, RadioButton rbEco, RadioButton rbOff,
                                     RadioButton rbLow, RadioButton rbMedium, RadioButton rbHigh) {
        if (rbAuto.isChecked()) {
            setTextcolor(rbAuto, R.color.cyan_light);
            setTextcolor(rbEco, R.color.white);
            setTextcolor(rbOff, R.color.white);
            setTintForFanMode(rbLow, R.color.white);
            setTintForFanMode(rbMedium, R.color.white);
            setTintForFanMode(rbHigh, R.color.white);
        } else if (rbEco.isChecked()) {
            setTextcolor(rbAuto, R.color.white);
            setTextcolor(rbEco, R.color.cyan_light);
            setTextcolor(rbOff, R.color.white);
            setTintForFanMode(rbLow, R.color.white);
            setTintForFanMode(rbMedium, R.color.white);
            setTintForFanMode(rbHigh, R.color.white);
        } else if (rbOff.isChecked()) {
            setTextcolor(rbAuto, R.color.white);
            setTextcolor(rbEco, R.color.white);
            setTextcolor(rbOff, R.color.cyan_light);
            setTintForFanMode(rbLow, R.color.white);
            setTintForFanMode(rbMedium, R.color.white);
            setTintForFanMode(rbHigh, R.color.white);
        } else if (rbLow.isChecked()) {
            setTextcolor(rbAuto, R.color.white);
            setTextcolor(rbEco, R.color.white);
            setTextcolor(rbOff, R.color.white);
            setTintForFanMode(rbLow, R.color.cyan_light);
            setTintForFanMode(rbMedium, R.color.white);
            setTintForFanMode(rbHigh, R.color.white);
        } else if (rbMedium.isChecked()) {
            setTextcolor(rbAuto, R.color.white);
            setTextcolor(rbEco, R.color.white);
            setTextcolor(rbOff, R.color.white);
            setTintForFanMode(rbLow, R.color.white);
            setTintForFanMode(rbMedium, R.color.cyan_light);
            setTintForFanMode(rbHigh, R.color.white);
        } else if (rbHigh.isChecked()) {
            setTextcolor(rbAuto, R.color.white);
            setTextcolor(rbEco, R.color.white);
            setTextcolor(rbOff, R.color.white);
            setTintForFanMode(rbLow, R.color.white);
            setTintForFanMode(rbMedium, R.color.white);
            setTintForFanMode(rbHigh, R.color.cyan_light);
        }

    }

    private void setTintForFanMode(RadioButton radioButton, int color) {
        for (Drawable drawable : radioButton.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onUtilityItemClicked(String utilitySkill, Boolean status) {
        interelUtilityPresenter.updateUtilityMode(utilitySkill, status);
    }

    @Override
    public void onUtilityItemClicked(String currentState) {
        interelUtilityPresenter.updateDynamicUtility(currentState);
    }

    @Override
    public void updateUtilityModeUi(String message) {
        showToastMessage(message);
    }

    @Override
    public void getDynamicUtilities(List<DynamicUtility> dynamicUtilityList) {
        temperatureObject = null;
        fanmodeObject = null;
        if (dynamicUtilityList != null && !dynamicUtilityList.isEmpty()) {
            for (int i = 0; i < dynamicUtilityList.size(); i++) {
                DynamicUtility utility = dynamicUtilityList.get(i);
                if (utility.getType().equalsIgnoreCase("temperature")) {
                    temperatureObject = utility;
                } else if (utility.getType().equalsIgnoreCase(FAN_MODE)) {
                    fanmodeObject = utility;
                }
            }
            if (temperatureObject != null || fanmodeObject != null) {
                binding.layoutTemperature.setVisibility(View.VISIBLE);
                if (temperatureObject != null) {
                    binding.layoutThermostatMode.layoutThermostat.setVisibility(View.VISIBLE);
                    dynamicUtilityList.remove(temperatureObject);
                    setTemperatureInUI(temperatureObject.getCurrentState());
                } else {
                    binding.layoutThermostatMode.layoutThermostat.setVisibility(View.GONE);
                }
                binding.radioGroupFanControlFeatureMode.setVisibility(View.GONE);
                if (fanmodeObject != null) {
                    binding.radioGroupFanControlSpeedControl.setVisibility(View.VISIBLE);
                    dynamicUtilityList.remove(fanmodeObject);
                    setFanMode(fanmodeObject.getCurrentState());
                } else {
                    binding.radioGroupFanControlSpeedControl.setVisibility(View.GONE);
                }
            } else {
                binding.layoutTemperature.setVisibility(View.GONE);
            }
            interelUtilityRVAdapter.updateList(getParentActivity(), dynamicUtilityList);
        }
    }

    private void setTemperatureInUI(String temperatureInUI) {
        binding.layoutThermostatMode.tvTempPoint.setText(temperatureInUI);
    }

    private void showToastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void onViewClicked(View view) {

        String lVal = binding.layoutThermostatMode.tvTempPoint.getText().toString();
        switch (view.getId()) {
            case R.id.iv_decrease:
                if (isFloat(lVal) && Float.parseFloat(lVal) > MIN_TEMP) {
                    tempPoint = Float.parseFloat(binding.layoutThermostatMode.tvTempPoint.getText().toString());
                    tempPoint = tempPoint - 1;
                    if (temperatureObject != null && temperatureObject.getStates() != null) {
                        interelUtilityPresenter.updateTemperature(String.valueOf(temperatureObject.getStates().get(0).getId()), (int) tempPoint);
                    } else
                        interelUtilityPresenter.setThermostatPoint(tempPoint);
                } else {
                    String message = String.format(getString(R.string.cant_set_below_18), MIN_TEMP + "");
                    showToastMessage(message);
                }
                break;
            case R.id.iv_increase:
                if (isFloat(lVal) && Float.valueOf(binding.layoutThermostatMode.tvTempPoint.getText().toString()) < MAX_TEMP) {
                    tempPoint = Float.parseFloat(binding.layoutThermostatMode.tvTempPoint.getText().toString());
                    tempPoint = tempPoint + 1;
                    if (temperatureObject != null && temperatureObject.getStates() != null) {
                        interelUtilityPresenter.updateTemperature(String.valueOf(temperatureObject.getStates().get(0).getId()), (int) tempPoint);
                    } else
                        interelUtilityPresenter.setThermostatPoint(tempPoint);
                } else {
                    String message = String.format(getString(R.string.cant_set_below_18), MAX_TEMP + "");
                    showToastMessage(message);
                }
                break;
        }
    }

    @Override
    public void onTemperatureUpdated(String message) {
        setTemperatureInUI(String.valueOf(tempPoint));
        showToastMessage(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interelUtilityPresenter.presenterCleanUps();
    }
}
