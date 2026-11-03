package com.sb.woohoo.ui.main.fragments.buysim.otpverification;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.EMAIL_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PACKAGE_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PHONE_ID;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentEmailVerificationBinding;
import com.sb.woohoo.databinding.FragmentOtpVerificationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.di.DaggerEmailVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.di.DaggerOtpVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.di.OtpVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.di.OtpVerificationModule;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class OtpVerificationFragment extends FeatureFragment<MainActivity> implements OtpVerificationContract.Views {

    private static final int TOTAL_TIME = 31000; // 30 seconds
    private static final int INTERVAL = 1000;    // 1 second
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    private String email;
    private GetPhoneDetails phoneId;
    private GetEachPackageDetails packageId;

    @Inject
    OtpVerificationPresenter presenter;
    private FragmentOtpVerificationBinding binding;

    public static OtpVerificationFragment newInstance() {
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCountdown();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        email = getArguments().getString(EMAIL_ID);
        packageId = (GetEachPackageDetails) getArguments().getSerializable(PACKAGE_ID);
        phoneId = (GetPhoneDetails) getArguments().getSerializable(PHONE_ID);
        initViews();
    }
    private void startCountdown() {
        new CountDownTimer(TOTAL_TIME, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.txtOtp.setText("Resend OTP in " + (millisUntilFinished / 1000) + " seconds");
            }

            @Override
            public void onFinish() {
                binding.txtOtp.setText("Resend OTP");
                binding.txtOtp.setClickable(true);
                binding.txtOtp.setEnabled(true);
                binding.txtOtp.setPaintFlags(binding.txtOtp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                binding.txtOtp.setOnClickListener(v -> {
                    Toast.makeText(getParentActivity(), "OTP resent!", Toast.LENGTH_SHORT).show();
                    startCountdown();
                });
            }
        }.start();
    }
    private void initViews() {
        binding.txtEmailIntro.setText("Please check ‘"+email+"’ for OTP");
        binding.btnNext.setOnClickListener(view1 -> {
            if(binding.editOtp.getText().toString().isEmpty()){
                MethodUtils.showToastMessage(getContext(),  "Otp is Empty");
            }else{
                if(isKeyboardOpen()){
                    hideKeyboard();
                }
                presenter.verifyOtp(binding.editOtp.getText().toString(),email);
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
        MethodUtils.showToastMessage(getContext(),  "Otp Verify Successfully");
        Bundle bundle = new Bundle();
        bundle.putSerializable(PACKAGE_ID, packageId);
        bundle.putSerializable(PHONE_ID, phoneId);
        bundle.putString(EMAIL_ID, email);
        CartVerificationFragment fragment = CartVerificationFragment.newInstance();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fragment);
    }

    @Override
    public void errorMessage(int emptyResultCode) {
        MethodUtils.showToastMessage(getContext(),  "Invalid Otp");

    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {

    }

    @Override
    public void initDagger() {
        OtpVerificationComponent otpVerificationComponent = DaggerOtpVerificationComponent.builder()
                .otpVerificationModule(new OtpVerificationModule(this))
                .build();

        otpVerificationComponent.injectOtpVerificationFragment(this);
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
