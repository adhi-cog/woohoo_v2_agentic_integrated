package com.sb.woohoo.ui.main.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BaseHelper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.CompletionCallback;
import com.sb.woohoo.ui.main.fragments.music.MusicCallbackListener;
import com.sb.woohoo.ui.main.fragments.music.MusicService;
import com.sb.woohoo.ui.main.sip.compatibility.Compatibility;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.SpotifyPlayer;

import javax.inject.Inject;

public class MusicHelper extends BaseHelper {
    // Constants.
    private static final String TAG = MusicHelper.class.getSimpleName();
    // Components.
    private MusicService mMusicService;
    private Intent mMusicIntent;
    // Listeners.
    private MusicCallbackListener mMusicListener;
    private CompletionCallback mCompletionListener;
    // Variables.
    private boolean wasMusicPlaying = false;
    private boolean isMusicServiceBound;
    private MainActivity mainActivity;
    /******************************************* {@link ServiceConnection}s *******************************************/

    // Music Service Connection.
    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder; // Instantiate Binder
            mMusicService = binder.getService(); // Get Service
            isMusicServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isMusicServiceBound = false;
        }
    };
    /******************* {@link com.spotify.sdk.android.player.SpotifyPlayer.InitializationObserver} ******************/

    private SpotifyPlayer.InitializationObserver initializationObserver = new SpotifyPlayer.InitializationObserver() {
        @Override
        public void onInitialized(SpotifyPlayer spotifyPlayer) {
            // TODO : Provide callback to MusicFragment to process the Intent.
            /*mPresenter.processIntents ();*/
        }

        @Override
        public void onError(Throwable throwable) {
            printLog(throwable.getMessage());
        }
    };

    @Inject
    public MusicHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        //startMusicService(mainActivity);
    }

    /******************************************* {@link BaseHelper} Methods *******************************************/

    @Override
    public void onHelperCleanUp(Context context) {
        super.onHelperCleanUp(context);
        if (mMusicService!=null && mMusicService.isPlaying()) mMusicService.stopMusic();
       // stopMusicService(context);
    }

    /********************************************* {@code private} Methods ********************************************/

    private void startMusicService(Context context) {
        if (mMusicIntent == null) {
            // Instantiate Intent.
            mMusicIntent = new Intent(context, MusicService.class);

            // Bind the Service.
            try {
                //FIXME Activity com.sb.woohoo.ui.main.MainActivity has leaked ServiceConnection com.sb.woohoo.ui.main.helpers.MusicHelper$1@2dc8f687 that was originally bound here
                //    android.app.ServiceConnectionLeaked: Activity com.sb.woohoo.ui.main.MainActivity has leaked ServiceConnection com.sb.woohoo.ui.main.helpers.MusicHelper$1@2dc8f687 that was originally bound here
                context.bindService(mMusicIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                printLog("Security Exception while binding service!");
            }

            // Start the Service.
            // context.startService(mMusicIntent);
            Compatibility.startService(context,mMusicIntent);
        }
    }

    private void stopMusicService(Context context) {
        if (isMusicServiceBound) {
            try {
                context.unbindService(mMusicConnection);
                printLog("Un-Bind Service");
            } catch (IllegalArgumentException e) {
                mMusicService.terminateSpotify();
                printLog(e.getMessage());
            } finally {
                mMusicService = null;
                mMusicIntent = null;
                mMusicListener = null;
                mCompletionListener = null;
            }
        }
    }

    @SuppressWarnings("unused")
    private void printLog(String message) {
        LoggerWrapper.i(TAG, message);
    }

    /********************************************* {@code public} Methods *********************************************/

    public void setListener(MusicCallbackListener listener, CompletionCallback callback) {
        this.mMusicListener = listener;
        this.mCompletionListener = callback;
    }

    public void setSpotifyPlayer(Context context, String token) {
        mMusicService.connect(token);
        mMusicService.setListener(mMusicListener, mCompletionListener);
    }

    public void terminateSpotifyPlayer() {
        if (isPlayerInitialized()) {
            mMusicService.terminateSpotify();
        }
    }

    public boolean isMusicPlaying() {
        return wasMusicPlaying;
    }

    public void setMusicPlaying(boolean wasMusicPlaying) {
        this.wasMusicPlaying = wasMusicPlaying;
    }

    public void pauseMusic() {
        if (isPlayerInitialized()) {
            if (mMusicService.isPlaying()) {
                setMusicPlaying(true);
                mMusicService.pauseMusic(null);
            }
        }
    }

    public void resumeMusic() {
        if (isPlayerInitialized()) {
            if (!isPlaying()) {
                mMusicService.resumeMusic();
                setMusicPlaying(true);
            }
        }
    }

    public void resumeMusicWasPlaying() {
        if (isPlayerInitialized()) {
            if (isMusicPlaying()) {
                mMusicService.resumeMusic();
                setMusicPlaying(false);
            }
        }
    }

    public void stopMusic() {
        if (isPlayerInitialized()) {
            mMusicService.stopMusic();
            setMusicPlaying(true);
        }
    }

    public void skipToNextMusic() {
        if (isPlayerInitialized()) {
           /* Metadata metadata = mMusicService.getMetaData();
            if (metadata != null) {
                Metadata.Track nextTrack = metadata.nextTrack;
                if (nextTrack != null) {

                } else {
                    mainActivity.onRespondToUser(mainActivity.getString(R.string.next_song_not_available));
                }
            }*/
            mMusicService.skipToNext();
        }
    }

    public void skipToPreviousMusic() {
        if (isPlayerInitialized()) {
            /*Metadata metadata = mMusicService.getMetaData();
            if (metadata != null) {
                Metadata.Track prevTrack = metadata.prevTrack;
                if (prevTrack != null) {
                    mMusicService.skipToPrevious();
                } else {
                    mainActivity.onRespondToUser(mainActivity.getString(R.string.prev_song_not_available));
                }
            }*/
            mMusicService.skipToPrevious();
        }
    }

    public boolean isPlaying() {
        return isPlayerInitialized() && mMusicService.isPlaying();
    }

    public boolean isRepeating() {
        return isPlayerInitialized() && mMusicService.isRepeating();
    }

    public void setRepeat(boolean repeat) {
        if (isPlayerInitialized()) {
            mMusicService.setRepeat(repeat);
        }
    }

    public void setShuffle(boolean shuffle) {
        if (isPlayerInitialized()) {
            mMusicService.setShuffle(shuffle);
        }
    }

    public boolean isLoggedIn() {
        return isPlayerInitialized() && mMusicService.isLoggedIn();
    }

    public boolean isPlayerInitialized() {
        return mMusicService != null && isMusicServiceBound && mMusicService.isPlayerInitialized();
    }

    public boolean isPlayerTerminated() {
        return isMusicServiceBound && mMusicService.isPlayerTerminated();
    }

    public void seekMusic(int position) {
        if (isPlayerInitialized()) {
            mMusicService.seekMusic(position);
        }
    }

    public boolean isShuffling() {
        return isPlayerInitialized() && mMusicService.isShuffling();
    }

    public @Nullable
    Metadata getMetaData() {
        return isPlayerInitialized() ? null : null;
    }

    public int getMusicCurrentPosition() {
        return isPlayerInitialized() ? mMusicService.musicCurrentPosition() : -1;
    }

    public void playMusic(String spotifyURI) {
        mMusicService.playMusic(spotifyURI);
    }
}
