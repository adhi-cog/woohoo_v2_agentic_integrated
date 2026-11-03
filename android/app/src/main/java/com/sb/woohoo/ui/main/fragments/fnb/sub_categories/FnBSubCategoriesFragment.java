package com.sb.woohoo.ui.main.fragments.fnb.sub_categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.utils.Constants.FNB_PARAMS.FRAGMENT_SUB_CATEGORIES_TYPE;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentFnbCategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoryBannerAdapter;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBProductsFragment;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.DaggerFnBSubCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.FnBSubCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.FnBSubCategoriesModule;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.pojo.FnBSubCategoriesResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FnBSubCategoriesFragment extends FeatureFragment<MainActivity> implements FnBSubCategoriesContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private String CATEGORY_ID;
    private Boolean SUB_CATEGORY_TYPE;

    @Inject
    FnBSubCategoriesPresenter fnBSubCategoriesPresenter;

    private List<FnBSubCategoriesResponse.Category> categoriesList = new ArrayList<>();
    private FnBSubCategoriesAdapter fnBCategoriesAdapter;
    private FnBCategoryBannerAdapter fnBCategoryBannerAdapter;
    private List<Banner> bannerList = new ArrayList<>();
    private FragmentFnbCategoriesBinding binding;

    public static FnBSubCategoriesFragment newInstance() {
        return new FnBSubCategoriesFragment();
    }

    @Override
    public String createTag() {
        return this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFnbCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if(getArguments() != null)
        {
            CATEGORY_ID = getArguments().getString("category_id");
            SUB_CATEGORY_TYPE = getArguments().getBoolean("category_sub_type");
            fnBSubCategoriesPresenter.makeSubCategoriesRequest(CATEGORY_ID);
        }
    }

    private void initViews() {

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            fnBCategoryBannerAdapter = new FnBCategoryBannerAdapter(getParentActivity(), bannerList);
            binding.bannerViewpager.setAdapter(fnBCategoryBannerAdapter);
            binding.tabLayout.setupWithViewPager(binding.bannerViewpager);
        }
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fnBCategoriesAdapter = new FnBSubCategoriesAdapter(getParentActivity(), categoriesList, (position, category) -> {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", CATEGORY_ID);
            bundle.putBoolean("category_sub_type", SUB_CATEGORY_TYPE);
            bundle.putString("sub_category_id", String.valueOf(category.getId()));
            FnBProductsFragment fnBProductsFragment = FnBProductsFragment.newInstance();
            fnBProductsFragment.setArguments(bundle);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FnBLeftPaneFragment fnBLeftPaneFragment = FnBLeftPaneFragment.getInstance(FnBProductsFragment.class.getSimpleName());
                getParentActivity().switchFragmentUsingAdd(fnBLeftPaneFragment, fnBProductsFragment);
            } else {
                getParentActivity().switchFragmentUsingReplace(fnBProductsFragment);
            }
        });
        binding.recyclerCategories.setAdapter(fnBCategoriesAdapter);

        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setVisibility(View.VISIBLE);

        updateCart();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

        FnBSubCategoriesComponent fnBSubCategoriesComponent = DaggerFnBSubCategoriesComponent.builder()
                .fnBSubCategoriesModule(new FnBSubCategoriesModule(this)).build();
        fnBSubCategoriesComponent.injectFnBSubCategoriesFragment(this);
    }

    @Override
    public void bindViews(View view) {
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equalsIgnoreCase(Constants.RC_DOMAIN_ID))
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_sub_menus));
        else
            binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_sub_categories));

        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setOnClickListener(view1 -> onCartClick());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    public void updateCart()
    {
        if (getParentActivity().getTotalCartCount() == 0) {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.GONE);
        } else {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.VISIBLE);
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setText(
                    ""+getParentActivity().getTotalCartCount());
        }
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return fnBSubCategoriesPresenter;
    }

    @Override
    public void errorMessage(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.item_not_available));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.try_again));
        }
        onBack();
    }

    @Override
    public void onSuccess(List<FnBSubCategoriesResponse.Category> categoryList, List<Banner> list)
    {
        if (categoryList != null && !categoryList.isEmpty()) {
            categoriesList = categoryList;
            fnBCategoriesAdapter.updateLayoutItemHeight(binding.layoutCategories.getHeight() / 2);
            fnBCategoriesAdapter.updateAdapter(categoriesList);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FnBLeftPaneFragment fragment = FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
            fragment.onDataFetched((ArrayList<Banner>) list);
        } else {
            if (list != null && !list.isEmpty()) {
                binding.imgBannerDefault.setVisibility(View.GONE);
                binding.bannerViewpager.setVisibility(View.VISIBLE);
                binding.tabLayout.setVisibility(View.VISIBLE);
                this.bannerList = list;
                fnBCategoryBannerAdapter.updateAdapter(bannerList);
            } else {
                binding.imgBannerDefault.setVisibility(View.VISIBLE);
                binding.bannerViewpager.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    void onBack() {
        getParentActivity().onBackPressed();
    }

    void onCartClick() {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", CATEGORY_ID);
//        bundle.putBoolean("category_sub_type", SUB_CATEGORY_TYPE);
//        bundle.putString("sub_category_id", SUB_CATEGORY_ID);
        bundle.putString("fragment_type", FRAGMENT_SUB_CATEGORIES_TYPE);
        FnBOrderFragment fnBOrderFragment = FnBOrderFragment.newInstance();
        fnBOrderFragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fnBOrderFragment);
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
        fnBSubCategoriesPresenter.presenterCleanUps();
    }
}
