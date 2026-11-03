package com.sb.woohoo.ui.main.fragments.devices.discovery;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDevice;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueLightContainer;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DeviceDiscoveryPresenter extends BasePresenter implements DeviceDiscoveryContract.Actions {


    private DeviceDiscoveryRepository mRepository;
    private DeviceDiscoveryFragment mView;
    private List<Light> lightList;

    @Inject
    DeviceDiscoveryPresenter(DeviceDiscoveryRepository deviceDiscoveryRepository, DeviceDiscoveryFragment deviceDiscoveryFragment) {
        this.mRepository = deviceDiscoveryRepository;
        this.mView = deviceDiscoveryFragment;
        mRepository.onAttach(DeviceDiscoveryPresenter.this);
        lightList = new ArrayList<>();
    }

    @Override
    public void onAPIError(String message) {
        mView.showErrorMessage(message);
    }

    @Override
    public void onPhilipsHueLightDataFetched(PhilipsHueLightContainer lightContainer,
                                             PhilipsHueDevice philipsHueDevice,
                                             String username) {

        lightList = new ArrayList<>();
        // Update hubId and Bridge data to light object
        String hubID = WooHooApp
                .getSharedPrefsHelper()
                .get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null);

        if (lightContainer != null) {
            for (Map.Entry<String, Light> lights : lightContainer.getLights().entrySet()) {

                String lightId = lights.getKey();
                Light light = lights.getValue();

                // Set light and Hub ids to light
                light.setLightId(lightId);
                light.setHubId(hubID);
                light.setBridgeId(philipsHueDevice.getId());
                light.setBridgeIp(philipsHueDevice.getInternalIpAddress());
                light.setBridgeUserId(username);
                light.setState(light.getState());

                // Add light to available register light-list
                lightList.add(light);

                // Update to adapter
                mView.updateLightsAdapter(lightList);

                //Ask user to register light if its from voice command
                IntentInfo intentInfo = getIntentInfo();
                if (intentInfo != null) {
                    if (getIntentInfo().getAction() == Categories.Devices.Light.LIGHTS_REGISTER) {
                        if (mView.mAdapter.getItemCount() > 0) {
                            mView.getParentActivity().askFollowUpQuery(LightConstants.LIGHT_REG_UTTERANCE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        if (intentInfo != null) {
            switch (intentInfo.getAction()) {
                case Categories.Devices.Light.LIGHTS_SEARCH:
                    mRepository.searchDevicesAPICall();
                    break;

                case Categories.Devices.Light.LIGHTS_REGISTER_AUTO:
                    if (intentInfo.getParameters().containsKey(LightConstants.NLP_PARAM_KEY_INDEX)) {
                        String value = (String) intentInfo.getParameters()
                                .get(LightConstants.NLP_PARAM_KEY_INDEX);

                        if (value != null) {
                            int index = Integer.parseInt(String.valueOf(value));
                            if (mView.mAdapter.getItemCount() >= index) {
                                Light light = mView.mAdapter.getItemAt(index - 1);
                                mRepository.registerLightApiCall(light);
                            } else {
                                mView.respondToUser(LightConstants.LIGHT_SELECT_UTTERANCE);
                            }
                        }
                    }
                    break;
            }
        } else {
            mRepository.searchDevicesAPICall();
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }

    void registrationFinishCallback() {
        mView.onRegisterFinished();
    }

    void registerLight(Light light) {
        mRepository.registerLightApiCall(light);
    }
}