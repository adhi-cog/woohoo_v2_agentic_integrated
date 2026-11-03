package com.sb.woohoo.ui.main.fragments.overview.saudieventdetail;

import static com.sb.woohoo.analytics.CustomEvents.category_name_tourism;
import static com.sb.woohoo.analytics.CustomEvents.event_name_st;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_EVENT_DETAILS;
import static com.sb.woohoo.ui.main.sip.utils.FileUtils.convertDate;

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
import com.sb.woohoo.databinding.FragmentEventDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.di.DaggerEventDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.di.EventDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.di.EventDetailModule;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.DataList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.utils.QRUtils;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class EventDetailFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private DataList category;
    private FragmentEventDetailBinding binding;

    private Dialog mQRDialog = null;

    @Inject
    AnalyticsHelper helper;

    public static EventDetailFragment newInstance() {
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        return eventDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.EventDetailFragment(), null);
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        EventDetailComponent eventDetailComponent = DaggerEventDetailComponent.builder()
                .eventDetailModule(new EventDetailModule(this)).build();
        eventDetailComponent.injectEventDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (getArguments() != null) {
            category = (DataList) getArguments().getSerializable(ITEM_EVENT_DETAILS);
        }
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    private void initViews() {
        if (category != null) {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getTitle());
            binding.txtDescription.setText(Html.fromHtml(category.getCopy()));

            binding.txtLocation.setText(getString(R.string.location)+" : "+category.getCity());
            binding.txtDate.setText(getString(R.string.start)+" : "+ convertDate(category.getStartDate())+ "      "+getString(R.string.end)+" : "+convertDate(category.getEndDate()));
            binding.txtTime.setText(getString(R.string.from)+" : "+ category.getStartTime()+ "      "+getString(R.string.to)+" : "+category.getEndTime());

            if (!TextUtils.isEmpty(category.getFeatureEventImage())) {
                Picasso.get().load(category.getFeatureEventImage()).into(binding.imgBannerDefault);
            } else if (!TextUtils.isEmpty(category.getS7featureEventImage())) {
                Picasso.get().load(category.getS7featureEventImage()).into(binding.imgBannerDefault);
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
                    statsDumpRequest.setIntentIdentifierName(category.getTitle());
                    getParentActivity().callStatsDumpAPI(statsDumpRequest);
                    if (category != null) {
                        helper.bookItem(new TourismEvent.SelectItem(
                                category.getTitle(),
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
        getActivity().onBackPressed();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(false);
        if(mQRDialog != null && mQRDialog.isShowing()){
            mQRDialog.dismiss();
            mQRDialog = null;
        }
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
//        fnBCategoriesPresenter.presenterCleanUps();
    }
}
