package com.sb.woohoo.ui.main.fragments.faq.questions;

import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestion;

import java.util.List;

public interface FaqQnAContract {
    interface Views {
        void onQuestionsResponse(List<FaqQuestion> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onQuestionsResponse(List<FaqQuestion> list);
        void onErrorResponse(int errorMessage);
    }
}
