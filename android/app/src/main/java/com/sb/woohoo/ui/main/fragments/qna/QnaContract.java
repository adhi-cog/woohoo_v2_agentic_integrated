package com.sb.woohoo.ui.main.fragments.qna;

import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframData;

public interface QnaContract {
    interface Views {
        void showAnswer(String answer, String image);

        void errorInFetchingAnswer(String errorMessage);

        void showError(String errorMessage);

        void speakAnswer(String answer);

        void showProgressBar();

        void hideProgressBar();
    }

    interface Actions {
        void askQuestion(String question);

        void onWolframAlphaSuccess(WolframData answer);

        void onWolframAlphaFailure();

        void onWolframAlphaError(String error);
    }
}