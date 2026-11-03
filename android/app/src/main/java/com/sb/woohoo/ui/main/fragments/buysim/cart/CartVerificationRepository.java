package com.sb.woohoo.ui.main.fragments.buysim.cart;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.BuyPackageRequest;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.BuyPackageResponse;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo.OtpVerifyResponse;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetAllPhoneNumbersResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartVerificationRepository extends BaseRepository<CartVerificationPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public CartVerificationRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }


    public void callBuyPackageApi(String email, GetEachPackageDetails packageId, GetEachPackageDetails id) {
        BuyPackageRequest buyPackageRequest  = new BuyPackageRequest();
        buyPackageRequest.setEmail(email);
        buyPackageRequest.setPackageId(packageId.getId());
        buyPackageRequest.setNumberId(id.getId());
        compositeDisposable.add(
                wooHooApiCall.callBuyPackageApi(buyPackageRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BuyPackageResponse>() {
                            @Override
                            public void onSuccess(BuyPackageResponse response) {
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
