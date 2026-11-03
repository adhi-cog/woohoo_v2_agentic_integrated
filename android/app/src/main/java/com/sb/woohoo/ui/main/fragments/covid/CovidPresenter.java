package com.sb.woohoo.ui.main.fragments.covid;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.covid.pojo.CovidSubmitRequest;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class CovidPresenter extends BasePresenter implements CovidContract.Actions {

    private CovidRepository covidRepository;
    private CovidFragment mView;

    @Inject
    public CovidPresenter(CovidRepository covidRepository, CovidFragment mView) {
        this.covidRepository = covidRepository;
        this.mView = mView;
        covidRepository.onAttach(this);
    }

    public void covidAPIRequest() {
        covidRepository.makeCovidAPIRequest();
        mView.showProgress();
    }

    @Override
    public void covidSuccess(List<Question> covidResponse) {
        mView.covidResult(covidResponse);
        mView.hideProgress();
    }

    @Override
    public void covidMessage(String error) {
        mView.covidMessage(error);
        mView.hideProgress();
    }

    @Override
    public void submitCovid(CovidSubmitRequest covidSubmitRequest) {
        covidRepository.submitCovid(covidSubmitRequest);
        mView.showProgress();
    }

    @Override
    public void covidSaveSuccess(String response) {
        mView.covidSaveSuccess();
        mView.covidMessage(response);
        mView.hideProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        covidRepository.repositoryCleanUps();
    }
}
