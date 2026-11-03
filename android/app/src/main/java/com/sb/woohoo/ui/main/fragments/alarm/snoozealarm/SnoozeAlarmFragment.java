package com.sb.woohoo.ui.main.fragments.alarm.snoozealarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSnoozeAlarmBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmContract;
import com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.di.DaggerSnoozeAlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.di.SnoozeAlarmComponent;
import com.sb.woohoo.ui.main.fragments.alarm.snoozealarm.di.SnoozeAlarmModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class SnoozeAlarmFragment  extends FeatureFragment<MainActivity> implements AlarmContract.View, PopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_ALARM;

    @Inject
    AnalyticsHelper helper;

    private FragmentSnoozeAlarmBinding binding;
    public SnoozeAlarmFragment() {
    }

    public static SnoozeAlarmFragment newInstance(IntentInfo intentInfo) {
        SnoozeAlarmFragment fragment = new SnoozeAlarmFragment();

        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        SnoozeAlarmComponent snoozeAlarmComponent = DaggerSnoozeAlarmComponent.builder()
                .snoozeAlarmModule(new SnoozeAlarmModule(this))
                .build();

        snoozeAlarmComponent.injectSnoozeAlarmFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.snooze_alarm);
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void displayAlarms() {

    }

    @Override
    public void alarmAdded() {

    }

    @Override
    public void alarmDeleted() {

    }

    @Override
    public void alarmsDeleted() {

    }

    @Override
    public void alarmUpdated() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.SnoozeAlert(),  null);
        binding = FragmentSnoozeAlarmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
