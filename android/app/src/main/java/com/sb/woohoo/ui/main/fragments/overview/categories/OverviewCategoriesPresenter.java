package com.sb.woohoo.ui.main.fragments.overview.categories;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class OverviewCategoriesPresenter extends BasePresenter implements OverviewCategoriesContract.Actions {

    private OverviewCategoriesRepository overviewCategoriesRepository;
    private OverviewCategoriesFragment mView;

    @Inject
    public OverviewCategoriesPresenter(OverviewCategoriesRepository overviewCategoriesRepository, OverviewCategoriesFragment mView) {
        this.overviewCategoriesRepository = overviewCategoriesRepository;
        this.mView = mView;
        this.overviewCategoriesRepository.onAttach(this);
    }

    public void categoriesRequest() {
        overviewCategoriesRepository.makeAboutUsRequest();
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        //askNewsWithCountryCode();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        overviewCategoriesRepository.repositoryCleanUps();
    }

    @Override
    public void onSuccess(List<Category> categories) {
        mView.onCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }

}

