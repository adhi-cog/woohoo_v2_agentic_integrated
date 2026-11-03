package com.sb.woohoo.ui.main.fragments.amadeusServices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentAmadeusServicesFragmentBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.amadeusServices.di.AmadeusServiceModule;
import com.sb.woohoo.ui.main.fragments.amadeusServices.di.AmadeusServicesComponent;
import com.sb.woohoo.ui.main.fragments.amadeusServices.di.DaggerAmadeusServicesComponent;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusDynamicOrderRequest;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrderData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AmadeusServicesFragment
        extends FeatureFragment<MainActivity>
        implements AmadeusServicesContract.Views,
        AmadeusOrdersAdapter.OnAmadeusOrderClick {

    @Inject
    AnalyticsHelper helper;
    @Inject
    AmadeusServicePresenter amadeusServicePresenter;
    @Inject
    AmadeusOrdersAdapter amadeusOrdersAdapter;

    List<AmadeusOrderData> amadeusOrdersList = new ArrayList<>();

    private FragmentAmadeusServicesFragmentBinding binding;
    public static AmadeusServicesFragment newInstance() {
        return new AmadeusServicesFragment();
    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        AmadeusServicesComponent amadeusServicesComponent = DaggerAmadeusServicesComponent
                .builder()
                .amadeusServiceModule(new AmadeusServiceModule(this))
                .build();
        amadeusServicesComponent.injectFragment(this);

    }

    @Override
    public void bindViews(View view) {
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.AmadeusServices(),  null);
        binding = FragmentAmadeusServicesFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

    }

    private void initViews() {
        binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.amenities));
        binding.fragmentToDoToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.recyclerOrders.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        binding.recyclerOrders.setAdapter(amadeusOrdersAdapter);
        amadeusOrdersAdapter.setOnAmadeusOrderClick(this);
        getOrders();

    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getActivity(), message);
    }

    @Override
    public void getOrders() {
        amadeusServicePresenter.fetchListOfAmadeusOrders();
    }

    @Override
    public void onAllOrdersForUserFetched(List<AmadeusOrderData> amadeusOrderData) {
        amadeusOrdersList.clear();
        if (amadeusOrderData != null && amadeusOrderData.size() > 0) {
            amadeusOrdersList.addAll(amadeusOrderData);
            amadeusOrdersAdapter.updateListOnUI(amadeusOrdersList);
        }
    }

    @Override
    public void showProgressBar(boolean show) {
        binding.rlProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showEmptyView(boolean show) {
        binding.tvEmptyText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        try {
            MethodUtils.showToastMessage(requireActivity(), errorMessage);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOrderItemClick(String orderName, int orderCount) {
        if (orderCount > 0) {
            AmadeusDynamicOrderRequest amadeusDynamicOrderRequest = new AmadeusDynamicOrderRequest();
            List<AmadeusDynamicOrderRequest.Order> ordersList = new ArrayList<>();
            ordersList.add(new AmadeusDynamicOrderRequest.Order(Integer.parseInt(orderName),orderCount));
            amadeusDynamicOrderRequest.setItems(ordersList);
            amadeusServicePresenter.createDynamicAmadeusOrder(amadeusDynamicOrderRequest);
        } else {
            showErrorMessage(getString(R.string.enter_qty));
        }
    }
}
