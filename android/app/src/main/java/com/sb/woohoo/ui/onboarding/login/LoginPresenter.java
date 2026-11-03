package com.sb.woohoo.ui.onboarding.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiFragment;
import com.sb.woohoo.ui.main.fragments.settings.wifi.WifiPresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;

import java.util.Objects;

import javax.inject.Inject;

import androidx.core.app.ActivityCompat;

import io.sentry.Sentry;
import io.sentry.protocol.User;

public class LoginPresenter
        extends BasePresenter
        implements LoginContract.Actions {
    private LoginFragment mView;
    private LoginRepository loginRepository;
    private WifiFragment mWiFiSelectionFragment;

    @Inject
    LoginPresenter(LoginFragment loginFragment, LoginRepository loginRepository, WifiFragment wifiSelectionFragment) {
        this.mView = loginFragment;
        this.loginRepository = loginRepository;
        this.loginRepository.onAttach(this);
        this.mWiFiSelectionFragment = wifiSelectionFragment;
    }

    @Override
    public void startLogin() {
        String email = mView.getEmailAddress();
        String password = mView.getPassword();

        if (email != null && password != null) {
            //Email validation can be changed once the confirmation with API team
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mView.showErrorMessage(R.string.error_email_msg);
            } else if (password.isEmpty()) {
                mView.showErrorMessage(R.string.error_password_msg);
            } else {
                mView.showProgress();
                loginRepository.startLoginRequest(email, password, getDeviceID());
            }
        } else {
            //Just a random check as of now..
            mView.showErrorMessage(R.string.error_generic);
        }
    }

    @Override
    public void loginSuccess(LoginModel loginModel) {
        // Hide the Progress Bar.0
        mView.hideProgress();

        //We can save the data i.e. LoginModel to SharedPreferences here and then we can change the fragment
        loginRepository.saveUserData(loginModel);
        Sentry.configureScope(scope -> {
            User user = new User();
            user.setEmail(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_EMAIL, ""));
            user.setId(WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""));
            scope.setUser(user);
        });

        // Update Firebase Analytics instance
        mView.updateAnalyticsInstance();

        // Tell the View to call registerHub () method of OnboardingActivity.
        mView.registerHub();

        // As of now, showing launching the Home Screen directly, ignoring Face Recognition Screen during OnBoarding.
        mView.showHomePage();

        // To set the Face Recognition, uncomment the below code.
		/*// If Factory Reset is performed, then just change the Fragment in the view-pager, else start MainActivity.
		if ( loginRepository.isFactoryResetPerformed () ) {
			mView.changeFragment ();
		} else {
			mView.showHomePage ();
		}*/
    }

    @Override
    public void loginError(Object msg) {
        mView.hideProgress();
        String msgString;
        if (msg instanceof Integer)
            msgString = mView.getString((Integer) msg);
        else
            msgString = (String) msg;
        mView.showErrorMessage(msgString);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @SuppressLint("HardwareIds")
    @Override
    public String getDeviceID() {
        String id = null;
        String testID = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_TEST_DEVICE_ID, Constants.USE_SYSTEM_ID);
        if (!testID.equalsIgnoreCase(Constants.USE_SYSTEM_ID)) {
            id = testID;
        }  else {
            id = WooHooApp.sDeviceId;
        }
        // we are using ssaid since we are going android-9. else {
           /* TelephonyManager manager = (TelephonyManager) Objects.requireNonNull(mView.getContext()).getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mView.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (manager != null) id = manager.getDeviceId();
            }
        }*/
        if (TextUtils.isEmpty(id)) {
            id = getDataFromWifiSelectionFragment();
        }
        return id;
    }

    private String getDataFromWifiSelectionFragment() {
        String data = "NA";
        if (mWiFiSelectionFragment != null) {
            data = getMACAddress(mWiFiSelectionFragment.getWifiPresenter());
        }
        return data;
    }

    private String getMACAddress(WifiPresenter presenter) {
        return presenter.getWifiInformation("MAC_ADDRESS");
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        loginRepository.repositoryCleanUps();
    }
}
