package com.sb.woohoo.ui.main.fragments.devices.launcher;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.LightIntentData;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.DevicesUtils;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class DeviceLauncherPresenter extends BasePresenter implements DeviceLauncherContract.Actions {

    private DeviceLauncherRepository mRepository;
    private DeviceLauncherFragment mView;

    @Inject
    DeviceLauncherPresenter(DeviceLauncherRepository deviceLauncherRepository, DeviceLauncherFragment deviceLauncherFragment) {
        this.mRepository = deviceLauncherRepository;
        this.mView = deviceLauncherFragment;
        mRepository.onAttach(this);
    }

    @Override
    public void onAPIError(String message) {
        mView.showMessage(message);
    }

    @Override
    public void onAPISuccess(String message) {
        mView.showMessage(message);
    }

    @Override
    public void updateLightName(Light light, String newName) {
        if (!newName.isEmpty()) {
            light.setName(newName);
        }
        mRepository.updateLightNameAPICall(light, newName);
    }

    void speakToUser(String message) {
        mView.respondToUser(message);
    }

    @Override
    public void deleteLight(Light light) {
        mRepository.deleteLightAPICall(light);
    }

    @Override
    public void onLightDateFetchSuccess(List<Light> lightList) {
        if (lightList != null && !lightList.isEmpty()) {
            mView.updateAvailableDevicesToAdapter(lightList);
        } else {
            mView.showEmptyLayout();
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        LightIntentData lightIntentData = new LightIntentData();
        int mode = -1;

        if (intentInfo == null) {
            mRepository.searchHueDevices(null,null);
            mView.showProgress();
        } else {
            HashMap<String, Object> intentParameters = intentInfo.getParameters();
            String name = "";
            if (intentParameters.size() != 0) {
                name = intentParameters.get(LightConstants.NLP_KEY_NAME).toString();
            }

            switch (intentInfo.getAction()) {
                case Categories.Devices.Light.LIGHTS_SHOW:
                    mRepository.searchHueDevices(intentInfo,null);
                    mView.showProgress();
                    break;
                case Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON: {
                    lightIntentData.setMode(LightConstants.LightState.MODE_ON);
                    if (!name.isEmpty()) {
                        lightIntentData.setName(name);
                    }
                    makeApiCall(intentInfo, lightIntentData);
                }
                break;
                case Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF: {
                    lightIntentData.setMode(LightConstants.LightState.MODE_OFF);
                    if (!name.isEmpty()) {
                        lightIntentData.setName(name);
                    }
                    makeApiCall(intentInfo, lightIntentData);
                }
                break;
                case Categories.Devices.Light.LIGHTS_ALL_HUE: {
                    lightIntentData.setMode(LightConstants.LightState.MODE_HUE);
                    String color = intentParameters.get(LightConstants.NLP_KEY_COLOR).toString();
                    if (!name.isEmpty()) {
                        lightIntentData.setName(name);
                    }
                    if (!color.isEmpty()) {
                        lightIntentData.setColor(color);
                    }
                    makeApiCall(intentInfo, lightIntentData);
                }
                break;
                case Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS:
                case Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS:
                case Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS: {
                    lightIntentData.setMode(LightConstants.LightState.MODE_BRIGHTNESS);
                    String percent = intentParameters.get("percentage").toString();
                    if (name != null && !name.isEmpty()) {
                        lightIntentData.setName(name);
                    }
                    if (percent != null && !percent.isEmpty()) {
                        lightIntentData.setPercentage(percent);
                    }
                    makeApiCall(intentInfo, lightIntentData);

                }
                break;

//                case Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS: {
//                    lightIntentData.setMode(LightConstants.LightState.MODE_BRIGHTNESS);
//                    String percent = intentParameters.get("percent").toString();
//                    if (name != null && !name.isEmpty()) {
//                        lightIntentData.setName(name);
//                    }
//                    if (percent != null && !percent.isEmpty()) {
//                        lightIntentData.setPercentage(percent);
//                    }
//                }
//                break;
//                case Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS: {
//
//                }
//                break;
            }

//            String color = intentParameters.get("color").toString();
//            String percentage = intentParameters.get("percentage").toString();

            //Categories.Devices.Light.LIGHTS_SHOW


//            if (color != null && !color.isEmpty()) {
//                lightIntentData.setName(color);
//            }
//
//            if (percentage != null && !percentage.isEmpty()) {
//                lightIntentData.setName(percentage);
//            }
        }

//        if (intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_ON ||
//                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_TOGGLE_OFF ||
//                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_BRIGHTNESS ||
//                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_ALL_HUE ||
//                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_INC_BRIGHTNESS ||
//                intentInfo.getAction() == Categories.Devices.Light.LIGHTS_DEC_BRIGHTNESS) {
//
//        }
    }

    private void makeApiCall(IntentInfo intentInfo, LightIntentData lightIntentData) {
//        if (name.equalsIgnoreCase(LightConstants.NLP_VALUE_ALL)) {
//            mRepository.searchHueDevices(lightIntentData);
//        } else {
            mRepository.searchHueDevices(intentInfo,lightIntentData);
//        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }

    void updateListItemToAdapter(Light light, int position) {
        mView.updateItemFromRecyclerView(light, position);
    }

    void updateListItemToAdapter(Light light) {
        mView.updateItemFromRecyclerView(light);
    }

    void deleteListItemToAdapter(Light light) {
        mView.deleteItemFromRecyclerView(light);

    }

    public void callPhilipsFrag(IntentInfo lightIntentData) {
        mView.callPhilipsFrag(lightIntentData);
    }
}

