package com.sb.woohoo.ui.onboarding;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubParameters;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubResponse;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Repository for {@link OnBoardingPresenter}.
 *
 * @author Ritwik Jamuar
 */
public class OnBoardingRepository extends BaseRepository<OnBoardingPresenter> {
    private final String REGISTER_HUB_DETAILS = "registerHubDetails";
    private WooHooApiCall mAPICall;
    private CompositeDisposable compositeDisposable;

    @Inject
    OnBoardingRepository(WooHooApiCall apiCall, CompositeDisposable compositeDisposable) {
        this.mAPICall = apiCall;
        this.compositeDisposable = compositeDisposable;
    }

    /*--------------------------------------- Methods to Register Hub Device Details ---------------------------------------*/

    /**
     * Performs an API call for registering details of Hub Device.
     *
     * @param parameters Instance of {@link RegisterHubParameters} to be given to REST API.
     */
    void registerHubDetails(RegisterHubParameters parameters) {

        compositeDisposable.add(
                mAPICall.registerHubDetails(parameters)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<RegisterHubResponse>() {
                            @Override
                            public void onSuccess(RegisterHubResponse registerHubResponse) {
                                onRegisterHubResponse(registerHubResponse, REGISTER_HUB_DETAILS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                getActions().onAPICallError(e.getMessage());
                            }
                        })
        );
    }

    /*---------------------------------------- Register Hub Device Response Methods ----------------------------------------*/

    /**
     * <p>Manages the parsing of {@link Response} body of type {@link RegisterHubResponse}.</p>
     */
    private void onRegisterHubResponse(RegisterHubResponse hubResponse, String api) {
        if (hubResponse == null) getActions().onAPICallError("DeviceData Corrupted");
        else if (hubResponse.isSuccess() == 200) {
            switch (api) {
                case REGISTER_HUB_DETAILS:
                    handleRegisterHubResponse();
                    break;
            }
        } else getActions().onAPICallError(hubResponse.getMessage());
    }

    /*-------------------------------------- Methods to Handle RegisterDeviceResponse --------------------------------------*/

    /**
     * Handles {@link RegisterHubResponse} for API Call : Register Hub Details.
     */
    private void handleRegisterHubResponse() {
        getActions().onHubRegistrationSuccess();
    }
}