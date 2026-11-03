package com.sb.woohoo.ui.main.fragments.reservation.confrim;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.UpcomingServiceFragment;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.UpcomingServicesRepository;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.UpcomingServiceContract;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingServicesResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;

import javax.inject.Inject;

public class BookingConfirmPresenter extends BasePresenter implements UpcomingServiceContract.Action {

    private BookingConfirmRepository bookingConfirmRepository;
    private BookingConfirmFragment mView;

    @Inject
    public BookingConfirmPresenter(BookingConfirmRepository bookingConfirmRepository, BookingConfirmFragment mView) {
        this.bookingConfirmRepository = bookingConfirmRepository;
        this.mView = mView;
        bookingConfirmRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        bookingConfirmRepository.repositoryCleanUps();
    }

    public void errorMessage(String error) {
        mView.errorMessage(error);
        mView.hideProgress();
    }

    public void makeCancelBookingRequest(int id) {
        bookingConfirmRepository.makeCancelBookingRequest(id);
        mView.showProgress();
    }

    public void makeDiningRestaurantBooking(DiningRestaurantRequest diningRestaurantRequest) {
        bookingConfirmRepository.makeDiningRestaurant(diningRestaurantRequest);
        mView.showProgress();
    }

    public void onBookingResponse(BookingConfirmResponse.Data data) {
        mView.hideProgress();
        mView.onBookingResponse(data);
    }

    public void onCancelBookingConfirm() {
        mView.hideProgress();
        mView.onCancelBookingConfirm();
    }
}
