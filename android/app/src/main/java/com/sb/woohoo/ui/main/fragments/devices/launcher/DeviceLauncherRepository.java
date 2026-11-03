package com.sb.woohoo.ui.main.fragments.devices.launcher;

import com.google.gson.JsonObject;
import com.sb.woohoo.custom.Colors;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.devices.LauncherControl;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.LightControl;
import com.sb.woohoo.ui.main.fragments.devices.LightIntentData;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo.LightFullInfo;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo.LightInfoState;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.DevicesUtils;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * APIs
 * <p>
 * 1   . Get register lights from server
 * 2   . After 1. Update IP addresses to every lights
 * 3   . After 2. Update reachable status to every light
 * 4   . Update light name
 * 5   . Delete light
 */
public class DeviceLauncherRepository extends BaseRepository<DeviceLauncherPresenter> {

    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;
    private LightIntentData lightIntentData;

    @Inject
    DeviceLauncherRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void searchHueDevices(IntentInfo intentInfo,LightIntentData lightIntentData) {
        this.lightIntentData = lightIntentData;
        new LauncherControl(mApiCall, compositeDisposable, new LauncherControl.LauncherControlCallback() {
            @Override
            public void onSearchLightSuccess(List<Light> lightList) {
                getActions().onLightDateFetchSuccess(lightList);
                if (lightList != null && !lightList.isEmpty()) {
                    updateReachableStatus(intentInfo,lightList);
                } else {
                    if (lightIntentData != null) {
                        getActions().speakToUser(LightConstants.ALL_LIGHTS_NOT_REG);
                    }
                }
            }

            @Override
            public void onError(String message) {
                getActions().onAPIError(message);
            }
        }).searchLights();
    }

    private void updateReachableStatus(IntentInfo intentInfo, List<Light> lightList) {
        if (lightIntentData != null && !lightIntentData.getName().equalsIgnoreCase(LightConstants.NLP_VALUE_ALL)) {
            Light light = searchItem(lightList);
            if (light != null) {
                callPhilipsControl(intentInfo,light);
            } else {
                getActions().speakToUser(LightConstants.LIGHT_NOT_REG);
            }
        } else {
            for (int i = 0; i < lightList.size(); i++) {
                reachableStatusAPICall(intentInfo,lightList.get(i), i);
            }
        }
    }

    private synchronized void reachableStatusAPICall(IntentInfo intentInfo, Light light, int position) {

        String url = LightConstants.HTTP
                + light.getBridgeIp()
                + LightConstants.API
                + light.getBridgeUserId()
                + LightConstants.LIGHTS + light.getLightId();

        compositeDisposable.add(
                mApiCall.getLightStatus(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LightFullInfo>() {
                            @Override
                            public void onSuccess(LightFullInfo lightFullInfo) {
                                light.setName(lightFullInfo.getName());
                                LightInfoState state = lightFullInfo.getState();
                                light.getState().setAlert(state.getAlert());
                                light.getState().setBri(state.getBri());
                                light.getState().setColormode(state.getColormode());
                                light.getState().setCt(state.getCt());
                                light.getState().setOn(state.getOn());
                                light.getState().setReachable(state.getReachable());

                                if (lightIntentData != null) {
                                    if (lightIntentData.getName().equalsIgnoreCase(LightConstants.NLP_VALUE_ALL)) {
                                        if (light.getState().isReachable()) {
                                            callPhilipsControl(intentInfo,light);
                                        }
                                    }
                                } else {
                                    getActions().updateListItemToAdapter(light, position);
                                }
                                updateLightDataAPICall(light);
                            }

                            @Override
                            public void onError(Throwable e) {
                                MethodUtils.printLog(DeviceLauncherRepository.class, e.getMessage());
                                light.getState().setReachable(false);
                                getActions().updateListItemToAdapter(light, position);
                                updateLightDataAPICall(light);
                            }
                        }));
    }

