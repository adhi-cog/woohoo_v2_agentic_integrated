package com.sb.woohoo.ui.main.fragments.feedback;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;

import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentFeedbackBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.feedback.di.DaggerFeedbackComponent;
import com.sb.woohoo.ui.main.fragments.feedback.di.FeedbackComponent;
import com.sb.woohoo.ui.main.fragments.feedback.di.FeedbackModule;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.Question;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FeedbackFragment extends FeatureFragment<MainActivity> implements FeedbackContract.Views {

    private static String ARG_KEY_FEEDBACK_LIST = "FEEDBACK_LIST";
    private static String ARG_KEY_SOCKET_FEEDBACK = "ARG_SOCKET_FEEDBACK";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private FragmentFeedbackBinding binding;

    @Inject
    FeedbackAdapter feedbackSlideShowAdapter;

    @Inject
    FeedbackPresenter feedbackPresenter;

    @Inject
    AnalyticsHelper helper;

    private int dotCount;
    private ImageView[] dots;

    private List<Question> feedbackList = new ArrayList<>();
    private boolean mIsDataFromSocket = false;


    public int recentPos = 0;
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
            //String title = feedbackList.get(position).getQuestion();
            for (int i = 0; i < dotCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getParentActivity(),
                        R.drawable.circle_slideshow_not_select_item));
            }
            dots[position].setImageDrawable(ContextCompat.getDrawable(getParentActivity(),
                    R.drawable.circle_slideshow_selectitem));
            //getParentActivity().readNewsHeadline(title, false);

            feedbackSlideShowAdapter.updateUI(recentPos);
            if (dotCount != 0 && dotCount == position + 1) {
                binding.buttonSubmit.setVisibility(View.VISIBLE);
                binding.layoutBackNext.setVisibility(View.GONE);
            } else {
                binding.buttonSubmit.setVisibility(View.INVISIBLE);
                binding.layoutBackNext.setVisibility(View.VISIBLE);
                if (position == 0) {
                    binding.btnBack.setVisibility(View.GONE);
                } else {
                    binding.btnBack.setVisibility(View.VISIBLE);
                }
                binding.btnNext.setVisibility(View.VISIBLE);
            }
        }
    };

    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
        return fragment;
    }

    public static FeedbackFragment newInstance(@Nullable ArrayList<Question> aFeedbackList) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle lBundle = new Bundle();
        lBundle.putParcelableArrayList(ARG_KEY_FEEDBACK_LIST, aFeedbackList);
        lBundle.putBoolean(ARG_KEY_SOCKET_FEEDBACK, true);
        fragment.setArguments(lBundle);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return FeedbackFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Feedback(), null);
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        FeedbackComponent feedbackComponent = DaggerFeedbackComponent.builder()
                .feedbackModule(new FeedbackModule(this))
                .build();

        feedbackComponent.injectFeedbackFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_feedback_text_view_title));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
        //If data is not from socket, call the API
        if(!mIsDataFromSocket){
            feedbackPresenter.feedbackAPIRequest();
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return feedbackPresenter;
    }

    private void initViews()
    {
        binding.viewpagerNewsHeadline.setAdapter(feedbackSlideShowAdapter);
        binding.viewpagerNewsHeadline.setOffscreenPageLimit(7);
        binding.viewpagerNewsHeadline.addOnPageChangeListener(pageChangeListener);
        if (getArguments() != null) {
            List<Question> lFeedbackList = getArguments().getParcelableArrayList(ARG_KEY_FEEDBACK_LIST);
            mIsDataFromSocket = getArguments().getBoolean(ARG_KEY_SOCKET_FEEDBACK, false);
            feedbackResult(lFeedbackList);
        }else{
            feedbackResult(feedbackList);
        }


        onClickEvents();
    }

    private void initializeViewPagerIndicator() {

        binding.selectDot.removeAllViews();

        dotCount = feedbackSlideShowAdapter.getCount();

        dots = new ImageView[dotCount];

        for (int i = 0; i < dotCount; i++) {

            if (dots != null) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.circle_slideshow_not_select_item));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                binding.selectDot.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.circle_slideshow_selectitem));
            if (dotCount == 1) {
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
    public void feedbackMessage(int errorCode) {
        binding.buttonSubmit.setEnabled(true);
        if (errorCode == NETWORK_SUCCESS_CODE) {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.feedback_thanks));
        } else if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getParentActivity(), getString(R.string.try_again));
        }
    }

    @Override
    public void feedbackResult(List<Question> questionList)
    {
        try {
            if (questionList != null && questionList.size() > 0) {
                feedbackList = questionList;
                feedbackSlideShowAdapter.updateFeedbackFragments(feedbackList);
                binding.viewpagerNewsHeadline.setCurrentItem(0, true);
                initializeViewPagerIndicator();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

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
    public void feedbackSaveSuccess() {
        //getParentActivity().stopTextToSpeech(false);
        ShowConfirmDialog();
    }

    private void ShowConfirmDialog()
    {
        Dialog lDialog = new Dialog(requireContext(), R.style.DialogTheme);
        lDialog.setContentView(R.layout.dialog_feedback_success);
        LottieAnimationView lAnimatorView = lDialog.findViewById(R.id.feedback_success_img);
        lAnimatorView.setAnimation(R.raw.feedback_success_animator);
        lAnimatorView.addAnimatorListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lDialog.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        lAnimatorView.setRepeatCount(1);
        lAnimatorView.playAnimation();
        lDialog.setCanceledOnTouchOutside(false);
        lDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getParentActivity().onBackPressed();
            }
        });

        if (!lDialog.isShowing()) {
            lDialog.show();
        }
    }

    public boolean isLastPage() {
        return binding.viewpagerNewsHeadline.getCurrentItem() == (feedbackSlideShowAdapter.getCount() - 1);
    }

    public void proceedToNextPage() {
        if (binding.viewpagerNewsHeadline.getCurrentItem() + 1 < feedbackSlideShowAdapter.getCount())
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
        binding.buttonSubmit.setEnabled(false);
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        FeedbackSubmitRequest feedbackSubmitRequest = new FeedbackSubmitRequest();
        feedbackSubmitRequest.setDeviceId(deviceId);
        int avgRating = 0;
        for(Question list : feedbackSlideShowAdapter.getList()){
            avgRating = avgRating + list.getRating();
        }
        int avg = avgRating / feedbackSlideShowAdapter.getList().size();

        feedbackSubmitRequest.setOverallRating("" +avg);

        List<FeedbackSubmitRequest.FeedbackSubmitList> list = new ArrayList<FeedbackSubmitRequest.FeedbackSubmitList>();

        for(Question entry : feedbackSlideShowAdapter.getList()){
            FeedbackSubmitRequest.FeedbackSubmitList value = new FeedbackSubmitRequest.FeedbackSubmitList();
            value.setRating("" + entry.getRating());
            value.setFeedback("" + entry.getFeedBack());
            value.setCategory("" + entry.getCategory());
            list.add(value);
        }
        feedbackSubmitRequest.setFeedback(list);
        feedbackPresenter.submitFeedback(feedbackSubmitRequest);
    }

    void onBackButtonClicked() {
        binding.viewpagerNewsHeadline.setCurrentItem(binding.viewpagerNewsHeadline.getCurrentItem() - 1);
    }

    void onNextButtonClicked() {
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
        feedbackPresenter.presenterCleanUps();
    }
}
