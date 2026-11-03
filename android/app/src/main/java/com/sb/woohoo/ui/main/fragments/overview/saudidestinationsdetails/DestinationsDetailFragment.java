package com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails;

import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_DESTINATIONS_DETAILS;
import static com.sb.woohoo.utils.Constants.BASE_URL_FOR_ST;

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
import com.sb.woohoo.databinding.FragmentDestinationsDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.DataItem;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.di.DaggerDestinationsDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.di.DestinationsDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.di.DestinationsDetailModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class DestinationsDetailFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private DataItem category;
    private FragmentDestinationsDetailBinding binding;

    @Inject
    AnalyticsHelper helper;

    public static DestinationsDetailFragment newInstance() {
        return new DestinationsDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.DestinationDetailFragment(), null);
        binding = FragmentDestinationsDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        DestinationsDetailComponent deetailComponent = DaggerDestinationsDetailComponent.builder()
                .destinationsDetailModule(new DestinationsDetailModule(this)).build();
        deetailComponent.injectDestinationsDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (getArguments() != null) {
            category = (DataItem) getArguments().getSerializable(ITEM_DESTINATIONS_DETAILS);
        }
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    private void initViews() {
        if (category != null && category.getDetails() != null) {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getHeading());

            binding.txtDescription.setText(getString(R.string.description)+" : "+Html.fromHtml(category.getDescription()));
            String bestFor = String.join(", ", category.getBestFor());
            binding.txtLocation.setText(getString(R.string.location)+" : "+category.getTitle()+ "\n"+getString(R.string.best_for)+" : "+bestFor);
            String areaAround = String.join(",", category.getAreasAround());
            binding.txtDate.setText(getString(R.string.area_near_by)+" : "+ areaAround);

            if(category.getRecommendations() != null){
                binding.txtTravel.setText(category.getRecommendations().getTitle());
            }

            StringBuilder travel = new StringBuilder();
            for(int i=0; i<category.getRecommendations().getItems().size(); i++) {
                travel.append(i+1).append(". ").append(category.getRecommendations().getItems().get(i).getTitle()).append(" ?\n")
                        .append(category.getRecommendations().getItems().get(i).getDescription()).append("\n\n");
            }

            binding.txtTravelTips.setText(travel);

            binding.txtRec.setText(category.getDetails().getTitle());
            StringBuilder rec = new StringBuilder();
            for(int i=0; i<category.getDetails().getItems().size(); i++) {
                rec.append(i+1).append(". ").append(category.getDetails().getItems().get(i).getTitle()).append("\n")
                        .append(category.getDetails().getItems().get(i).getDescription()).append("\n\n");
            }

            binding.txtRecTips.setText(rec);

            if (!TextUtils.isEmpty(category.getImage())) {
                Picasso.get().load(BASE_URL_FOR_ST+category.getImage()).into(binding.imgBannerDefault);
            }
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        requireActivity().onBackPressed();
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
//        fnBCategoriesPresenter.presenterCleanUps();
    }
}
