package com.sb.woohoo.ui.main.fragments.overview.subcategories;

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
import com.sb.woohoo.databinding.FragmentOverviewSubcategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.CategoryConstants;
import com.sb.woohoo.ui.main.fragments.overview.item.OverviewItemFragment;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.ItemDetailFragment;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.di.DaggerOverviewSubCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.di.OverviewSubCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.di.OverviewSubCategoriesModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OverviewSubCategoriesFragment extends FeatureFragment<MainActivity> implements OverviewSubCategoriesContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;


    OverviewSubCategoriesAdapter overviewSubCategoriesAdapter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    OverviewSubCategoriesPresenter overviewSubCategoriesPresenter;
    private List<Category> subCategoriesList = new ArrayList<>();
    private FragmentOverviewSubcategoriesBinding binding;

    public static OverviewSubCategoriesFragment newInstance() {
        OverviewSubCategoriesFragment fragment = new OverviewSubCategoriesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewSubCategories(), null);
        binding = FragmentOverviewSubcategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        overviewSubCategoriesPresenter.subCategoriesRequest(getArguments().getString(CategoryConstants.Params.SUB_CATEGORY_ID));
    }

    private void initViews() {
        overviewSubCategoriesAdapter = new OverviewSubCategoriesAdapter(getActivity(), WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(), subCategoriesList, subCategory -> {
            if (subCategory.isHasItem()) {
                Bundle bundle = new Bundle();
                bundle.putString(CategoryConstants.Params.SUB_CATEGORY_ID, String.valueOf(subCategory.getCategoryId()));
                bundle.putString(CategoryConstants.Params.TITLE, String.valueOf(subCategory.getName()));
                OverviewItemFragment overviewItemFragment = OverviewItemFragment.newInstance();
                overviewItemFragment.setArguments(bundle);
                getParentActivity().switchFragmentUsingReplace(overviewItemFragment);

            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(CategoryConstants.Params.ITEM_CATEGORY, subCategory);
                bundle.putString(CategoryConstants.Params.TITLE, String.valueOf(subCategory.getName()));
                ItemDetailFragment itemDetailFragment = ItemDetailFragment.newInstance();
                itemDetailFragment.setArguments(bundle);
                getParentActivity().switchFragmentUsingReplace(itemDetailFragment);
            }
        });
        binding.recyclerCategories.setAdapter(overviewSubCategoriesAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onSubCategoriesResponse(List<Category> list) {
        if (list != null && !list.isEmpty()) {
            subCategoriesList.clear();
            subCategoriesList.addAll(list);
            overviewSubCategoriesAdapter.updateListAndNotify(subCategoriesList);
        }
    }

    @Override
    public void onErrorResponse(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.try_again));
        }
        getParentActivity().onBackPressed();
    }

    @Override
    public void showProgress() {
        if(binding.shimmerFrameLayout.isShimmerStarted()){
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
        OverviewSubCategoriesComponent overviewSubCategoriesComponent = DaggerOverviewSubCategoriesComponent.builder()
                .overviewSubCategoriesModule(new OverviewSubCategoriesModule(this)).build();
        overviewSubCategoriesComponent.injectOverviewSubCategoriesFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getArguments().getString(CategoryConstants.Params.TITLE));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return overviewSubCategoriesPresenter;
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getActivity().onBackPressed();
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
        overviewSubCategoriesPresenter.presenterCleanUps();
    }
}