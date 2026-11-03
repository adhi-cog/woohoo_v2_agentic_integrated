package com.sb.woohoo.ui.main.fragments.music;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.sb.woohoo.utils.Constants.MusicFragment.REDIRECT_URI;
import static com.sb.woohoo.utils.Constants.MusicFragment.SPOTIFY_CLIENT_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.callbacks.CompletionCallback;
import com.sb.woohoo.utils.others.MethodUtils;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

public class MusicService extends Service
        implements ConnectionStateCallback, Player.NotificationCallback {

    public static String MUSIC_REFERENCE = "music_reference";
    private final IBinder mMusicBinder = new MusicBinder();
    private MusicCallbackListener mListener;
    private SpotifyAppRemote mSpotifyAppRemote;

    private CompletionCallback completionCallback;
    private Player.OperationCallback mSpotifyOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            // TODO : Figure out a way to give the call to MusicFragment once when player is initialized.
            LoggerWrapper.i(getTag(), "onSuccess ()");
        }

        @Override
        public void onError(Error error) {
            LoggerWrapper.e(getTag(), "onError () : " + error.toString());
        }
    };

    public void setListener(MusicCallbackListener listener, CompletionCallback callback) {
        this.mListener = listener;
        this.completionCallback = callback;
    }

    public String getTag() {
        return MusicService.class.getSimpleName();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        terminateSpotify();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the Foreground Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService();
        }
    }

    private void startForegroundService(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(101, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "music_service";
        NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    /**
     * Shuts down Spotify.
     */
    public void terminateSpotify() {
        try {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************************** Public Methods *************************************************/

    public void playMusic(@NonNull String uri)
    {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .play(uri);
        }else{
            MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.player_not_initialised));
        }
    }

    public void pauseMusic(CompletionCallback pauseCallback) {
        //this.completionCallback = pauseCallback;
        if (getPlayBackState() !=null && !getPlayBackState().isPaused){
            mSpotifyAppRemote.getPlayerApi().pause();
        }else{
            MethodUtils.showToastMessage(getApplicationContext(), getString(R.string.player_not_initialised));
        }
    }

    public void resumeMusic() {
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().resume();
            seekMusic(0);
        }
    }

    public void stopMusic() {
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().pause();
            seekMusic(0);
        }
    }

    public void seekMusic(int position) {
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().seekTo(position);
        }
    }

   /* public Metadata getMetaData() {
        return mSpotifyPlayer.getMetadata();
    }*/

    private PlayerState getPlayBackState() {
        return mSpotifyAppRemote == null ? null : mSpotifyAppRemote.getPlayerApi().getPlayerState().await().getData();
    }

    public int musicCurrentPosition() {
        return getPlayBackState() == null ? -1 : (int) getPlayBackState().playbackPosition / 1000;
    }

    public boolean isPlaying() {
        return getPlayBackState() != null && !getPlayBackState().isPaused;
    }

    public boolean isRepeating() {
        return getPlayBackState() != null && getPlayBackState().playbackOptions.repeatMode != 0;
    }

    public boolean isShuffling() {
        return getPlayBackState() != null && getPlayBackState().playbackOptions.isShuffling;
    }

    public void setRepeat(boolean repeat) {
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().setRepeat(repeat ? 1 : 0);
        }
    }

    public void setShuffle(boolean shuffle) {
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().setShuffle(shuffle);
        }
    }

    public void skipToNext() {
       /* Metadata metadata = getMetaData();
        if (metadata != null) {
            Metadata.Track nextTrack = metadata.nextTrack;

            if (nextTrack != null) {
                mSpotifyPlayer.skipToNext(mSpotifyOperationCallback);
            }
        }*/

        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().skipNext();
        }
    }

    public void skipToPrevious() {
       /* Metadata metadata = getMetaData();
        if (metadata != null) {
            Metadata.Track prevTrack = metadata.prevTrack;
            if (prevTrack != null) {
                mSpotifyPlayer.skipToPrevious(mSpotifyOperationCallback);
            }
        }*/

        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().skipPrevious();
        }
    }

    public boolean isLoggedIn() {
        return mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected();
    }

    public boolean isPlayerInitialized() {
        return mSpotifyAppRemote != null;
    }

    public boolean isPlayerTerminated() {
        return mSpotifyAppRemote == null || !mSpotifyAppRemote.isConnected();
    }

    /***************************************** Connection State Callback Methods ****************************************/

    @Override
    public void onLoggedIn() {
        mListener.onLoggedIn();
    }

    @Override
    public void onLoggedOut() {
        mListener.onLoggedOut();
    }

    @Override
    public void onLoginFailed(Error error) {
        mListener.onLoginFailed();
    }

    @Override
    public void onTemporaryError() {
        LoggerWrapper.e(getTag(), "onTemporaryError ()");
    }

    @Override
    public void onConnectionMessage(String s) {
        LoggerWrapper.e(getTag(), s);
    }

    /******************************************* Notification Callback Methods ******************************************/

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        switch (playerEvent) {
            case kSpPlaybackNotifyContextChanged:

               // mListener.takeDataToRespondUser(mSpotifyPlayer.getMetadata());

                break;
            case kSpPlaybackNotifyPlay:
                mListener.onPlaying();
                break;
            case kSpPlaybackNotifyPause:
                mListener.onPaused();
                if (completionCallback != null) {
                    completionCallback.onActionComplete();
                }
                break;
            case kSpPlaybackNotifyNext:
                mListener.onNextTrack();
                break;
            case kSpPlaybackNotifyPrev:
                mListener.onPreviousTrack();
                break;
            case kSpPlaybackNotifyShuffleOn:
                mListener.onShuffleOn();
                break;
            case kSpPlaybackNotifyShuffleOff:
                mListener.onShuffleOff();
                break;
            case kSpPlaybackNotifyRepeatOn:

                mListener.onRepeatOn();
                break;
            case kSpPlaybackNotifyRepeatOff:
                mListener.onRepeatOff();
                break;
            case kSpPlaybackNotifyAudioDeliveryDone:
                mListener.onStopped();
                break;
            case kSpPlaybackNotifyTrackChanged:
                mListener.onTrackChanged();
                break;
            case kSpPlaybackNotifyTrackDelivered:
                mListener.onTrackDelivered();
                break;
            case kSpPlaybackNotifyMetadataChanged:
                mListener.onTrackDelivered();
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        mListener.onPlaybackError(error);
    }

    /********************************************** Service Callback Methods ********************************************/

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void connect(String token)
    {
        if(mSpotifyAppRemote != null){
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            mSpotifyAppRemote = null;
        }

        SpotifyAppRemote.connect(
                getApplicationContext(),
                new ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build(),

                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        LoggerWrapper.d(getTag(), " Inside OnConnected : "+spotifyAppRemote.getConnectApi());
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        LoggerWrapper.e(getTag(), " Inside onFailure : "+error.getMessage());
                        error.printStackTrace();
                    }
                });
    }
}
