package com.sb.woohoo.ui.onboarding.di;

import com.sb.woohoo.ui.onboarding.OnboardingActivity;

import dagger.Component;

/**
 * {@link Component} for {@link OnboardingActivity}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = OnBoardingModule.class)
@OnBoardingScope
public interface OnBoardingComponent {
    void injectOnBoardingActivity(OnboardingActivity activity);
}