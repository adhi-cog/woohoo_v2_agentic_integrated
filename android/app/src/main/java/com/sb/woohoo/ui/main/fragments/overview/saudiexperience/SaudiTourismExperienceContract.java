package com.sb.woohoo.ui.main.fragments.overview.saudiexperience;

import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.DataList;

import java.util.List;

public class SaudiTourismExperienceContract {

    interface Views {
        void onCategoriesResponse(List<DataList> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<DataList> categories);
        void errorMessage(int error);
    }
}
