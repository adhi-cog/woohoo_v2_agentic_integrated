package com.sb.woohoo.utils.others;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;

public class MediaPlayerUtil {

    private MediaPlayer mediaPlayer;
    private Context context = null;
    private String TAG = MediaPlayerUtil.class.getSimpleName();

    public MediaPlayerUtil(Context context,
                           Uri uri,
                           SurfaceHolder holder,
                           AudioAttributes audioAttributes,
                           int audioSessionId) {
        this.mediaPlayer = MediaPlayer.create(context, uri, holder, audioAttributes, audioSessionId);
    }

    public MediaPlayerUtil(Context context,
                           int resid,
                           AudioAttributes audioAttributes,
                           int audioSessionId) {
        this.mediaPlayer = MediaPlayer.create(context, resid, audioAttributes, audioSessionId);

    }

    public MediaPlayerUtil(Context context,
                           Uri uri,
                           SurfaceHolder holder) {
        this.mediaPlayer = MediaPlayer.create(context, uri, holder);

    }

    public MediaPlayerUtil(Context context,
                           int resid) {
        this.mediaPlayer = MediaPlayer.create(context, resid);
    }

    public MediaPlayerUtil(Context context,
                           Uri uri) {
        this.mediaPlayer = MediaPlayer.create(context, uri);
    }

    public void setPlayerLooping(boolean flag) {
        mediaPlayer.setLooping(flag);
    }

    public void startPlayer() {
        mediaPlayer.start();
    }

    public void stopPlayer() {
        mediaPlayer.stop();
    }

    public void releasePlayer() {
        mediaPlayer.release();
    }

    public void setMediaPlayerVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public boolean isMediaPlayerPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void resetMediaPlayer() {
        mediaPlayer.reset();
    }

    public void setNextMediaPlayer(MediaPlayer mPlayer) {
        mediaPlayer.setNextMediaPlayer(mPlayer);
    }

    public void setScreenOnWhilePlayingMediaPlayer(boolean flag) {
        mediaPlayer.setScreenOnWhilePlaying(flag);
    }
}
