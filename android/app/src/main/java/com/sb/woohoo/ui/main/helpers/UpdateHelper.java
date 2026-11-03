package com.sb.woohoo.ui.main.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.fragments.settings.update.test.UpdateService;
import com.sb.woohoo.ui.main.sip.compatibility.Compatibility;

import javax.inject.Inject;

public class UpdateHelper extends BaseHelper {
    private static final String TAG = UpdateHelper.class.getSimpleName();
    private UpdateService mUpdateService;
    private boolean isUpdateServiceBound;
    private Intent mUpdateIntent;
    private ServiceConnection mUpdateConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            UpdateService.UpdateBinder binder = (UpdateService.UpdateBinder) iBinder; // Instantiate Binder
            mUpdateService = binder.getService(); // Get Service
            LoggerWrapper.i(TAG, "Got Update Service!");
            isUpdateServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isUpdateServiceBound = false;
        }
    };

    @Inject
    public UpdateHelper(Context context) {
        startUpdateService(context);
    }

    public UpdateService getUpdateService() {
        LoggerWrapper.i(TAG, "Using Update Service!");
        return new UpdateService();
    }

    private void unbindTimerService() {
        if (mUpdateConnection != null) {
            if (isUpdateServiceBound) {
                mUpdateService.onUnbind(mUpdateIntent);
            }
        }
    }

    private void startUpdateService(Context context) {
        if (mUpdateIntent == null) {
            // Instantiate Intent.
            mUpdateIntent = new Intent(context, UpdateService.class);

            // Bind the Service.
            try {
                context.bindService(mUpdateIntent, mUpdateConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                LoggerWrapper.e(TAG, "Security Exception while binding service!");
            }

            // Start the Service.
            Compatibility.startService(context,mUpdateIntent);
        }
    }

    @Override
    public void onHelperCleanUp() {
        super.onHelperCleanUp();
        unbindTimerService();
    }
}