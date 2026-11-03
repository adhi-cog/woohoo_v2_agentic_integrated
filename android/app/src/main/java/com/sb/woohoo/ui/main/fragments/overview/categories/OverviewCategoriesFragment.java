package com.sb.woohoo.ui.main.fragments.overview.categories;

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
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentOverviewCategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.CategoryConstants;
import com.sb.woohoo.ui.main.fragments.overview.categories.di.DaggerOverviewCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.overview.categories.di.OverviewCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.overview.categories.di.OverviewCategoriesModule;
import com.sb.woohoo.ui.main.fragments.overview.item.OverviewItemFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.OverviewSubCategoriesFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OverviewCategoriesFragment extends FeatureFragment<MainActivity> implements OverviewCategoriesContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    OverviewCategoriesAdapter overviewCategoriesAdapter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    OverviewCategoriesPresenter overviewCategoriesPresenter;
    private Category selectedCategory;
    private List<Category> categoriesList = new ArrayList<>();
    private FragmentOverviewCategoriesBinding binding;

    public static OverviewCategoriesFragment newInstance() {
        OverviewCategoriesFragment fragment = new OverviewCategoriesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentOverviewCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        overviewCategoriesPresenter.categoriesRequest();

    }

    private void initViews() {
        overviewCategoriesAdapter = new OverviewCategoriesAdapter(getActivity(), WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(), categoriesList, category -> {
            selectedCategory = category;
            if (selectedCategory.isSubCategory()) {
                Bundle bundle = new Bundle();
                bundle.putString(CategoryConstants.Params.SUB_CATEGORY_ID, String.valueOf(selectedCategory.getId()));
                bundle.putString(CategoryConstants.Params.TITLE, String.valueOf(category.getName()));
                OverviewSubCategoriesFragment overviewSubCategoriesFragment = OverviewSubCategoriesFragment.newInstance();
                overviewSubCategoriesFragment.setArguments(bundle);
                getParentActivity().switchFragmentUsingReplace(overviewSubCategoriesFragment);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(CategoryConstants.Params.SUB_CATEGORY_ID, String.valueOf(category.getId()));
                bundle.putString(CategoryConstants.Params.TITLE, String.valueOf(category.getName()));
                OverviewItemFragment overviewItemFragment = OverviewItemFragment.newInstance();
                overviewItemFragment.setArguments(bundle);
                getParentActivity().switchFragmentUsingReplace(overviewItemFragment);
            }

        });
        binding.recyclerCategories.setAdapter(overviewCategoriesAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onCategoriesResponse(List<Category> list) {
        if (list != null && !list.isEmpty()) {
            categoriesList.clear();
            categoriesList.addAll(list);
            overviewCategoriesAdapter.updateListAndNotify(categoriesList);
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

    @Override
    public void initDagger() {
        OverviewCategoriesComponent overviewCategoriesComponent = DaggerOverviewCategoriesComponent.builder()
                .overviewCategoriesModule(new OverviewCategoriesModule(this)).build();
        overviewCategoriesComponent.injectOverviewCategoriesFragment(this);
    }

    @Override
    public void bindViews(View view) {
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1");
        if (domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.compendium_park_royal));
        } else if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID)) {
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview));
        } else
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_gbtb));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return overviewCategoriesPresenter;
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
        overviewCategoriesPresenter.presenterCleanUps();
    }
}