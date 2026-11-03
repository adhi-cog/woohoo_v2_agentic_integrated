package com.sb.woohoo.ui.main.fragments.faq.questions;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategory;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestion;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FaqQnAPresenter extends BasePresenter implements FaqQnAContract.Actions{

    private FaqQnARepository faqQnARepository;
    private FaqQnAFragment mView;
    @Inject
    public FaqQnAPresenter(FaqQnARepository faqQnARepository, FaqQnAFragment mView) {
        this.faqQnARepository = faqQnARepository;
        this.mView = mView;
        this.faqQnARepository.onAttach(this);
    }

    public void getFaqQuestions(String categoryId) {
        faqQnARepository.getFaqQuestions(categoryId);
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onQuestionsResponse(List<FaqQuestion> list) {
        mView.onQuestionsResponse(list);
        mView.hideProgress();
    }

    @Override
    public void onErrorResponse(int errorMessage) {
        mView.onErrorResponse(errorMessage);
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        faqQnARepository.repositoryCleanUps();
    }
}
