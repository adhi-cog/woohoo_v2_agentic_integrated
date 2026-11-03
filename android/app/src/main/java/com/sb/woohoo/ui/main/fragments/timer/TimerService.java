package com.sb.woohoo.ui.main.fragments.timer;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.helpers.BroadcastHelper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimerService extends Service implements CounterActions {

    public static final String TIMER_DATA = "timer_data";
    private final IBinder mTimerBinder = new TimerBinder();
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    private LocalBroadcastManager localBroadcastManager;
    private TimerListener timerListener;
    private double timerTotalTime;
    private String timeTimeUnit;
    private static final int ID_SERVICE = 101;

    public void registerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager lNotifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = createNotificationChannel(lNotifiManager);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(PRIORITY_MIN)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            startForeground(ID_SERVICE, notification);
        }else{
            startService(new Intent(this, TimerService.class));
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "TimerService";
        String channelName = "WoohooTimerService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mTimerBinder;
    }

    public String getTimeTimeUnit() {
        return timeTimeUnit;
    }

    public void setTimeTimeUnit(String timeTimeUnit) {
        this.timeTimeUnit = timeTimeUnit;
    }

    public double getTimerTotalTime() {
        return timerTotalTime;
    }

    public void setTimerTotalTime(double timerTotalTime) {
        this.timerTotalTime = timerTotalTime;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
    }

    @Override
    public void onStartTimer(double time, String unit) {

        setTimerTotalTime(time);
        setTimeTimeUnit(unit);

        if (isTimerRunning) {
            onStopTimer();
        }

        long millisInFuture = getTimeInMillis(time, unit);

        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            public void onTick(long millisUntilFinished) {
                timerListener.onTick(millisUntilFinished);
                setTimerRunning(true);
            }

            public void onFinish() {
                timerListener.onFinish();

                setTimerRunning(false);
                //Start the new Intent

                Timer timer = new Timer();
                timer.setTime(time);
                timer.setUnit(unit);

                // Create intent with action
                Intent localIntent = new Intent(BroadcastHelper.BroadcastIntents.TIMER_INTENT);
                localIntent.putExtra(TIMER_DATA, timer);
                localBroadcastManager.sendBroadcast(localIntent);
            }
        }.start();
    }

    private long getTimeInMillis(double time, String unit) {

        switch (unit) {
            case "s":
                return (long) (time * 1000);
            case "min":
                return (long) (time * 60 * 1000);
            case "h":
                return (long) (time * 60 * 60 * 1000);
            default:
                //FIXME handle it properly later
                return 0;
        }
    }

    @Override
    public void onStopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            setTimerRunning(false);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
