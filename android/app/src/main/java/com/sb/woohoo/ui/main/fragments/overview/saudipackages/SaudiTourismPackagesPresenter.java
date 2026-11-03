package com.sb.woohoo.ui.main.fragments.overview.saudipackages;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.DataList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class SaudiTourismPackagesPresenter extends BasePresenter implements SaudiTourismPackagesContract.Actions {

    private SaudiTourismPackagesFragment mView;
    private SaudiTourismPackagesRepository saudiTourismPackagesRepository;

    @Inject
    public SaudiTourismPackagesPresenter(SaudiTourismPackagesRepository saudiTourismPackagesRepository, SaudiTourismPackagesFragment mView) {
        this.saudiTourismPackagesRepository = saudiTourismPackagesRepository;
        this.mView = mView;
        this.saudiTourismPackagesRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }

    public void onSuccess(List<DataList> categories) {
        mView.onPackagesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

    public void packagesAPIRequest() {
        saudiTourismPackagesRepository.packagesAPIRequest();
        mView.showProgress();
    }
}

