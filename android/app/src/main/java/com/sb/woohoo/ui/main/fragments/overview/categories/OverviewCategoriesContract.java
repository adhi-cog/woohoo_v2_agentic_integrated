package com.sb.woohoo.ui.main.fragments.overview.categories;

import com.sb.woohoo.ui.main.fragments.overview.Category;

import java.util.List;

public class OverviewCategoriesContract {

    interface Views {
        void onCategoriesResponse(List<Category> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<Category> categories);
        void errorMessage(int error);
    }
}
