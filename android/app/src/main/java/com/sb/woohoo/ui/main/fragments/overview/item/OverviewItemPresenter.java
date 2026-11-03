package com.sb.woohoo.ui.main.fragments.overview.item;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.item.pojo.OverviewItemResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class OverviewItemPresenter extends BasePresenter implements OverviewItemContract.Actions {

    private OverviewItemRepository overviewItemRepository;
    private OverviewItemFragment mView;

    @Inject
    public OverviewItemPresenter(OverviewItemRepository overviewItemRepository, OverviewItemFragment mView) {
        this.overviewItemRepository = overviewItemRepository;
        this.mView = mView;
        this.overviewItemRepository.onAttach(this);
    }

    public void makeItemRequest(String id) {
        overviewItemRepository.makeOverviewProducts(id);
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        //askNewsWithCountryCode();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        overviewItemRepository.repositoryCleanUps();
    }

    @Override
    public void onSuccess(List<Category> categories) {
        mView.onCategoriesResponse(categories);
        mView.hideProgress();
    }

    @Override
    public void errorMessage(String error) {
        mView.onErrorResponse(error);
        mView.hideProgress();
    }
}