    private void callPhilipsControl(IntentInfo intentInfo,Light light) {
        if (light.getState().isReachable()) {
            switch (lightIntentData.getMode()) {
                case LightConstants.LightState.MODE_OFF:
                    light.getState().setOn(false);
                    break;
                case LightConstants.LightState.MODE_ON:
                    light.getState().setOn(true);
                    break;
                case LightConstants.LightState.MODE_HUE: {
                    String value = lightIntentData.getColor();
                    Colors colors = new Colors();
                    if (colors.getColorCode(value) != null) {
                        if (LightConstants.LightType.EXTENDED_COLOR_LIGHT.equalsIgnoreCase(light.getType())) {
                            int colorValue = Integer.parseInt(colors.getColorCode(value));
                            light.getState().setHue(colorValue);
                        } else {
                            if (value.equalsIgnoreCase(LightConstants.NLP_VALUE_COLOR_BLUE)) {
                                light.getState().setCt(LightConstants.COLOR_BLUE_VALUE);
                            } else if (value.equalsIgnoreCase(LightConstants.NLP_VALUE_COLOR_YELLOW)) {
                                light.getState().setCt(LightConstants.COLOR_YELLOW_VALUE);
                            } else {
                                //TODO This is White ambiance light. Can't change Colour
                                getActions().speakToUser(LightConstants.LIGHT_HUE_VALUE_ERROR_MSG);
                            }
                        }
                    }
                }
                break;
                case LightConstants.LightState.MODE_BRIGHTNESS:

                    String value = lightIntentData.getPercentage();

                    if (value != null) {
                        value = value.replaceAll(LightConstants.SYMBOL_PERCENT, "");
                        try {
                            int number = Integer.parseInt(String.valueOf(value));
                            double value2 = number * LightConstants.COLOR_TEMPERATURE_BRIGHTNESS_MUL_FACTOR;
                            light.getState().setBri((int) value2);
                        } catch (NumberFormatException e) {
                            getActions().speakToUser(LightConstants.LIGHT_BRIGHTNESS_VALUE_ERROR_MSG);
                        }
                    }
                    break;
            }

//            if (lightIntentData.getName() != null) {
//                LightControl lightControl = new LightControl(new LightControl.LightControlCallback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError(String message) {
//
//                    }
//                });
//                lightControl.changeLightState(light, lightIntentData.getMode(), compositeDisposable, mApiCall);
//            }

            DevicesUtils.updateLightStatus(light);
            getActions().callPhilipsFrag(intentInfo);

        } else {
            if (lightIntentData != null) {
                getActions().speakToUser(light.getName() + LightConstants.LIGHT_NOT_REACHABLE);
            }
        }
    }

    private void updateLightDataAPICall(Light light) {
        Light newLight = new Light();
        newLight.setName(light.getName());

        State state = new State();

        state.setAlert(light.getState().getAlert());
        state.setBri(light.getState().getBri());
        state.setColormode(light.getState().getColormode());
        state.setCt(light.getState().getCt());
        state.setOn(light.getState().isOn());
        state.setReachable(light.getState().isReachable());

        newLight.setState(state);

        compositeDisposable.add(
                mApiCall.updateLightData(light.getId(), newLight)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    void updateLightNameAPICall(Light light, String newName) {

        String url = LightConstants.HTTP
                + light.getBridgeIp()
                + LightConstants.API
                + light.getBridgeUserId()
                + LightConstants.LIGHTS
                + light.getLightId();

        JsonObject rename = new JsonObject();
        rename.addProperty(LightConstants.JSON_KEY_NAME, newName);

        compositeDisposable.add(
                mApiCall.renameLightFromHub(url, rename)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                light.setName(newName);
                                updateLightNameInDb(light, newName);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));

    }

    private void updateLightNameInDb(Light light, String newName) {

        JsonObject rename = new JsonObject();
        rename.addProperty(LightConstants.JSON_KEY_NAME, newName);

        compositeDisposable.add(
                mApiCall.updateLightName(light.getId(), rename)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                getActions().updateListItemToAdapter(light);
                                getActions().onAPISuccess(LightConstants.LIGHT_NAME_UPDATE_MSG);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    void deleteLightAPICall(Light light) {

        String url = LightConstants.HTTP
                + light.getBridgeIp()
                + LightConstants.API
                + light.getBridgeUserId()
                + LightConstants.LIGHTS
                + light.getLightId();


        compositeDisposable.add(
                mApiCall.deleteLightFromHub(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                deleteLightServerAPICall(light);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));

    }

    void deleteLightServerAPICall(Light light) {
        compositeDisposable.add(
                mApiCall.deleteLight(light.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getActions().deleteListItemToAdapter(light);
                                getActions().onAPISuccess(LightConstants.LIGHT_DELETE_MSG);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));

    }

    private Light searchItem(List<Light> lightList) {
        for (Light light : lightList) {
            if (light.getName() != null && light.getName().equalsIgnoreCase(lightIntentData.getName())) {
                return light;
            }
        }
        return null;
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}
