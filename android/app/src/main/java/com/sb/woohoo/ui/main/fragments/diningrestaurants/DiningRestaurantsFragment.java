package com.sb.woohoo.ui.main.fragments.diningrestaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentDiningRestaurantsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.di.DaggerDiningRestaurantsComponent;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.di.DiningRestaurantsComponent;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.di.DiningRestaurantsModule;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurant;
import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DiningRestaurantsFragment extends FeatureFragment<MainActivity> implements DiningRestaurantsContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private DiningRestaurantsAdapter diningRestaurantsAdapter;

    @Inject
    DiningRestaurantsPresenter diningRestaurantsPresenter;

    private List<DiningRestaurant> restaurantList = new ArrayList<>();
    private FragmentDiningRestaurantsBinding binding;

    public static DiningRestaurantsFragment newInstance() {
        DiningRestaurantsFragment fragment = new DiningRestaurantsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiningRestaurantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        diningRestaurantsPresenter.makeRestaurantsRequest();
    }

    private void initViews() {
        createRestaurantsData();
        binding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        diningRestaurantsAdapter = new DiningRestaurantsAdapter(getActivity(), restaurantList, diningRestaurant -> {
            if (diningRestaurant.getType().equalsIgnoreCase(Constants.TABLE_CHECK)) {
                getParentActivity().switchFragmentUsingReplace(ReservationFragment.newInstance(String.valueOf(diningRestaurant.getId()), true));
            } else {
                //getParentActivity().switchFragmentUsingReplace(WebViewFragment.newInstance(diningRestaurant.getUrl(), WebViewFragment.WebviewLaunchMode.Dining));
            }
        });
        binding.recyclerRestaurants.setAdapter(diningRestaurantsAdapter);
    }

    private void createRestaurantsData() {
        if (restaurantList == null)
            restaurantList = new ArrayList<>();
        restaurantList.clear();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onRestaurantsMessage(String error) {
        Toast.makeText(getParentActivity(), error, Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();
    }

    @Override
    public void onRestaurantsFetched(List<DiningRestaurant> diningRestaurantList) {
        createRestaurantsData();
        diningRestaurantsAdapter.updateRestaurantsList(diningRestaurantList);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void initDagger() {
        DiningRestaurantsComponent diningRestaurantsComponent = DaggerDiningRestaurantsComponent.builder()
                .diningRestaurantsModule(new DiningRestaurantsModule(this))
                .build();

        diningRestaurantsComponent.injectDiningRestaurantsFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_restaurant_title));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return diningRestaurantsPresenter;
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        diningRestaurantsPresenter.presenterCleanUps();
    }
}
