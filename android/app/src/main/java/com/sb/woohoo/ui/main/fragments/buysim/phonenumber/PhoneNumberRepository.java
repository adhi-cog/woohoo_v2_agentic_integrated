package com.sb.woohoo.ui.main.fragments.buysim.phonenumber;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetAllPackagesResponse;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetAllPhoneNumbersResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PhoneNumberRepository extends BaseRepository<PhoneNumberPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public PhoneNumberRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }


    public void getAllPhoneNumbers() {
        compositeDisposable.add(
                wooHooApiCall.getAllPhoneNumber()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<GetAllPhoneNumbersResponse>() {
                            @Override
                            public void onSuccess(GetAllPhoneNumbersResponse response) {
                                Log.e("Response",""+response.getData().size());
                                int status = response.getStatus();
                                if (status == NETWORK_SUCCESS_CODE) {
                                    if (!response.getData().isEmpty()) {
                                        getActions().onSuccess(response.getData());
                                    } else {
                                        getActions().errorMessage(EMPTY_RESULT_CODE);
                                    }
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
