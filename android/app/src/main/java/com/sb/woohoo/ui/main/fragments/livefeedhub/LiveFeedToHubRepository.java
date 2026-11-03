package com.sb.woohoo.ui.main.fragments.livefeedhub;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.livefeed.pojo.LiveFeedTokenModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubStatus;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubStatusModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.StartLiveFeedModel;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.sb.woohoo.utils.Constants.LIVE_FEED_VIDEO_TYPE;

public class LiveFeedToHubRepository extends BaseRepository<LiveFeedToHubPresenter> {

    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LiveFeedToHubRepository(Retrofit retrofit, CompositeDisposable compositeDisposable) {
        wooHooApiCall = retrofit.create(WooHooApiCall.class);
        this.compositeDisposable = compositeDisposable;
    }

    /*----------------------------------------------------GET HUB STATUS----------------------------------*/
    void requestHubStatus(String remoteHubId) {
        compositeDisposable.add(
                wooHooApiCall.getHubStatus(remoteHubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubStatusModel>() {
                            @Override
                            public void onSuccess(HubStatusModel hubStatusModel) {
                                onHubStatusFetched(hubStatusModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }


    void requestHubUserStatus(String remoteHubId, String userId) {
        compositeDisposable.add(
                wooHooApiCall.getHubUserStatus(remoteHubId, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubStatusModel>() {
                            @Override
                            public void onSuccess(HubStatusModel hubStatusModel) {
                                onHubStatusFetched(hubStatusModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    private void onHubStatusFetched(HubStatusModel hubStatusModel) {
        HubStatus hubStatus = hubStatusModel != null ? hubStatusModel.getHubStatus() : null;
        if (hubStatusModel != null) {
            switch (hubStatusModel.getStatus()) {
                case 200:
                    if (hubStatus.getConnected()) {
                        if ((hubStatus.getIsBusy() != null && hubStatus.getIsBusy()) ||
                                (hubStatus.getSurveillanceMode() != null && hubStatus.getSurveillanceMode())
                                || (hubStatus.getLiveFeedAccess() != null && !hubStatus.getLiveFeedAccess())) {
                            getActions().onAPICallError(R.string.hub_busy);
                        } else {
                            getActions().onHubAvailable();
                        }
                    } else {
                        //getActions().onAPICallError(R.string.hub_currently_offline);
                        getActions().onHubAvailable();
                    }
                    break;
                case 401:
                    getActions().onAPICallError(R.string.authorization_error);
                    break;
                case 403:
                    getActions().onAPICallError(R.string.hub_busy);
                    break;
                case 500:
                    getActions().onAPICallError(R.string.internal_server_error);
                    break;
                default:
                    getActions().onAPICallError(hubStatusModel.getError());
                    break;

            }
        }
    }

    /***********************************************{@GET_LIVE_FEED_TOKEN}*********************************/
    void requestLiveFeedToken(String deviceId) {

        compositeDisposable.add(
                wooHooApiCall.getLiveFeedToken(deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LiveFeedTokenModel>() {
                            @Override
                            public void onSuccess(LiveFeedTokenModel liveFeedTokenModel) {
                                switch (liveFeedTokenModel.getStatus()) {
                                    case 200:
                                        getActions().onLiveFeedTokenSuccess(liveFeedTokenModel.getData());
                                        break;
                                    case 401:
                                        getActions().onAPICallError(R.string.authorization_error);
                                        break;
                                    case 500:
                                        getActions().onAPICallError(R.string.internal_server_error);
                                        break;
                                    case 502:
                                        getActions().onAPICallError(R.string.problem_in_fetching_data);
                                        break;
                                    default:
                                        getActions().onAPICallError(liveFeedTokenModel.getError());
                                        break;

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                                LoggerWrapper.i("API_ERROR", "onError: " + e.getCause());
                            }
                        }));
    }


    /*-------------------------------------------UPDATE HUB STATUS------------------------------------*/
    public void updateHubStatus(boolean isBusy) {

        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        compositeDisposable.add(
                wooHooApiCall.updateHubStatus(deviceId, isBusy)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onComplete() {
                                LoggerWrapper.i("HUB_STATUS", "onHubStatusUpdateSuccess: SUCCESS ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onHubStatusUpdateError(e.getMessage());
                            }
                        }));
    }


    /*------------------------START LIVE FEED ----------------------------------------------*/
    void startLiveFeed(String remoteHubID, boolean contactLiveFeed, final String contactUserId) {

        compositeDisposable.add(
                wooHooApiCall.startLiveFeed(remoteHubID, WooHooApp.getSharedPrefsHelper()
                                .get(SharedPrefsKey.PREF_KEY_DEVICE_ID, null), contactLiveFeed,
                        contactUserId, LIVE_FEED_VIDEO_TYPE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StartLiveFeedModel>() {
                            @Override
                            public void onSuccess(StartLiveFeedModel startLiveFeedModel) {
                                onStartLiveFeedSuccess(startLiveFeedModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    private void onStartLiveFeedSuccess(StartLiveFeedModel startLiveFeedModel) {
        if (startLiveFeedModel == null) {
            getActions().onAPICallError(R.string.body_corrupted);
        } else {
            getActions().startLiveFeed();
        }
    }


    /****************************{CLEAN_UP}********************************/

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}
