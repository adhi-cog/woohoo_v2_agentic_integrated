package com.sb.woohoo.ui.main.fragments.overview.item;

import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.item.pojo.OverviewItemResponse;

import java.util.List;

public class OverviewItemContract {

    interface Views {
        void onCategoriesResponse(List<Category> list);
        void onErrorResponse(String errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<Category> categories);
        void errorMessage(String error);
    }
}
