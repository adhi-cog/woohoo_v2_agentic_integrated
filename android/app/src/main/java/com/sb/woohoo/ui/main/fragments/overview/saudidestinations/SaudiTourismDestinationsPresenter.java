package com.sb.woohoo.ui.main.fragments.overview.saudidestinations;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.Data;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class SaudiTourismDestinationsPresenter extends BasePresenter implements SaudiTourismDestinationsContract.Actions {

    private SaudiTourismDestinationsFragment mView;
    private SaudiTourismDestinationsRepository saudiTourismPackagesRepository;

    @Inject
    public SaudiTourismDestinationsPresenter(SaudiTourismDestinationsRepository saudiTourismPackagesRepository, SaudiTourismDestinationsFragment mView) {
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

    public void onSuccess(List<Data> categories) {
        mView.onDestinationsResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

    public void destinationsAPIRequest(String language) {
        saudiTourismPackagesRepository.destinationsAPIRequest(language);
        mView.showProgress();
    }
}

