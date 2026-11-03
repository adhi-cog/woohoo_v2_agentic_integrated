package com.sb.woohoo.ui.main.fragments.feedback;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class FeedbackPresenter extends BasePresenter implements FeedbackContract.Actions {

    private FeedbackRepository feedbackRepository;
    private FeedbackFragment mView;

    @Inject
    public FeedbackPresenter(FeedbackRepository feedbackRepository, FeedbackFragment mView) {
        this.feedbackRepository = feedbackRepository;
        this.mView = mView;
        feedbackRepository.onAttach(this);
    }

    public void feedbackAPIRequest() {
        feedbackRepository.makeFeedbackAPIRequest();
        mView.showProgress();
    }

    @Override
    public void feedbackSuccess(List<Question> feedbackResponse) {
        mView.feedbackResult(feedbackResponse);
        mView.hideProgress();
    }

    @Override
    public void feedbackMessage(int error) {
        mView.feedbackMessage(error);
        mView.hideProgress();
    }

    @Override
    public void submitFeedback(FeedbackSubmitRequest feedbackSubmitRequest) {
        feedbackRepository.submitFeedback(feedbackSubmitRequest);
        mView.showProgress();
    }

    @Override
    public void feedbackSaveSuccess(int value) {
        mView.hideProgress();
        mView.feedbackMessage(value);
        mView.feedbackSaveSuccess();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        //askNewsWithCountryCode();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        feedbackRepository.repositoryCleanUps();
    }
}
