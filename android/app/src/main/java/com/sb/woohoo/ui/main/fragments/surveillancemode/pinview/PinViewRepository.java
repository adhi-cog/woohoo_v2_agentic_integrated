package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.pojo.SurveillanceModeResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class PinViewRepository extends BaseRepository<PinViewPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public PinViewRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void setUserPin(String pin) {
        compositeDisposable.add(
                wooHooApiCall.setUserPin(pin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                setHubInSurveillanceMode(pin, true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
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
                                if (surveillanceModeResponse != null) {
                                    getActions().onHubModeInSurveillanceSuccess(surveillanceModeResponse.getData().getData().getSurveillanceMode());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException httpException = (HttpException) e;
                                    int statusCode = httpException.code();
                                    if (statusCode == 400) {
                                        getActions().onHubModeChangeFailed();
                                    }
                                } else {
                                    getActions().onAPICallError(e.getMessage());
                                }
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
