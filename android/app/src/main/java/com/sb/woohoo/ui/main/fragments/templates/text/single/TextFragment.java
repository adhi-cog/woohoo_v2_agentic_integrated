package com.sb.woohoo.ui.main.fragments.templates.text.single;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentTextBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.templates.text.single.di.DaggerTextComponent;
import com.sb.woohoo.ui.main.fragments.templates.text.single.di.TextComponent;
import com.sb.woohoo.ui.main.fragments.templates.text.single.di.TextModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateBackground;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.pojo.template.TextTemplate;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import jp.wasabeef.picasso.transformations.BlurTransformation;

//import jp.wasabeef.picasso.transformations.BlurTransformation;

public class TextFragment extends FeatureFragment<MainActivity> implements TextContract.View, PopFragment {

    private static String BUNDLE_DATA = "templateData";

    @Inject
    TextPresenter textPresenter;

    private FragmentTextBinding binding;

    public static TextFragment newInstance(TemplateData templateData) {
        TextFragment fragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_DATA, templateData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        TextComponent component = DaggerTextComponent.builder()
                .textModule(new TextModule(this))
                .build();

        component.injectTextTemplateComponent(this);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return textPresenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TemplateData<TextTemplate> textTemplateTemplateData = (TemplateData<TextTemplate>) bundle.getSerializable(BUNDLE_DATA);
            updateDataToViews(textTemplateTemplateData);
        }
        if (getArguments() != null) {
            getArguments().remove(BUNDLE_DATA);
        }
    }

    @Override
    public void processTemplates(TemplateData templateData) {
        super.processTemplates(templateData);
        TemplateData<TextTemplate> textTemplateTemplateData = templateData;
        updateDataToViews(textTemplateTemplateData);
    }

    private void updateDataToViews(TemplateData<TextTemplate> textTemplateTemplateData) {
        if (textTemplateTemplateData != null) {
            String title = (String) textTemplateTemplateData.getDisplayTemplate().get("title");
            String text = textTemplateTemplateData.getTemplate().getText();

            if (text != null && !text.isEmpty()) {
                binding.fragmentTextTemplateTextView.setText(text);
                getParentActivity().onRespondToUser(text);
            }

            if (title != null && !title.isEmpty()) {
                binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(title);
            } else {
                binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText("");
            }

            setTemplateBackground(textTemplateTemplateData.getTemplate().getBackground());
        }
    }

    private void setTemplateBackground(TemplateBackground background) {
        if (background != null) {
            switch (background.getType()) {
                case TemplateBackground.BackgroundType.COLOR: {
                    String bgColor = background.getData();
                    int color = getColorFromTemplate(bgColor);
                    binding.fragmentTextImageView.setImageDrawable(null);
                    binding.fragmentTextImageView.setBackgroundColor(color);
                }
                break;
                case TemplateBackground.BackgroundType.IMAGE: {
                    String imgUrl = background.getData();
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Picasso.get()
                                .load(imgUrl.trim())
                                .transform(new BlurTransformation(getParentActivity(), 10, 1))
                                .fit()
                                .centerCrop()
                                .into(binding.fragmentTextImageView);
                    }
                }
                break;
            }
        }
    }

    private int getColorFromTemplate(String color) {
        int colorInteger;
        try {
            colorInteger = Color.parseColor(color);
            return colorInteger;
        } catch (IllegalArgumentException e) {
            colorInteger = Color.BLACK;
            return colorInteger;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTextBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void goBack() {
        getParentActivity().onBackPressed();
    }


    void onClickRootView() { /* Do nothing here  */ }
}
