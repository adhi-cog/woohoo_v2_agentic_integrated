package com.sb.woohoo.ui.onboarding.login;

import static com.sb.woohoo.ui.featuresettings.DeviceIdExtensionKt.getTestEmailId;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentLoginBinding;
import com.sb.woohoo.ui.base.BaseFragment;
import com.sb.woohoo.ui.featuresettings.FeatureSettingsActivity;
import com.sb.woohoo.ui.main.helpers.ConnectivityHelper;
import com.sb.woohoo.ui.onboarding.OnboardingActivity;
import com.sb.woohoo.ui.onboarding.login.di.DaggerLoginComponent;
import com.sb.woohoo.ui.onboarding.login.di.LoginComponent;
import com.sb.woohoo.ui.onboarding.login.di.LoginModule;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;


public class LoginFragment
        extends BaseFragment<OnboardingActivity>
        implements LoginContract.View,
        AdapterView.OnItemSelectedListener {

    // Present.
    @Inject
    LoginPresenter loginPresenter;
    @Inject
    ConnectivityHelper connectivityHelper;
    @Inject
    AnalyticsHelper helper;

    private boolean isPasswordVisible = false;


    public LoginFragment() { /*Required empty public constructor*/ }

    private FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /****************************************** {@link BaseFragment} Methods ******************************************/

    @Override
    public String createTag() {
        return LoginFragment.class.getSimpleName();
    }

    /******************************************* Callbacks *******************************************/

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            final ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeViews(view);
        attachParent((OnboardingActivity) getActivity());
        injectFragment();
        super.onViewCreated(view, savedInstanceState);
        helper.track(new Screen.Login(), null);
        binding.editEmail.requestFocus();

        setHubId();

        setClickEvents();


//        String langCode = LocaleHelper.getLanguage(getActivity());
//
//        lang[0] = "Language ("+langCode+")";
//
//        binding.spinner.setOnItemSelectedListener(this);
//        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, lang);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.spinner.setAdapter(aa);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHubId();
    }

    /********************************************* {@code private} Methods ********************************************/

    private void injectFragment() {
        LoginComponent loginComponent =
                DaggerLoginComponent
                        .builder()
                        .loginModule(new LoginModule(this))
                        .build();
        loginComponent.injectLoginFragment(this);
    }

    private void initializeViews(View view) {
    }

    private void toggleButton(Button button, boolean toggle) {
        button.setEnabled(toggle);
    }

    private void toggleEditText(EditText editText, boolean toggle) {
        editText.setFocusable(toggle);
        editText.setFocusableInTouchMode(toggle);
        editText.setEnabled(toggle);
        editText.setCursorVisible(toggle);
    }

    private void toggleUIElements(boolean toggle) {
        toggleButton(binding.btnLogin, toggle);
        toggleEditText(binding.editEmail, toggle);
        toggleEditText(binding.editPassword, toggle);
    }

    /************************************** {@link LoginContract.View} Callbacks **************************************/

    @Override
    public void changeFragment() {
        //Hiding the keyboard
        MethodUtils.hideSoftInputKeyboard(getView(), getActivity());
    }

    @Override
    public void showHomePage() {
        getParentActivity().openMainActivity(true);
    }

    @Override
    public void registerHub() {
        getParentActivity().registerHub();
    }

    @Override
    public void showErrorMessage(int stringResource) {
        Toast.makeText(getActivity(), getResources().getString(stringResource), Toast.LENGTH_SHORT).show();
        toggleUIElements(true);
    }

    @Override
    public void showErrorMessage(String stringMessage) {
        Toast.makeText(getActivity(), stringMessage, Toast.LENGTH_SHORT).show();
        toggleUIElements(true);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        toggleUIElements(false);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public String getEmailAddress() {
        return binding.editEmail == null ? "" : binding.editEmail.getText().toString();
    }

    @Override
    public String getPassword() {
        return binding.editPassword == null ? "" : binding.editPassword.getText().toString();
    }

    public void setHubId() {
        this.binding.hubId.setText(loginPresenter.getDeviceID());
        setTestEmailId(loginPresenter.getDeviceID());
    }

    private void setTestEmailId(String testDeviceID) {
        binding.editEmail.setText(getTestEmailId(testDeviceID));
    }

    /******************************************** onClick Listeners *******************************************/

    private void setClickEvents() {
        binding.tvSetFeatures.setOnClickListener(view -> onSetFeatureClicked());
        binding.loginRootLayout.setOnClickListener(view -> onRootClicked());
        binding.btnLogin.setOnClickListener(view -> onLoginClicked());
        binding.hubId.setOnClickListener(view -> onHubIdClicked());
        binding.passwordToggleButton.setOnClickListener(view -> onPasswordToggleClicked());
    }
    void onSetFeatureClicked() {
        getParentActivity().startActivity(new Intent(getParentActivity(), FeatureSettingsActivity.class));
    }

    void onRootClicked() {
        MethodUtils.hideSoftInputKeyboard(getActivity());
    }

    void onLoginClicked() {
        if (MethodUtils.isNetworkAvailable(getParentActivity())) {
            checkForActiveInternet();
        } else {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.check_internet));
        }
    }

    void onHubIdClicked() {
        MethodUtils.copyTextToClipBoard(getParentActivity(), loginPresenter.getDeviceID(), getString(R.string.message_device_id_copied));
    }

    private void checkForActiveInternet() {
        connectivityHelper.setListener(new ConnectivityHelper.ConnectivityHandlerCallback() {
            @Override
            public void internetDown() {
                Toast.makeText(getActivity(), getString(R.string.wifi_no_internet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void serverDown() {
                Toast.makeText(getActivity(), getString(R.string.network_issues), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void internetUp() {
                loginPresenter.startLogin();
            }
        });
        connectivityHelper.checkInternet();
    }
    void onPasswordToggleClicked() {
        binding.editPassword.setSelection(binding.editPassword.getText().length());
        if (!isPasswordVisible) {
            isPasswordVisible = true;
            binding.passwordToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_icon_show));
            binding.editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.editPassword.setSelection(binding.editPassword.getText().length());
        } else {
            isPasswordVisible = false;
            binding.passwordToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_hide_password));
            binding.editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.editPassword.setSelection(binding.editPassword.getText().length());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (view != null) {
            ((TextView) view).setPadding(20, 0, 20, 0);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds
                    (null, null,
                            AppCompatResources.getDrawable(getActivity(), R.drawable.ic_spinner),
                            null);
            ((TextView) view).setCompoundDrawablePadding(20);
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
        }
        if (position == 1) {
            LocaleHelper.setLocale(getParentActivity(), Constants.LANGUAGE.ENGLISH);
            Intent intent = getParentActivity().getIntent();
            getParentActivity().finish();
            startActivity(intent);
        } else if (position == 2) {
            LocaleHelper.setLocale(getParentActivity(), Constants.LANGUAGE.ARABIC);
            Intent intent = getParentActivity().getIntent();
            getParentActivity().finish();
            startActivity(intent);
        } else if (position == 3) {
            LocaleHelper.setLocale(getParentActivity(), Constants.LANGUAGE.CHINESH);
            Intent intent = getParentActivity().getIntent();
            getParentActivity().finish();
            startActivity(intent);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginPresenter.presenterCleanUps();
    }

    public void updateAnalyticsInstance() {
        helper.updateHelper();
    }
}
