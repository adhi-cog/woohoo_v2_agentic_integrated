package com.sb.woohoo.ui.main.fragments.templates.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sb.woohoo.databinding.FragmentTemplateVideoListBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.pojo.template.VideoListTemplate;
import com.sb.woohoo.ui.main.pojo.template.VideoMedia;

import java.util.List;

public class VideoListFragment extends FeatureFragment<MainActivity> {

    public static final String TEMPLATE_KEY = "template_data";

    private VideoListAdapter listAdapter;
    private FragmentTemplateVideoListBinding binding;

    public VideoListFragment() {
    }

    public static VideoListFragment newInstance(TemplateData templateData) {
        VideoListFragment videoListFragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TEMPLATE_KEY, templateData);
        videoListFragment.setArguments(bundle);
        return videoListFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTemplateVideoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        //FIXME later
        TemplateData<VideoListTemplate> templateData = (TemplateData<VideoListTemplate>) bundle.getSerializable(TEMPLATE_KEY);
        String title = (String) templateData.getDisplayTemplate().get("title");
        List<VideoMedia> videoMediaList = templateData.getTemplate().getMedia();

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(title);

        listAdapter = new VideoListAdapter(this);
        binding.viewpager.setAdapter(listAdapter);

        binding.viewpager.setAnimationEnabled(true);
        binding.viewpager.setFadeEnabled(true);
        binding.viewpager.setFadeFactor(0.6f);

        listAdapter.updateVideoList(videoMediaList);
    }

    public void addNextVideoToList() {
        Fragment fragment = FragmentTransactionManager.getCurrentFragment(getFragmentManager());
    }

    public void addPreviousVideoToList() {
        Fragment fragment = FragmentTransactionManager.getCurrentFragment(getFragmentManager());
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

    }

    @Override
    public void bindViews(View view) {
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
