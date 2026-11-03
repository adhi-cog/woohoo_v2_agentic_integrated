package com.sb.woohoo.ui.onboarding;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubParameters;

import javax.inject.Inject;

/**
 * Presenter for {@link OnboardingActivity}.
 *
 * @author Ritwik Jamuar.
 */
public class OnBoardingPresenter extends BasePresenter implements OnBoardingContract.Actions {
    private OnboardingActivity mView;
    private OnBoardingRepository mRepository;

    @Inject
    public OnBoardingPresenter(OnboardingActivity view, OnBoardingRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(OnBoardingPresenter.this);
    }

    /*-------------------------------------------------- Register Hub Details -------------------------------------------------*/

    /**
     * Requests {@link OnBoardingRepository} to perform an API call for registering Hub Details to the Backend.
     *
     * @param parameters Instance of {@link RegisterHubParameters} containing details of Hub Device.
     */
    void registerHub(RegisterHubParameters parameters) {
        mRepository.registerHubDetails(parameters);
    }

    @Override
    public void onHubRegistrationSuccess() {
//		SharedPrefsHelper helper = WooHooApp.getSharedPrefsHelper ();
//		helper.put ( SharedPrefsKey.PREF_KEY_HUB_ID, hubID );
        LoggerWrapper.i(OnBoardingPresenter.class.getSimpleName(), "onHubRegistrationSuccess ()");
    }

    /*----------------------------------------------------- Common Methods ----------------------------------------------------*/

    @Override
    public void onAPICallError(String errorMessage) {
        mView.showError(errorMessage);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
}