package com.sb.woohoo.ui.onboarding.di;

import com.google.gson.Gson;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.onboarding.OnBoardingPresenter;
import com.sb.woohoo.ui.onboarding.OnBoardingRepository;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.utils.gps.GPSLocation;

import androidx.fragment.app.FragmentManager;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link OnboardingActivity}.
 *
 * @author Ritwik Jamuar.
 */
@Module
public class OnBoardingModule {
    private OnboardingActivity mActivity;

    public OnBoardingModule(OnboardingActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    public FragmentManager providesFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    @Provides
    public OnBoardingPresenter providesOnBoardingPresenter(OnBoardingRepository repository) {
        return new OnBoardingPresenter(mActivity, repository);
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(mActivity).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public Gson getGson() {
        return WooHooApp.get(mActivity).getWooHooAppComponent().getGson();
    }

    @Provides
    public GPSLocation providesGPSLocation() {
        return new GPSLocation(mActivity);
    }

    @Provides
    OnboardingActivity providesOnBoardingActivity() {
        return mActivity;
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}