package com.sb.woohoo.ui.main.fragments.fnb.order;

import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.OrderData;

import java.util.List;

public class FnBOrdersContract {

    interface Views {

        void errorMessage();


        void onSuccess(String successMessage, OrderData orderData);


//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(String successMessage, OrderData  orderData);

        void errorMessage();

    }
}
