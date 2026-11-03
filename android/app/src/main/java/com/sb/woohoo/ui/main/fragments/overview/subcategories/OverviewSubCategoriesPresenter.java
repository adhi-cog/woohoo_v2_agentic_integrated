package com.sb.woohoo.ui.main.fragments.overview.subcategories;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class OverviewSubCategoriesPresenter extends BasePresenter implements OverviewSubCategoriesContract.Actions {

    private OverviewSubCategoriesRepository overviewCategoriesRepository;
    private OverviewSubCategoriesFragment mView;

    @Inject
    public OverviewSubCategoriesPresenter(OverviewSubCategoriesRepository overviewCategoriesRepository, OverviewSubCategoriesFragment mView) {
        this.overviewCategoriesRepository = overviewCategoriesRepository;
        this.mView = mView;
        this.overviewCategoriesRepository.onAttach(this);
    }

    public void subCategoriesRequest(String id) {
        overviewCategoriesRepository.makeOverviewSubCategoriesRequest(id);
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
        mView.onSubCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(int error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }
}

