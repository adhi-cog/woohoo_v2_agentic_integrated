package com.sb.woohoo.ui.main.fragments.timer.alert;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentTimerAlertBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.timer.Timer;
import com.sb.woohoo.ui.main.fragments.timer.TimerPresenter;
import com.sb.woohoo.ui.main.fragments.timer.TimerService;
import com.sb.woohoo.ui.main.fragments.timer.alert.di.DaggerTimerAlertComponent;
import com.sb.woohoo.ui.main.fragments.timer.alert.di.TimerAlertComponent;
import com.sb.woohoo.ui.main.fragments.timer.alert.di.TimerAlertModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import java.util.Objects;

import javax.inject.Inject;

/**
 * A simple subclass.
 */
public class TimerAlertFragment extends FeatureFragment<MainActivity> implements TimerAlertContract.View, NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_TIMER;

    @Inject
    TimerAlertPresenter timerAlertPresenter;
    @Inject
    MediaPlayerUtil mediaPlayerUtil;
    private FragmentTimerAlertBinding binding;

    public TimerAlertFragment() {
        // Required empty public constructor
    }

    public static TimerAlertFragment newInstance(Intent intent) {
        Timer timer = (Timer) Objects.requireNonNull(intent.getExtras()).getSerializable(TimerService.TIMER_DATA);
        Bundle args = new Bundle();
        args.putSerializable(TimerService.TIMER_DATA, timer);
        TimerAlertFragment fragment = new TimerAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTimerAlertBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Timer timer = (Timer) getArguments().getSerializable(TimerService.TIMER_DATA);
            double time = 0;
            String unit = null;
            if (timer != null) {
                time = timer.getTime();
                unit = timer.getUnit();
            }
            int timeInInt = (int) time;
            String timerDoneText = null;
            if (unit != null) {
                timerDoneText = String.format(getString(R.string.your_timer_is_done),(int) timeInInt,TimerPresenter.getTimeString(timeInInt, unit));
            }
            binding.textTimer.setText(timerDoneText);
            respondToUser(timerDoneText);
            mediaPlayerUtil.setPlayerLooping(true);
            mediaPlayerUtil.startPlayer();
        }
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        TimerAlertComponent timerAlertComponent = DaggerTimerAlertComponent.builder()
                .timerAlertModule(new TimerAlertModule(this))
                .build();

        timerAlertComponent.injectTimerAlertFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentCommonToolbar.commonToolbarTitle.setText(R.string.fragment_timer_title);
        binding.fragmentCommonToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackClicked());
        binding.done.setOnClickListener(view1 -> onDoneClicked());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return timerAlertPresenter;
    }

    void onBackClicked() {
        getParentActivity().onBackPressed();
    }

    void onDoneClicked() {
        getParentActivity().onBackPressed();
    }

    void onRootClicked() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerUtil.releasePlayer();
        getParentActivity().resumePlayingVideo();
    }

    public void dismissTimer() {
        getParentActivity().onBackPressed();
    }
}
