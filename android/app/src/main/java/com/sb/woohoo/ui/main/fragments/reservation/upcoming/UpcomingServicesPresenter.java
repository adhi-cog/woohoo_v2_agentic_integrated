package com.sb.woohoo.ui.main.fragments.reservation.upcoming;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.UpcomingServiceContract;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.PropertyService;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingReservationData;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingService;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class UpcomingServicesPresenter extends BasePresenter implements UpcomingServiceContract.Action {

    private UpcomingServicesRepository upcomingServicesRepository;
    private UpcomingServiceFragment mView;

    @Inject
    public UpcomingServicesPresenter(UpcomingServicesRepository upcomingServicesRepository, UpcomingServiceFragment mView) {
        this.upcomingServicesRepository = upcomingServicesRepository;
        this.mView = mView;
        upcomingServicesRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        upcomingServicesRepository.repositoryCleanUps();
    }

    public void makeUpcomingServicesRequest() {
        upcomingServicesRepository.makeUpcomingServicesRequest();
        mView.showProgress();
    }

    public void getAllDiningReservations(String id) {
        upcomingServicesRepository.getAllDiningReservations(id);
        mView.showProgress();
    }

    public void onSuccess(ArrayList<UpcomingService> upcomingServices) {
        mView.hideProgress();
        mView.onUpcomingServicesFetched(upcomingServices);
    }

    public void errorMessage(String error) {
        mView.errorMessage(error);
        mView.hideProgress();
    }

    public void makeCancelBookingRequest(int id) {
        upcomingServicesRepository.makeCancelBookingRequest(id);
        mView.showProgress();
    }

    public void onCancelBookingConfirm() {
        mView.hideProgress();
        mView.onCancelBookingConfirm();
    }

    public void onUpcomingReservationResponse(List<UpcomingReservationData> list) {
        ArrayList<UpcomingService> upcomingServices = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UpcomingReservationData data = list.get(i);
            UpcomingService upcomingService = new UpcomingService();
            PropertyService propertyService = new PropertyService();
            upcomingService.setId(data.getId());
            upcomingService.setStartTimeInFormat(data.getStartAt());
            propertyService.setBackgroundImageUrl(data.getThumbnailUrl());
            propertyService.setName(data.getFirstName() + " " + data.getLastName());
            upcomingService.setPropertyService(propertyService);
            upcomingServices.add(upcomingService);
        }
        onSuccess(upcomingServices);
    }
}
