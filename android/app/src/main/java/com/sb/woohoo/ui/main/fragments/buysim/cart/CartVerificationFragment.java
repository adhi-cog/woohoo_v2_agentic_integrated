package com.sb.woohoo.ui.main.fragments.buysim.cart;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.EMAIL_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PACKAGE_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PHONE_ID;
import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.URL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentCartVerificationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.cart.di.CartVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.cart.di.CartVerificationModule;
import com.sb.woohoo.ui.main.fragments.buysim.cart.di.DaggerCartVerificationComponent;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.RedirectUrl;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;
import com.sb.woohoo.ui.main.fragments.buysim.qrscan.QRScanFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class CartVerificationFragment extends FeatureFragment<MainActivity> implements CartVerificationContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    CartVerificationPresenter presenter;
    private FragmentCartVerificationBinding binding;
    private String email;
    private GetPhoneDetails phoneId;
    private GetEachPackageDetails packageId;

    public static CartVerificationFragment newInstance() {
        CartVerificationFragment fragment = new CartVerificationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            email = getArguments().getString(EMAIL_ID);
            packageId = (GetEachPackageDetails) getArguments().getSerializable(PACKAGE_ID);
            phoneId = (GetPhoneDetails) getArguments().getSerializable(PHONE_ID);
        } catch (Exception e) {

        }
        initViews();
    }

    private void initViews() {
        int total = packageId.getPrice()+29;
        binding.txtPackage.setText(packageId.getName());
        binding.txtPrice.setText(""+packageId.getPrice()+" "+packageId.getPriceUnit());
        binding.txtUsPrice.setText("$"+packageId.getUsdPrice());
        binding.txtOne.setText("• "+packageId.getInfo().get(0));
        binding.txtTwo.setText("• "+packageId.getInfo().get(1));
        binding.txtThree.setText("• "+packageId.getInfo().get(2));
        binding.txtFour.setText("• "+packageId.getInfo().get(3));
        binding.txtPriceOne.setText(""+packageId.getPrice()+" "+packageId.getPriceUnit());
        binding.txtTotal.setText(""+total+" "+packageId.getPriceUnit());

        binding.confirmButton.setOnClickListener(view1 -> {
            presenter.callBuyPackageApi(email, packageId, packageId);
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
    public void initDagger() {
        CartVerificationComponent cartVerificationComponent = DaggerCartVerificationComponent.builder()
                .cartVerificationModule(new CartVerificationModule(this))
                .build();

        cartVerificationComponent.injectCartVerificationFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.cart));
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

    @Override
    public void onSuccess(RedirectUrl data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(URL, data);
        QRScanFragment fragment = QRScanFragment.newInstance();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fragment);
    }

    @Override
    public void errorMessage(int emptyResultCode) {

    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {

    }

}
