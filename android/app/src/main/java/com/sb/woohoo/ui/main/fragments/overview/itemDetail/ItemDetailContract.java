package com.sb.woohoo.ui.main.fragments.overview.itemDetail;

import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;

import java.util.List;

public class ItemDetailContract {

    interface Views {

        void errorMessage(String errorMessage);


        void onSuccess(List<FnBCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);


//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(List<FnBCategoriesResponse.Category> newsHeadline, List<Banner> bannerList);

        void errorMessage(String error);

    }
}
