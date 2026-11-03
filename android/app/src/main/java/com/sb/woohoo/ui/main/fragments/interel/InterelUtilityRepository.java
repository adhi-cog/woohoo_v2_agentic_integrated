package com.sb.woohoo.ui.main.fragments.interel;

import android.text.TextUtils;
import android.util.Log;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtilityResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.InterelUtilityResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UpdateDynamicTypeUtilityRequest;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UpdateDynamicUtilityRequest;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UtilityUpdationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.Activate;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.BathroomOff;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.BathroomOn;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.BedroomStripLightOff;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.BedroomStripLightOn;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.Close;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.CurtainClose;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.CurtainOpen;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.Deactivate;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.MasterOff;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.MasterOn;
import static com.sb.woohoo.ui.main.fragments.interel.InterelUtilityContract.CONSTANT.Open;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION.FAN_MODE;
import static com.sb.woohoo.utils.Constants.ROOM_AUTOMATION.ROOM_AUTOMATION;

public class InterelUtilityRepository extends BaseRepository<InterelUtilityPresenter> {

    private CompositeDisposable compositeDisposable;
    private WooHooApiCall wooHooApiCall;
    private SharedPrefsHelper sharedPrefsHelper;
    private String hubId;

    @Inject
    public InterelUtilityRepository(CompositeDisposable compositeDisposable, WooHooApiCall wooHooApiCall,
                                    SharedPrefsHelper sharedPrefsHelper) {
        this.compositeDisposable = compositeDisposable;
        this.wooHooApiCall = wooHooApiCall;
        this.sharedPrefsHelper = sharedPrefsHelper;
        hubId = sharedPrefsHelper.get(PREF_KEY_DEVICE_ID, null);
    }

