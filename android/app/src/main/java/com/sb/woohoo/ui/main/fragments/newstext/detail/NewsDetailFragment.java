package com.sb.woohoo.ui.main.fragments.newstext.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentNewsDetailBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.newstext.detail.di.DaggerNewsDetailComponent;
import com.sb.woohoo.ui.main.fragments.newstext.detail.di.NewsDetailComponent;
import com.sb.woohoo.ui.main.fragments.newstext.detail.di.NewsDetailModule;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.Article;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;

import javax.inject.Inject;

public class NewsDetailFragment extends FeatureFragment<MainActivity> {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;


    @Inject
    NewsDetailPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;

    private Article mArticle;
    private FragmentNewsDetailBinding binding;

    public static NewsDetailFragment newInstance(Bundle bundle) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }


    @Override
    public String createTag() {
        return NewsDetailFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.NewsDetail(),  null);
        binding = FragmentNewsDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

        NewsDetailComponent component = DaggerNewsDetailComponent.builder()
                .newsDetailModule(new NewsDetailModule(this))
                .build();

        component.injectNewsDetailFragment(this);
    }

    @Override
    public void bindViews(View view) {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArticle = bundle.getParcelable("newsArticle");
        }
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_news_text_view_title));
        bindValue();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    private void bindValue() {
        if (mArticle != null) {
            binding.newsDetailHeadline.setText(mArticle.getTitle());
            binding.newsDetailText.setText(mArticle.getDescription());
            if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID)) {
                getParentActivity().readNewsHeadline(mArticle.getDescription(), true);
            }
        }
    }

    void onParentClicked() {
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
        getParentActivity().stopTextToSpeech(false);
    }
}
