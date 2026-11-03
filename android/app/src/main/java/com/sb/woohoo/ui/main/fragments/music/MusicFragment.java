package com.sb.woohoo.ui.main.fragments.music;

import static com.sb.woohoo.utils.Constants.MusicFragment.REDIRECT_URI;
import static com.sb.woohoo.utils.Constants.MusicFragment.SPOTIFY_CLIENT_ID;
import static com.sileria.android.Kit.getSystemService;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentMusicBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.music.di.DaggerMusicComponent;
import com.sb.woohoo.ui.main.fragments.music.di.MusicComponent;
import com.sb.woohoo.ui.main.fragments.music.di.MusicModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.inject.Inject;

public class MusicFragment
        extends FeatureFragment<MainActivity>
        implements MusicContracts.View, NonPopFragment {

    private final String SEPARATOR_DASH = " - ";
    private final String QUESTION_MARK = "?";
    private final String STR_PLAYING = "Playing ";
    private final String STR_BY = " by ";
    private final String MSG_NO_SONGS
            = "No Songs found by that name!";
    private final String N0_SPOTIFY_APP
            = "Please install Spotify!";
    private final String MSG_WOOHOO_CONNECT
            = "Use WooHoo mobile app or website to connect your Spotify account";
    static boolean respondUserAboutSong = false;

    // Presenter.
    @Inject
    MusicPresenter mPresenter;
    @Inject
    Picasso picasso;
    @Inject
    AnalyticsHelper helper;

    private FragmentMusicBinding binding;


    // Other Components.
    private SpotifyAppRemote mSpotifyAppRemote;
    private final ErrorCallback mErrorCallback = this::logError;
    TrackProgressBar mTrackProgressBar;
    private String currentTrackUri = null;


    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState)
                {
                    if (playerState.track != null)
                    {
                        if (playerState.playbackSpeed > 0) {
                            mTrackProgressBar.unpause();
                        } else {
                            mTrackProgressBar.pause();
                        }

                        if (playerState.isPaused) {
                            binding.fragmentMusicImageViewPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        } else {
                            binding.fragmentMusicImageViewPlay.setImageResource(R.drawable.ic_pause_white_24dp);
                        }

                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            binding.fragmentMusicCover.setImageBitmap(bitmap);
                                        });
                        // Invalidate seekbar length and position
                        binding.fragmentMusicSeekBar.setMax((int) playerState.track.duration);
                        mTrackProgressBar.setMaxDuration(playerState.track.duration);
                        mTrackProgressBar.update(playerState.playbackPosition);
                        binding.fragmentMusicTextViewEndDuration.setText(MethodUtils.formatTime(playerState.track.duration));

                        setMarquee(binding.fragmentMusicTextViewSongName);
                        setMarquee(binding.fragmentMusicTextViewArtistName);

                        binding.fragmentMusicTextViewSongName.setText(playerState.track.name);
                        binding.fragmentMusicTextViewArtistName.setText(playerState.track.artist.name);
                    }
                    binding.fragmentMusicSeekBar.setEnabled(true);
                }
            };

    public MusicFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link MusicFragment} using the
     * provided parameters.
     *
     * @param intentInfo {@link IntentInfo}'s instance which contains parameters for Service
     *                   Operation.
     * @return A new instance of {@link MusicFragment}.
     */
    public static MusicFragment newInstance(IntentInfo intentInfo) {
        MusicFragment fragment = new MusicFragment();
        if (intentInfo != null) {
            fragment.processData(intentInfo, null);
        }
        return fragment;
    }

    @Override
    public String getCategory() {
        return Categories.Skills.COMMAND_SKILL_MUSIC;
    }

    @Override
    public String createTag() {
        return MusicFragment.class.getSimpleName();
    }

    @Override
    public void controlMusicPlay() {
        logMessage("Inside controlMusicPlay");
        if(mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().resume();
        }
    }

    public void controlMusicStop() {
        logMessage("Inside controlMusicStop");
        if(mSpotifyAppRemote != null) {
            mSpotifyAppRemote.getPlayerApi().pause();
            mSpotifyAppRemote.getPlayerApi().seekTo(0);
            binding.fragmentMusicTextViewStartDuration.setText(R.string.zero_time);
        }
    }

    @Override
    public void controlMusicRepeat() {
        logMessage("Inside controlMusicRepeat");
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote.getPlayerApi().setRepeat(1);
        }
    }

    @Override
    public void controlMusicPause() {
        logMessage("Inside controlMusicPause");
        if(!mSpotifyAppRemote.getPlayerApi().getPlayerState().await().getData().isPaused){
            onPause();
        }
    }

    @Override
    public void controlMusicPrev() {
        logMessage("Inside controlMusicPrev");
        if(mSpotifyAppRemote != null){
            binding.fragmentMusicTextViewStartDuration.setText(R.string.zero_time);
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipPrevious()
                    .setResultCallback(data -> logMessage("skip previous"))
                    .setErrorCallback(mErrorCallback);
        }
    }

    @Override
    public void controlMusicNext() {
        logMessage("Inside controlMusicNext");
        if(mSpotifyAppRemote != null){
            binding.fragmentMusicTextViewStartDuration.setText(R.string.zero_time);
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipNext()
                    .setResultCallback(data -> logMessage("skip next"))
                    .setErrorCallback(mErrorCallback);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        helper.track(new Screen.Music(),  null);
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /************************************** Fragment Callbacks ************************************/
    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects the {@link MusicFragment} to {@link MusicComponent}.
     */
    @Override
    public void initDagger() {
        MusicComponent component =
                DaggerMusicComponent
                        .builder()
                        .musicModule(new MusicModule(MusicFragment.this))
                        .build();
        component.injectMusicFragment(MusicFragment.this);
    }

    /**
     * Initializes the Views.
     */
    @Override
    public void bindViews(View view) {
        binding.commonToolbar.commonToolbarTitle.setText(getResources().getString(R.string.music));
        mTrackProgressBar = new TrackProgressBar(binding.fragmentMusicSeekBar);
        setClickEvents();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress();
        binding.fragmentMusicSearchBar.setOnEditorActionListener((v, actionId, event) -> {
            String lQuery = binding.fragmentMusicSearchBar.getText().toString().trim();
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                if(!lQuery.isEmpty())
                {
                    showProgress();

                    HashMap<String, String> intentParameters = new HashMap<>();
                    intentParameters.put("query",binding.fragmentMusicSearchBar.getText().toString().trim());
                    IntentInfo lInfo = new IntentInfo(Categories.Skills.Music.MUSIC_SEARCH);
                    lInfo.setParameters(intentParameters);
                    mPresenter.processActions(lInfo);

                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(binding.fragmentMusicSearchBar.getWindowToken(), 0);
                }else{
                    respondToUser(MSG_NO_SONGS);
                }
                return false;
            }
            return false;
        });
    }

    @Override
    public void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage("Inside onDestroy");
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        featureFragmentCleanUps();
        mSpotifyAppRemote = null;
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    private void updateMusicUI(Metadata metadata) {
        hideProgress();
        toggleMusicPlayerVisibility(true);
        Metadata.Track track = metadata.currentTrack;
        if (track != null) {
            binding.fragmentMusicTextViewEndDuration.setText(MethodUtils.formatTime(track.durationMs));
            binding.fragmentMusicSeekBar.setMax((int) track.durationMs / 1000);
            binding.fragmentMusicSeekBar.setProgress(getParentActivity().getMusicCurrentPosition());

            setMarquee(binding.fragmentMusicTextViewSongName);
            setMarquee(binding.fragmentMusicTextViewArtistName);

            binding.fragmentMusicTextViewSongName.setText(track.name);

            binding.fragmentMusicTextViewArtistName.setText(String.valueOf(track.artistName + SEPARATOR_DASH + track.albumName));

            binding.fragmentMusicTextViewArtistName.setText((track.albumName == null || track.albumName.equals(QUESTION_MARK)) ? track.artistName : track.artistName + SEPARATOR_DASH + track.albumName);

            picasso.load(track.albumCoverWebUrl)
                    .placeholder(R.drawable.ic_music_placeholder)
                    .into(binding.fragmentMusicCover);
        }

        toggleControlUI(binding.fragmentMusicImageViewNext, metadata.nextTrack == null);

        toggleControlUI(binding.fragmentMusicImageViewPrev, metadata.prevTrack == null);
    }

    private void toggleControlUI(AppCompatImageView appCompatImageView, boolean isdull) {
        if (appCompatImageView != null && this.isVisible() && isdull) {
            appCompatImageView.setImageAlpha(100);
            appCompatImageView.setEnabled(false);
        }

        if (appCompatImageView != null && this.isVisible() && !isdull) {
            appCompatImageView.setImageAlpha(255);
            appCompatImageView.setEnabled(true);
        }
    }

    /**
     * Initializes Spotify Player with given User OAuth Token.
     *
     * @param userOAuthToken String representing OAuth Token of User,
     *                       used to initialize Spotify Player via OAuth.
     */
    @Override
    public void initializeSpotifyPlayer(String userOAuthToken) {
        //FIXME Context cannot be null error
        //getParentActivity().setSpotifyPlayer(userOAuthToken);
        connect(false);
    }

    @Override
    public void speakNoResult() {

        Toast.makeText(getParentActivity(), getString(R.string.no_songs_found), Toast.LENGTH_LONG).show();

        respondToUser(MSG_NO_SONGS);
        getParentActivity().onBackPressed();
    }

    @Override
    public boolean isMusicService() {
        return !getParentActivity().isMusicPlayerInitialized() || getParentActivity().isMusicPlayerTerminated();
    }

    /*********************************** Views on-Click Listeners *********************************/

    private void setClickEvents() {
        binding.fragmentMusicImageViewPrev.setOnClickListener(view -> onPrevPressed());
        binding.fragmentMusicImageViewNext.setOnClickListener(view -> onNextPressed());
        binding.fragmentMusicImageViewPlay.setOnClickListener(view -> onPlayPressed());
        binding.back.setOnClickListener(view -> onBackPressed());
        binding.commonToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressedToolBar());
    }

    void onPrevPressed() {
        //getParentActivity().previousSong();
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipPrevious()
                    .setResultCallback(data -> logMessage("skip previous"))
                    .setErrorCallback(mErrorCallback);
        }
        respondUserAboutSong = false;
    }

    void onNextPressed() {
        //getParentActivity().nextSong();
        if(mSpotifyAppRemote != null){
            mSpotifyAppRemote
                    .getPlayerApi()
                    .skipNext()
                    .setResultCallback(data -> logMessage("skip next"))
                    .setErrorCallback(mErrorCallback);
        }
        respondUserAboutSong = false;
    }

    void onPlayPressed() {
       /* if (getParentActivity().isMusicPlaying()) {
            getParentActivity().pauseMusicPlaying();
        } else {
            getParentActivity().resumeMusicPlaying();
        }*/

        onPlayPauseButtonClicked();
    }

    void onPressesAnyWhere() {

    }

    void onBackPressed() {
        controlMusicStop();
        getParentActivity().onBackPressed();
    }

    public void onBackPressedToolBar() {
        controlMusicStop();
        getParentActivity().onBackPressed();
    }

    /*************************************** Contract Methods *************************************/

    @Override
    public void toggleMusicPlayerVisibility(boolean isAccountLinked) {
        logMessage("Inside toggleMusicPlayerVisibility " + isAccountLinked);
        if (!isAccountLinked ) {
            //getParentActivity().terminateMusicPlayer();
            binding.fragmentMusicLayoutNoAccount.setVisibility(View.VISIBLE);
            binding.fragmentMusicLinearLayoutMusic.setVisibility(View.GONE);
            respondToUser(MSG_WOOHOO_CONNECT);
        } else {
            binding.fragmentMusicLayoutNoAccount.setVisibility(View.GONE);
            binding.fragmentMusicLinearLayoutMusic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void playQuerySong(String trackUri) {
        toggleMusicPlayerVisibility(true);
        currentTrackUri = trackUri;
        logMessage("Inside playQuerySong ");
        logMessage("Inside playQuerySong : currentTrackUri : "+currentTrackUri);
        if(mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected()){
            mSpotifyAppRemote.getPlayerApi().play(currentTrackUri);
        }
        hideProgress();
    }

    @Override
    public void showErrorMessage(Object message) {
        String msgString;
        if (message instanceof Integer)
            msgString = getString((Integer) message);
        else
            msgString = (String) message;
        MethodUtils.showToastMessage(getActivity(), msgString);
        getParentActivity().onBackPressed();
    }

    /********************************* Music Callback Listener Methods ****************************/

    public void onLoggedIn() {
        LoggerWrapper.i(getTag(), "Spotify : onLoggedIn()");
        Bundle bundle = getArguments();
        if (bundle == null) {
            mPresenter.initPresenter();
        } else {
            LoggerWrapper.i(getTag(), "onLoggedIn : currentTrackUri : "+currentTrackUri);
            if (currentTrackUri != null) {
                playQuerySong(currentTrackUri);
            }
        }
    }

    public void onLoginFailed() { /*initializeSpotifyPlayer(Constants.MusicFragment.USER_OAUTH_TOKEN);*/ }

    public void onLoggedOut() {
    }

    public void onPlaybackError(Error error) {
        MethodUtils.printLog(MusicFragment.class, error.toString());
    }

    public void onPlaying() {
        logMessage("Inside onPaused setImageDrawable pause");
        binding.fragmentMusicImageViewPlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_white_24dp));
    }

    public void onPaused() {
        logMessage("Inside onPaused setImageDrawable play");
        binding.fragmentMusicImageViewPlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_arrow_white_24dp));
    }

    public void onNextTrack() {
        logMessage("Inside onNextTrack");
        if (!getParentActivity().isMusicPlaying()) {
            getParentActivity().resumeMusicPlaying();
        }
    }

    public void onPreviousTrack() {
        logMessage("Inside onPreviousTrack");
        if (!getParentActivity().isMusicPlaying()) {
            getParentActivity().resumeMusicPlaying();
        }
    }

    public void onShuffleOn() {
    }

    public void onShuffleOff() {
    }

    public void onRepeatOn() {
    }

    public void onRepeatOff() {
    }

    public void onStopped() {
        logMessage("Inside onStopped");
        binding.fragmentMusicSeekBar.setProgress(0);
    }

    public void onTrackChanged() {
        logMessage("onTrackChanged");
       /* binding.fragmentMusicSeekBar.setProgress(0);
        Metadata metadata = getParentActivity().getMusicMetaData();
        if(metadata!=null)
        updateMusicUI(metadata);*/
       // binding.fragmentMusicSeekBar.setProgress(0);
    }

    public void onTrackDelivered() {
    }

    public void onMetadataChanged() {
    }

    public void takeDataToRespondUser(Metadata metadata)
    {
        logMessage("Inside takeDataToRespondUser");
        Metadata.Track track = metadata.currentTrack;
        if (track != null) {
            String songName = track.name;
            String artistName = track.artistName;

            if (respondUserAboutSong && songName != null && artistName != null) {
                respondToUser(STR_PLAYING + songName + STR_BY + artistName);
            }
        }
    }

    private void setMarquee(TextView textView) {
        textView.setSingleLine();
        textView.setSelected(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    public void onActionComplete() {
    }

    public void onActionCancelled() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    private void logError(Throwable throwable) {
        MethodUtils.printLog(MusicFragment.class, throwable.getMessage());
    }

    private void logMessage(String msg) {
        LoggerWrapper.d("MusicFragment", msg);
    }

    private void connect(boolean showAuthView)
    {
        logMessage("Inside Connect");
        SpotifyAppRemote.connect(
                this.getContext(),
                new ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(showAuthView)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        onConnectedd();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        logMessage("Inside onFailure");
                        hideProgress();
                        if(error instanceof SpotifyConnectionTerminatedException) {
                            connect(false);
                        }else if (error instanceof NotLoggedInException || error instanceof UserNotAuthorizedException) {
                            // Show login button and trigger the login flow from auth library when clicked
                            respondToUser(MSG_WOOHOO_CONNECT);
                            toggleMusicPlayerVisibility(false);
                        } else if (error instanceof CouldNotFindSpotifyApp) {
                            // Show button to download Spotify
                            respondToUser(N0_SPOTIFY_APP);
                            toggleMusicPlayerVisibility(false);
                        } else {
                            logError(error);
                            toggleMusicPlayerVisibility(false);
                        }
                    }
                });
    }

    public void onConnectedd()
    {
        toggleMusicPlayerVisibility(true);
        mSpotifyAppRemote
                .getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(mPlayerStateEventCallback);

        if(currentTrackUri != null && !currentTrackUri.isEmpty()){
            logMessage("Inside playQuerySong : Playing");
            mSpotifyAppRemote.getPlayerApi().play(currentTrackUri);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        logMessage("onResume");
        if(mSpotifyAppRemote!= null){
            mSpotifyAppRemote.getPlayerApi().resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        logMessage("onPause");
        if(mSpotifyAppRemote!= null){
            mSpotifyAppRemote.getPlayerApi().pause();
        }
    }

    public void onPlayPauseButtonClicked()
    {
        if(mSpotifyAppRemote!= null)
        {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .getPlayerState()
                    .setResultCallback(
                            playerState -> {
                                if (playerState.isPaused) {
                                    mSpotifyAppRemote
                                            .getPlayerApi()
                                            .resume()
                                            .setResultCallback(
                                                    empty -> logMessage("play"))
                                            .setErrorCallback(mErrorCallback);
                                } else {
                                    mSpotifyAppRemote
                                            .getPlayerApi()
                                            .pause()
                                            .setResultCallback(
                                                    empty -> logMessage("pause"))
                                            .setErrorCallback(mErrorCallback);
                                }
                            });
        }
    }

    private class TrackProgressBar
    {
        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(progress == seekBar.getMax())
                        {
                            binding.fragmentMusicTextViewStartDuration.setText(R.string.zero_time);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(mSpotifyAppRemote!= null){
                            mSpotifyAppRemote
                                    .getPlayerApi()
                                    .seekTo(seekBar.getProgress())
                                    .setErrorCallback(mErrorCallback);
                        }
                        binding.fragmentMusicTextViewStartDuration.setText(MethodUtils.formatTime(seekBar.getProgress()/1000));
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        binding.fragmentMusicTextViewStartDuration.setText(MethodUtils.formatTime((progress + LOOP_DURATION)/1000));
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setMaxDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }
}
