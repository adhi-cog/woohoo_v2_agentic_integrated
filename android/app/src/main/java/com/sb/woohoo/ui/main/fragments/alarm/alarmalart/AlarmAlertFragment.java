package com.sb.woohoo.ui.main.fragments.alarm.alarmalart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentAlarmAlertBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmRepository;
import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.di.AlarmAlertComponent;
import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.di.AlarmAlertModule;
import com.sb.woohoo.ui.main.fragments.alarm.alarmalart.di.DaggerAlarmAlertComponent;
import com.sb.woohoo.ui.main.fragments.alarm.broadcastreceiver.AlarmReceiver;
import com.sb.woohoo.ui.main.fragments.alarm.broadcastreceiver.SetAlarm;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MediaPlayerUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class AlarmAlertFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    public static int ALARM_SNOOZE_ID = 500;
    private static String ALARM_KEY = "alarmKey";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_ALARM;

    @Inject
    AnalyticsHelper helper;
    @Inject
    AlarmRepository alarmRepository;

//    @Inject
//    MediaPlayer mMediaPlayer;

    @Inject
    MediaPlayerUtil mediaPlayerUtil;

    @Inject
    AlarmAlertPresenter mPresenter;

    Alarm alarm;

    private FragmentAlarmAlertBinding binding;
    public AlarmAlertFragment() {
    }

    public static AlarmAlertFragment newInstance(Alarm alarmAlert) {
        Bundle args = new Bundle();
        args.putSerializable(ALARM_KEY, alarmAlert);
        AlarmAlertFragment fragment = new AlarmAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return AlarmAlertFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.AlarmAlert(),  null);
        binding = FragmentAlarmAlertBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        AlarmAlertComponent alarmAlertComponent = DaggerAlarmAlertComponent.builder()
                .alarmAlertModule(new AlarmAlertModule(this))
                .build();

        alarmAlertComponent.injectAlarmAlertFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            alarm = (Alarm) getArguments().getSerializable(ALARM_KEY);
        }

        if (alarm != null) {
            binding.alarmTime.setText(getTime(alarm.getDateTime()));

            mediaPlayerUtil.setPlayerLooping(true);
            mediaPlayerUtil.startPlayer();
        }

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.Alarm));

        binding.snooze.setOnClickListener(v -> snoozeAlarm());

        binding.dismiss.setOnClickListener(v -> dismissAlarm());

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    public void snoozeAlarm() {
        mediaPlayerUtil.stopPlayer();
        SetAlarm.setAlarm(getContext(), AlarmReceiver.class, alarm, ALARM_SNOOZE_ID, alarm.isEveryday(), true);
        getParentActivity().onBackPressed();
    }

    public void dismissAlarm() {
        mediaPlayerUtil.stopPlayer();
        if (alarm.isSnooze()) {
            SetAlarm.cancelAlarm(getContext(), AlarmReceiver.class, ALARM_SNOOZE_ID);
            alarmRepository.deleteAlarm(alarm, false, -1);
        } else if (!alarm.isEveryday()) {
            int alarmID = Integer.parseInt(alarm.getAlarmID());
            SetAlarm.cancelAlarm(getContext(), AlarmReceiver.class, alarmID);
            alarmRepository.deleteAlarm(alarm, false, -1);
        }
        getParentActivity().onBackPressed();
    }

    private String getTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        Date date;
        String outputTime;
        try {
            date = df.parse(time);
            outputTime = outputFormat.format(date);
            return outputTime.replace("AM", "am").replace("PM", "pm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    void onBackPressed() {
        dismissAlarm();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerUtil.releasePlayer();
        getParentActivity().resumePlayingVideo();
    }

}
