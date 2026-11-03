package com.sb.woohoo.ui.main.fragments.newsaudio;

public interface NewsAudioContract {
    interface View {
        void showError(String errorCause);

        void playAudioNews(String audioSourceURL);

        void pausePlayer();

        void playPlayer();

        void stopPlayer();
    }

    interface Action {
        void onAPIError(String errorCause);

        void onNewsAudioSourceFound(String audioSourceURL);
    }
}