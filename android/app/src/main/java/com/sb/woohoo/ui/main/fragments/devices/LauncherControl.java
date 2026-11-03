package com.sb.woohoo.ui.main.fragments.devices;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDevice;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.GetAllDevices;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.utils.others.DevicesUtils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LauncherControl {

    private LauncherControlCallback launcherControlCallback;
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    public LauncherControl(WooHooApiCall wooHooApiCall,
                           CompositeDisposable compositeDisposable,
                           LauncherControlCallback launcherControlCallback) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
        this.launcherControlCallback = launcherControlCallback;
    }

    public void searchLights() {
        compositeDisposable.add(
                wooHooApiCall.getDevicesList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetAllDevices>() {
                            @Override
                            public void onSuccess(GetAllDevices getAllDevices) {
                                if (getAllDevices != null) {
                                    List<Light> lightList = getAllDevices.getData();
                                    if (lightList.isEmpty()) {
                                        launcherControlCallback.onSearchLightSuccess(lightList);
                                    } else {
                                        searchBridges(lightList);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                launcherControlCallback.onError(e.getMessage());
                            }
                        }));
    }

    private void searchBridges(List<Light> lightList) {
        compositeDisposable.add(
                wooHooApiCall.searchBridges(DevicesUtils.MEET_HUE_BASE_URL)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<PhilipsHueDevice>>() {
                            @Override
                            public void onSuccess(List<PhilipsHueDevice> philipsHueDevices) {
                                updateIPAddresses(philipsHueDevices, lightList);
                            }

                            @Override
                            public void onError(Throwable e) {
                                launcherControlCallback.onError(e.getMessage());
                            }
                        }));
    }

    private void updateIPAddresses(List<PhilipsHueDevice> bridges, List<Light> lightList) {
        if (lightList != null && !lightList.isEmpty()) {
            if (bridges != null && bridges.size() > 0) {
                for (final Light light : lightList) {
                    // Need to update new IP Address by bridgeId
                    for (PhilipsHueDevice bridge : bridges) {
                        if (bridge.getId().equalsIgnoreCase(light.getBridgeId())) {
                            light.setBridgeIp(bridge.getInternalIpAddress());
                            break;
                        }
                    }
                }
            }
            launcherControlCallback.onSearchLightSuccess(lightList);
        }
    }

    public interface LauncherControlCallback {
        void onSearchLightSuccess(List<Light> lightList);

        void onError(String message);
    }
}
