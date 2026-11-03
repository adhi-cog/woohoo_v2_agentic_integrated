package com.sb.woohoo.ui.main.fragments.fnb.categories;

import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.FnbEvent;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FnBCategoriesPresenter extends BasePresenter implements FnBCategoriesContract.Actions {

private FnBCategoriesRepository fnBCategoriesRepository;
private FnBCategoriesFragment mView;

    @Inject
    public AnalyticsHelper helper;

    @Inject
    public FnBCategoriesPresenter(FnBCategoriesRepository fnBCategoriesRepository, FnBCategoriesFragment mView) {
        this.fnBCategoriesRepository = fnBCategoriesRepository;
        this.mView = mView;
        fnBCategoriesRepository.onAttach(this);
    }

    public void makeCategoriesRequest(){
        helper.trackFnb(new FnbEvent.FnbCategoriesInitiated());
        fnBCategoriesRepository.makeCategoriesRequest();
        mView.showProgress();
    }
    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onSuccess(List<FnBCategoriesResponse.Category> categoryList, List<Banner> bannerList) {
        mView.onSuccess(categoryList, bannerList);
        mView.hideProgress();
    }


    @Override
    public void errorMessage(int error) {
        mView.errorMessage(error);
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        fnBCategoriesRepository.repositoryCleanUps();
    }
}

