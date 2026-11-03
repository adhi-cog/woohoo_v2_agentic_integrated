package com.sb.woohoo.ui.main.fragments.reminders;

import static android.content.Context.ALARM_SERVICE;

import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentReminderAlertBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver.ReminderReceiver;
import com.sb.woohoo.ui.main.fragments.reminders.di.DaggerReminderAlertComponent;
import com.sb.woohoo.ui.main.fragments.reminders.di.ReminderAlertComponent;
import com.sb.woohoo.ui.main.fragments.reminders.di.ReminderAlertModule;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.timer.Timer;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by Satyaraj on 3/10/2018.
 */

public class ReminderAlertFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private static final String REMINDER_KEY = "reminderKey";
    private static final String AlARM_KEY = "alarmKey";
    private static final String TIMER_KEY = "timerKey";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_REMINDER;


    @Inject
    ReminderRepository reminderRepository;

    @Inject
    MediaPlayer mMediaPlayer;

    @Inject
    AnalyticsHelper helper;

    private Reminder reminder;
    private Alarm alarm;
    private Timer timer;
    private FragmentReminderAlertBinding binding;

    public ReminderAlertFragment() {

    }

    public static ReminderAlertFragment newInstance(Reminder reminder) {
        Bundle args = new Bundle();
        args.putSerializable(REMINDER_KEY, reminder);
        ReminderAlertFragment fragment = new ReminderAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ReminderAlertFragment newInstance(Alarm alarm) {
        Bundle args = new Bundle();
        args.putSerializable(AlARM_KEY, alarm);
        ReminderAlertFragment fragment = new ReminderAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ReminderAlertFragment newInstance(Timer timer) {
        Bundle args = new Bundle();
        args.putSerializable(TIMER_KEY, timer);
        ReminderAlertFragment fragment = new ReminderAlertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return ReminderAlertFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.ReminderAlert(),  null);
        binding = FragmentReminderAlertBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ReminderAlertComponent reminderAlertComponent = DaggerReminderAlertComponent.builder()
                .reminderAlertModule(new ReminderAlertModule(this))
                .build();

        reminderAlertComponent.injectReminderAlertFragment(this);
    }

    @Override
    public void bindViews(View view) {
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setReminderTone();

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.Notification));
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void setReminderTone() {
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();
    }

    private void initViews() {

        if (getArguments() != null) {
            reminder = (Reminder) getArguments().getSerializable(REMINDER_KEY);
        }

        binding.btnDone.setOnClickListener(v -> dismissReminder());

        if (reminder != null) {
            binding.reminderTitle.setText(reminder.getReminder());
            binding.reminderTime.setText(getTime());
        }
    }

    public void dismissReminder() {
        mMediaPlayer.stop();
        ReminderFragment f = (ReminderFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ReminderFragment.class.getSimpleName());
        if (f != null)
            f.deleteReminder();
        else {
            reminderRepository.deleteReminder(reminder.getReminderId(), 100);
            Intent intent = new Intent(getContext(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(reminder.getReminderId()), intent, flagForPendingIntent());
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
        getParentActivity().onBackPressed();
    }

    @SuppressLint("DefaultLocale")
    private String getTime() {
        Calendar c = Calendar.getInstance();
        try {
            return String.format("%02d", c.get(Calendar.HOUR)) + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        getParentActivity().resumePlayingVideo();
    }

    void onBackPressed() {
        dismissReminder();
    }


    void onRootClicked() {

    }
}
