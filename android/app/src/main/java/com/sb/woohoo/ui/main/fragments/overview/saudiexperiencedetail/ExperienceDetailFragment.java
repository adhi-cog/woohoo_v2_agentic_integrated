package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail;

import static com.sb.woohoo.analytics.CustomEvents.category_name_tourism;
import static com.sb.woohoo.analytics.CustomEvents.event_name_st;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_EXPERIENCE_DETAILS_ID;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.TourismEvent;
import com.sb.woohoo.custom.QRDialog;
import com.sb.woohoo.databinding.FragmentExperienceDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.di.DaggerExperienceDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.di.ExperienceDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.di.ExperienceDetailModule;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo.Data;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.QRUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class ExperienceDetailFragment extends FeatureFragment<MainActivity> implements ExperienceDetailContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private String experienceId;
    private FragmentExperienceDetailBinding binding;

    @Inject
    ExperienceDetailPresenter experienceDetailPresenter;

    @Inject
    AnalyticsHelper helper;

    private Dialog mQRDialog = null;

    public static ExperienceDetailFragment newInstance() {
        ExperienceDetailFragment experienceDetailFragment = new ExperienceDetailFragment();
        return experienceDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExperienceDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ExperienceDetailComponent eventDetailComponent = DaggerExperienceDetailComponent.builder()
                .experienceDetailModule(new ExperienceDetailModule(this)).build();
        eventDetailComponent.injectExperienceDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (getArguments() != null) {
            experienceId = getArguments().getString(ITEM_EXPERIENCE_DETAILS_ID);
        }
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    private void initViews() {
        if (experienceId != null) {
            experienceDetailPresenter.experienceAPIRequest(experienceId, LocaleHelper.getLanguage(getActivity()));
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
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        experienceDetailPresenter.presenterCleanUps();
    }

    Data experienceData = new Data();

    public void onCategoriesResponse(Data data) {
        if (data != null) {
            experienceData = data;
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(experienceData.getName());
            binding.txtLocation.setText(requireContext().getString(R.string.location)+" : "+experienceData.getPrimaryDestination());
            binding.txtLocationDetail.setText(requireContext().getString(R.string.address)+" : "+experienceData.getFormattedAddress());
            binding.txtAge.setText(requireContext().getString(R.string.suitable_for)+"  : "+experienceData.getAgeFrom()+" - "+experienceData.getAgeTo()+" "+getString(R.string.years_old));
            binding.txtRefund.setText(requireContext().getString(R.string.refund_policy)+"  : "+experienceData.getRefundPolicy());
            if (!experienceData.getImages().isEmpty())
                Picasso.get().load(experienceData.getImages().get(0)).into(binding.imgBannerDefault);

            processQR(data);
        }
    }

    private void processQR(Data aData)
    {
        if (aData != null)
        {
            String aLink = aData.qrLink();
            if (aLink != null && !aLink.isEmpty())
            {
                binding.btnBookNow.setVisibility(View.VISIBLE);
                binding.btnBookNow.setOnClickListener(view ->
                {
                    showQRDialog(aLink);
                    StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
                    statsDumpRequest.setCategoryName(category_name_tourism);
                    statsDumpRequest.setEventName(event_name_st);
                    statsDumpRequest.setIntentIdentifier(""+aData.getId());
                    statsDumpRequest.setIntentIdentifierName(aData.getName());
                    getParentActivity().callStatsDumpAPI(statsDumpRequest);
                    helper.bookItem(new TourismEvent.SelectItem(aData.getName(),
                            aData.getId()));
                });
            } else {
                binding.btnBookNow.setVisibility(View.GONE);
            }
        }else{
            binding.btnBookNow.setVisibility(View.GONE);
        }
    }

    private void showQRDialog(String aLink)
    {
        if (URLUtil.isValidUrl(aLink))
        {
            Bitmap lBitmap = QRUtils.INSTANCE.generateQRCode(aLink, -1);
            if(lBitmap != null) {
                mQRDialog = new QRDialog(requireActivity(), lBitmap);
                if(!mQRDialog.isShowing()){
                    mQRDialog.show();
                }
            }
        }
    }

    @Override
    public void onErrorResponse(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(), getString(R.string.try_again));
        }
        getParentActivity().onBackPressed();
    }

    @Override
    public void showProgress() {
        if (binding.shimmerFrameLayout.isShimmerStarted()) {
            binding.shimmerFrameLayout.stopShimmer();
        }
        binding.shimmerFrameLayout.startShimmer();
        binding.shimmerFrameLayout.setVisibility(View.VISIBLE);
        binding.scrollview.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.shimmerFrameLayout.stopShimmer();
        binding.shimmerFrameLayout.setVisibility(View.GONE);
        binding.scrollview.setVisibility(View.VISIBLE);
    }
}
