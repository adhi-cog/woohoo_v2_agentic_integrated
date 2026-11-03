package com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail;

import static com.sb.woohoo.analytics.CustomEvents.category_name_tourism;
import static com.sb.woohoo.analytics.CustomEvents.event_name_st;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_PACKAGES_DETAILS;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.analytics.TourismEvent;
import com.sb.woohoo.custom.QRDialog;
import com.sb.woohoo.databinding.FragmentPackagesDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.DataList;
import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.di.DaggerPackagesDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.di.PackagesDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.di.PackagesDetailModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.utils.QRUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class PackagesDetailFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private DataList category;
    private FragmentPackagesDetailBinding binding;
    private Dialog mQRDialog = null;
    public static PackagesDetailFragment newInstance() {
        return new PackagesDetailFragment();
    }

    @Inject
    AnalyticsHelper helper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.EventsPackageFragment(), null);
        binding = FragmentPackagesDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        PackagesDetailComponent packagesDetailComponent = DaggerPackagesDetailComponent.builder()
                .packagesDetailModule(new PackagesDetailModule(this)).build();
        packagesDetailComponent.injectPackagesDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (getArguments() != null) {
            category = (DataList) getArguments().getSerializable(ITEM_PACKAGES_DETAILS);
        }
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    private void initViews() {
        if (category != null)
        {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getName());
            binding.txtDescription.setText(getString(R.string.description)+" : "+Html.fromHtml(category.getDescription()));

            binding.txtLocation.setText(getString(R.string.location)+" : "+category.getCity());
            binding.txtDate.setText(getString(R.string.list_price)+" : "+ category.getListPrice()+ "      "+getString(R.string.discount_price)+" : "+category.getDiscountedPrice());
            binding.txtTime.setText(getString(R.string.duration)+" : "+ category.getDuration()+ " "+category.getDurationUnit());

            if (!TextUtils.isEmpty(category.getBackgroundImage())) {
                Picasso.get().load(category.getBackgroundImage()).into(binding.imgBannerDefault);
            }

            if(category != null && category.getId() != null && !category.getId().isEmpty())
            {
                binding.btnBookNow.setVisibility(View.VISIBLE);
                binding.btnBookNow.setOnClickListener(view ->
                {
                    showQRDialog(category.getId());

                    StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
                    statsDumpRequest.setCategoryName(category_name_tourism);
                    statsDumpRequest.setEventName(event_name_st);
                    statsDumpRequest.setIntentIdentifier(""+category.getId());
                    statsDumpRequest.setIntentIdentifierName(category.getName());
                    getParentActivity().callStatsDumpAPI(statsDumpRequest);

                    if (category != null) {
                        helper.bookItem(new TourismEvent.SelectItem(category.getName(),
                                category.getId()));
                    }
                });
            }else{
                binding.btnBookNow.setVisibility(View.GONE);
            }
        }
    }


    private void showQRDialog(String aEndPoint)
    {
        String lUrl = QRUtils.INSTANCE.formUrl(requireContext(), aEndPoint);
        if(!lUrl.isEmpty())
        {
            Bitmap lBitmap = QRUtils.INSTANCE.generateQRCode(lUrl, -1);
            if(lBitmap != null) {
                mQRDialog = new QRDialog(requireActivity(), lBitmap);
                if(!mQRDialog.isShowing()){
                    mQRDialog.show();
                }
            }
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
        super.onDestroy();
        if(mQRDialog != null && mQRDialog.isShowing()){
            mQRDialog.dismiss();
            mQRDialog = null;
        }
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
//        fnBCategoriesPresenter.presenterCleanUps();
    }
}
