package com.sb.woohoo.ui.main.fragments.remote;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteActionResponse;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelResponse;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TVRemoteRepository extends BaseRepository<TVRemotePresenter> {

    private final String DEVICE_ID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;


    @Inject
    public TVRemoteRepository(WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    public void makeChannelAPI(String actionId) {
        TVRemoteRequest tVRemoteRequest = new TVRemoteRequest();
        tVRemoteRequest.setActionId(actionId);
        compositeDisposable.add(
                wooHooApiCall.getAllChannel(tVRemoteRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TVRemoteChannelResponse>() {
                            @Override
                            public void onSuccess(TVRemoteChannelResponse response) {
                                onChannelResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onTVRemoteMessage(e.getMessage());
                            }
                        }));
    }

    public void makeRemoteAPI() {
        compositeDisposable.add(
                wooHooApiCall.getAllTVActions()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TVRemoteResponse>() {
                            @Override
                            public void onSuccess(TVRemoteResponse response) {
                                onDiningRestaurantResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onTVRemoteMessage(e.getMessage());
                            }
                        }));
    }

    private void onChannelResponse(TVRemoteChannelResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (!response.getData().isEmpty()) {
                getActions().onChannelListFetched(response.getData());
            } else {
                getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    private void onDiningRestaurantResponse(TVRemoteResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (!response.getData().isEmpty()) {
                getActions().onTVRemoteDataFetched(response.getData());
            } else {
                getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    private void onRemoteComponentResponse(TVRemoteResponse response) {
        int status = response.getStatus();
        if (status == 200) {
            if (!response.getData().isEmpty()) {
                getActions().onRemoteComponentResponse();
            } else {
                getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
            }
        } else if (status == 502) {
            getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
        }
    }

    public void controlTVCommand(String actionId) {
        TVRemoteRequest tVRemoteRequest = new TVRemoteRequest();
        tVRemoteRequest.setActionId(actionId);
        compositeDisposable.add(
                wooHooApiCall.controlTVCommand(tVRemoteRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TVRemoteActionResponse>() {
                            @Override
                            public void onSuccess(TVRemoteActionResponse response) {
                                int status = response.getStatus();
                                if (status == 200) {
                                        getActions().onRemoteComponentResponse();
                                } else if (status == 502) {
                                    getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onTVRemoteMessage(e.getMessage());
                            }
                        }));
    }

    public void controlChannelTVCommand(TVRemoteChannelRequest tVRemoteChannelRequest) {

        compositeDisposable.add(
                wooHooApiCall.controlChannelTVCommand(tVRemoteChannelRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<TVRemoteActionResponse>() {
                            @Override
                            public void onSuccess(TVRemoteActionResponse response) {
                                int status = response.getStatus();
                                if (status == 200) {
                                    if (response.getData() != null && response.getData().getStatus() != null) {
                                        getActions().onRemoteComponentResponse();
                                    } else {
                                        getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.no_result_fetched));
                                    }
                                } else if (status == 502) {
                                    getActions().onTVRemoteMessage(WooHooApp.getAppContext().getString(R.string.bad_gateway));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onTVRemoteMessage(e.getMessage());
                            }
                        }));
    }
}
