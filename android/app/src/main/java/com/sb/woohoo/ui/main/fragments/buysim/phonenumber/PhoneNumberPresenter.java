package com.sb.woohoo.ui.main.fragments.buysim.phonenumber;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class PhoneNumberPresenter extends BasePresenter  implements PhoneNumberContract.Actions {

    private PhoneNumberRepository phoneNumberRepository;
    private PhoneNumberFragment mView;

    @Inject
    public PhoneNumberPresenter(PhoneNumberRepository phoneNumberRepository, PhoneNumberFragment mView) {
        this.phoneNumberRepository = phoneNumberRepository;
        this.mView = mView;
        phoneNumberRepository.onAttach(this);
    }

    public void getAllPhoneNumbers() {
        phoneNumberRepository.getAllPhoneNumbers();
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        phoneNumberRepository.repositoryCleanUps();
    }

    @Override
    public void onSuccess(ArrayList<GetPhoneDetails> data) {

        mView.onSuccess(data);
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
