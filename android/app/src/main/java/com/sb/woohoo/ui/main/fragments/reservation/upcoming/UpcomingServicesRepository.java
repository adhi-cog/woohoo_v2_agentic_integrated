package com.sb.woohoo.ui.main.fragments.reservation.upcoming;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.CancelBookingResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingReservationResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingServicesResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpcomingServicesRepository extends BaseRepository<UpcomingServicesPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public UpcomingServicesRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeUpcomingServicesRequest() {
        compositeDisposable.add(
                wooHooApiCall.getUpcomingServices()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UpcomingServicesResponse>() {
                            @Override
                            public void onSuccess(UpcomingServicesResponse response) {
                                onServicesResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onServicesResponse(UpcomingServicesResponse upcomingServicesResponse) {
        int status = upcomingServicesResponse.getStatus();
        if (status == 200) {
            //Adding null check
            if (upcomingServicesResponse.getData() !=null &&  !upcomingServicesResponse.getData().isEmpty()) {
                getActions().onSuccess(upcomingServicesResponse.getData());
            } else {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }

    public void makeCancelBookingRequest(int id) {
        compositeDisposable.add(
                wooHooApiCall.makeCancelBookingRequest(String.valueOf(id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CancelBookingResponse>() {
                            @Override
                            public void onSuccess(CancelBookingResponse response) {
                                onCancelBookingResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onCancelBookingResponse(CancelBookingResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (response.getData() != null) {
                getActions().onCancelBookingConfirm();
            } else {
                //getActions().errorMessage(noResult);
                getActions().onCancelBookingConfirm();
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    public void getAllDiningReservations(String id) {
        compositeDisposable.add(
                wooHooApiCall.getAllDiningReservations(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UpcomingReservationResponse>() {
                            @Override
                            public void onSuccess(UpcomingReservationResponse response) {
                                try {
                                    onDiningReservationsResponse(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onDiningReservationsResponse(UpcomingReservationResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (!response.getData().isEmpty()) {
                getActions().onUpcomingReservationResponse(response.getData());
            } else {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }
}
