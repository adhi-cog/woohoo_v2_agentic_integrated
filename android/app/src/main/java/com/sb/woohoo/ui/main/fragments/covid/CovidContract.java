package com.sb.woohoo.ui.main.fragments.covid;

import com.sb.woohoo.ui.main.fragments.covid.pojo.CovidSubmitRequest;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionList;

import java.util.List;

public interface CovidContract {

    interface Views {

        void covidMessage(String errorMessage);

        void covidResult(List<Question> newsHeadlines);

        void covidShow(QuestionList newsDetail);

//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();

        void covidSaveSuccess();
    }

    interface Actions {

        void covidSuccess(List<Question> newsHeadline);

        void covidMessage(String error);

        void submitCovid(CovidSubmitRequest covidSubmitRequest);

        void covidSaveSuccess(String response);
    }
}
