package com.sb.woohoo.ui.main.fragments.buysim.buypackage;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class BuySimPresenter extends BasePresenter  implements BuySimContract.Actions {

    private BuySimRepository buySimRepository;
    private BuySimFragment mView;

    @Inject
    public BuySimPresenter(BuySimRepository buySimRepository, BuySimFragment mView) {
        this.buySimRepository = buySimRepository;
        this.mView = mView;
        buySimRepository.onAttach(this);
    }

    public void makePackageApi() {
        buySimRepository.makePackageApi();
//        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        buySimRepository.repositoryCleanUps();
    }

    @Override
    public void onSuccess(ArrayList<GetEachPackageDetails> data) {
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
