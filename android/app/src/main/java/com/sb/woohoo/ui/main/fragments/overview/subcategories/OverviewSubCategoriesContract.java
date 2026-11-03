package com.sb.woohoo.ui.main.fragments.overview.subcategories;

import com.sb.woohoo.ui.main.fragments.overview.Category;

import java.util.List;

public class OverviewSubCategoriesContract {

    interface Views {
        void onSubCategoriesResponse(List<Category> list);

        void onErrorResponse(int errorMessage);

        void showProgress();

        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<Category> categories);

        void errorMessage(int error);

    }
}
