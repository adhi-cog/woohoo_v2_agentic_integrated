package com.sb.woohoo.ui.main.fragments.devices.discovery;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonObject;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDevice;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDeviceError;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDeviceResponse;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDeviceSuccess;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueLightContainer;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.register.PostLightData;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.utils.others.DevicesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * APIs
 * <p>
 * 1   . Search for bridge and get Access to bridge
 * 2   . Get connected Lights from bridge
 */
public class DeviceDiscoveryRepository extends BaseRepository<DeviceDiscoveryPresenter> {

    private static List<PhilipsHueDevice> searchedDevices;
    private final int SCAN_THRESHOLD = 2;
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;
    private int scanComplete = 0;
    private HashMap<String, String> hubAccess = new HashMap<>();
    private HashMap<String, String> hubAccessNew = new HashMap<>();
    // Hub contact Handlers
    private Handler hubCheckHandler = new Handler();
    private Runnable hubCheckRunnable = () -> {
        if (hubAccess.size() <= 0) {
            requestHubAuthorization();
        }
        scanComplete++;
    };

    @Inject
    public DeviceDiscoveryRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void searchDevicesAPICall() {
        findHueBridges();
    }

    //Get all the bridges from HUE API
    private void findHueBridges() {
        compositeDisposable.add(
                mApiCall.searchBridges(DevicesUtils.MEET_HUE_BASE_URL)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<PhilipsHueDevice>>() {
                            @Override
                            public void onSuccess(List<PhilipsHueDevice> philipsHueDevices) {
                                LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : https://discovery.meethue.com/");

                                if (philipsHueDevices != null) {
                                    DeviceDiscoveryRepository.searchedDevices = philipsHueDevices;
                                    requestHubAuthorization();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    // GET hub authorization from philips bridge.
    private void requestHubAuthorization() {
        for (final PhilipsHueDevice philipsHueDevice : searchedDevices) {
            String localDeviceUrl = LightConstants.HTTP
                    + philipsHueDevice.getInternalIpAddress()
                    + LightConstants.API;

            String userId= WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_USER_ID, "");

            String id=LightConstants.JSON_PROPERTY_DEVICE_VALUE+"_"+userId;
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty(LightConstants.JSON_PROPERTY_DEVICE_TYPE,id);

            compositeDisposable.add(
                    mApiCall.authorizationDevice(localDeviceUrl, requestBody)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<PhilipsHueDeviceResponse>>() {
                                @Override
                                public void onSuccess(List<PhilipsHueDeviceResponse> philipsHueDeviceResponses) {
                                    PhilipsHueDeviceResponse philipsHueDeviceResponse = philipsHueDeviceResponses.get(0);
                                    if (philipsHueDeviceResponse.getError() != null) {
                                        PhilipsHueDeviceError error = philipsHueDeviceResponse.getError();
                                        if (error != null) {
                                            getActions().onAPIError(error.getDescription());
                                            if (error.getType() == PhilipsHueDeviceError.DEVICE_NOT_AUTHORIZED) {
                                                if (scanComplete != SCAN_THRESHOLD) {
                                                    // Run a 90 second timer and ask user to push the button
                                                    int waitTime = 10 * 1000;
                                                    LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : Auth link click");
                                                    hubCheckHandler.postDelayed(hubCheckRunnable, waitTime);
                                                }
                                            }
                                        }
                                    } else if (philipsHueDeviceResponse.getSuccess() != null) {
                                        PhilipsHueDeviceSuccess success = philipsHueDeviceResponse.getSuccess();
                                        if (success != null) {
                                            String hubAuthorizationToken = success.getUsername();
                                            if (hubAuthorizationToken != null) {
                                                hubAccess.put(philipsHueDevice.getInternalIpAddress(), hubAuthorizationToken);
                                                LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : Auth sucess");
                                                getLightsFromBridge(philipsHueDevice, hubAuthorizationToken);

                                                //searchOfNewLightsApiCall(philipsHueDevice, hubAuthorizationToken);

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    getActions().onAPIError(e.getMessage());
                                }
                            }));
        }
    }

    // After getting bridge access get lights
    private void getLightsFromBridge(final PhilipsHueDevice philipsHueDevice, final String username) {
        final String url = LightConstants.HTTP
                + philipsHueDevice.getInternalIpAddress()
                + LightConstants.API
                + username;

        compositeDisposable.add(
                mApiCall.getDevicesFromHub(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PhilipsHueLightContainer>() {
                            @Override
                            public void onSuccess(PhilipsHueLightContainer philipsHueLightContainer) {

                                LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : Get All lights ");
                                searchOfNewLightsApiCall(philipsHueDevice, username);
                                getActions().onPhilipsHueLightDataFetched(philipsHueLightContainer,
                                        philipsHueDevice,
                                        username);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    void registerLightApiCall(Light light) {
        PostLightData postLightData = new PostLightData();
        postLightData.setUniqueid(light.getUniqueid());
        postLightData.setHubId(light.getHubId());
        postLightData.setLightId(light.getLightId());
        postLightData.setName(light.getName());
        postLightData.setState(light.getState());
        postLightData.setBridgeId(light.getBridgeId());
        postLightData.setBridgeUserId(light.getBridgeUserId());
        postLightData.setBridgeIp(light.getBridgeIp());
        postLightData.setType(light.getType());

        compositeDisposable.add(
                mApiCall.registerPhilipsHub(postLightData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getActions().registrationFinishCallback();
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }


    public void searchOfNewLightsApiCall(PhilipsHueDevice philipsHueDevice, String bridgeUserId) {

        String url = LightConstants.HTTP
                + philipsHueDevice.getInternalIpAddress()
                + LightConstants.API
                + bridgeUserId
                + LightConstants.LIGHTS_;
        compositeDisposable.add(
                mApiCall.searchLightFromHub(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                                LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : Search lights ");
                                int TIME = 3000;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        getNewLightsApiCall(philipsHueDevice, philipsHueDevice.getInternalIpAddress(), bridgeUserId);

                                    }
                                }, TIME);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));

    }

    public void getNewLightsApiCall(PhilipsHueDevice philipsHueDevice, String bridgeIp, String bridgeUserId) {

        String url = LightConstants.HTTP
                + bridgeIp
                + LightConstants.API
                + bridgeUserId
                + LightConstants.LIGHTS_
                + LightConstants.NEW;

        compositeDisposable.add(
                mApiCall.getNewLightFromHub(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                                LoggerWrapper.i(DeviceDiscoveryRepository.class.getSimpleName(),"**** API : GET New lights ");
                                getLightsFromBridge(philipsHueDevice, bridgeUserId);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));

    }
}