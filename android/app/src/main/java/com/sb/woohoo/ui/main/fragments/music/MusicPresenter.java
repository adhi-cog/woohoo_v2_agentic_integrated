package com.sb.woohoo.ui.main.fragments.music;

import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_NEXT;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_PAUSE;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_PLAY;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_PLAYLIST;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_PREV;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_REPEAT;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_CONTROL_STOP;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_PLAY;
import static com.sb.woohoo.ui.main.Categories.Skills.Music.MUSIC_SEARCH;
import static com.sb.woohoo.ui.main.fragments.music.MusicFragment.respondUserAboutSong;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Presenter for {@link MusicFragment}
 *
 * @author Ritwik Jamuar.
 */
public class MusicPresenter
        extends BasePresenter
        implements MusicContracts.Action {
    private final String TOKEN_EXP_MSG = "Token Expired!";
    private final String NLP_KEY_ARTIST = "artist";
    private final String NLP_KEY_GENRE = "genre";
    private final String NLP_KEY_QUERY = "query";
    private final String NLP_KEY_TRACK = "track";
    private final String NLP_KEY_PLAYLIST = "playlist";
    private final String NLP_KEY_USER_PLAYLIST = "user_playlist";
    private MusicContracts.View mView;
    private MusicRepository mRepository;
    private IntentInfo mIntentInfo;

    @Inject
    public MusicPresenter(MusicFragment view, MusicRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(MusicPresenter.this);
    }

    void initPresenter() {
        mRepository.searchTopCharts();
    }

    @Override
    public void onAccountNotLinked() {
        mView.hideProgress();
        mView.toggleMusicPlayerVisibility(false);
    }

    @Override
    public void onError(Object message) {
        mView.hideProgress();
        mView.showErrorMessage(message);
//        MethodUtils.printLog(MusicPresenter.class, message);
    }

    @Override
    public void onNoResult() {
        mView.speakNoResult();
    }

    @Override
    public void onTokenExpired() {
        //mView.toggleMusicPlayerVisibility(false);
        mView.showErrorMessage(TOKEN_EXP_MSG);
    }

    @Override
    public void onQueryResult(String trackUri) {
        mView.playQuerySong(trackUri);
    }

    @Override
    public void onTokenSuccess(String token) {
        mView.initializeSpotifyPlayer(token);
        processIntents();
    }

    private void fetchToken() {
        mRepository.getToken();
    }

    private void searchSong(String query, String type) {
        mRepository.makeApiRequest(query, type);
    }

    private void getTopCharts() {
        mRepository.searchTopCharts();
    }

    @Override
    public void processActions(IntentInfo mIntentInfo) {
        this.mIntentInfo = mIntentInfo;
        if (mView.isMusicService()) {
            //Get the token if the player is not initialized
            fetchToken();
        } else {
            processIntents();
        }
    }

    private void processIntents() {

        if (mIntentInfo == null) {
            mIntentInfo = getIntentInfo();
            getTopCharts();
            respondUserAboutSong = true;
        }

        if (mIntentInfo != null) {
            switch (mIntentInfo.getAction()) {
                case MUSIC_PLAY: {
                    getTopCharts();
                    respondUserAboutSong = true;
                }
                break;

                case MUSIC_SEARCH: {
                    HashMap<String, String> intentParameters = mIntentInfo.getParameters();
                    String artist = "", genre = "", query = "";
                    if (intentParameters.containsKey(NLP_KEY_ARTIST)) {
                        artist = intentParameters.get(NLP_KEY_ARTIST);
                    }
                    if (intentParameters.containsKey(NLP_KEY_GENRE)) {
                        genre = intentParameters.get(NLP_KEY_GENRE);
                    }
                    if (intentParameters.containsKey(NLP_KEY_QUERY)) {
                        query = intentParameters.get(NLP_KEY_QUERY);
                    }

                    boolean isArtistEmpty = artist == null || artist.length() == 0;
                    boolean isQueryEmpty = query == null || query.length() == 0;
                    boolean isPlaylistEmpty = genre == null || genre.length() == 0;

                    if (!isArtistEmpty) {
                        if (isQueryEmpty) {
                            searchSong(artist, NLP_KEY_ARTIST);
                        } else {
                            searchSong(query + " " + artist, NLP_KEY_TRACK);
                        }
                    } else if (!isQueryEmpty) {
                        searchSong(query, NLP_KEY_TRACK);
                    } else if (!isPlaylistEmpty) {
                        searchSong(genre, NLP_KEY_PLAYLIST);
                    }
                    respondUserAboutSong = true;
                }
                break;

                case MUSIC_CONTROL_PLAYLIST:
                    HashMap<String, String> intentParameters = mIntentInfo.getParameters();
                    String userPlayList = "";

                    if (intentParameters.containsKey(NLP_KEY_PLAYLIST)) {
                        userPlayList = intentParameters.get(NLP_KEY_PLAYLIST);
                    }

                    searchSong(userPlayList, NLP_KEY_USER_PLAYLIST);
                    respondUserAboutSong = true;
                    break;

                case MUSIC_CONTROL_NEXT:
                    mView.controlMusicNext();
                    respondUserAboutSong = false;
                    break;

                case MUSIC_CONTROL_PREV:
                    mView.controlMusicPrev();
                    respondUserAboutSong = false;
                    break;

                case MUSIC_CONTROL_PAUSE:
                    mView.controlMusicPause();
                    respondUserAboutSong = false;
                    break;

                case MUSIC_CONTROL_REPEAT:
                    mView.controlMusicRepeat();
                    respondUserAboutSong = false;
                    break;

                case MUSIC_CONTROL_STOP:
                    mView.controlMusicStop();
                    respondUserAboutSong = false;
                    break;

                case MUSIC_CONTROL_PLAY:
                    mView.controlMusicPlay();
                    respondUserAboutSong = false;
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