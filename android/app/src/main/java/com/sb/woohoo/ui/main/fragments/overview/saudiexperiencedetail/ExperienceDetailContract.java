package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail;

import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo.Data;

import java.util.List;

public class ExperienceDetailContract {

    interface Views {

        void onCategoriesResponse(Data data);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(Data data);
        void errorMessage(int error);
    }
}
