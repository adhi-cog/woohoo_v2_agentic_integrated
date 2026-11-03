package com.sb.woohoo.ui.main.fragments.video;

import com.sb.woohoo.ui.main.fragments.video.pojo.Video;

import java.util.List;

public interface VideoListContract {
    interface View {

        void playVideo(int ordinal);

        void updateCurrentVideosList(List<Video> videos);

        void playVideo(Video video);

        void showProgress();

        void hideProgress();

        void showError(String message);
    }

    interface Action {

        void callVideoListApiBySearch(String query);

        void onAPIError(Object message);
    }
}
