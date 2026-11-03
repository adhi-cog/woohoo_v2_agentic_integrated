package com.sb.woohoo.ui.main.fragments.newstext.headline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentNewsHeadlineBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.newstext.detail.NewsDetailFragment;
import com.sb.woohoo.ui.main.fragments.newstext.headline.di.DaggerNewsHeadlineComponent;
import com.sb.woohoo.ui.main.fragments.newstext.headline.di.NewsHeadlineComponent;
import com.sb.woohoo.ui.main.fragments.newstext.headline.di.NewsHeadlineModule;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.Article;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class NewsHeadlineFragment extends FeatureFragment<MainActivity> implements NewsHeadlineContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private FragmentNewsHeadlineBinding binding;

    @Inject
    NewsHeadlineSlideShowAdapter newsHeadlineSlideShowAdapter;

    @Inject
    NewsHeadlinePresenter newsHeadlinePresenter;

    @Inject
    AnalyticsHelper helper;

    private int dotCount;
    private ImageView[] dots;

    private List<Article> articleList = new ArrayList<>(0);
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            String title = articleList.get(position).getTitle();
            for (int i = 0; i < dotCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_not_select_item));
            }
            dots[position].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_selectitem));
            if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID)) {
                getParentActivity().readNewsHeadline(title, false);
            }
            if (position == 0){
                changeLeftRightArrowVisibility(View.GONE, View.VISIBLE);
            } else if (position == dotCount-1){
                changeLeftRightArrowVisibility(View.VISIBLE, View.GONE);
            } else {
                changeLeftRightArrowVisibility(View.VISIBLE, View.VISIBLE);
            }
        }
    };

    private void changeLeftRightArrowVisibility(int leftArrowVisibility, int rightArrowVisibility) {
        binding.imgLeft.setVisibility(leftArrowVisibility);
        binding.imgRight.setVisibility(rightArrowVisibility);
    }

    public static NewsHeadlineFragment newInstance(IntentInfo intentInfo) {
        NewsHeadlineFragment fragment = new NewsHeadlineFragment();
        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return NewsHeadlineFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.NewsHeadline(),  null);
        binding = FragmentNewsHeadlineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        NewsHeadlineComponent newsHeadlineComponent = DaggerNewsHeadlineComponent.builder()
                .newsHeadlineModule(new NewsHeadlineModule(this))
                .build();

        newsHeadlineComponent.injectNewsHeadlineFragment(this);
    }

    @Override
    public void bindViews(View view) {
        setClickEvents();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_news_text_view_title));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public BasePresenter getPresenter() {
        return newsHeadlinePresenter;
    }

    private void initViews() {
        binding.viewpagerNewsHeadline.setAdapter(newsHeadlineSlideShowAdapter);
        binding.viewpagerNewsHeadline.addOnPageChangeListener(pageChangeListener);
    }

    private void initializeViewPagerIndicator() {

        binding.selectDot.removeAllViews();

        dotCount = newsHeadlineSlideShowAdapter.getCount();

        dots = new ImageView[dotCount];

        for (int i = 0; i < dotCount; i++) {

            if (dots != null) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.circle_slideshow_not_select_item));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                binding.selectDot.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_slideshow_selectitem));
        }
    }

    @Override
    public void onNewsHeadlineError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        respondToUser(getString(R.string.something_went_wrong));
        getParentActivity().onBackPressed();
    }

    @Override
    public void onNewsResult(List<Article> newsHeadlines) {
        articleList.clear();
        articleList.addAll(newsHeadlines);
        String title = articleList.get(0).getTitle();
        if (!WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.RC_DOMAIN_ID)) {
            getParentActivity().readNewsHeadline(title, false);
        }
        newsHeadlineSlideShowAdapter.updateNewsHeadlines(newsHeadlines);
        binding.viewpagerNewsHeadline.setCurrentItem(0, true);
        if (newsHeadlines.size() == 1)
            changeLeftRightArrowVisibility(View.GONE, View.GONE);
        initializeViewPagerIndicator();
    }

    @Override
    public void newsDetailShow(Article newsDetail) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("newsArticle", newsDetail);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    public boolean isLastPage() {
        return binding.viewpagerNewsHeadline.getCurrentItem() == (newsHeadlineSlideShowAdapter.getCount() - 1);
    }

    public void proceedToNextPage() {
        if (binding.viewpagerNewsHeadline.getCurrentItem() + 1 < newsHeadlineSlideShowAdapter.getCount())
            binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() + 1, true);
    }

    void proceedToPreviousPage() {
        if (binding.viewpagerNewsHeadline.getCurrentItem() - 1 >= 0)
            binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() - 1, true);
    }

    private void setClickEvents() {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBack());
        binding.fragmentNewsHeadlineRelativeLayoutParent.setOnClickListener(view -> onParentClicked());
        binding.imgRight.setOnClickListener(view -> onRightArrowClicked());
        binding.imgLeft.setOnClickListener(view -> onLeftArrowClicked());
    }
    void onBack() {
        getParentActivity().onBackPressed();
    }

    void onParentClicked() {
    }

    void onLeftArrowClicked() {
        proceedToPreviousPage();
    }

    void onRightArrowClicked() {
        proceedToNextPage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        newsHeadlinePresenter.presenterCleanUps();
    }
}
