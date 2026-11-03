package com.sb.woohoo.ui.main.fragments.fnb.leftpane;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentLeftcontentFnbBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoryBannerAdapter;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.di.DaggerFnBLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.di.FnBLeftPaneComponent;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.di.FnBLeftPaneModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FnBLeftPaneFragment extends FeatureFragment<MainActivity> {

    private FnBCategoryBannerAdapter fnBCategoryBannerAdapter;
    private FragmentLeftcontentFnbBinding binding;

    public static FnBLeftPaneFragment getInstance(String rightFragmentName) {
        if(map.containsKey(rightFragmentName)) {
            if(map.get(rightFragmentName).get() == null) {
                map.remove(rightFragmentName);
            } else {
                return map.get(rightFragmentName).get();
            }

        }
        FnBLeftPaneFragment fragment = new FnBLeftPaneFragment();
        map.put(rightFragmentName, new WeakReference(fragment));
        return fragment;
    }

    private static Map<String, WeakReference<FnBLeftPaneFragment>> map = new HashMap();

    /*public static FnBLeftPaneFragment newInstance(ArrayList<Banner> bannerList) {
        fragment = new FnBLeftPaneFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("banners", bannerList);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLeftcontentFnbBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        FnBLeftPaneComponent component =
                DaggerFnBLeftPaneComponent
                        .builder()
                        .fnBLeftPaneModule(new FnBLeftPaneModule(FnBLeftPaneFragment.this))
                        .build();
        component.injectFnBLeftPaneFragment(FnBLeftPaneFragment.this);
    }

    @Override
    public void bindViews(View view) {
        /*List<Banner> list = getArguments().getParcelableArrayList("banners");
        fnBCategoryBannerAdapter = new FnBCategoryBannerAdapter(getParentActivity(), list);
        bannerViewPager.setAdapter(fnBCategoryBannerAdapter);
        tabLayout.setupWithViewPager(bannerViewPager);
        bannerViewPager.setAdapter(fnBCategoryBannerAdapter);

        if (list != null && !list.isEmpty()) {
            imgBannerDefault.setVisibility(View.GONE);
            bannerViewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            fnBCategoryBannerAdapter.updateAdapter(list);

        } else {
            Toast.makeText(getActivity(), R.string.fnb_categories_no_offers_found, Toast.LENGTH_LONG).show();
            imgBannerDefault.setVisibility(View.VISIBLE);
            bannerViewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    public void onDataFetched(ArrayList<Banner> list) {

        //Need to re check why this binding is NULL
        if(binding == null || binding.bannerViewpager == null){
            return;
        }

        fnBCategoryBannerAdapter = new FnBCategoryBannerAdapter(getParentActivity(), list);
        binding.bannerViewpager.setAdapter(fnBCategoryBannerAdapter);
        binding.tabLayout.setupWithViewPager(binding.bannerViewpager);
        binding.bannerViewpager.setAdapter(fnBCategoryBannerAdapter);

        if (list != null && !list.isEmpty()) {
            binding.imgBannerDefault.setVisibility(View.GONE);
            binding.bannerViewpager.setVisibility(View.VISIBLE);
            binding.tabLayout.setVisibility(View.VISIBLE);
            fnBCategoryBannerAdapter.updateAdapter(list);

        } else {
            Toast.makeText(getActivity(), R.string.fnb_categories_no_offers_found, Toast.LENGTH_LONG).show();
            binding.imgBannerDefault.setVisibility(View.VISIBLE);
            binding.bannerViewpager.setVisibility(View.GONE);
            binding.tabLayout.setVisibility(View.GONE);
        }
    }

}
