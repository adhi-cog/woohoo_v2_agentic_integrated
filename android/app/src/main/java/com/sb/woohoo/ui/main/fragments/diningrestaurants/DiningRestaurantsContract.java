package com.sb.woohoo.ui.main.fragments.diningrestaurants;

import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurant;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;

import java.util.List;

public interface DiningRestaurantsContract {

    interface Views {

        void onRestaurantsMessage(String error);

        void onRestaurantsFetched(List<DiningRestaurant> diningRestaurantList);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onRestaurantsFetched(List<DiningRestaurant> diningRestaurantList);
        void onRestaurantsMessage(String error);
    }
}
