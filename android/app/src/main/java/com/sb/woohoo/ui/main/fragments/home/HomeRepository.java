package com.sb.woohoo.ui.main.fragments.home;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.MainRepository;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMembersResponse;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsResponse;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo.DeviceSettings;
import com.sb.woohoo.ui.main.pojo.Appstore;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.sip.SIPConfigurationResponse;
import com.sb.woohoo.ui.main.sip.SipAccountManager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;

import android.util.Log;
import android.widget.EditText;

/**
 * Created by Shrey on 18/03/18.
 */

public class HomeRepository extends BaseRepository<HomePresenter> {
    private static final String TAG = HomeRepository.class.getSimpleName();

    String hubID;
    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    HomeRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
        hubID = WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, null);
    }


    public void fetchHotelDetails() {
        compositeDisposable.add(
                mApiCall.getHotelDetails(hubID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HotelDetailsResponse>() {
                            @Override
                            public void onSuccess(HotelDetailsResponse hotelDetailsResponse) {
                                if (hotelDetailsResponse.getStatus() == 200) {
                                    LoggerWrapper.d(TAG, "Socket Event - fetchHotelDetails() API Response Success");
                                    getActions().onHotelDetailsFetched(hotelDetailsResponse.getData());
                                } else {
                                    LoggerWrapper.d(TAG, "Socket Event - fetchHotelDetails() API Response Fail");
                                    getActions().onAPICallError(hotelDetailsResponse.getError());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.d(TAG, "Socket Event - fetchHotelDetails() API onError");
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }
    /*-------------------------------------- Methods to fetch Family Members -----------------------------------*/

    /**
     * Calls the API to fetch Family Members.
     */
    void fetchHotelContacts() {
        compositeDisposable.add(
                mApiCall.getAllHotelStaffMembers(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, ""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StaffMembersResponse>() {
                            @Override
                            public void onSuccess(StaffMembersResponse staffMembersResponse) {

                                switch (staffMembersResponse.getStatus()) {
                                    case 200: {
                                        onStaffMembersFetched(staffMembersResponse);
                                    }
                                    break;
                                    case 401: {
                                    }
                                    break;
                                    case 502: {
                                        getActions().onAPICallError("Bad Gateway");
                                    }
                                    break;
                                    default: {
                                        getActions().onAPICallError(staffMembersResponse.getError());
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        }));
    }

    /**
     * Handles the successful response of API call, fetched as {@link StaffMembersResponse}.
     *
     * @param staffMembersResponse {@link StaffMembersResponse}.
     */
    private void onStaffMembersFetched(StaffMembersResponse staffMembersResponse) {
        if (staffMembersResponse == null) {
            getActions().onAPICallError("PostResponse Corrupted");
        } else {
            if (staffMembersResponse.getStatus() == 200) {
                List<StaffMember> members = staffMembersResponse.getData();
                if (members == null || members.size() == 0) {
                    getActions().onStaffMembersNotFound();
                } else {
                    getActions().onStaffMembersFetched(members);
                }
            } else {
                getActions().onAPICallError(staffMembersResponse.getMessage());
            }
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
    private void responseAppStoreCall(Appstore appstoreRes) {
        if (appstoreRes != null) {
            List<Appstore.Data> list = appstoreRes.getData();
            if (list != null && list.size() > 0) {
                getActions().onStoreListUpdate(list);
            }
        }
    }

    public void appStoreList() {
        compositeDisposable.add(mApiCall.getAppStoreList( WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Appstore>() {

                    @Override
                    public void onSuccess(Appstore appstoreRes) {
                        LoggerWrapper.i(MainRepository.class.getSimpleName(), "**** get store api");
                        responseAppStoreCall(appstoreRes);

                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** get store api error " + e.getMessage());

                        getActions().onError(R.string.try_again);
                    }
                }));
    }

    public void getSIPConfiguration() {
        compositeDisposable.add(mApiCall.getSIPConfiguration().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<SIPConfigurationResponse>() {
            @Override
            public void onSuccess(SIPConfigurationResponse saudiTourismResponse) {
                LoggerWrapper.i("SIPConfig", "response - " + saudiTourismResponse);
                if(saudiTourismResponse.getData() != null) {
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SIP_ID, saudiTourismResponse.getData().getUsername());
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SIP_PASS, saudiTourismResponse.getData().getPassword());
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SIP_DOMAIN, saudiTourismResponse.getData().getDomain());
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SIP_PROXY, saudiTourismResponse.getData().getProxy());
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_SIP_TRANSPORT, saudiTourismResponse.getData().getTransport());
                    WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, true);
                    getActions().onSIPCredentialFetch();
                }else{
                    SipAccountManager.getInstance().clearCredentialsCache();
                }
            }

            @Override
            public void onError(Throwable e) {
                LoggerWrapper.i("SIPConfig", "onError - " + e.getMessage());
                SipAccountManager.getInstance().clearCredentialsCache();
            }
        }));
    }

    public void startSessionApi(DrawerGridItems drawerGridItems) {

        compositeDisposable.add(mApiCall.startSession(
                        WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""), "" + drawerGridItems.getData().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<StartSession>() {

                    @Override
                    public void onSuccess(StartSession appstoreRes) {
                        getActions().onStartSessionSucess(appstoreRes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** session start error " + e.getMessage());
                        getActions().onError(R.string.try_again);

                    }
                }));
    }

    public void fetchDeviceNameApiRequest() {
        compositeDisposable.add(
                mApiCall.getDeviceSetting(hubID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DeviceSettings>() {
                            @Override
                            public void onSuccess(DeviceSettings deviceSettings) {
                                if (deviceSettings.getData() != null && !deviceSettings.getData().getName().isEmpty()) {
                                    getActions().updateDeviceName(deviceSettings.getData().getName());
                                } else {
                                    LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** fetchDeviceNameApiRequest No result fetched");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LoggerWrapper.e(MainRepository.class.getSimpleName(), "**** fetchDeviceNameApiRequest  error " + e.getMessage());
                            }
                        }));
    }
}


