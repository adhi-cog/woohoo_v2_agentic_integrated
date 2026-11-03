package com.sb.woohoo.ui.main.fragments.fnb.sub_categories;

import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.pojo.FnBSubCategoriesResponse;

import java.util.List;

public class FnBSubCategoriesContract {

    interface Views {

        void errorMessage(int errorMessage);


        void onSuccess(List<FnBSubCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);


//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(List<FnBSubCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);

        void errorMessage(int error);

    }
}
