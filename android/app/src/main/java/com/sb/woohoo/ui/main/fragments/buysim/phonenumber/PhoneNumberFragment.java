package com.sb.woohoo.ui.main.fragments.buysim.phonenumber;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PACKAGE_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PHONE_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentPhoneNumberBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.EmailVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.di.DaggerPhoneNumberComponent;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.di.PhoneNumberComponent;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.di.PhoneNumberModule;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class PhoneNumberFragment extends FeatureFragment<MainActivity> implements PhoneNumberContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    private PhoneNumberAdapter phoneNumberAdapter;
    @Inject
    PhoneNumberPresenter presenter;
    private FragmentPhoneNumberBinding binding;
    private GetEachPackageDetails packageId;
    private GetPhoneDetails selectedPhone;
    public static PhoneNumberFragment newInstance() {
        PhoneNumberFragment fragment = new PhoneNumberFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        packageId = (GetEachPackageDetails) getArguments().getSerializable(PACKAGE_ID);
        initViews();
        presenter.getAllPhoneNumbers();
    }

    private void initViews() {
        binding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        phoneNumberAdapter = new PhoneNumberAdapter(getActivity(), new ArrayList<>(), data ->{
             binding.btnNext.setVisibility(View.VISIBLE);
            selectedPhone = data;
        });

        binding.btnNext.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PACKAGE_ID, packageId);
            bundle.putSerializable(PHONE_ID, selectedPhone);
            EmailVerificationFragment fragment = EmailVerificationFragment.newInstance();
            fragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(fragment);

        });
        binding.recyclerRestaurants.setAdapter(phoneNumberAdapter);
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
    public void onSuccess(ArrayList<GetPhoneDetails> data) {
        phoneNumberAdapter.updatePackageList(data);
    }

    @Override
    public void errorMessage(int emptyResultCode) {

    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {

    }

    @Override
    public void initDagger() {
        PhoneNumberComponent phoneNumberComponent = DaggerPhoneNumberComponent.builder()
                .phoneNumberModule(new PhoneNumberModule(this))
                .build();

        phoneNumberComponent.injectPhoneNumberFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.select_number));
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
}
