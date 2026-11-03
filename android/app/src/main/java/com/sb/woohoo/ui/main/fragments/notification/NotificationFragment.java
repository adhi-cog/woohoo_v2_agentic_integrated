package com.sb.woohoo.ui.main.fragments.notification;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentNotificationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmAdapter;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;
import com.sb.woohoo.ui.main.fragments.reminders.ReminderPresenter;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.timer.Timer;
import com.sb.woohoo.ui.main.fragments.timer.TimerPresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private static final String NOTIFICATION = "notification";
    private static final String REMINDER_KEY = "reminderKey";
    private static final String AlARM_KEY = "alarmKey";
    private static final String TIMER_KEY = "timerKey";
    private static final String NOTIFICATION_LIST = "notificationList";


    MediaPlayer mMediaPlayer;

    private NotificationAdapter notificationAdapter;
    private FragmentNotificationBinding binding;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(Reminder reminder) {
        Bundle args = new Bundle();
        args.putString(NOTIFICATION, REMINDER_KEY);
        args.putSerializable(REMINDER_KEY, reminder);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotificationFragment newInstance(Alarm alarm) {
        Bundle args = new Bundle();
        args.putString(NOTIFICATION, AlARM_KEY);
        args.putSerializable(AlARM_KEY, alarm);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotificationFragment newInstance(Timer timer) {
        Bundle args = new Bundle();
        args.putString(NOTIFICATION, TIMER_KEY);
        args.putSerializable(TIMER_KEY, timer);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NotificationFragment newInstance(ArrayList<Notification> notificationList) {
        Bundle args = new Bundle();
        args.putString(NOTIFICATION, NOTIFICATION_LIST);
        args.putSerializable(NOTIFICATION_LIST, notificationList);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Notification getNotification(Notifications notifications) {
        if (notifications instanceof Reminder) {
            Reminder reminder = (Reminder) notifications;
            if (reminder.getTime() != null && reminder.getReminder() != null) {
                String notificationTitle = "Reminder · " + ReminderPresenter.getTime(reminder.getDateTime());
                String notificationContent = reminder.getReminder();
                return new Notification(REMINDER_KEY, notificationTitle, notificationContent);
            }
        } else if (notifications instanceof Alarm) {
            Alarm alarm = (Alarm) notifications;
            if (alarm.getDateTime() != null) {
                String notificationTitle = "Alarm";
                String notificationContent = AlarmAdapter.getTime(alarm.getDateTime());
                return new Notification(AlARM_KEY, notificationTitle, notificationContent);
            }
        } else if (notifications instanceof Timer) {
            Timer timer = (Timer) notifications;
            if (timer.getTime() != 0 && timer.getUnit() != null) {
                int time = (int) timer.getTime();
                String units = TimerPresenter.getTimeString(time, timer.getUnit());
                String notificationTitle = "Timer";
                String notificationContent = "Your " + time + " " + units + " timer is done.";
                return new Notification(TIMER_KEY, notificationTitle, notificationContent);
            }
        }
        return null;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

    }

    @Override
    public void bindViews(View view) {
        binding.fragmentNotificationHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fragmentNotificationHeader.commonToolbarTitle.setText("Notification");
        notificationAdapter = new NotificationAdapter();
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(notificationAdapter);
        binding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        if (getArguments() != null) {
            String notificationKey = (String) getArguments().getSerializable(NOTIFICATION);
            if (notificationKey != null) {
                switch (notificationKey) {
                    case REMINDER_KEY:
                        Reminder reminder = (Reminder) getArguments().getSerializable(REMINDER_KEY);
                        if (reminder != null && reminder.getTime() != null && reminder.getReminder() != null) {
                            String notificationTitle = "Reminder · " + ReminderPresenter.getTime(reminder.getDateTime());
                            String notificationContent = reminder.getReminder();
                            Notification notification = new Notification(REMINDER_KEY, notificationTitle, notificationContent);
                            getParentActivity().onRespondToUser("You have a reminder to " + notificationContent);
                            notificationAdapter.addIntoList(notification);
                            setAlarmTone();
                        }
                        break;
                    case AlARM_KEY:
                        Alarm alarm = (Alarm) getArguments().getSerializable(AlARM_KEY);
                        if (alarm != null && alarm.getDateTime() != null) {
                            String notificationTitle = "Alarm";
                            String notificationContent = AlarmAdapter.getTime(alarm.getDateTime());
                            Notification notification = new Notification(AlARM_KEY, notificationTitle, notificationContent);
                            getParentActivity().onRespondToUser("You have an alarm at " + notificationContent);
                            notificationAdapter.addIntoList(notification);
                            setAlarmTone();
                        }
                        break;
                    case TIMER_KEY:
                        Timer timer = (Timer) getArguments().getSerializable(TIMER_KEY);
                        if (timer != null && timer.getTime() != 0 && timer.getUnit() != null) {
                            int time = (int) timer.getTime();
                            String units = TimerPresenter.getTimeString(time, timer.getUnit());

                            String notificationTitle = "Timer";
                            String notificationContent = "Your " + time + " " + units + " timer is done.";
                            Notification notification = new Notification(TIMER_KEY, notificationTitle, notificationContent);
                            notificationAdapter.addIntoList(notification);
                            getParentActivity().onRespondToUser("Your " + time + " " + units + " timer is done.");
                            setAlarmTone();
                        }
                        break;
                    case NOTIFICATION_LIST:
                        ArrayList<Notification> notifications = getArguments().getParcelableArrayList(NOTIFICATION_LIST);
                        notificationAdapter.updateList(notifications);
                        getParentActivity().onRespondToUser("These are some notifications which you have missed.");
                }
            }
        }
    }

    private void setAlarmTone() {
        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.alert_ringtone);

        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    public void updateNotification(Reminder reminder) {
        if (reminder != null && reminder.getTime() != null && reminder.getReminder() != null) {
            Notification notification;
            String notificationTitle = "Reminder · " + ReminderPresenter.getTime(reminder.getDateTime());
            String notificationContent = reminder.getReminder();
            notification = new Notification(REMINDER_KEY, notificationTitle, notificationContent);
            notificationAdapter.addIntoList(notification);
            getParentActivity().onRespondToUser("You have a reminder to " + notificationContent);
        }
    }

    public void updateNotification(Alarm alarm) {
        if (alarm != null && alarm.getDateTime() != null) {
            Notification notification;
            String notificationTitle = "Alarm";
            String notificationContent = alarm.getDateTime();
            notification = new Notification(AlARM_KEY, notificationTitle, notificationContent);
            notificationAdapter.addIntoList(notification);
        }
    }

    public void updateNotification(Timer timer) {
        if (timer != null && timer.getTime() != 0 && timer.getUnit() != null) {
            Notification notification;
            int time = (int) timer.getTime();
            String units = TimerPresenter.getTimeString(time, timer.getUnit());
            String notificationTitle = "Timer";
            String notificationContent = "Your " + time + " " + units + " timer is done.";
            notification = new Notification(TIMER_KEY, notificationTitle, notificationContent);
            notificationAdapter.addIntoList(notification);
            getParentActivity().onRespondToUser("Your " + time + " " + units + " timer is done.");
        }
    }

    public void showAllNotifications(List<Notification> notifications) {
        notificationAdapter.addMoreItems(notifications);
        getParentActivity().onRespondToUser("Notifications which you have missed.");
    }

    public void dismissNotification() {
        getParentActivity().onBackPressed();
    }

    public void onBackPressed() {
        mMediaPlayer.stop();
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }
}
