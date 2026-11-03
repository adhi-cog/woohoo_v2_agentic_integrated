package com.sb.woohoo.ui.main.fragments.overview.saudipackages;


import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_EVENT_DETAILS;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_PACKAGES_DETAILS;
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
import com.sb.woohoo.databinding.FragmentSaudiTourismPackagesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.di.DaggerSaudiTourismPackagesComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.di.SaudiTourismPackagesComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.di.SaudiTourismPackagesModule;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.DataList;
import com.sb.woohoo.ui.main.fragments.overview.saudipackagesdetail.PackagesDetailFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SaudiTourismPackagesFragment extends FeatureFragment<MainActivity> implements SaudiTourismPackagesContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    SaudiTourismPackagesPresenter saudiTourismPackagesPresenter;

    private FragmentSaudiTourismPackagesBinding binding;

    public static SaudiTourismPackagesFragment newInstance() {
        SaudiTourismPackagesFragment fragment = new SaudiTourismPackagesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentSaudiTourismPackagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        saudiTourismPackagesPresenter.packagesAPIRequest();
    }

    private void initViews() {
        saudiTourismAdapter = new SaudiTourismAdapter(getActivity(),
                WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(),
                Packageslist, packagesDetails ->
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM_PACKAGES_DETAILS, packagesDetails);
            PackagesDetailFragment packagesDetailFragment = PackagesDetailFragment.newInstance();
            packagesDetailFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(packagesDetailFragment);

        });
        binding.recyclerCategories.setAdapter(saudiTourismAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }
    @Override
    public void initDagger() {
        SaudiTourismPackagesComponent saudiTourismPackagesComponent = DaggerSaudiTourismPackagesComponent.builder()
                .saudiTourismPackagesModule(new SaudiTourismPackagesModule(this)).build();
        saudiTourismPackagesComponent.injectSaudiTourismPackagesFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_st_packages));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return saudiTourismPackagesPresenter;
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
        saudiTourismPackagesPresenter.presenterCleanUps();
    }

    List<DataList> Packageslist = new ArrayList<>();
    SaudiTourismAdapter saudiTourismAdapter;

    public void onPackagesResponse(List<DataList> list) {
        if (list != null && !list.isEmpty()) {
            Packageslist.clear();
            Packageslist.addAll(list);
            saudiTourismAdapter.updateListAndNotify(Packageslist);
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