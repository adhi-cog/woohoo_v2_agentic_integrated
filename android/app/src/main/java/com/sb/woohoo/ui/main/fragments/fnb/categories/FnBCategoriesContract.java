package com.sb.woohoo.ui.main.fragments.fnb.categories;

import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;

import java.util.List;

public class FnBCategoriesContract {

    interface Views {

        void errorMessage(int errorMessage);


        void onSuccess(List<FnBCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);


//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(List<FnBCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);

        void errorMessage(int error);

    }
}
