package com.sb.woohoo.ui.main.fragments.music;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link MusicFragment}.
 *
 * @author Ritwik Jamuar
 */
public interface MusicContracts {
    interface View {
        void toggleMusicPlayerVisibility(boolean isAccountLinked);

        void playQuerySong(String trackUri);

        void showErrorMessage(Object message);

        void initializeSpotifyPlayer(String userOAuthToken);

        void speakNoResult();

        boolean isMusicService();

        void controlMusicPlay();

        void controlMusicStop();

        void controlMusicRepeat();

        void controlMusicPause();

        void controlMusicPrev();

        void controlMusicNext();

        void showProgress();

        void hideProgress();
    }

    interface Action {
        void onAccountNotLinked();

        void onError(Object message);

        void onNoResult();

        void onTokenExpired();

        void onQueryResult(String trackUri);

        void onTokenSuccess(String token);
    }
}