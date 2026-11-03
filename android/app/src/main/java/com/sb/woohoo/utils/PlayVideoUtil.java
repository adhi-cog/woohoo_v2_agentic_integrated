package com.sb.woohoo.utils;

import android.content.Context;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.sb.woohoo.WooHooApp;

public class PlayVideoUtil {
    public static ExoPlayer initialisePlayer(Context aContext, ExoPlayer player){
        if (player == null) {
            player =  new ExoPlayer.Builder(aContext)
                    .setRenderersFactory(new DefaultRenderersFactory(aContext))
                    .setTrackSelector(new DefaultTrackSelector(aContext))
                    .setLoadControl(new DefaultLoadControl())
                    .build();
            player.setPlayWhenReady(true);
            player.seekTo(0, 0);
        }
        return player;
    }

    public static void playVideoInLoop(Context aContext, String videoSourceURL, SimpleExoPlayer player){
        MediaSource mMediaSource = buildMediaSource(aContext, videoSourceURL);
        LoopingMediaSource loopingSource = new LoopingMediaSource(mMediaSource);
        if (player != null) {
            player.prepare(
                    loopingSource,
                    true,
                    false
            );
        }
    }

    public static void playVideo(Context aContext, String videoSourceURL, ExoPlayer player){
        MediaSource mMediaSource = buildMediaSource(aContext, videoSourceURL);
        if (player != null) {
            player.setMediaSource(mMediaSource);
        }
    }

    public static MediaSource buildMediaSource(Context aContext, String audioSourceURL)
    {
        return WooHooApp.mProgressiveMediaSource.
                createMediaSource(MediaItem.fromUri(audioSourceURL));
    }
}
