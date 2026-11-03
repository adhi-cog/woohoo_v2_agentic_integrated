package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

import com.google.gson.JsonObject;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo.DeviceSettings;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo.TimeZoneUpdate;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Repository for {@link DeviceSettingsPresenter}.
 */
public class DeviceSettingsRepository extends BaseRepository<DeviceSettingsPresenter> {

    private WooHooApiCall mAPICall;
    private String deviceId;
    private CompositeDisposable compositeDisposable;

    @Inject
    public DeviceSettingsRepository(WooHooApiCall mAPICall, CompositeDisposable compositeDisposable) {
        this.mAPICall = mAPICall;
        this.compositeDisposable = compositeDisposable;
        deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }

    /*---------------------------------------------- Methods to Get Device Name -----------------------------------------------*/

    /**
     * Performs an API call for Get device name of User.
     */
    void fetchDeviceNameApiRequest() {
        compositeDisposable.add(
                mAPICall.getDeviceSetting(deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DeviceSettings>() {
                            @Override
                            public void onSuccess(DeviceSettings deviceSettings) {
                                if (deviceSettings.getData() != null && !deviceSettings.getData().getName().isEmpty()) {
                                    getActions().onDeviceNameSuccess(deviceSettings.getData().getName());
                                } else {
                                    getActions().onDeviceNameStatusError("No result fetched");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onDeviceNameStatusError(e.getMessage());
                            }
                        }));
    }

    /**
     * Performs an API call for Update device name of User.
     */
    void updateDeviceNameApiRequest(String name) {
        compositeDisposable.add(
                mAPICall.updateHubName(deviceId, name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getActions().onDeviceNameStatusSuccess();
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onDeviceNameStatusError(e.getMessage());
                            }
                        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

    public void updateTimeZone(String timeZone) {
        JsonObject rename = new JsonObject();
        rename.addProperty(LightConstants.JSON_KEY_TIME_ZONE, timeZone);

        compositeDisposable.add(
                mAPICall.updateTimeZone(deviceId, rename)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TimeZoneUpdate>() {
                            @Override
                            public void onSuccess(TimeZoneUpdate deviceSettings) {
                                if (deviceSettings.data != null) {
                                    getActions().onAPISuccess(deviceSettings.message);
                                } else {
                                    getActions().onAPIError("No result fetched");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPIError(e.getMessage());
                            }
                        }));
    }
}
