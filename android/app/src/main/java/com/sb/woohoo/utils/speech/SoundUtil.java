package com.sb.woohoo.utils.speech;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundUtil {

    private static MediaPlayer mediaPlayer;

    public static void playSound(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            // If the MediaPlayer is already playing, stop it and reset
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        // Initialize MediaPlayer with the sound file
        mediaPlayer = MediaPlayer.create(context, soundResourceId);

        if (mediaPlayer != null) {
            // Start playing the sound
            mediaPlayer.start();
        }
    }

    public static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            // Release the MediaPlayer resources
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

