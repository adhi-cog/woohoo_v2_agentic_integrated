package com.sb.woohoo.ui.main.fragments.fnb.sub_categories;

import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.FnbEvent;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.pojo.FnBSubCategoriesResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FnBSubCategoriesPresenter extends BasePresenter implements FnBSubCategoriesContract.Actions {

    private FnBSubCategoriesRepository fnBSubCategoriesRepository;
    private FnBSubCategoriesFragment mView;

    @Inject
    public AnalyticsHelper helper;

    @Inject
    public FnBSubCategoriesPresenter(FnBSubCategoriesRepository fnBSubCategoriesRepository, FnBSubCategoriesFragment mView) {
        this.fnBSubCategoriesRepository = fnBSubCategoriesRepository;
        this.mView = mView;
        fnBSubCategoriesRepository.onAttach(this);
    }

    public void makeSubCategoriesRequest(String id){
        helper.trackFnb(new FnbEvent.FnbSubCategoriesInitiated());
        fnBSubCategoriesRepository.makeSubCategoriesRequest(id);
        mView.showProgress();
    }
    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onSuccess(List<FnBSubCategoriesResponse.Category> categoryList, List<Banner> bannerList) {
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
        fnBSubCategoriesRepository.repositoryCleanUps();
    }
}

