package com.sb.woohoo.ui.main.fragments.amadeusServices;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusDynamicOrderRequest;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrderData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class AmadeusServicePresenter
        extends BasePresenter
        implements AmadeusServicesContract.Actions {

    private AmadeusServicesContract.Views views;
    private AmadeusServiceRepository amadeusServiceRepository;

    @Inject
    public AmadeusServicePresenter(AmadeusServicesFragment fragment, AmadeusServiceRepository amadeusServiceRepository) {
        this.views = fragment;
        this.amadeusServiceRepository = amadeusServiceRepository;
        amadeusServiceRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onOrderCreated(String message) {

        views.showProgressBar(false);
        views.showMessage(message);
        views.getOrders();
    }

    @Override
    public void fetchListOfAmadeusOrders() {
        views.showProgressBar(true);
        amadeusServiceRepository.getDynamicOrders();
    }

    @Override
    public void onOrdersFetched(List<AmadeusOrderData> amadeusOrderData) {
        views.showProgressBar(false);
        if (amadeusOrderData.size() > 0) {
            views.showEmptyView(false);
            views.onAllOrdersForUserFetched(amadeusOrderData);
        } else {
            views.showEmptyView(true);
        }
    }

    @Override
    public void createDynamicAmadeusOrder(AmadeusDynamicOrderRequest amadeusDynamicOrderRequest) {
        views.showProgressBar(true);
        amadeusServiceRepository.createDynamicOrder(amadeusDynamicOrderRequest);
    }

    @Override
    public void onApiError(String errorMessage) {
        views.showProgressBar(false);
        views.showErrorMessage(errorMessage);
    }
}
