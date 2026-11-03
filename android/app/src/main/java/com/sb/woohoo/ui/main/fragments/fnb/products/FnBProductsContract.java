package com.sb.woohoo.ui.main.fragments.fnb.products;

import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.FnBProductsResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;

import java.util.List;

public class FnBProductsContract {

    interface Views {

        void errorMessage(int errorMessage);


        void onSuccess(List<Product> productsList, List<Banner> bannerList);


//        void readToUserHeadline(List<Article> newsHeadlines);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(List<Product> productsList, List<Banner> bannerList);

        void errorMessage(int error);

    }
}
