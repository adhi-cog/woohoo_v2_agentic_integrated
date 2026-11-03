package com.sb.woohoo.ui.main.fragments.diningrestaurants;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurant;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class DiningRestaurantsPresenter extends BasePresenter  implements DiningRestaurantsContract.Actions {

    private DiningRestaurantsRepository diningRestaurantsRepository;
    private DiningRestaurantsFragment mView;

    @Inject
    public DiningRestaurantsPresenter(DiningRestaurantsRepository diningRestaurantsRepository, DiningRestaurantsFragment mView) {
        this.diningRestaurantsRepository = diningRestaurantsRepository;
        this.mView = mView;
        diningRestaurantsRepository.onAttach(this);
    }

    public void makeRestaurantsRequest() {
        diningRestaurantsRepository.makeRestaurantsRequest();
        mView.showProgress();
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        diningRestaurantsRepository.repositoryCleanUps();
    }

    @Override
    public void onRestaurantsFetched(List<DiningRestaurant> diningRestaurantList) {
        mView.onRestaurantsFetched(diningRestaurantList);
        mView.hideProgress();
    }

    @Override
    public void onRestaurantsMessage(String error) {
        mView.onRestaurantsMessage(error);
        mView.hideProgress();
    }
}
