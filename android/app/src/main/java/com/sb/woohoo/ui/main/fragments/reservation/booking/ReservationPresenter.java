package com.sb.woohoo.ui.main.fragments.reservation.booking;

import com.google.gson.JsonObject;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationServicesResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.TimeSlot;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ReservationPresenter extends BasePresenter implements ReservationContract.Action {

    private ReservationRepository reservationRepository;
    private ReservationFragment mView;

    @Inject
    public ReservationPresenter(ReservationRepository reservationRepository, ReservationFragment mView) {
        this.reservationRepository = reservationRepository;
        this.mView = mView;
        reservationRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        reservationRepository.repositoryCleanUps();
    }

    public void makeServicesRequest(String restaurantId, boolean isFromDiningRestaurants) {
        if (isFromDiningRestaurants) {
            reservationRepository.getDiningReservationDates(restaurantId);
        } else
            reservationRepository.makeServicesRequest();
        mView.showProgress();
    }

    public void getDiningRestaurantTimeSlot(String restaurantId, String selectedDate) {
        reservationRepository.getDiningReservationTimeSlot(restaurantId, selectedDate);
    }

    public void makeTimeSlotRequest(String id, String date) {
        reservationRepository.makeTimeSlotRequest(id, date);
        mView.showProgress();
    }

    public void onSuccess(ArrayList<ReservationServicesResponse.ReservationService> ReservationServices) {
        mView.hideProgress();
        mView.onServicesFetched(ReservationServices);
    }

    public void errorMessage(String error) {
        mView.errorMessage(error);
        mView.hideProgress();
    }

    public void onTimeSlotFetched(ArrayList<TimeSlot> slots) {
        mView.hideProgress();
        mView.onTimeSlotFetched(slots);
    }

    public void makeReservation(ReservationRequest reservationRequest) {
        reservationRepository.makeReservation(reservationRequest);
        mView.showProgress();
    }

    public void onBookingResponse(BookingConfirmResponse.Data data) {
        mView.hideProgress();
        mView.onBookingResponse(data);
    }

    public void onDiningRestaurantsDatesResponse(List<String> data) {
        mView.hideProgress();
        mView.onDiningRestaurantsDatesResponse(data);
    }

    public void onDiningRestaurantTimeSlotResponse(List<String> data) {
        mView.hideProgress();
        mView.onDiningRestaurantTimeSlotResponse(data);
    }
}
