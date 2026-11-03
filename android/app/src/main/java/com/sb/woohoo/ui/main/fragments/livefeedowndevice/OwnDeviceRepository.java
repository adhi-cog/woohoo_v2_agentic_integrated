package com.sb.woohoo.ui.main.fragments.livefeedowndevice;

import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubListModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubData;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubListModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OwnDeviceRepository extends BaseRepository<OwnDevicePresenter> {

    private WooHooApiCall mAPICall;
    private Gson mGson;
    private CompositeDisposable compositeDisposable;

    @Inject
    public OwnDeviceRepository(WooHooApiCall mAPICall, Gson mGson, CompositeDisposable compositeDisposable) {
        this.mAPICall = mAPICall;
        this.mGson = mGson;
        this.compositeDisposable = compositeDisposable;
    }

    /*-------------------------------------- Methods to fetch User  DeviceData -----------------------------------*/

    /**
     * Calls the API to fetch User Details.
     */

    public void fetchListOfHubs() {
        compositeDisposable.add(
                mAPICall.getAllHubs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HubListModel>() {
                            @Override
                            public void onSuccess(HubListModel hubListModel) {
                                onHubListFetched(hubListModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    /**
     * Handles the successful response of API call, fetched as {@link HubListModel}.
     *
     * @param {@link Response} of {@link HubListModel}.
     */

    private void onHubListFetched(HubListModel hubListModel) {
        if (hubListModel == null) {
            getActions().onAPICallError(R.string.postresponse_corrupted);
        } else {
            switch (hubListModel.getStatus()) {
                case 200:
                    getActions().onHubListFetched(hubListModel);
                    break;
                case 401: // TODO : Call Refresh Session Token API or Re-Login.
                    getActions().onAPICallError(R.string.authorization_error);
                    break;
                case 502:
                    getActions().onAPICallError(R.string.bad_gateway);
                    break;
                default:
                    getActions().onAPICallError(hubListModel.getError());
                    break;
            }
        }
    }

    /***************************************{GET OTHERS HUB WITH ACCESS}**************************************/
    void fetchHubsWithAccess() {
        compositeDisposable.add(
                mAPICall.getHubAccessFromOtherFamily()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OtherFamilyModel>() {
                            @Override
                            public void onSuccess(OtherFamilyModel othersHubListModel) {
                                onOtherHubsFetched(othersHubListModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    private void onOtherHubsFetched(OtherFamilyModel othersHubListModel) {
        if (othersHubListModel == null) {
            getActions().onAPICallError(R.string.postresponse_corrupted);
        } else {
            switch (othersHubListModel.getStatus()) {
                case 200:
                    getActions().onOtherHubListFetched(othersHubListModel);
                    break;
                case 401: // TODO : Call Refresh Session Token API or Re-Login.
                    getActions().onAPICallError("Authorization Error");
                    break;
                case 502:
                    getActions().onAPICallError(R.string.bad_gateway);
                    break;
                default:
                    getActions().onAPICallError(othersHubListModel.getError());
                    break;
            }
        }
    }

    /************************************{GET HUB LIST OF OTHER FAMILY}**********************************/
    public void getHubListOfOtherFamily(String id) {
        compositeDisposable.add(
                mAPICall.getHubListOfOtherFamilyMembers(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OthersHubListModel>() {
                            @Override
                            public void onSuccess(OthersHubListModel othersHubListModel) {
                                if (othersHubListModel.getStatus() == 200) {
                                    onListOfHubsFetchedSuccess(othersHubListModel.getData());
                                } else {
                                    getActions().onAPICallError(othersHubListModel.getError());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    private void onListOfHubsFetchedSuccess(List<OthersHubData> othersHubListModelData) {
        getActions().onListOfHubsFetchedSuccess(othersHubListModelData);
    }


    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}
