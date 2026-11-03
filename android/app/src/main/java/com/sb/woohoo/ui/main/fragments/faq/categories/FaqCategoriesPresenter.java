package com.sb.woohoo.ui.main.fragments.faq.categories;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategory;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FaqCategoriesPresenter extends BasePresenter implements FaqCategoriesContract.Actions {

    private FaqCategoriesRepository faqCategoriesRepository;
    private FaqCategoriesFragment mView;

    @Inject
    public FaqCategoriesPresenter(FaqCategoriesRepository faqCategoriesRepository, FaqCategoriesFragment mView) {
        this.faqCategoriesRepository = faqCategoriesRepository;
        this.mView = mView;
        this.faqCategoriesRepository.onAttach(this);
    }

    public void faqCategoriesRequest() {
        faqCategoriesRepository.makeFaqCategoriesRequest();
        mView.showProgress();
    }

    @Override
    public void onCategoriesResponse(List<FaqCategory> list) {
        mView.onCategoriesResponse(list);
        mView.hideProgress();
    }

    @Override
    public void onErrorResponse(int errorMessage) {
        mView.onErrorResponse(errorMessage);
        mView.hideProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        //askNewsWithCountryCode();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        faqCategoriesRepository.repositoryCleanUps();
    }
}

