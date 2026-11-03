package com.sb.woohoo.ui.main.helpers;

import static com.sileria.android.Kit.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmPresenter;
import com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver.SetReminder;
import com.sb.woohoo.ui.main.fragments.timer.TimerFragment;

import javax.inject.Inject;

/**
 * Handles all the callbacks of {@link BroadcastReceiver}.
 */
@SuppressWarnings("DanglingJavadoc")
public class BroadcastHelper extends BaseHelper {
    private BroadcastListener mListener;

    /**
     * Broadcast receiver to receiver all the actions.
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (intent.getAction()) {
                    case BroadcastIntents.ALARM_INTENT:
                        mListener.onBroadcastTrigger(intent, AlarmPresenter.ALARM_KEY);
                        break;
                    case BroadcastIntents.REMINDER_INTENT:
                        mListener.onBroadcastTrigger(intent, SetReminder.REMINDER_KEY);
                        break;
                    case BroadcastIntents.TIMER_INTENT:
                        mListener.onBroadcastTrigger(intent, TimerFragment.TIMER_KEY);
                        break;
                    case BroadcastIntents.BATTERY_INTENT:
                        mListener.onBatteryStateReceived(intent);
                        break;
                    case BroadcastIntents.DATE_INTENT:
                        mListener.onDateChangeReceived(intent);
                        break;
                    default:
                        mListener.onIntentNotFound(intent);
                }
            } else {
                mListener.onActionNotFoundForBroadcast(intent);
            }
        }
    };

    @Inject
    public BroadcastHelper() {

    }

    public BroadcastListener getListener() {
        return mListener;
    }

    public void setListener(BroadcastListener listener) {
        this.mListener = listener;
    }

	/*public void registerBroadcast ( Context context, String intent ) {
		context.registerReceiver ( mBroadcastReceiver, new IntentFilter ( intent ) );
	}*/

    /**
     * Registers all the actions of Broadcast Receiver.
     *
     * @param context {@link Context} of {@link android.app.Activity}. used to un-register the
     *                               Broadcast Receiver.
     */
    private final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    private VolumeChangeListener volumeChangeListener;

    public void registerBroadcasts(Context context) {
        // Instantiate Intent Filter.
        IntentFilter intentFilter = new IntentFilter();

        // Add all actions to IntentFilter.
        intentFilter.addAction(BroadcastIntents.ALARM_INTENT);
        intentFilter.addAction(BroadcastIntents.REMINDER_INTENT);
        intentFilter.addAction(BroadcastIntents.BATTERY_INTENT);
        intentFilter.addAction(BroadcastIntents.DATE_INTENT);

        // Register the Broadcast Receiver with Intent Filter.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(mBroadcastReceiver, intentFilter,
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(mBroadcastReceiver, intentFilter);
        }

        // Add all actions of local broadcasts to IntentFilter.
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(BroadcastIntents.TIMER_INTENT);
        localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter1);


        this.volumeChangeListener = new VolumeChangeListener();

        IntentFilter intentFilterVolChange = new IntentFilter(VOLUME_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(volumeChangeListener, intentFilterVolChange, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(volumeChangeListener, intentFilterVolChange);
        }
    }

    private class VolumeChangeListener extends BroadcastReceiver {
        // VOLUME CHANGED ACTION triggers 3 times
        // Making use of increment variable to make sure to skip the extra calls
        private int callCount = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            callCount++;
            if (intent.getAction().equals(VOLUME_CHANGED_ACTION)) {
                if (callCount % 3 == 0) {
                    AudioManager audioManager =
                            (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    int SYSTEM_currentVolume =
                            audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    LoggerWrapper.d("MUSIC", "STREAM_RING " + SYSTEM_currentVolume);
                    SYSTEM_currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    LoggerWrapper.d("MUSIC", "STREAM_MUSIC " + SYSTEM_currentVolume);
                    SYSTEM_currentVolume =
                            audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                    LoggerWrapper.d("MUSIC", "STREAM_NOTIFICATION " + SYSTEM_currentVolume);

                    mListener.onVolumeListener();

                }
            }
        }
    }

    @Override
    public void onHelperCleanUp(Context context) {
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }
        mBroadcastReceiver = null;
        mListener = null;
        try {
            context.unregisterReceiver(volumeChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************************** {@code interface}s
     *  ***********************************************/

    /**
     * Contains constants as Action Name for {@link IntentFilter}.
     */
    public interface BroadcastIntents {
        String ALARM_INTENT = "alarm_intent";
        String REMINDER_INTENT = "reminder_intent";
        String TIMER_INTENT = "timer_intent";
        String BATTERY_INTENT = Intent.ACTION_BATTERY_CHANGED;
        String DATE_INTENT = "home_intent";
    }

    /**
     * Listener for {@link BroadcastReceiver} action callbacks.
     */
    public interface BroadcastListener {

        /**
         * Called when {@code BroadcastIntents.ALARM_INTENT} is received as action of
         * {@link BroadcastReceiver}.
         *
         * @param intent {@link Intent} containing data of Alarm, Reminder or Timer.
         */
        void onBroadcastTrigger(Intent intent, String key);

        void onVolumeListener();

        /**
         * Called when {@code BroadcastIntents.BATTERY_INTENT} is received as action of
         * {@link BroadcastReceiver}.
         *
         * @param intent {@link Intent} containing data of Battery.
         */
        void onBatteryStateReceived(Intent intent);

        /**
         * Called when {@code BroadcastIntents.DATE_INTENT} is received as action of
         * {@link BroadcastReceiver}.
         *
         * @param intent {@link Intent} containing data of Date.
         */
        void onDateChangeReceived(Intent intent);

        /**
         * Called when no intent has been found in {@link BroadcastIntents}.
         *
         * @param intent {@link Intent} containing some other information about Broadcast.
         */
        void onIntentNotFound(Intent intent);

        /**
         * Called when no action has been found in {@link Intent} of {@link BroadcastReceiver}.
         *
         * @param intent {@link Intent} containing some other information about Broadcast.
         */
        void onActionNotFoundForBroadcast(Intent intent);
    }
}