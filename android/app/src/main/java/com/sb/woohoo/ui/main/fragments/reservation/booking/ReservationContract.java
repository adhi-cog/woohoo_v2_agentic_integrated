package com.sb.woohoo.ui.main.fragments.reservation.booking;

import com.sb.woohoo.ui.main.fragments.video.pojo.Video;

import java.util.List;

public interface ReservationContract {
    interface View {

        void showProgress();

        void hideProgress();

        void showError(String message);
    }

    interface Action {

        //void callVideoListApiBySearch(String query);

        //void onAPIError(Object message);
    }
}
