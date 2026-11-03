package com.sb.woohoo.ui.main.fragments.devices.huecontrol;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_SESSION_TOKEN;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentPhilipsHueBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.di.DaggerPhilipsHueComponent;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.di.PhilipsHueComponent;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.di.PhilipsHueModule;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.DevicesUtils;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class PhilipsHueFragment extends FeatureFragment<MainActivity> implements PhilipsHueContract.View {

    private final String FEATURE_CATEGORY = Categories.Devices.COMMAND_DEVICES_LIGHTS;
    private final int MAX_VALUE_SEEK_BAR = 255;

    @Inject
    PhilipsHuePresenter mPresenter;
    @Inject
    Gson gson;
    @Inject
    JsonParser jsonParser;
    @Inject
    AnalyticsHelper helper;

    private Light light;
    private State state;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            binding.fragmentPhilipsSwitch.setChecked(state.isOn());
            if (state.isOn()) {
                binding.hueControlLayout.setVisibility(View.VISIBLE);
                binding.offLightLayout.setVisibility(View.GONE);
            } else {
                binding.hueControlLayout.setVisibility(View.GONE);
                binding.offLightLayout.setVisibility(View.VISIBLE);
            }
            binding.fragmentPhilipsSeekBarBrightness.setProgress(state.getBri());

            switch (light.getType()) {
                case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                    binding.colorpickerHueControl.setGradientView(R.drawable.color_picker);
                    break;

                case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                    binding.colorpickerHueControl.setGradientView(R.drawable.color_picker_white_ambiance);
                    break;
            }
            handler.removeCallbacks(runnable);
        }
    };
    private FragmentPhilipsHueBinding binding;

    public PhilipsHueFragment() {
    }

    public static PhilipsHueFragment newInstance(IntentInfo lightIntentInfo) {
        PhilipsHueFragment fragment = new PhilipsHueFragment();
        fragment.processData(lightIntentInfo, null);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public void setUIComponents(Light light) {
        if (state != null) {
            this.light = light;
            this.state = light.getState();
            handler.postDelayed(runnable, 100);
        }
    }

    @Override
    public String createTag() {
        return PhilipsHueFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.PhilipsHue(),  null);
        binding = FragmentPhilipsHueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
        if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_HUE ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS
                ) {
            light = DevicesUtils.findLightFromIntentInfo(intentInfo);
        } else {
            light = (Light) intentInfo.getIntentPojo();
        }


        if (light != null) {
            state = light.getState();
        } else {
            if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON ||
                    intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF ||
                    intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS ||
                    intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_HUE ||
                    intentInfo.getAction() == Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS ||
                    intentInfo.getAction() == Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS) {

                getParentActivity().onRespondToUser("Their is no lights available");
                //TODO : Lights not available message to user
            }
        }
    }

    @Override
    public void initDagger() {
        PhilipsHueComponent component = DaggerPhilipsHueComponent.builder()
                .philipsHueModule(new PhilipsHueModule(this))
                .build();

        component.injectPhilipsHueFragment(this);
    }

    @Override
    public void bindViews(View view) {
        initView();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        featureFragmentCleanUps();
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    private void initView() {
        attachParent((MainActivity) getActivity());

        binding.fragmentCommonToolbar.commonToolbarTitle.setText(R.string.fragment_philips_toolbar_title);
        binding.fragmentPhilipsSeekBarBrightness.setMax(MAX_VALUE_SEEK_BAR);

        binding.fragmentPhilipsSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (state != null) {
                state.setOn(isChecked);
                light.setState(state);
                binding.hueControlLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                binding.offLightLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                /**
                 *  MAKE APICALL FOR UPDATING LIGHT AND EMIT LIGHT STATE TO CLIENT
                 */
                mPresenter.updateLight(light, LightConstants.LightState.MODE_ON);
                getParentActivity().onLightStateEmitSocket(buildJsonObject(light));
                LoggerWrapper.i("Light Data", " " + buildJsonObject(light).toString());
            }

        });


        binding.fragmentPhilipsSeekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                /**
                 *  MAKE APICALL FOR UPDATING LIGHT
                 */
                if (state != null) {
                    state.setBri(i);
                    light.setState(state);
                    mPresenter.updateLight(light, LightConstants.LightState.MODE_BRIGHTNESS);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /**
                 *   EMIT LIGHT STATE TO CLIENT
                 */
                getParentActivity().onLightStateEmitSocket(buildJsonObject(light));
            }
        });

        binding.colorpickerHueControl.setGradientView(R.drawable.color_picker);
        binding.colorpickerHueControl.setColorSelectedListener(this);

        binding.fragmentCommonToolbar.commonToolbarBack.setOnClickListener(view -> onBackClicked());
    }

    JsonObject buildJsonObject(Light light) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", WooHooApp.getSharedPrefsHelper().get(PREF_KEY_SESSION_TOKEN, ""));
        jsonObject.add("lightData", jsonParser.parse(gson.toJson(light)).getAsJsonObject());
        jsonObject.addProperty("from", "hub");
        return jsonObject;
    }

    @Override
    public void onColorSelected(int i, boolean isTapUp) {
        /**
         *  MAKE APICALL FOR UPDATING LIGHT AND EMIT LIGHT STATE TO CLIENT
         */
        if (light != null && state != null) {
            mPresenter.onColorChanged(light, i, isTapUp);
        }
    }

    void onClickRootView() { /* Do nothing here*/}

    void onBackClicked() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getContext(), message);
    }

    void onAddDeviceClicked() {

    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }
}
