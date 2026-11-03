package com.sb.woohoo.ui.main.fragments.newsaudio;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

public class NewsAudioPresenter
        extends BasePresenter
        implements NewsAudioContract.Action {
    private NewsAudioContract.View mView;
    private NewsAudioRepository mRepository;

    public NewsAudioPresenter(NewsAudioFragment view, NewsAudioRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(NewsAudioPresenter.this);
    }

    /******************************* Fetch News Broadcast Methods *********************************/

    private void fetchNewsBroadcast() {
        mRepository.requestNewsBroadcastAudio();
    }

    @Override
    public void onNewsAudioSourceFound(String audioSourceURL) {
        mView.playAudioNews(audioSourceURL);
    }

    /*************************************** Common Methods ***************************************/

    @Override
    public void onAPIError(String errorCause) {
        mView.showError(errorCause);
    }

    @Override
    public void processActions(IntentInfo mIntentInfo) {
        if (mIntentInfo != null) {
            switch (mIntentInfo.getAction()) {
                case Categories.Skills.News.NEWS_CONTROL_PLAY:
                case Categories.Skills.News.NEWS_CONTROL_LAUNCH:
                case Categories.Controls.Player.PLAYER_RESUME:
                case Categories.Controls.Player.PLAYER_PLAY: {
                    fetchNewsBroadcast();
                }
                break;

                case Categories.Skills.News.NEWS_CONTROL_PAUSE:
                case Categories.Controls.Player.PLAYER_PAUSE: {
                    mView.pausePlayer();
                }
                break;

                case Categories.Skills.News.NEWS_CONTROL_STOP:
                case Categories.Controls.Player.PLAYER_STOP: {
                    mView.stopPlayer();
                }
                break;
            }
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}