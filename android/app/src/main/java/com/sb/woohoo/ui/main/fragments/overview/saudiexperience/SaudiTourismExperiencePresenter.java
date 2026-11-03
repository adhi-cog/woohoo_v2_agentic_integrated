package com.sb.woohoo.ui.main.fragments.overview.saudiexperience;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.DataList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class SaudiTourismExperiencePresenter extends BasePresenter implements SaudiTourismExperienceContract.Actions {

    private SaudiTourismExperienceFragment mView;
    private SaudiTourismExperienceRepository saudiTourismEventsRepository;

    @Inject
    public SaudiTourismExperiencePresenter(SaudiTourismExperienceRepository saudiTourismEventsRepository, SaudiTourismExperienceFragment mView) {
        this.saudiTourismEventsRepository = saudiTourismEventsRepository;
        this.mView = mView;
        this.saudiTourismEventsRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }

    public void onSuccess(List<DataList> categories) {
        mView.onCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

    public void experienceAPIRequest(String language) {
        saudiTourismEventsRepository.experienceAPIRequest(language);
        mView.showProgress();
    }
}

