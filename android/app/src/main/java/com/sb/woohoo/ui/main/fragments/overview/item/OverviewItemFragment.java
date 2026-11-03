package com.sb.woohoo.ui.main.fragments.overview.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentOverviewItemBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.CategoryConstants;
import com.sb.woohoo.ui.main.fragments.overview.item.di.DaggerOverviewItemComponent;
import com.sb.woohoo.ui.main.fragments.overview.item.di.OverviewItemComponent;
import com.sb.woohoo.ui.main.fragments.overview.item.di.OverviewItemModule;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.ItemDetailFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OverviewItemFragment extends FeatureFragment<MainActivity> implements OverviewItemContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    OverviewItemsAdapter overviewItemsAdapter;
    @Inject
    AnalyticsHelper helper;

    @Inject
    OverviewItemPresenter overviewSubCategoriesPresenter;
    private List<Category> itemsList = new ArrayList<>();
    private FragmentOverviewItemBinding binding;

    public static OverviewItemFragment newInstance() {
        OverviewItemFragment fragment = new OverviewItemFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewSubCategories(), null);
        binding = FragmentOverviewItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        overviewSubCategoriesPresenter.makeItemRequest(getArguments().getString(CategoryConstants.Params.SUB_CATEGORY_ID));
    }

    private void initViews() {
        overviewItemsAdapter = new OverviewItemsAdapter(getActivity(), WooHooApp.get(getActivity()).getWooHooAppComponent().getPicasso(), itemsList, overviewItem -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CategoryConstants.Params.ITEM_CATEGORY, overviewItem);
            ItemDetailFragment itemDetailFragment = ItemDetailFragment.newInstance();
            itemDetailFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(itemDetailFragment);
        });
        binding.recyclerCategories.setAdapter(overviewItemsAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onCategoriesResponse(List<Category> list) {
        if (list != null && !list.isEmpty()) {
            itemsList.clear();
            itemsList.addAll(list);
            overviewItemsAdapter.updateListAndNotify(itemsList);
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
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
        OverviewItemComponent overviewItemComponent = DaggerOverviewItemComponent.builder()
                .overviewItemModule(new OverviewItemModule(this)).build();
        overviewItemComponent.injectOverviewItemFragment(this);
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
