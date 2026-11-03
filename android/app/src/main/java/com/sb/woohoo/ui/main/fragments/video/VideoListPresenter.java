package com.sb.woohoo.ui.main.fragments.video;

import android.view.View;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.video.pojo.Video;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoResponse;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoTrendingResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.HashMap;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.sb.woohoo.ui.main.Categories.Controls.Player.PLAYER_STOP;

public class VideoListPresenter extends BasePresenter implements VideoListContract.Action {

    static int REQUEST_ALL = 100;
    static int REQUEST_FIRST = 101;
    static int REQUEST_VIDEO = 107;
    private VideoListFragment view;
    private VideoListRepository repository;
    private String query;

    @Inject
    public VideoListPresenter(VideoListFragment videoListFragment, VideoListRepository videoListRepository) {
        this.view = videoListFragment;
        this.repository = videoListRepository;
        repository.onAttach(this);
    }

    void displayList(VideoResponse videoResponse) {
        view.hideProgress();
        view.updateCurrentVideosList(videoResponse.getData());
    }

    void displayList(VideoTrendingResponse videoTrendingResponse) {
        view.hideProgress();
        view.updateCurrentVideosList(videoTrendingResponse.getData().getItems());
    }

    void playFirstVideo(Video video) {
        view.hideProgress();
        view.playVideo(video);
    }

    private void getSelectedVideo(int ordinal) {
        view.playVideo(ordinal);
    }

    private void getVideoList(String query) {
        view.showProgress();
        repository.getMediaRequest(query, REQUEST_ALL);
    }

    private void getTrendingList() {
        view.showProgress();
        repository.getTrendingVideo(REQUEST_ALL);
    }

    private void getFirstTrending() {
        view.showProgress();
        repository.getTrendingVideo(REQUEST_FIRST);
    }

    private void getFirstVideo(String query) {
        view.showProgress();
        repository.getMediaRequest(query, REQUEST_FIRST);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {


        if(intentInfo==null){
            getVideoList("");
        }else {
            int action = intentInfo.getAction();
            HashMap<String, String> intent = intentInfo.getParameters();
            getQuery(intentInfo);

            switch (action) {
                case Categories.Skills.Video.VIDEO_PLAY_ORDINAL:
                case Categories.Skills.Video.VIDEO_PLAY_ORDINAL_ALTERNATIVE:
                    int position = Integer.parseInt(intent.get("ordinal"));
                    getSelectedVideo(position - 1);
                    break;

                case Categories.Skills.Video.VIDEO_SEARCH:
                    if (isVideoPlaying()) {
                        view.getParentActivity().onBackPressed();
                    }
                    view.setVideoLayoutViews(View.VISIBLE);
                    view.setVideoTile();
                    getVideoList(query);
                    view.binding.videoList.smoothScrollToPosition(0);
                    break;

                case Categories.Skills.Video.VIDEO_AUTOPLAY:
                    view.setVideoLayoutViews(View.INVISIBLE);
                    getFirstVideo(query);
                    break;

                case Categories.Skills.Video.VIDEO_CONTROLS_BACK:
                case PLAYER_STOP:
                    view.handleBackPressed();
                    break;

                case Categories.Skills.Video.VIDEO_LAUNCH:
                    if (isVideoPlaying()) {
                        view.getParentActivity().onBackPressed();
                    }
                    view.setVideoLayoutViews(View.VISIBLE);
                    view.setVideoTile();
                    getTrendingList();
                    view.binding.videoList.smoothScrollToPosition(0);
                    break;

                case Categories.Skills.Video.VIDEO_LAUNCH_AUTOPLAY:
                    view.setVideoLayoutViews(View.INVISIBLE);
                    getFirstTrending();
                    break;

                default:
                    view.processVideoAction(intentInfo);
            }
        }
    }

    private boolean isVideoPlaying() {
        FragmentManager fragmentManager = view.getParentActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("VideoFragment");
        return fragment instanceof VideoFragment;
    }

    @SuppressWarnings("unchecked")
    String getQuery(IntentInfo intentInfo) {
        HashMap<String, String> intent = intentInfo.getParameters();
        if (intent.containsKey("query")) {
            if (intent.containsKey("type")) {
                query = String.valueOf(intent.get("query") + " " + intent.get("type"));
            } else {
                query = String.valueOf(intent.get("query"));
            }
        } else {
            query = null;
        }
        return query;
    }

    @Override
    public void callVideoListApiBySearch(String query) {

        getVideoList(query);
    }

    @Override
    public void onAPIError(Object message) {
        String msgString;
        if (message instanceof Integer)
            msgString = view.getString((Integer) message);
        else
            msgString = (String) message;
        view.showError(msgString);
        view.hideProgress();
    }
}
