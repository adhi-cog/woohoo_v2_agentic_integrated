package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger;

import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.google.gson.JsonSyntaxException;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.pojo.SurveillanceModeResponse;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class SurveillanceTriggerRepository extends BaseRepository<SurveillanceTriggerPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public SurveillanceTriggerRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void setHubInSurveillanceMode(String mPin, boolean isInSurveillanceMode) {
        String hubId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        compositeDisposable.add(
                wooHooApiCall.changeHubModeToSurveillance(hubId, isInSurveillanceMode, mPin, "hub")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SurveillanceModeResponse>() {

                            @Override
                            public void onSuccess(SurveillanceModeResponse surveillanceModeResponse) {
                                if (surveillanceModeResponse.getData() != null) {
                                    getActions().onHubModeInSurveillanceSuccess(surveillanceModeResponse.getData().getData().getSurveillanceMode());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                String errors[] = new String[10];
                                String errorBody;
                                if (e instanceof HttpException) {
                                    try {
                                        if (((HttpException) e).response().errorBody() != null) {
                                            errorBody = ((HttpException) e).response().errorBody().string();
                                            errors = errorBody.split(",");
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (e instanceof JsonSyntaxException) {
                                    LoggerWrapper.e(SurveillanceTriggerRepository.class.getSimpleName(), "getErrorMessage: " + e.toString());
                                } else {
                                    LoggerWrapper.e(SurveillanceTriggerRepository.class.getSimpleName(), "getErrorMessage: " + e.toString());
                                }
                                getActions().onError(errors[1]);
                            }
                        }));
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        wooHooApiCall = null;
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}
