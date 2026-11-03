package com.sb.woohoo.ui.main.fragments.fnb.categories;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.databinding.ItemCategoryBannerBinding;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FnBCategoryBannerAdapter extends PagerAdapter {

    int currentPosition = 0;

    private Picasso picasso;
    private Activity fnBCategoriesFragment;
    private List<Banner> bannerList;
    private ItemCategoryBannerBinding binding;

    public FnBCategoryBannerAdapter(Activity fnBCategoriesFragment, List<Banner> bannerList) {
        this.fnBCategoriesFragment = fnBCategoriesFragment;
        this.bannerList = bannerList;
    }

    public void updateAdapter(List<Banner> bannerList){
        this.bannerList = bannerList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // Instantiate Parent View.
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = ItemCategoryBannerBinding.inflate(inflater, container, false);
        }

        container.addView(binding.getRoot());

        Banner banner = bannerList.get(position);
        Picasso.get().load(banner.getImage()).into(binding.imgBanner);
        if (TextUtils.isEmpty(banner.getTitle()) && TextUtils.isEmpty(banner.getSubtitle())) {
            binding.layoutBannerTitle.setAlpha(0.0f);
        } else {
            binding.layoutBannerTitle.setAlpha(0.6f);
        }
        if (banner.getTitle()!= null){
            binding.txtTitle.setVisibility(View.VISIBLE);
            binding.txtTitle.setText(banner.getTitle());
        }else
            binding.txtTitle.setVisibility(View.GONE);

        if (banner.getSubtitle()!= null){
            binding.txtSubTitle.setVisibility(View.VISIBLE);
            binding.txtSubTitle.setText(banner.getSubtitle());
        }else
            binding.txtSubTitle.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
