package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SurveillanceRepository extends BaseRepository<SurveillancePresenter> {

    private final CompositeDisposable compositeDisposable;
    private WooHooApiCall apicall;
    private String deviceId;

    @Inject
    public SurveillanceRepository(Retrofit retrofit, CompositeDisposable compositeDisposable) {
        apicall = retrofit.create(WooHooApiCall.class);
        deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        this.compositeDisposable = compositeDisposable;
    }

    /*-------------------------------------------UPDATE HUB STATUS------------------------------------*/
    public void updateHubStatus(boolean isBusy) {
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");

//        compositeDisposable.add(
//                apicall.updateHubStatus(deviceId, isBusy)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableCompletableObserver() {
//
//                            @Override
//                            public void onComplete() {
//                                getActions().onHubStatusUpdateSuccess();
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                getActions().onHubStatusUpdateError(e.getMessage());
//                            }
//                        }));
    }

    /****************************{@STOP SURVEILLANCE}***********************************************/

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}
