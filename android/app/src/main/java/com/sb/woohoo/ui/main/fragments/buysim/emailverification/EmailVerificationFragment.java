package com.sb.woohoo.ui.main.fragments.buysim.emailverification;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.EMAIL_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PACKAGE_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PHONE_ID;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentEmailVerificationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.di.DaggerEmailVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.di.EmailVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.di.EmailVerificationModule;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;
import com.sb.woohoo.ui.main.fragments.faq.questions.FaqQnAFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class EmailVerificationFragment extends FeatureFragment<MainActivity> implements EmailVerificationContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    EmailVerificationPresenter presenter;
    private FragmentEmailVerificationBinding binding;

    private GetPhoneDetails phoneId;
    private GetEachPackageDetails packageId;

    public static EmailVerificationFragment newInstance() {
        EmailVerificationFragment fragment = new EmailVerificationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        packageId = (GetEachPackageDetails) getArguments().getSerializable(PACKAGE_ID);
        phoneId = (GetPhoneDetails) getArguments().getSerializable(PHONE_ID);
        initViews();
    }

    private void initViews() {
        binding.btnNext.setOnClickListener(view1 -> {

            if(binding.editEmail.getText().toString().isEmpty()){
                MethodUtils.showToastMessage(getContext(),  "Empty EmailId");
            }else if(!(binding.editEmail.getText().toString().equals(binding.reEditEmail.getText().toString()))){
                MethodUtils.showToastMessage(getContext(),  "Mis-match in EmailId");
            }else{
                if(isKeyboardOpen()){
                    hideKeyboard();
                }
                MethodUtils.showToastMessage(getContext(),  "Otp Sending....");
                presenter.sendOtpToEmailId(binding.editEmail.getText().toString());
            }
        });

    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }


    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess() {
        MethodUtils.showToastMessage(getContext(),  "Otp Send Successfully");
        Bundle bundle = new Bundle();
        bundle.putSerializable(PACKAGE_ID, packageId);
        bundle.putSerializable(PHONE_ID, phoneId);
        bundle.putString(EMAIL_ID, binding.editEmail.getText().toString());
        OtpVerificationFragment otpVerificationFragment = OtpVerificationFragment.newInstance();
        otpVerificationFragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(otpVerificationFragment);
    }

    @Override
    public void errorMessage(int emptyResultCode) {

    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {

    }

    @Override
    public void initDagger() {
        EmailVerificationComponent emailVerificationComponent = DaggerEmailVerificationComponent.builder()
                .emailVerificationModule(new EmailVerificationModule(this))
                .build();

        emailVerificationComponent.injectEmailVerificationFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.email_verification));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        presenter.presenterCleanUps();
    }

    private void hideKeyboard() {
        if(getView() != null) {
            InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
        }
    }

    private boolean isKeyboardOpen() {
        InputMethodManager imm = (InputMethodManager) getParentActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }
}
