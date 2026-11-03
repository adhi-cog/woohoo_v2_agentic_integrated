package com.sb.woohoo.ui.main.fragments.reservation.upcoming.di;

import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingService;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingServicesResponse;

import java.util.ArrayList;

public interface UpcomingServiceContract {
    interface View {

        void showProgress();

        void hideProgress();

        void showError(String message);

        void onUpcomingServicesFetched(ArrayList<UpcomingService> upcomingServices);

        void onCancelBookingConfirm();
    }

    interface Action {

        //void callVideoListApiBySearch(String query);

        //void onAPIError(Object message);
    }
}
