package com.sb.woohoo.ui.main.fragments.buysim.otpverification;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.API_FAIL_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.util.Log;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo.EmailIdOtpRequest;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo.OtpVerifyResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OtpVerificationRepository extends BaseRepository<OtpVerificationPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public OtpVerificationRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }


    public void verifyOtp(String otp, String email) {
        EmailIdOtpRequest request = new EmailIdOtpRequest();
        request.setEmail(email);
        request.setOtp(otp);
        compositeDisposable.add(
                wooHooApiCall.otpVerify(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OtpVerifyResponse>() {
                            @Override
                            public void onSuccess(OtpVerifyResponse response) {
                                int status = response.getStatus();
                                if (status == NETWORK_SUCCESS_CODE) {
                                    getActions().onSuccess();
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
