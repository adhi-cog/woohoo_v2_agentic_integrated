package com.sb.woohoo.ui.main.fragments.templates.image.single;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.databinding.FragmentImageSingleBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.ImageTemplate;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.squareup.picasso.Picasso;

public class ImageSingleFragment extends FeatureFragment<MainActivity> {

    private static String BUNDLE_DATA = "templateData";

    private FragmentImageSingleBinding binding;

    public ImageSingleFragment() {
        // Required empty public constructor
    }

    public static ImageSingleFragment newInstance(TemplateData templateData) {
        ImageSingleFragment fragment = new ImageSingleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_DATA, templateData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageSingleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        TemplateData<ImageTemplate> imageTemplateTemplateData = (TemplateData<ImageTemplate>) bundle.getSerializable(BUNDLE_DATA);
        if (imageTemplateTemplateData != null) {
            updateDataToViews(imageTemplateTemplateData);
        }

        if (getArguments() != null) {
            getArguments().remove(BUNDLE_DATA);
        }
    }

    @Override
    public void processTemplates(TemplateData templateData) {
        updateDataToViews(templateData);
    }

    private void updateDataToViews(TemplateData<ImageTemplate> textTemplateTemplateData) {
        String title = (String) textTemplateTemplateData.getDisplayTemplate().get("title");
        String imgDesc = textTemplateTemplateData.getTemplate().getMedia().getImageDescription();
        String imgUrl = textTemplateTemplateData.getTemplate().getMedia().getImageUrl();

        String outputSpeechText = textTemplateTemplateData.getOutputSpeech().getSpeechText();

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(title);
        binding.fragmentImageSingleTextDescription.setText(imgDesc);

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Picasso.get().load(imgUrl.trim()).into(binding.fragmentImageSingleTemplateImage);
        }

        if (outputSpeechText != null && !outputSpeechText.isEmpty()) {
            getParentActivity().onRespondToUser(outputSpeechText);
        }

        if (imgDesc != null && !imgDesc.isEmpty()) {
            getParentActivity().onRespondToUser(imgDesc);
        }
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

    }

    @Override
    public void bindViews(View view) {
       binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> goBack());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    void goBack() {
        getParentActivity().onBackPressed();
    }

    void onClickRootView() { /* Do nothing here  */ }
}
