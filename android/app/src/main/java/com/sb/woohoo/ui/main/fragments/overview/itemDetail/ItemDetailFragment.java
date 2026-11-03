package com.sb.woohoo.ui.main.fragments.overview.itemDetail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentItemDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.CategoryConstants;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.di.DaggerItemDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.di.ItemDetailComponent;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.di.ItemDetailModule;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.ItemDetailLeftPaneFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class ItemDetailFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    @Inject
    AnalyticsHelper helper;
    private Category category;
    private FragmentItemDetailBinding binding;

    public static ItemDetailFragment newInstance() {
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        return itemDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        helper.track(new Screen.FnBCategories(),  null);
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ItemDetailComponent itemDetailComponent = DaggerItemDetailComponent.builder()
                .itemDetailModule(new ItemDetailModule(this)).build();
        itemDetailComponent.injectItemDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable(CategoryConstants.Params.ITEM_CATEGORY);
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
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ItemDetailLeftPaneFragment fragment = ItemDetailLeftPaneFragment.newInstance(category.getImageUrl());
                getParentActivity().setLeftPane(fragment);
                binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getName());
                binding.txtTitle.setVisibility(View.GONE);
                binding.txtDescription.setText(category.getDescription());
            } else {
                binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getName());
                binding.txtTitle.setVisibility(View.VISIBLE);
                binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(category.getName());
                binding.txtTitle.setVisibility(View.GONE);
                binding.txtDescription.setText(category.getDescription());
                if (!TextUtils.isEmpty(category.getImageUrl()))
                    Picasso.get().load(category.getImageUrl()).into(binding.imgBannerDefault);
            }

            if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.GBTB_DOMAIN_ID)) {
                binding.txtTitle.setTextSize(getResources().getDimension(R.dimen.sp_32));
                binding.txtDescription.setTextSize(getResources().getDimension(R.dimen.sp_30));
            } else {
                binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_18));
                binding.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_18));
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
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
//        fnBCategoriesPresenter.presenterCleanUps();
    }
}
