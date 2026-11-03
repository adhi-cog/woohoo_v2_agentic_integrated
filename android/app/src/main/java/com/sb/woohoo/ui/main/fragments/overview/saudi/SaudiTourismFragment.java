package com.sb.woohoo.ui.main.fragments.overview.saudi;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSaudiTourismBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudi.di.DaggerSaudiTourismComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudi.di.SaudiTourismComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudi.di.SaudiTourismModule;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.SaudiTourismDestinationsFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.SaudiTourismEventsFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.SaudiTourismExperienceFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.SaudiTourismPackagesFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

public class SaudiTourismFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;

    private FragmentSaudiTourismBinding binding;

    public static SaudiTourismFragment newInstance() {
        SaudiTourismFragment fragment = new SaudiTourismFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentSaudiTourismBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClickAction();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void initClickAction() {
        binding.itemEvents.setOnClickListener(view ->{
            getParentActivity().switchFragmentUsingReplace(SaudiTourismEventsFragment.newInstance());
        });
        binding.itemExperience.setOnClickListener(view ->{
            getParentActivity().switchFragmentUsingReplace(SaudiTourismExperienceFragment.newInstance());
        });
        binding.itemPackages.setOnClickListener(view ->{
            getParentActivity().switchFragmentUsingReplace(SaudiTourismPackagesFragment.newInstance());
        });

        binding.itemDestination.setOnClickListener(view ->{
            getParentActivity().switchFragmentUsingReplace(SaudiTourismDestinationsFragment.newInstance());
        });

    }

    private void initViews() {

        }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }
    @Override
    public void initDagger() {
        SaudiTourismComponent saudiTourismComponent = DaggerSaudiTourismComponent.builder()
                .saudiTourismModule(new SaudiTourismModule(this)).build();
        saudiTourismComponent.injectSaudiTourismFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_st));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
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
    }
}