package com.sb.woohoo.ui.main.fragments.buysim.emailverification;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class EmailVerificationPresenter extends BasePresenter  implements EmailVerificationContract.Actions {

    private EmailVerificationRepository emailVerificationRepository;
    private EmailVerificationFragment mView;

    @Inject
    public EmailVerificationPresenter(EmailVerificationRepository emailVerificationRepository, EmailVerificationFragment mView) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.mView = mView;
        emailVerificationRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        emailVerificationRepository.repositoryCleanUps();
    }

    public void sendOtpToEmailId(String emailId) {
        emailVerificationRepository.sendOtpToEmailId(emailId);
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
