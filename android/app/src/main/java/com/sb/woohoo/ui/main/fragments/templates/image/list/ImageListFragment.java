package com.sb.woohoo.ui.main.fragments.templates.image.list;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.databinding.FragmentImageListBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.ImageListTemplate;
import com.sb.woohoo.ui.main.pojo.template.ImageMedia;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;

import java.util.List;

/**
 * A simple subclass.
 */
public class ImageListFragment extends FeatureFragment<MainActivity> {


    private FragmentImageListBinding binding;

    public ImageListFragment() {
        // Required empty public constructor
    }

    public static ImageListFragment newInstance(TemplateData templateData) {
        ImageListFragment imageListFragment = new ImageListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("templateData", templateData);
        imageListFragment.setArguments(bundle);
        return imageListFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        TemplateData<ImageListTemplate> imageTemplateTemplateData = (TemplateData<ImageListTemplate>) bundle.getSerializable("templateData");
        updateDataToViews(imageTemplateTemplateData);
    }

    @Override
    public void processTemplates(TemplateData templateData) {
        super.processTemplates(templateData);

        TemplateData<ImageListTemplate> imageTemplateTemplateData = templateData;
        updateDataToViews(imageTemplateTemplateData);
    }

    private void updateDataToViews(TemplateData<ImageListTemplate> imageTemplateTemplateData) {
        String title = (String) imageTemplateTemplateData.getDisplayTemplate().get("title");
        List<ImageMedia> imageMediaList = imageTemplateTemplateData.getTemplate().getMedia();

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(title);

        ImageListAdapter listAdapter = new ImageListAdapter(this);
        binding.viewpager.setAdapter(listAdapter);
        binding.viewpager.setAnimationEnabled(true);
        binding.viewpager.setFadeEnabled(true);
        binding.viewpager.setFadeFactor(0.6f);

        listAdapter.updateImageList(imageMediaList);
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


    void onClickRootView() { /* Do nothing here  */ }
}
