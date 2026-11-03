package com.sb.woohoo.ui.main.fragments.feedback;

import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;

import java.util.List;

public interface FeedbackContract {

    interface Views {

        void feedbackMessage(int errorCode);

        void feedbackResult(List<Question> feedbackList);

        void showProgress();

        void hideProgress();

        void feedbackSaveSuccess();
    }

    interface Actions {

        void feedbackSuccess(List<Question> feedbackList);

        void feedbackMessage(int errorCode);

        void submitFeedback(FeedbackSubmitRequest feedbackSubmitRequest);

        void feedbackSaveSuccess(int response);
    }
}
