package com.sb.woohoo.ui.main.fragments.reservation.booking;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantTimeSlotResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantsDateResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationServicesResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationTimeSlotsResponse;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReservationRepository extends BaseRepository<ReservationPresenter> {

    private final String DEVICE_ID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public ReservationRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeServicesRequest() {
        compositeDisposable.add(
                wooHooApiCall.getReservationServices()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ReservationServicesResponse>() {
                            @Override
                            public void onSuccess(ReservationServicesResponse response) {
                                onServicesResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onServicesResponse(ReservationServicesResponse reservationServicesResponse) {
        int status = reservationServicesResponse.getStatus();
        if (status == 200) {
            if (!reservationServicesResponse.getData().isEmpty()) {
                getActions().onSuccess(reservationServicesResponse.getData());
            } else {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    public void makeTimeSlotRequest(String id, String date) {
        compositeDisposable.add(
                wooHooApiCall.getReservationTimeSlot(id, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ReservationTimeSlotsResponse>() {
                            @Override
                            public void onSuccess(ReservationTimeSlotsResponse response) {
                                onTimeSlotResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onTimeSlotResponse(ReservationTimeSlotsResponse reservationTimeSlotsResponse) {
        int status = reservationTimeSlotsResponse.getStatus();
        if (status == 200) {
            if (reservationTimeSlotsResponse.getData() != null) {
                getActions().onTimeSlotFetched(reservationTimeSlotsResponse.getData().getSlots());
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

    public void makeReservation(ReservationRequest obj) {
        compositeDisposable.add(
                wooHooApiCall.makeReservation(obj)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BookingConfirmResponse>() {
                            @Override
                            public void onSuccess(BookingConfirmResponse response) {
                                onBookingResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onBookingResponse(BookingConfirmResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (response.getData() != null) {
                getActions().onBookingResponse(response.getData());
            } else {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    public void getDiningReservationDates(String restaurantId) {
        compositeDisposable.add(
                wooHooApiCall.getDiningReservationDates(restaurantId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DiningRestaurantsDateResponse>() {
                            @Override
                            public void onSuccess(DiningRestaurantsDateResponse response) {
//                                onServicesResponse(response);
                                onDiningReservationDateResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().errorMessage(e.getMessage());
                            }
                        }));
    }

    private void onDiningReservationDateResponse(DiningRestaurantsDateResponse response) {
        if (response != null) {
            if (response.getStatus() == 200 && response.getData() != null) {
                getActions().onDiningRestaurantsDatesResponse(response.getData());
            } else if (response.getStatus() == 502) {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
            }
        }
    }

    public void getDiningReservationTimeSlot(String restaurantId, String selectedDate) {
        compositeDisposable.add(
                wooHooApiCall.getDiningReservationDates(restaurantId, selectedDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DiningRestaurantTimeSlotResponse>() {
                            @Override
                            public void onSuccess(DiningRestaurantTimeSlotResponse response) {
//                                onServicesResponse(response);
                                try {
                                    onDiningReservationTimeSlotResponse(response);
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

    private void onDiningReservationTimeSlotResponse(DiningRestaurantTimeSlotResponse response) {
        if (response != null) {
            if (response.getStatus() == 200 && response.getData() != null) {
                getActions().onDiningRestaurantTimeSlotResponse(response.getData());
            } else if (response.getStatus() == 502) {
                getActions().errorMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
            }
        }
    }
}
