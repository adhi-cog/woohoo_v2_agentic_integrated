package com.sb.woohoo.ui.main.fragments.buysim.qrscan;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.URL;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sb.woohoo.databinding.FragmentQrScanBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.RedirectUrl;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class QRScanFragment extends FeatureFragment<MainActivity>{

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
   QRScanPresenter presenter;
    private FragmentQrScanBinding binding;
    private RedirectUrl redirectUrl;

    public static QRScanFragment newInstance() {
        QRScanFragment fragment = new QRScanFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQrScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            redirectUrl = (RedirectUrl) getArguments().getSerializable(URL);
        }catch (Exception e){

        }
        initViews();
        generateQRCode(redirectUrl.getRedirectUrl());
    }
    private void initViews() {
        binding.confirmButton.setOnClickListener(view1 -> {
            Fragment currentFragment = FragmentTransactionManager.getCurrentFragment(getParentActivity().getSupportFragmentManager());
            while (!(currentFragment instanceof HomeFragment)) {
                getParentActivity().onBackPressed();
                currentFragment = FragmentTransactionManager.getCurrentFragment(getParentActivity().getSupportFragmentManager());
            }
        });

    }

    private void generateQRCode(String url) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Set the dimensions of the QR code
            int width = 300;
            int height = 300;
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

            // Create a bitmap from the BitMatrix
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            // Set the bitmap to the ImageView
            binding.ivQrScan.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText("");
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
