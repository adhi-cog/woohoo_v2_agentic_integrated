package com.sb.woohoo.ui.main.fragments.settings;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateModel;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.utils.db.DbHandler;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.sentry.Sentry;
import io.sentry.protocol.User;

/**
 * Repository for {@link SettingsPresenter}.
 */
class SettingsRepository extends BaseRepository<SettingsPresenter> {
    private SharedPrefsHelper mSharedPrefsHelper;
    private WooHooApiCall wooHooApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    SettingsRepository(SharedPrefsHelper sharedPrefsHelper, WooHooApiCall wooHooApiCall, CompositeDisposable compositeDisposable) {
        this.mSharedPrefsHelper = sharedPrefsHelper;
        this.wooHooApiCall = wooHooApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void performLogOut() {
        compositeDisposable.add(wooHooApiCall.logOutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {
                        onLogOutSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getActions().onAPIError(e.getMessage());
                    }
                }));

    }

    /**
     * Tells {@link SharedPrefsHelper} to Clear {@link android.content.SharedPreferences} of this application.
     */
    private void onLogOutSuccess() {
        SipAccountManager.getInstance().clearCredentialsCache();
        mSharedPrefsHelper.logOut();
        DbHandler dbHandler = new DbHandler();
        dbHandler.deleteFileIfthere();
        // Since performing log-out also clears the below Key, so setting it explicitly will make sure that
        // logging out is not interpreted as Factory Reset.
        mSharedPrefsHelper.put(SharedPrefsKey.PREF_KEY_IS_FACTORY_RESET_DONE, false);

        String userID = mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_USER_ID, "");
        if (userID.isEmpty()) {
            Sentry.configureScope(scope -> {
                scope.setUser(null);
            });
            getActions().onLoggedOutSuccessfully();
        } else {
            getActions().onAPIError(R.string.something_went_wrong);
        }
    }

    void preformCheckUpdate() {
        String hubID = mSharedPrefsHelper.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        compositeDisposable.add(wooHooApiCall.getUpdate(hubID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateModel>() {

                    @Override
                    public void onSuccess(UpdateModel updateModel) {
                        responseCall(updateModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getActions().onCheckUpdateFailed();
                    }
                }));
    }

    private void responseCall(UpdateModel updateModel) {
        if (updateModel != null) {
            UpdateDataModel updateDataModel = updateModel.getData();
            if (updateDataModel != null) {
                getActions().onCheckUpdateSuccess(updateDataModel);
            } else {
                getActions().onCheckUpdateFailed();
            }
        } else {
            getActions().onCheckUpdateFailed();
        }
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
        compositeDisposable.dispose();
    }
}