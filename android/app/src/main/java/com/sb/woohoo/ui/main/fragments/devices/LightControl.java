package com.sb.woohoo.ui.main.fragments.devices;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonObject;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.pojo.LightStateChangeError;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.pojo.LightStateChangeResponse;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LightControl {

    private final String TAG = LightControl.class.getSimpleName();

    private LightControlCallback lightControlCallback;

    public LightControl(LightControlCallback lightControlCallback) {
        this.lightControlCallback = lightControlCallback;
    }

    public void changeLightState(Light light, int mode, CompositeDisposable compositeDisposable, WooHooApiCall mApiCall) {

        String BASE_URL = LightConstants.HTTP
                + light.getBridgeIp()
                + LightConstants.API
                + light.getBridgeUserId()
                + LightConstants.LIGHTS
                + light.getLightId()
                + LightConstants.STATE;

        JsonObject requestBody = buildJsonObject(light, mode);

        compositeDisposable.add(
                mApiCall.changeLightState(BASE_URL, requestBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<LightStateChangeResponse>>() {

                            @Override
                            public void onSuccess(List<LightStateChangeResponse> lightStateChangeResponses) {
                                if (lightStateChangeResponses != null && !lightStateChangeResponses.isEmpty()) {
                                    LightStateChangeResponse lightStateChangeResponse = lightStateChangeResponses.get(0);
                                    if (null != lightStateChangeResponse.getSuccessMessage()) {
                                        lightControlCallback.onSuccess();
                                        LoggerWrapper.i(TAG, lightStateChangeResponse.getSuccessMessage().toString());
                                    } else if (null != lightStateChangeResponse.getError()) {
                                        LightStateChangeError errorContainer = lightStateChangeResponse.getError();
                                        if (errorContainer != null) {
                                            lightControlCallback.onError(lightStateChangeResponse.getError().getDescription());
                                            LoggerWrapper.e(TAG, lightStateChangeResponse.getError().getDescription());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                lightControlCallback.onError(e.getMessage());
                                LoggerWrapper.e(TAG, e.getMessage());
                            }
                        }));
    }

    private JsonObject buildJsonObject(Light light, int mode) {
        State lightState = light.getState();
        JsonObject requestBody = new JsonObject();
        if (mode == LightConstants.LightState.MODE_BRIGHTNESS) {
            requestBody.addProperty(LightConstants.JSON_KEY_BRI, lightState.getBri());
        } else if (mode == LightConstants.LightState.MODE_ON || mode == LightConstants.LightState.MODE_OFF) {
            requestBody.addProperty(LightConstants.JSON_KEY_ON, lightState.isOn());
        } else if (mode == LightConstants.LightState.MODE_HUE) {
            switch (light.getType()) {
                case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                    requestBody.addProperty(LightConstants.JSON_KEY_HUE, lightState.getHue());
                    break;

                case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                    requestBody.addProperty(LightConstants.JSON_KEY_CT, lightState.getCt());
                    break;
            }
        } else if (mode == LightConstants.LightState.FROM_SOCKET) {
            switch (light.getType()) {
                case LightConstants.LightType.EXTENDED_COLOR_LIGHT:
                    requestBody.addProperty(LightConstants.JSON_KEY_BRI, lightState.getBri());
                    requestBody.addProperty(LightConstants.JSON_KEY_ON, lightState.isOn());
                    requestBody.addProperty(LightConstants.JSON_KEY_HUE, lightState.getHue());
                    break;

                case LightConstants.LightType.COLOR_TEMPERATURE_LIGHT:
                    requestBody.addProperty(LightConstants.JSON_KEY_BRI, lightState.getBri());
                    requestBody.addProperty(LightConstants.JSON_KEY_ON, lightState.isOn());
                    requestBody.addProperty(LightConstants.JSON_KEY_CT, lightState.getCt());
                    break;
            }
        } else if (mode == LightConstants.LightState.MODE_RENAME) {
            requestBody.addProperty(LightConstants.JSON_KEY_NAME, light.getName());
        }
        return requestBody;
    }

    public interface LightControlCallback {
        void onSuccess();

        void onError(String message);
    }
}
