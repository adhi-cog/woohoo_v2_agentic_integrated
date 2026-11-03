package com.sb.woohoo.ui.onboarding.login;

/**
 * Created by admin on 2/20/18.
 */

public interface LoginContract {
    interface View {
        void changeFragment();

        /**
         * Tells View to show Home Page by Navigating to {@link com.sb.woohoo.ui.main.MainActivity}.
         */
        void showHomePage();

        /**
         * Tells the View to call {@code registerHub ()} method in {@link com.sb.woohoo.ui.onboarding.OnboardingActivity}
         */
        void registerHub();

        void showErrorMessage(int stringResource);

        void showErrorMessage(String stringMessage);

        void showProgress();

        void hideProgress();

        /**
         * Gets the Email Address from {@link android.widget.EditText} in the UI.
         *
         * @return String denoting EMail Address entered by User.
         */
        String getEmailAddress();

        /**
         * Gets the Password from {@link android.widget.EditText} in the UI.
         *
         * @return String denoting Password entered by User.
         */
        String getPassword();

    }

    interface Actions {
        void startLogin();

        void loginSuccess(LoginModel loginModel);

        void loginError(Object msg);

        String getDeviceID();
    }
}