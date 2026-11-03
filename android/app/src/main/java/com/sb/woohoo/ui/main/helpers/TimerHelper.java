package com.sb.woohoo.ui.main.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.timer.TimerPresenter;
import com.sb.woohoo.ui.main.fragments.timer.TimerService;
import com.sb.woohoo.ui.main.sip.compatibility.Compatibility;

import javax.inject.Inject;

public class TimerHelper extends BaseHelper {

    private static final String TAG = TimerHelper.class.getSimpleName();
    private TimerService mTimerService;
    private boolean isTimerServiceBound;
    private Intent mTimerIntent;
    private MainActivity mainActivity;
    private ServiceConnection mTimerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) iBinder; // Instantiate Binder
            mTimerService = binder.getService(); // Get Service
            LoggerWrapper.i(TAG, "Got Timer Service!");
            setTimerServiceBound(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            setTimerServiceBound(false);
        }
    };

    @Inject
    public TimerHelper(MainActivity context) {
        this.mainActivity = context;
        startTimerService(context);
    }

    public boolean isTimerServiceBound() {
        return isTimerServiceBound;
    }

    private void setTimerServiceBound(boolean timerServiceBound) {
        isTimerServiceBound = timerServiceBound;
    }

    public TimerService getTimerService() {
        LoggerWrapper.i(TAG, "Using Timer Service!");
        return mTimerService;
    }

    private void startTimerService(Context context) {
        if (mTimerIntent == null) {
            // Instantiate Intent.
            mTimerIntent = new Intent(context, TimerService.class);

            // Bind the Service.
            try {
                //FIXME Activity com.sb.woohoo.ui.main.MainActivity has leaked ServiceConnection com.sb.woohoo.ui.main.helpers.TimerHelper$1@1c347423 that was originally bound here
                //android.app.ServiceConnectionLeaked: Activity com.sb.woohoo.ui.main.MainActivity has leaked ServiceConnection com.sb.woohoo.ui.main.helpers.TimerHelper$1@1c347423 that was originally bound here
                context.bindService(mTimerIntent, mTimerConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                LoggerWrapper.e(TAG, "Security Exception while binding service!");
                e.printStackTrace();
            }
            Compatibility.startService(context,mTimerIntent);
        }
    }

    private void unbindTimerService() {
        if (mTimerConnection != null) {
            if (isTimerServiceBound()) {
                mTimerService.onUnbind(mTimerIntent);
            }
        }
    }

    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        unbindTimerService();
    }

    public void stopTimer() {
        if (isTimerServiceBound()) {
            if (mTimerService.isTimerRunning()) {
                mTimerService.onStopTimer();
                double time = mTimerService.getTimerTotalTime();
                String unit = mTimerService.getTimeTimeUnit();
                String text = String.format(mainActivity.getString(R.string.your_timer_cancelled),(int) time,TimerPresenter.getTimeString((int) time, unit));
                mainActivity.onRespondToUser(text);
            } else {
                mainActivity.onRespondToUser(mainActivity.getString(R.string.no_timer_set_));
            }
        } else {
            mainActivity.onRespondToUser(mainActivity.getString(R.string.no_timer_set_));
        }
    }
}
