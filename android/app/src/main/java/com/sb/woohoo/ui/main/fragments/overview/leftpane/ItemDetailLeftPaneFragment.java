package com.sb.woohoo.ui.main.fragments.overview.leftpane;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sb.woohoo.databinding.FragmentLeftcontentItemDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.di.DaggerItemDetailLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.di.ItemDetailLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.overview.leftpane.di.ItemDetailLeftPaneModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.squareup.picasso.Picasso;

public class ItemDetailLeftPaneFragment extends FeatureFragment<MainActivity> {

    private FragmentLeftcontentItemDetailBinding binding;

    public static ItemDetailLeftPaneFragment newInstance(String bannerImage) {
        ItemDetailLeftPaneFragment fragment = new ItemDetailLeftPaneFragment();
        Bundle args = new Bundle();
        args.putString("banner", bannerImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLeftcontentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ItemDetailLeftPaneComponent component =
                DaggerItemDetailLeftPaneComponent
                        .builder()
                        .itemDetailLeftPaneModule(new ItemDetailLeftPaneModule(ItemDetailLeftPaneFragment.this))
                        .build();
        component.injectItemDetailLeftPaneFragment(ItemDetailLeftPaneFragment.this);
    }

    @Override
    public void bindViews(View view) {
        binding.imgBannerDefault.setVisibility(View.VISIBLE);
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("banner")))
            Picasso.get().load(getArguments().getString("banner")).into(binding.imgBannerDefault);
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }
}
