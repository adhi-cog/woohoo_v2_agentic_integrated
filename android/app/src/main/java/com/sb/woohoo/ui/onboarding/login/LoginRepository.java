package com.sb.woohoo.ui.onboarding.login;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.UserProperty;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;

import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by admin on 2/20/18.
 */
public class LoginRepository
        extends BaseRepository<LoginPresenter> {
    // Other Components.
    private WooHooApiCall mApiCall;
    private SharedPrefsHelper mSharedPrefsHelper;
    private CompositeDisposable compositeDisposable;


    @Inject
    LoginRepository(WooHooApiCall apiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = apiCall;
        this.mSharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * Performs User Login using {@link WooHooApiCall}.
     *
     * @param userName String denoting User Name ( actually User's Email Address ).
     * @param password String denoting Password.
     */
    void startLoginRequest(String userName, String password, String deviceID) {
        compositeDisposable.add(
                mApiCall.getLoginResponse(userName, password, deviceID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LoginModel>() {
                            @Override
                            public void onSuccess(LoginModel loginModel) {
                                if (loginModel != null) {
                                    getActions().loginSuccess(loginModel);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                handleRxError(e);
                            }
                        }));
    }

    private void handleRxError(Throwable e) {
        if (e instanceof HttpException) {
            ResponseBody body = ((HttpException) e).response().errorBody();
            int code = ((HttpException) e).code();
            //If 502 is not handled then you will get JsonSyntaxException while fetching LoginModel
            //Because 502's response is HTML and others have LoginModel type of Response...
            if (code == 502) {
                getActions().loginError(R.string.server_is_down);
            } else {
                if (body != null) {
                    LoginModel loginModel = null;
                    try {
                        loginModel = new Gson().fromJson(body.string(), LoginModel.class);
                    } catch (Exception e1) {
                        getActions().loginError(e1.getMessage());
                    }
                    if (loginModel != null) {
                        getActions().loginError(loginModel.getError());
                    }
                }
            }
        } else if (e instanceof SSLHandshakeException) {
            getActions().loginError(R.string.ssl_certi_error);
        } else {
            getActions().loginError(e.getMessage());
        }
    }

    /**
     * Saves User DeviceData in {@link android.content.SharedPreferences}.
     *
     * @param userData Instance of {@link LoginModel} containing User DeviceData.
     */
    void saveUserData(LoginModel userData) {
        SharedPrefsHelper helper = mSharedPrefsHelper;
        DataModel dataModel = userData.getData();

        if (dataModel != null) {
            helper.put(SharedPrefsKey.PREF_KEY_FIRST_NAME, dataModel.getFirstName());
            helper.put(SharedPrefsKey.PREF_KEY_LAST_NAME, dataModel.getLastName());
            helper.put(SharedPrefsKey.PREF_KEY_EMAIL, dataModel.getEmail());
            helper.put(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, dataModel.getSessionToken());
            helper.put(SharedPrefsKey.PREF_KEY_STATUS, dataModel.getStatus());
            helper.put(SharedPrefsKey.PREF_KEY_REFRESH_TOKEN, dataModel.getRefreshToken());
            helper.put(SharedPrefsKey.PREF_KEY_USER_ID, dataModel.getUserId());
            helper.put(SharedPrefsKey.PREF_USER_ID_TYPE, dataModel.getUserIdType());
            helper.put(SharedPrefsKey.PREF_DOMAIN_ID, dataModel.getDomainId());
            helper.put(SharedPrefsKey.PREF_INFRA_SYS_URL, dataModel.getInfrasysUrl());

            FirebaseCrashlytics.getInstance().setCustomKey(UserProperty.DOMAIN_ID, dataModel.getDomainId());
            //TODO - update analytics FirebaseAnalytics.getInstance(this).setUserProperty(UserProperty.DOMAIN_ID, dataModel.getDomainId());
        }
    }

//    public static <T> T update(Throwable e, T type) {
//        if (e instanceof HttpException) {
//            ResponseBody body = ((HttpException) e).response().errorBody();
//            if (body != null) {
//                LoginModel loginModel = null;
//                try {
//                    loginModel = new Gson().fromJson(body.string(), LoginModel.class);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                if (loginModel != null) {
//                    getActions().loginError(loginModel.getError());
//                }
//            }
//        }
//        return type;
//    }

    /**
     * Checks whether Factory Reset has been performed by user or not.
     * Factory Reset is considered with boolean value of key {@code SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE}.
     * If this key-value doesn't exist, this means that user has cleared the app's data from Android's App Info.
     * And in that case, it is assumed that Factory Reset has been done, so returns {@code true} value.
     *
     * @return {@code true}, if Factory Reset is performed by User, else {@code false}
     */
    public boolean isFactoryResetPerformed() {
        if (mSharedPrefsHelper.contains(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE)) {
            return mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE, false);
        } else {
            return true;
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}