    /**************************************************{@GET_ROOM_STATE}*************************************/
    public void getRoomState() {
        compositeDisposable.add(
                wooHooApiCall.getRoomState(hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<InterelUtilityResponse>() {
                            @Override
                            public void onSuccess(InterelUtilityResponse interelUtilityResponse) {
                                if (interelUtilityResponse == null) {
                                    getActions().onAPICallError("Response Corrupted");
                                } else {
                                    onUtilityFetched(interelUtilityResponse);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    private void onUtilityFetched(InterelUtilityResponse interelUtilityResponse) {
        if (interelUtilityResponse.getStatus() == 200) {
            getActions().onUtilityStatusFetched(interelUtilityResponse.getData());
        } else {
            getActions().onAPICallError(interelUtilityResponse.getError());
        }
    }

    /*************************************{@CHANGE_UTILITIES_MODE}*******************************************/

    void setDNDMode(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setDNDMode(status ? Activate : Deactivate, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                if (utilityUpdationResponse == null) {
                                    getActions().onAPICallError("Response Corrupted");
                                } else {
                                    onUtilityUpdationSuccess(utilityUpdationResponse);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setMURMode(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setMurMode(status ? Activate : Deactivate, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }


    public void setCurtain(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setCurtainState(status ? CurtainOpen : CurtainClose, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }


    public void setBlind(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setBlindState(status ? Open : Close, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }


    public void changeSetPoint(Float tempPoint) {
        Integer temp = Math.round(tempPoint);
        if (temp < 18) {
            temp = 18;
        } else if (temp > 28) {
            temp = 28;
        }
        compositeDisposable.add(
                wooHooApiCall.changeThermostatSetPoint(temp, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void changeFanMode(String fanMode, int id) {
        UpdateDynamicTypeUtilityRequest updateDynamicTypeUtilityRequest = new UpdateDynamicTypeUtilityRequest();
        updateDynamicTypeUtilityRequest.setType(ROOM_AUTOMATION);
        UpdateDynamicTypeUtilityRequest.Item item = new UpdateDynamicTypeUtilityRequest.Item();
        item.setId(id);
        item.setType(FAN_MODE);
        item.setValue(fanMode);
        List<UpdateDynamicTypeUtilityRequest.Item> itemsList = new ArrayList<>();
        itemsList.add(item);
        updateDynamicTypeUtilityRequest.setItems(itemsList);
        compositeDisposable.add(
                wooHooApiCall.updateDynamicUtilityType(updateDynamicTypeUtilityRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                try {
                                    onUtilityUpdationSuccess(utilityUpdationResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setMasterOnOff(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setMasterOnOff(status ? MasterOn : MasterOff, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setMasterOnOff(boolean status, String enable, String disable) {
        compositeDisposable.add(
                wooHooApiCall.setMasterOnOff(status ? enable : disable, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setBathroomLightOnOff(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setMasterOnOff(status ? BathroomOn : BathroomOff, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setToggleLight(boolean status) {
        compositeDisposable.add(
                wooHooApiCall.setToggleLight(status ? BedroomStripLightOn : BedroomStripLightOff, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void setToggleLight(String status) {
        compositeDisposable.add(
                wooHooApiCall.setToggleLight(status, hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                onUtilityUpdationSuccess(utilityUpdationResponse);

                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }


    private void onUtilityUpdationSuccess(UtilityUpdationResponse utilityUpdationResponse) {
        if (utilityUpdationResponse.getStatus() == 200) {
            if (!TextUtils.isEmpty(utilityUpdationResponse.getMessage()))
                getActions().onUpdateModeSuccess(utilityUpdationResponse.getMessage());
            else
                getActions().onUpdateModeSuccess(utilityUpdationResponse.getData());
        } else {
            getActions().onAPICallError(utilityUpdationResponse.getError());
        }
    }

    public void getDynamicUtilities() {
        compositeDisposable.add(
                wooHooApiCall.getInterelDynamicUtilities(ROOM_AUTOMATION)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DynamicUtilityResponse>() {
                            @Override
                            public void onSuccess(DynamicUtilityResponse dynamicUtilityResponse) {
//                                onUtilityUpdationSuccess(utilityUpdationResponse);
                                try {
                                    if (dynamicUtilityResponse.getStatus() == 200) {
                                        getActions().getDynamicUtilitiesResponse(dynamicUtilityResponse.getData());
                                    } else {
                                        getActions().onAPICallError(dynamicUtilityResponse.getError());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void updateDynamicUtility(String currentStateId) {
        UpdateDynamicUtilityRequest updateDynamicUtilityRequest = new UpdateDynamicUtilityRequest();
        updateDynamicUtilityRequest.setType(ROOM_AUTOMATION);
        UpdateDynamicUtilityRequest.Item item = new UpdateDynamicUtilityRequest.Item();
        item.setId(currentStateId);
        List<UpdateDynamicUtilityRequest.Item> itemsList = new ArrayList<>();
        itemsList.add(item);
        updateDynamicUtilityRequest.setItems(itemsList);
        compositeDisposable.add(
                wooHooApiCall.updateDynamicUtility(updateDynamicUtilityRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                try {
                                    onUtilityUpdationSuccess(utilityUpdationResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("responseUtility", "updateDynamicUtility - error - " + e.getMessage());
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    public void updateTemperature(String currentStateId, int temperature) {
        UpdateDynamicUtilityRequest updateDynamicUtilityRequest = new UpdateDynamicUtilityRequest();
        updateDynamicUtilityRequest.setType(ROOM_AUTOMATION);
        UpdateDynamicUtilityRequest.Item item = new UpdateDynamicUtilityRequest.Item();
        item.setId(currentStateId);
        item.setTemperature(temperature);
        List<UpdateDynamicUtilityRequest.Item> itemsList = new ArrayList<>();
        itemsList.add(item);
        updateDynamicUtilityRequest.setItems(itemsList);
        compositeDisposable.add(
                wooHooApiCall.updateDynamicUtility(updateDynamicUtilityRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                try {
//                                    onUtilityUpdationSuccess(utilityUpdationResponse);
                                    onTemperatureUpdated(utilityUpdationResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("responseUtility", "updateDynamicUtility - error - " + e.getMessage());
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    private void onTemperatureUpdated(UtilityUpdationResponse utilityUpdationResponse) {
        if (utilityUpdationResponse.getStatus() == 200) {
            if (!TextUtils.isEmpty(utilityUpdationResponse.getMessage()))
                getActions().onTemperatureUpdated(utilityUpdationResponse.getMessage());
            else
                getActions().onTemperatureUpdated(utilityUpdationResponse.getData());
        } else {
            getActions().onAPICallError(utilityUpdationResponse.getError());
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }


}
