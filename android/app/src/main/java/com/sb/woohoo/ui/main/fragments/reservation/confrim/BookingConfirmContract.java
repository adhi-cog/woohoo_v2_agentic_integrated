package com.sb.woohoo.ui.main.fragments.reservation.confrim;

public interface BookingConfirmContract {
    interface View {

        void showProgress();

        void hideProgress();

        void showError(String message);

        void onCancelBookingConfirm();
    }

    interface Action {

        //void callVideoListApiBySearch(String query);

        //void onAPIError(Object message);
    }
}
