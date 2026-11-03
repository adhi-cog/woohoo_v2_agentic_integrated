package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo.Data;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class ExperienceDetailPresenter extends BasePresenter implements ExperienceDetailContract.Actions {

    private ExperienceDetailFragment mView;
    private ExperienceDetailRepository experienceDetailRepository;

    @Inject
    public ExperienceDetailPresenter(ExperienceDetailRepository experienceDetailRepository, ExperienceDetailFragment mView) {
        this.experienceDetailRepository = experienceDetailRepository;
        this.mView = mView;
        this.experienceDetailRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        experienceDetailRepository.repositoryCleanUps();
    }

    public void onSuccess(Data categories) {
        mView.onCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

    public void experienceAPIRequest(String experienceId, String language) {
        experienceDetailRepository.experienceAPIRequest(experienceId,language);
        mView.showProgress();
    }
}

