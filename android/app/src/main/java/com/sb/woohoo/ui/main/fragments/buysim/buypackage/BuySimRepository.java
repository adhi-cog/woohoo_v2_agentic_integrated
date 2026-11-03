package com.sb.woohoo.ui.main.fragments.buysim.buypackage;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetAllPackagesResponse;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.pojo.OtpSendResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BuySimRepository extends BaseRepository<BuySimPresenter> {
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;
    @Inject
    public BuySimRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }


    public void makePackageApi() {
        compositeDisposable.add(
                wooHooApiCall.getAllPackages()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetAllPackagesResponse>() {
                            @Override
                            public void onSuccess(GetAllPackagesResponse response) {
                                int status = response.getStatus();
                                if (status == NETWORK_SUCCESS_CODE) {
                                    getActions().onSuccess(response.getData());
                                } else {
                                    getActions().errorMessage(status);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("Error",""+e.getMessage());
                                getActions().onErrorResponse(API_FAIL_ERROR_CODE);
                            }
                        }));
    }
}
