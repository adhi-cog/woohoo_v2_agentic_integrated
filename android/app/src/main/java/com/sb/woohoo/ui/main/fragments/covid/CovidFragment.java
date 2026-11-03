package com.sb.woohoo.ui.main.fragments.covid;

import android.os.Bundle;
import android.util.Log;
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
import com.sb.woohoo.databinding.FragmentCovidBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.covid.di.CovidComponent;
import com.sb.woohoo.ui.main.fragments.covid.di.CovidModule;
import com.sb.woohoo.ui.main.fragments.covid.di.DaggerCovidComponent;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Covid;
import com.sb.woohoo.ui.main.fragments.covid.pojo.CovidSubmitRequest;
import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CovidFragment extends FeatureFragment<MainActivity> implements CovidContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;


    FragmentCovidBinding binding;

    @Inject
    CovidAdapter covidSlideShowAdapter;

    @Inject
    CovidPresenter covidPresenter;

    @Inject
    AnalyticsHelper helper;

    private int dotCount;
    private ImageView[] dots;

    private List<Question> covidList = new ArrayList<>(0);


    public static int recentPos = 0;
    public static HashMap<Integer, Covid> mapListCovid = new HashMap<>();
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            recentPos = position;
            String title = covidList.get(position).getQuestion();
            for (int i = 0; i < dotCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_not_select_item));
            }
            dots[position].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_selectitem));
            getParentActivity().readNewsHeadline(title, false);

            covidSlideShowAdapter.updateUI(recentPos);
            if (dotCount != 0 && dotCount == position + 1) {
                binding.buttonSubmit.setVisibility(View.VISIBLE);
                binding.layoutBackNext.setVisibility(View.GONE);
            } else {
                binding.buttonSubmit.setVisibility(View.INVISIBLE);
                binding.layoutBackNext.setVisibility(View.VISIBLE);
                if (position == 0) {
                    binding.btnBack.setVisibility(View.GONE);
                }else{
                    binding.btnBack.setVisibility(View.VISIBLE);
                }
                binding.btnNext.setVisibility(View.VISIBLE);
            }
        }
    };

    public static CovidFragment newInstance() {
        CovidFragment fragment = new CovidFragment();
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return CovidFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Covid(),  null);
        binding = FragmentCovidBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        CovidComponent covidComponent = DaggerCovidComponent.builder()
                .covidModule(new CovidModule(this))
                .build();

        covidComponent.injectCovidFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_covid_text_view_title));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        mapListCovid.clear();
        covidPresenter.covidAPIRequest();
    }

    @Override
    public BasePresenter getPresenter() {
        return covidPresenter;
    }

    private void initViews() {

        binding.viewpagerNewsHeadline.setAdapter(covidSlideShowAdapter);
        binding.viewpagerNewsHeadline.addOnPageChangeListener(pageChangeListener);
        onClickEvents();
        covidResult(covidList);


    }

    private void initializeViewPagerIndicator() {

        binding.selectDot.removeAllViews();

        dotCount = covidSlideShowAdapter.getCount();

        dots = new ImageView[dotCount];

        for (int i = 0; i < dotCount; i++) {

            if (dots != null) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.circle_slideshow_not_select_item));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                binding.selectDot.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_slideshow_selectitem));
            if (dotCount != 0 && dotCount == 1) {
                binding.buttonSubmit.setVisibility(View.VISIBLE);
                binding.layoutBackNext.setVisibility(View.GONE);
            } else {
                binding.buttonSubmit.setVisibility(View.INVISIBLE);
                binding.layoutBackNext.setVisibility(View.VISIBLE);
                binding.btnBack.setVisibility(View.GONE);
                binding.btnNext.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void covidMessage(String errorMessage) {
        Toast.makeText(getParentActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();
    }

    @Override
    public void covidResult(List<Question> newsHeadlines) {
        if (newsHeadlines != null && newsHeadlines.size() > 0) {
            mapListCovid.clear();
            for(int i=0; i<newsHeadlines.size(); i++){
                Covid covid = new Covid();
                covid.category= newsHeadlines.get(i).getCategory();
                mapListCovid.put(i,covid);
            }
            covidList.clear();
            covidList.addAll(newsHeadlines);
            String title = newsHeadlines.get(0).getQuestion();
            getParentActivity().readNewsHeadline(title, false);
            covidSlideShowAdapter.updateFeedbackFragments(newsHeadlines);
            binding.viewpagerNewsHeadline.setCurrentItem(0, true);
            initializeViewPagerIndicator();
        }
    }

    @Override
    public void covidShow(QuestionList newsDetail) {

    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void covidSaveSuccess() {
        getParentActivity().onBackPressed();
    }

    public boolean isLastPage() {
        return binding.viewpagerNewsHeadline.getCurrentItem() == (covidSlideShowAdapter.getCount() - 1);
    }

    public void proceedToNextPage() {
        if (binding.viewpagerNewsHeadline.getCurrentItem() + 1 < covidSlideShowAdapter.getCount())
            binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() + 1, true);
    }

    void proceedToPreviousPage() {
        if (binding.viewpagerNewsHeadline.getCurrentItem() - 1 >= 0)
            binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() - 1, true);
    }

    private void onClickEvents() {
        binding.buttonSubmit.setOnClickListener(view -> onSubmitBtnClicked());
        binding.btnBack.setOnClickListener(view -> onBackButtonClicked());
        binding.btnNext.setOnClickListener(view -> onNextButtonClicked());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBack());
    }
    void onSubmitBtnClicked() {
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        CovidSubmitRequest feedbackSubmitRequest = new CovidSubmitRequest();
        feedbackSubmitRequest.setDeviceId(deviceId);
        int avgRating = 0;
        for (Map.Entry<Integer, Covid> entry : mapListCovid.entrySet()) {
            avgRating = avgRating + entry.getValue().rating;
        }
        int avg = avgRating / 3;
        feedbackSubmitRequest.setOverallRating(avg + "");

        List<CovidSubmitRequest.CovidSubmitList> list = new ArrayList<CovidSubmitRequest.CovidSubmitList>();

        for (Map.Entry<Integer, Covid> entry : mapListCovid.entrySet()) {
            CovidSubmitRequest.CovidSubmitList value = new CovidSubmitRequest.CovidSubmitList();
            value.setRating("" + entry.getValue().rating);
            value.setFeedback("" + entry.getValue().value);
            value.setCategory("" + entry.getValue().category);
            list.add(value);
        }
        feedbackSubmitRequest.setFeedback(list);
        Log.e("kk", "" + feedbackSubmitRequest);
        covidPresenter.submitCovid(feedbackSubmitRequest);

    }

    void onBackButtonClicked(){
        binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() - 1);
    }

    void onNextButtonClicked(){
        binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() + 1);
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    void onParentClicked() {
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
        covidPresenter.presenterCleanUps();
    }
}
