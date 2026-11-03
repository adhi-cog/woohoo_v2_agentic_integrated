package com.sb.woohoo.ui.main.fragments.fnb.categories;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.utils.Constants.FNB_PARAMS.FRAGMENT_CATEGORIES_TYPE;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentFnbCategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.di.DaggerFnBCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.fnb.categories.di.FnBCategoriesComponent;
import com.sb.woohoo.ui.main.fragments.fnb.categories.di.FnBCategoriesModule;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.history.FnBHistoryFragment;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBProductsFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.TaxData;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.FnBSubCategoriesFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FnBCategoriesFragment extends
        FeatureFragment<MainActivity>
        implements FnBCategoriesContract.Views,
        NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private List<FnBCategoriesResponse.Category> categoriesList = new ArrayList<>();
    private FnBCategoriesAdapter fnBCategoriesAdapter;
    private FnBCategoryBannerAdapter fnBCategoryBannerAdapter;
    @Inject
    FnBCategoriesPresenter fnBCategoriesPresenter;
    private List<Banner> bannerList = new ArrayList<>();
    private FragmentFnbCategoriesBinding binding;

    public static FnBCategoriesFragment newInstance() {
        return new FnBCategoriesFragment();
    }

    public void updateCart() {
        LoggerWrapper.d(getTag(), "Inside updateCart");
        if (getParentActivity().getTotalCartCount() == 0) {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.GONE);
        } else {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.VISIBLE);
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setText(
                    ""+getParentActivity().getTotalCartCount());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFnbCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        onClickEvents();
        fnBCategoriesPresenter.makeCategoriesRequest();
    }



    private void initViews() {

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            fnBCategoryBannerAdapter = new FnBCategoryBannerAdapter(getParentActivity(), bannerList);
            binding.bannerViewpager.setAdapter(fnBCategoryBannerAdapter);
            binding.tabLayout.setupWithViewPager(binding.bannerViewpager);
        }
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fnBCategoriesAdapter = new FnBCategoriesAdapter(getParentActivity(), categoriesList, (position, category) -> {
            Bundle bundle = new Bundle();
            bundle.putString("category_id", String.valueOf(category.getId()));
            bundle.putBoolean("category_sub_type", category.isHasSubCategory());
            setTaxData(category);
            if (category.isHasSubCategory())
            {
                FnBSubCategoriesFragment fnBSubCategoriesFragment = FnBSubCategoriesFragment.newInstance();
                fnBSubCategoriesFragment.setArguments(bundle);
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    FnBLeftPaneFragment fnBLeftPaneFragment = FnBLeftPaneFragment.getInstance(FnBSubCategoriesFragment.class.getSimpleName());
                    getParentActivity().switchFragmentUsingAdd(fnBLeftPaneFragment, fnBSubCategoriesFragment);
                } else {
                    getParentActivity().switchFragmentUsingReplace(fnBSubCategoriesFragment);
                }
            } else {
                Toast.makeText(getParentActivity(), R.string.no_sub_categories, Toast.LENGTH_SHORT).show();
                FnBProductsFragment fnBProductsFragment = FnBProductsFragment.newInstance();
                fnBProductsFragment.setArguments(bundle);
                getParentActivity().switchFragmentUsingReplace(fnBProductsFragment);
            }
        });
        binding.recyclerCategories.setAdapter(fnBCategoriesAdapter);

        binding.fragmentSettingsRelativeLayoutHeader.layoutHistory.setVisibility(View.VISIBLE);
        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setVisibility(View.VISIBLE);
        updateCart();
    }


    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

        FnBCategoriesComponent fnBCategoriesComponent = DaggerFnBCategoriesComponent.builder()
                .fnBCategoriesModule(new FnBCategoriesModule(this)).build();
        fnBCategoriesComponent.injectFnBCategoriesFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_menus));
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return fnBCategoriesPresenter;
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
        getParentActivity().onBackPressed();
    }

    @Override
    public void onSuccess(List<FnBCategoriesResponse.Category> categoryList, List<Banner> list) {
        if (categoryList != null && !categoryList.isEmpty()) {
            Log.i("item_category", "onSuccess - category list size - " + categoryList.size());
            categoriesList = categoryList;
            fnBCategoriesAdapter.updateLayoutItemHeight(binding.layoutCategories.getHeight() / 2);
            fnBCategoriesAdapter.updateAdapter(categoriesList);
           // setTaxData(categoriesList.get(0));
        } else {
            LoggerWrapper.e("item_category", "onSuccess but no categories found");
            Toast.makeText(getParentActivity(), R.string.fnb_categories_error_ordering_disabled_due_to_no_categories, Toast.LENGTH_LONG).show();
            getParentActivity().onBackPressed();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FnBLeftPaneFragment fragment = FnBLeftPaneFragment.getInstance(FnBCategoriesFragment.class.getSimpleName());
            fragment.onDataFetched((ArrayList<Banner>) list);
        } else {
            if (list != null && !list.isEmpty()) {
                binding.imgBannerDefault.setVisibility(View.GONE);
                binding.bannerViewpager.setVisibility(View.VISIBLE);
                binding.tabLayout.setVisibility(View.VISIBLE);
                this.bannerList = list;
                fnBCategoryBannerAdapter.updateAdapter(bannerList);
            } else {
                Toast.makeText(getParentActivity(), R.string.fnb_categories_no_offers_found, Toast.LENGTH_LONG).show();
                binding.imgBannerDefault.setVisibility(View.VISIBLE);
                binding.bannerViewpager.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setTaxData(FnBCategoriesResponse.Category aCategory)
    {
        TaxData lData = new TaxData();
        lData.setServiceTax(aCategory.getServiceTax());
        lData.setConvenience(aCategory.getConvenience());
        lData.setGst(aCategory.getGst());

        lData.setConvenienceText(aCategory.getConvenienceName());
        lData.setServiceTaxText(aCategory.getServiceTaxName());
        lData.setGstText(aCategory.getGstName());
        LoggerWrapper.d("FNBOrderFrag", "setCategoryId called : "+aCategory.getId());
        lData.setCategoryId(aCategory.getId());
        getParentActivity().setTaxData(lData);
    }

    @Override
    public String createTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void onClickEvents() {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBack());
        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setOnClickListener(view -> onCartClick());
        binding.fragmentSettingsRelativeLayoutHeader.layoutHistory.setOnClickListener(view -> onHistoryClick());
    }
    void onBack() {
        getParentActivity().onBackPressed();
    }
    void onCartClick() {
        Bundle bundle = new Bundle();
        bundle.putString("fragment_type", FRAGMENT_CATEGORIES_TYPE);
        FnBOrderFragment fnBOrderFragment = FnBOrderFragment.newInstance();
        fnBOrderFragment.setArguments(bundle);

        getParentActivity().switchFragmentUsingReplace(fnBOrderFragment);
    }

    void onHistoryClick() {
        FnBHistoryFragment fnBHistoryFragment = FnBHistoryFragment.newInstance();
        getParentActivity().switchFragmentUsingReplace(fnBHistoryFragment);
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
        fnBCategoriesPresenter.presenterCleanUps();
    }
}
