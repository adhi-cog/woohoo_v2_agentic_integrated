package com.sb.woohoo.ui.main.fragments.faq.categories;

import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategory;

import java.util.List;

public interface FaqCategoriesContract {

    interface Views {
        void onCategoriesResponse(List<FaqCategory> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onCategoriesResponse(List<FaqCategory> list);
        void onErrorResponse(int errorMessage);
    }
}
