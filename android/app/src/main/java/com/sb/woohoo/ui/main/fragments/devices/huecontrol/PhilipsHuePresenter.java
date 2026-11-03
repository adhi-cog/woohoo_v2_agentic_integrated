package com.sb.woohoo.ui.main.fragments.devices.huecontrol;

import com.google.gson.Gson;
import com.sb.woohoo.custom.Colors;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.helpers.DeviceHelper;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.DevicesUtils;

import javax.inject.Inject;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class PhilipsHuePresenter extends BasePresenter implements PhilipsHueContract.Actions {

    @Inject
    Colors colors;
    @Inject
    DeviceHelper deviceHelper;
    private PhilipsHueFragment mView;
    private PhilipsHueRepository mRepository;
    private Gson mGson;

    @Inject
    PhilipsHuePresenter(PhilipsHueFragment mView, PhilipsHueRepository mRepository, Gson mGson) {
        this.mView = mView;
        this.mRepository = mRepository;
        this.mGson = mGson;
        mRepository.onAttach(this);
    }

    @Override
    public void updateLight(Light light, int mode) {
        mRepository.makeApiCall(light, mode);
    }

    @Override
    public int convertToHSV(int i) {
        float hsv[] = DevicesUtils.getHSV(i);
        return (int) hsv[0];
    }

    @Override
    public void onAPIError(String message) {
        mView.showMessage(message);
    }

    @Override
    public void lightStatusToSocket(Light light) {
        mView.getParentActivity().onLightStateEmitSocket(mView.buildJsonObject(light));
    }

    @Override
    public void onColorChanged(Light light, int i, boolean isTapUp) {
        State state = light.getState();
        int color;
        if (isTapUp) {
            color = convertToHSV(i);
            switch (light.getType()) {
                case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                    color = color * LightConstants.EXTENDED_COLOR_LIGHT_MUL_FACTOR;
                    state.setHue(color);
                    break;

                case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                    color = color * LightConstants.COLOR_TEMPERATURE_LIGHT_MUL_FACTOR;
                    state.setCt(LightConstants.COLOR_TEMPERATURE_LIGHT_SUB_FACTOR - color);
                    break;
            }
            light.setState(state);
            mRepository.makeApiCall(light, LightConstants.LightState.MODE_HUE);
            lightStatusToSocket(light);


        } else {
            color = convertToHSV(i);
            switch (light.getType()) {
                case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                    color = color * LightConstants.EXTENDED_COLOR_LIGHT_MUL_FACTOR;
                    state.setHue(color);
                    break;

                case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                    color = color * LightConstants.COLOR_TEMPERATURE_LIGHT_MUL_FACTOR;
                    state.setCt(LightConstants.COLOR_TEMPERATURE_LIGHT_SUB_FACTOR - color);
                    break;
            }
            light.setState(state);
            mRepository.makeApiCall(light, LightConstants.LightState.MODE_HUE);
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

        Light light;
        int hexColor;
        int hueColor;
        int mode = -1;

        if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_HUE ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS ||
                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS) {
            light = DevicesUtils.findLightFromIntentInfo(intentInfo);
        } else {
            light = (Light) intentInfo.getIntentPojo();
        }

        if (null != light) {
            State state = light.getState();
            switch (intentInfo.getAction()) {
                case LightConstants.LightState.FROM_SOCKET:
                    mode = LightConstants.LightState.FROM_SOCKET;
                    switch (light.getType()) {
                        case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                            hexColor = state.getHue();
                            hueColor = hexColor * LightConstants.EXTENDED_COLOR_LIGHT_MUL_FACTOR;
                            state.setHue(hueColor);
                            break;
                        case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                            break;
                    }
                    break;

                case Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF:
                    mode = LightConstants.LightState.MODE_OFF;
                    state.setOn(FALSE);
                    break;

                case Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON:
                    mode = LightConstants.LightState.MODE_ON;
                    state.setOn(TRUE);
                    break;

                case Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS:
                    mode = LightConstants.LightState.MODE_BRIGHTNESS;
                    if (intentInfo.getParameters().containsKey(LightConstants.NLP_KEY_PERCENTAGE)) {
                        String value = (String) intentInfo.getParameters()
                                .get(LightConstants.NLP_KEY_PERCENTAGE);

                        if (value != null) {
                            value = value.replaceAll(LightConstants.SYMBOL_PERCENT, "");
                            try {
                                int number = Integer.parseInt(String.valueOf(value));
                                double value2 = number * LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR;
                                state.setBri((int) value2);
                            } catch (NumberFormatException e) {
                                mView.respondToUser(LightConstants.LIGHT_BRIGHTNESS_VALUE_ERROR_MSG);
                            }
                        }
                    }
                    break;

                case Categories.Devices.Light.LIGHTS_ALL_HUE:
                    mode = LightConstants.LightState.MODE_HUE;
                    if (intentInfo.getParameters().containsKey(LightConstants.NLP_KEY_COLOR)) {
                        String value = (String) intentInfo.getParameters().get(LightConstants.NLP_KEY_COLOR);
                        if (value != null) {
                            if (colors.getColorCode(value) != null) {
                                if (LightConstants.LightType.EXTENDED_COLOR_LIGHT.equalsIgnoreCase(light.getType())) {
                                    int colorValue = Integer.parseInt(colors.getColorCode(value));
                                    state.setHue(colorValue);
                                } else {
                                    // FIXME : Static colors added
                                    if (value.equalsIgnoreCase(LightConstants.NLP_VALUE_COLOR_BLUE)) {
                                        state.setCt(LightConstants.COLOR_BLUE_VALUE);
                                    } else if (value.equalsIgnoreCase(LightConstants.NLP_VALUE_COLOR_YELLOW)) {
                                        state.setCt(LightConstants.COLOR_YELLOW_VALUE);
                                    } else {
                                        // FIXME : Handle this edge case : This is White ambiance light. Can't change Colour
                                    }
                                }
                            }
                        }
                    }
                    break;

                case Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS:
                    mode = LightConstants.LightState.MODE_BRIGHTNESS;
                    if (intentInfo.getParameters().containsKey(LightConstants.NLP_KEY_PERCENTAGE)) {
                        String value = (String) intentInfo.getParameters().get(LightConstants.NLP_KEY_PERCENTAGE);
                        if (value != null) {
                            value = value.replaceAll(LightConstants.SYMBOL_PERCENT, "");
                            try {
                                double oldBri = state.getBri();
                                int number = Integer.parseInt(String.valueOf(value));
                                double value2 = number * LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR;
                                state.setBri((int) (value2 + oldBri));
                                state.setOn(true);

                            } catch (NumberFormatException e) {
                                state.setBri((int)
                                        (LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_FALLBACK_MUL_FACTOR
                                                * LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR));
                                state.setOn(true);
                            }
                        }
                    }
                    break;

                case Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS:
                    mode = LightConstants.LightState.MODE_BRIGHTNESS;
                    if (intentInfo.getParameters().containsKey(LightConstants.NLP_KEY_PERCENTAGE)) {
                        String value = (String) intentInfo.getParameters().get(LightConstants.NLP_KEY_PERCENTAGE);
                        if (value != null) {
                            value = value.replaceAll(LightConstants.SYMBOL_PERCENT, "");
                            try {
                                double oldBri = state.getBri();
                                int number = Integer.parseInt(String.valueOf(value));
                                double value2 = number * LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR;
                                state.setBri((int) (oldBri - value2));
                                state.setOn(true);

                            } catch (NumberFormatException e) {
                                state.setBri(1);
                                state.setOn(true);
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
            state.setSat(LightConstants.COLOR_UNV_VALUE);
            light.setState(state);
            mView.setUIComponents(light);
            updateLight(light, mode);
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}
