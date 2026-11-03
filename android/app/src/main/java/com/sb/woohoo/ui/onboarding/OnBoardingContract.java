package com.sb.woohoo.ui.onboarding;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link OnboardingActivity}.
 *
 * @author Ritwik Jamuar
 */
public interface OnBoardingContract {
    interface View {
        void showError(String errorMessage);
    }

    interface Actions {
        void onHubRegistrationSuccess();

        void onAPICallError(String errorMessage);
    }
}