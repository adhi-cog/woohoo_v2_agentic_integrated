package com.sb.woohoo.ui.main.fragments.settings.update.test;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateCallback;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateDataModel;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateTask;
import com.sb.woohoo.ui.main.fragments.timer.TimerService;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UpdateService extends Service implements UpdateCallback {

    private final IBinder mUpdateBinder = new UpdateBinder();
    private UpdateTask updateTask;
    private UpdateListener updateListener;
    private UpdateDataModel updateDataModel;
    private static final int ID_SERVICE = 102;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mUpdateBinder;
    }

    public void registerListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "UpdateService";
        String channelName = "WoohooUpdateService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (updateTask != null) {
            if (getStatus() == AsyncTask.Status.PENDING || getStatus() == AsyncTask.Status.RUNNING) {
                updateTask.cancel(true);
            }
        }
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
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
    public void onPreExecute() {

    }

    public boolean isUpdateCancelled() {
        return updateTask.isCancelled();
    }

    @Override
    public void onProgressUpdate(int value, int fileSize) {
        updateListener.onProgressUpdate(value);
    }

    @Override
    public void onUpdateSuccess(String fileLocation) {
        updateListener.onUpdateDownloadSuccess(fileLocation);
    }

    @Override
    public void onUpdateFailed() {
        updateListener.onUpdateDownloadFailed();
    }

    @Override
    public void onUpdateCancelled() {
        updateListener.onCancelDownload();
    }

    public void startDownload(UpdateDataModel updateDataModel) {
        //TODO use update data model to send the data to UpdateFragment
        this.updateDataModel = updateDataModel;
        updateTask = new UpdateTask(updateDataModel.getVersionCode(), this);
        updateTask.execute(updateDataModel.getUrl());
    }

    public void cancelDownload() {
        if (getStatus() == AsyncTask.Status.PENDING || getStatus() == AsyncTask.Status.RUNNING) {
            updateTask.cancel(true);
        }
        //TODO make updateDataModel as null
    }

    public void startInstall() {

    }

    public AsyncTask.Status getStatus() {
        if (updateTask != null) {
            return updateTask.getStatus();
        } else {
            return AsyncTask.Status.PENDING;
        }
    }

    public interface UpdateListener {
        void onProgressUpdate(int value);

        void onCancelDownload();

        void onUpdateDownloadFailed();

        void onUpdateDownloadSuccess(String fileLocation);
    }

    public class UpdateBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }
}
