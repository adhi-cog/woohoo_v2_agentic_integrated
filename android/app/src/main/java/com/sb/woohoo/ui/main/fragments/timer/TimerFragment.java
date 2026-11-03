package com.sb.woohoo.ui.main.fragments.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentTimerBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.timer.di.DaggerTimerComponent;
import com.sb.woohoo.ui.main.fragments.timer.di.TimerComponent;
import com.sb.woohoo.ui.main.fragments.timer.di.TimerModule;
import com.sb.woohoo.ui.main.helpers.TimerHelper;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class TimerFragment extends FeatureFragment<MainActivity>
        implements TimerContract.View, TimerService.TimerListener, PopFragment {

    public static final String TIMER_KEY = "timer";
    private static final int defaultColor = R.attr.color_secondary;
    private static final int timerBg = R.drawable.bg_timer;

    @Inject
    TimerPresenter timerPresenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    TimerHelper timerHelper;
    private TimerService timerService;
    private FragmentTimerBinding binding;

    public TimerFragment() { /*Required empty public constructor*/ }

    public static TimerFragment newInstance(IntentInfo intentInfo) {
        TimerFragment fragment = new TimerFragment();
        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    @Override
    public String createTag() {
        return TimerFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerService = timerHelper.getTimerService();
        timerService.registerListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Timer(),  null);
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public String getCategory() {
        return Categories.Skills.COMMAND_SKILL_TIMER;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        TimerComponent timerComponent = DaggerTimerComponent.builder()
                .timerModule(new TimerModule(this))
                .build();

        timerComponent.injectTimerFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentCommonToolbar.commonToolbarTitle.setText(R.string.fragment_timer_title);
        binding.fragmentCommonToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackClicked());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUIForTimer() {
        binding.layoutTimer.setVisibility(View.VISIBLE);
        binding.layoutTimerEmpty.setVisibility(View.GONE);

        binding.wallpaper.setImageResource(timerBg);

        String unit = timerService.getTimeTimeUnit();
        double time = timerService.getTimerTotalTime();

        binding.fragmentTimerTextViewTimerTime.setText((int) time + " " + TimerPresenter.getTimeString((int) time, unit));
    }

    private void setUIForEmptyTimer() {
        binding.layoutTimer.setVisibility(View.GONE);
        binding.layoutTimerEmpty.setVisibility(View.VISIBLE);

        binding.wallpaper.setBackgroundColor(ContextCompat.getColor(getParentActivity(), defaultColor));
    }

    @Override
    public BasePresenter getPresenter() {
        return timerPresenter;
    }

    void onBackClicked() {
        getParentActivity().onBackPressed();
    }

    void onRootClicked() {
    }

    @Override
    public void cancelTimer() {
        timerService.onStopTimer();
        setUIForEmptyTimer();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        binding.fragmentTimerTextViewTimer.setText(MethodUtils.formatTimeInHrs(millisUntilFinished));
    }

    @Override
    public void onFinish() {
        getParentActivity().onBackPressed();
//         binding.fragmentTimerTextViewTimer.setText(MethodUtils.formatTimeInHrs(0L));
//        mTvTimerTotalTime.setText("");
    }

    @Override
    public void startTimer(double time, String unit) {
        timerService.onStartTimer(time, unit);
        setUIForTimer();
    }

    @Override
    public void checkAndShow() {
        if (timerService.isTimerRunning()) {
            setUIForTimer();
        } else {
            setUIForEmptyTimer();
            getParentActivity().onRespondToUser(getString(R.string.no_timer_set));
        }
    }
}