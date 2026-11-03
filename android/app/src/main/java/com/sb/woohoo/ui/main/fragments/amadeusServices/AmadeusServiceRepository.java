package com.sb.woohoo.ui.main.fragments.amadeusServices;

import android.util.Log;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusDynamicOrderRequest;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrdersResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UtilityUpdationResponse;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class AmadeusServiceRepository
        extends BaseRepository<AmadeusServicePresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;
    private String hubID;

    @Inject
    public AmadeusServiceRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
        hubID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null);
    }

    public void getDynamicOrders() {
        compositeDisposable.add(
                wooHooApiCall.getAllDynamicOrders().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<AmadeusOrdersResponse>() {

                    @Override
                    public void onSuccess(@NonNull AmadeusOrdersResponse amadeusOrdersResponse) {
                        if (amadeusOrdersResponse.getStatus() == 200) {
                            getActions().onOrdersFetched(amadeusOrdersResponse.getData());
                        } else {
                            getActions().onApiError(amadeusOrdersResponse.getError());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("orders_response", e.getMessage());
                    }
                }));
    }

    public void createDynamicOrder(AmadeusDynamicOrderRequest amadeusDynamicOrderRequest) {
        compositeDisposable.add(
                wooHooApiCall.createAmadeusDynamicOrder(amadeusDynamicOrderRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UtilityUpdationResponse>() {
                            @Override
                            public void onSuccess(UtilityUpdationResponse utilityUpdationResponse) {
                                getActions().onOrderCreated(utilityUpdationResponse.getData());
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                getActions().onApiError(e.getMessage());
                            }
                        })
        );
    }
}
