package com.sb.woohoo.ui.main.fragments.overview.saudipackages;


import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.DataList;

import java.util.List;

public class SaudiTourismPackagesContract {

    interface Views {
        void onPackagesResponse(List<DataList> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<DataList> categories);
        void errorMessage(int error);
    }
}
