package com.sb.woohoo.ui.main.fragments.overview.saudiexperience;


import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_EXPERIENCE_DETAILS_ID;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSaudiTourismExperienceBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.di.DaggerSaudiTourismExperienceComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.di.SaudiTourismExperienceComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.di.SaudiTourismExperienceModule;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.DataList;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.ExperienceDetailFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SaudiTourismExperienceFragment extends FeatureFragment<MainActivity> implements SaudiTourismExperienceContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    SaudiTourismExperiencePresenter saudiTourismExperiencePresenter;

    private FragmentSaudiTourismExperienceBinding binding;

    public static SaudiTourismExperienceFragment newInstance() {
        SaudiTourismExperienceFragment fragment = new SaudiTourismExperienceFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentSaudiTourismExperienceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        saudiTourismExperiencePresenter.experienceAPIRequest(LocaleHelper.getLanguage(getActivity()));
    }

    private void initViews() {
        saudiTourismAdapter = new SaudiTourismExperienceAdapter(getActivity(), WooHooApp.get(getParentActivity()).getWooHooAppComponent().getPicasso(),
                mExperiencelist, eventDetails -> {
            Bundle bundle = new Bundle();
            bundle.putString(ITEM_EXPERIENCE_DETAILS_ID, eventDetails.getId());
            ExperienceDetailFragment eventDetailFragment = ExperienceDetailFragment.newInstance();
            eventDetailFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(eventDetailFragment);

        });
        binding.recyclerCategories.setAdapter(saudiTourismAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }
    @Override
    public void initDagger() {
        SaudiTourismExperienceComponent saudiTourismExperienceComponent = DaggerSaudiTourismExperienceComponent.builder()
                .saudiTourismExperienceModule(new SaudiTourismExperienceModule(this)).build();
        saudiTourismExperienceComponent.injectSaudiTourismExperienceFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_st_experience));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return saudiTourismExperiencePresenter;
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
        saudiTourismExperiencePresenter.presenterCleanUps();
    }

    List<DataList> mExperiencelist = new ArrayList<>();
    SaudiTourismExperienceAdapter saudiTourismAdapter;

    public void onCategoriesResponse(List<DataList> list) {
        if (list != null && !list.isEmpty()) {
            mExperiencelist.clear();
            mExperiencelist.addAll(list);
            saudiTourismAdapter.updateListAndNotify(mExperiencelist);
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
        binding.recyclerCategories.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.shimmerFrameLayout.stopShimmer();
        binding.shimmerFrameLayout.setVisibility(View.GONE);
        binding.recyclerCategories.setVisibility(View.VISIBLE);
    }
}