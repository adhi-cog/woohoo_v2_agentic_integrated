package com.sb.woohoo.ui.main.fragments.amadeusServices;

import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusDynamicOrderRequest;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrderData;

import java.util.List;

public interface AmadeusServicesContract {
    interface Views {
        void showProgressBar(boolean show);

        void showErrorMessage(String errorMessage);

        void showEmptyView(boolean show);

        void getOrders();

        void showMessage(String message);

        void onAllOrdersForUserFetched(List<AmadeusOrderData> amadeusOrderData);
    }

    interface Actions {

        void onApiError(String errorMessage);

        void onOrderCreated(String message);

        void fetchListOfAmadeusOrders();

        void onOrdersFetched(List<AmadeusOrderData> amadeusOrderData);

        void createDynamicAmadeusOrder(AmadeusDynamicOrderRequest amadeusDynamicOrderRequest);
    }
}
