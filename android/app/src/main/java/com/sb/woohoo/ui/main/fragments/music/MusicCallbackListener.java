package com.sb.woohoo.ui.main.fragments.music;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;

/**
 * Interface used by {@link MusicService} to invoke Music Callbacks.
 *
 * @author Ritwik Jamuar.
 */
public interface MusicCallbackListener {
    /**
     * Invokes when User has been successfully logged in to Spotify.
     */
    void onLoggedIn();

    /**
     * Invokes when User Login was unsuccessful.
     */
    void onLoginFailed();

    /**
     * Invokes when User has been logged out from Spotify.
     */
    void onLoggedOut();

    /**
     * Invokes when there's error raised in Spotify Player during Playback.
     *
     * @param error Instance of Spotify Player's {@link Error}.
     */
    void onPlaybackError(Error error);

    /**
     * Invokes when Playback has started or has resumed
     */
    void onPlaying();

    /**
     * Invokes when Playback has been paused.
     */
    void onPaused();

    /**
     * Invokes when Playback has been skipped to next track.
     */
    void onNextTrack();

    /**
     * Invokes when Playback has been skipped to previous track.
     */
    void onPreviousTrack();

    /**
     * Invokes when Shuffle is switched ON.
     */
    void onShuffleOn();

    /**
     * Invokes when Shuffle is switched OFF.
     */
    void onShuffleOff();

    /**
     * Invokes when Repeat is switched ON.
     */
    void onRepeatOn();

    /**
     * Invokes when Repeat is switched OFF.
     */
    void onRepeatOff();

    /**
     * Invokes when Playback has been stopped.
     */
    void onStopped();

    /**
     * Invokes when the current track or its metadata has changed This event occurs when playback of a new/different track starts.
     */
    void onTrackChanged();

    /**
     * Invokes when application accepted all samples from the current track This is an informative event that indicates that all samples from the current track have been delivered to and accepted by the application.
     */
    void onTrackDelivered();

    /**
     * Invokes when Metadata is changed This event occurs when playback starts or changes to a different context, when a track switch occurs, etc.
     */
    void onMetadataChanged();

    void takeDataToRespondUser(Metadata metadata);
}