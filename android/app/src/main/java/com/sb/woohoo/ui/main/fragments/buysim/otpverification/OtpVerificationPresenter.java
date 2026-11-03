package com.sb.woohoo.ui.main.fragments.buysim.otpverification;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class OtpVerificationPresenter extends BasePresenter  implements OtpVerificationContract.Actions {

    private OtpVerificationRepository otpVerificationRepository;
    private OtpVerificationFragment mView;

    @Inject
    public OtpVerificationPresenter(OtpVerificationRepository otpVerificationRepository, OtpVerificationFragment mView) {
        this.otpVerificationRepository = otpVerificationRepository;
        this.mView = mView;
        otpVerificationRepository.onAttach(this);
    }


    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        otpVerificationRepository.repositoryCleanUps();
    }

    public void verifyOtp(String otp, String email) {
        otpVerificationRepository.verifyOtp(otp,email);
        mView.showProgress();
    }

    @Override
    public void onSuccess() {
        mView.onSuccess();
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int emptyResultCode) {
        mView.errorMessage(emptyResultCode);
        mView.hideProgress();
    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {
        mView.onErrorResponse(apiFailErrorCode);
        mView.hideProgress();
    }
}
