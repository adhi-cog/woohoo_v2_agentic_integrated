package com.sb.woohoo.ui.main.fragments.templates.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sb.woohoo.databinding.TemplateVideoItemListBinding;
import com.sb.woohoo.ui.main.pojo.template.VideoMedia;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends PagerAdapter {

    private int previousSelection = 0;
    private int nextSelection = 0;
    private int listLimit = 0;

    private List<VideoMedia> videoList = new ArrayList<>(0);
    private VideoListFragment videoListFragment;
    private TemplateVideoItemListBinding binding;

    VideoListAdapter(VideoListFragment videoListFragment) {
        this.videoListFragment = videoListFragment;
    }

    void updateVideoList(List<VideoMedia> image) {
        this.videoList.clear();
        for (int i = 0; i < image.size() && i < 10; i++) {
            this.videoList.add(image.get(i));
        }
        notifyDataSetChanged();
    }

    public VideoMedia getNextVideo() {
        if (isEmptyList()) {
            listLimit++;
            nextSelection = (nextSelection + 1) % videoList.size();
            return videoList.get(nextSelection);
        }
        return null;
    }

    public VideoMedia getPreviousVideo() {
        if (isEmptyList()) {
            listLimit++;
            previousSelection = Math.abs((previousSelection - 1) % videoList.size());
            return videoList.get(previousSelection);
        }
        return null;
    }

    public boolean isEmptyList() {
        return videoList.size() == 0;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //TODO GET THUMBNAIL IMAGE
        //String thumbnailUrl = videoList.get(position).getThumbnail();

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            binding = TemplateVideoItemListBinding.inflate(inflater, container, false);
        }


        //TODO GET THE URL OF THUMBNAIL FIRST
        //Picasso.with(videoListFragment.getContext()).load(thumbnailUrl).fit().into(imageView);

        container.addView(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
