package com.sb.woohoo.ui.main.fragments.qrcode;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentQrcodeBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.qrcode.di.DaggerQRCodeComponent;
import com.sb.woohoo.ui.main.fragments.qrcode.di.QRCodeComponent;
import com.sb.woohoo.ui.main.fragments.qrcode.di.QRCodeModule;
import com.sb.woohoo.ui.main.fragments.qrcode.pojo.QRCodeResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class QRCodeFragment extends FeatureFragment<MainActivity> implements QRCodeContract.Views{


    private String sTitle;

    private String qrCodeStr;
    private int qrCodeDrawable;
    @Inject
    AnalyticsHelper helper;

    @Inject
    QRCodePresenter qrCodePresenter;
    private FragmentQrcodeBinding binding;

    public static QRCodeFragment newInstance() {
        QRCodeFragment fragment = new QRCodeFragment();
        return fragment;
    }

    public static QRCodeFragment newInstance(String title, String qrCodeString, int qrCodeDrawable) {
        QRCodeFragment qrCodeFragment = new QRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("qr_code_string", qrCodeString);
        bundle.putInt("qr_code_drawable", qrCodeDrawable);
        qrCodeFragment.setArguments(bundle);
        return qrCodeFragment;
    }

    @Override
    public String createTag() {
        return QRCodeFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Covid(),  null);
        if (getArguments() != null){
            if (!TextUtils.isEmpty(getArguments().getString("title"))){
                sTitle = getArguments().getString("title");
            }
            qrCodeStr = getArguments().getString("qr_code_string");
            qrCodeDrawable = getArguments().getInt("qr_code_drawable");
        }
        binding = FragmentQrcodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        QRCodeComponent covidComponent = DaggerQRCodeComponent.builder()
                .qRCodeModule(new QRCodeModule(this))
                .build();

        covidComponent.injectQRCodeFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (TextUtils.isEmpty(sTitle))
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.qr_code));
        else
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.qr_code_feature, sTitle));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals( Constants.GBTB_DOMAIN_ID)){
            if (qrCodeDrawable != 0){
                Picasso.get()
                        .load(qrCodeDrawable)
                        .fit()
                        .into(binding.qrCode);
            }
            if (!TextUtils.isEmpty(qrCodeStr)){
                binding.tvQrCode.setText(qrCodeStr);
                getParentActivity().onRespondToUser(qrCodeStr);
            }
        }else{
            qrCodePresenter.qrCodeAPIRequest();
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }


    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        qrCodePresenter.presenterCleanUps();
    }

    @Override
    public void qrCodeMessage(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();
    }

    @Override
    public void qrCodeResult(QRCodeResponse response) {
        if(!TextUtils.isEmpty(response.getData())){
            Picasso.get()
                    .load(response.getData().trim())
                    .fit()
                    .into(binding.qrCode);
        }
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

}
