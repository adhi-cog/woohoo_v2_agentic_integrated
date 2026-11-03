package com.sb.woohoo.ui.main.fragments.fnb.history;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryData;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderRequest;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class FnBHistoryPresenter extends BasePresenter implements FnBHistoryContract.Actions {

private FnBHistoryRepository fnBHistoryRepository;
private FnBHistoryFragment mView;

    @Inject
    public FnBHistoryPresenter(FnBHistoryRepository fnBHistoryRepository, FnBHistoryFragment mView) {
        this.fnBHistoryRepository = fnBHistoryRepository;
        this.mView = mView;
        fnBHistoryRepository.onAttach(this);
    }

    public void makeOrderHistoryRequest(){
        fnBHistoryRepository.makeOrderHistoryRequest();
        mView.showProgress();
    }
    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onSuccess(String successMessage, ArrayList<FnBHistoryData> orders) {
        mView.onSuccess(successMessage, orders);
        mView.hideProgress();
    }

    @Override
    public void errorMessage() {
        mView.errorMessage();
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        fnBHistoryRepository.repositoryCleanUps();
    }
}

