package com.sb.woohoo.ui.main.fragments.templates.image.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.databinding.TemplateImageListBinding;
import com.sb.woohoo.ui.main.pojo.template.ImageMedia;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends PagerAdapter {

    private List<ImageMedia> imageList = new ArrayList<>(0);
    private ImageListFragment imageListFragment;
    private TemplateImageListBinding binding;

    ImageListAdapter(ImageListFragment imageListFragment) {
        this.imageListFragment = imageListFragment;
    }

    void updateImageList(List<ImageMedia> image) {
        this.imageList.clear();
        for (int i = 0; i < image.size() && i < 10; i++) {
            this.imageList.add(image.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        String url = imageList.get(position).getImageUrl();

        // Instantiate Parent View.
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            binding = TemplateImageListBinding.inflate(inflater, container, false);
        }
        Picasso.get().load(url).fit().into(binding.imageView);
        container.addView(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
