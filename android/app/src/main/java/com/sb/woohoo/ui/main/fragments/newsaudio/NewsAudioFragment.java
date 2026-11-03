package com.sb.woohoo.ui.main.fragments.newsaudio;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.MediaSource;
import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.databinding.FragmentNewsAudioBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.newsaudio.di.DaggerNewsAudioComponent;
import com.sb.woohoo.ui.main.fragments.newsaudio.di.NewsAudioComponent;
import com.sb.woohoo.ui.main.fragments.newsaudio.di.NewsAudioModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.PlayVideoUtil;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class NewsAudioFragment
        extends FeatureFragment<MainActivity>
        implements NewsAudioContract.View {

    // Constant.
    private final String BUNDLE_KEY = "news_audio";
    private final String EXO_PLAYER_USER_AGENT = "exoplayer-codelab";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private FragmentNewsAudioBinding binding;


    // Presenter.
    @Inject
    NewsAudioPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    // Other Components.
    private ExoPlayer mPlayer;
    private MediaSource mMediaSource;
    private Handler mNewsHandler;
    // Variables.
    private boolean isDurationSet = false;
    /************************************** {@link Runnable}s *************************************/

    private Runnable mNewsRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                if (mPlayer.getPlayWhenReady()) {
                    binding.fragmentNewsAudioSeekBar.setProgress((int) mPlayer.getCurrentPosition() / 1000);
                    mNewsHandler.postDelayed(this, 1000);
                }
            }
        }
    };
    /********************** {@link SeekBar.OnSeekBarChangeListener} Callbacks *********************/

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(
                        SeekBar seekBar,
                        int progress,
                        boolean fromUser) {
                    binding.fragmentNewsAudioTextViewStartDuration.setText(MethodUtils.formatTime(mPlayer.getCurrentPosition()));
                    if (fromUser) {
                        binding.fragmentNewsAudioTextViewStartDuration.setText(MethodUtils.formatTime(mPlayer.getCurrentPosition()));
                        mPlayer.seekTo(progress * 1000);
                        binding.fragmentNewsAudioSeekBar.setProgress(progress);
                    }
                }
            };

    private Player.Listener mExoPlayerEventListener = new Player.Listener() {


        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            Player.Listener.super.onTimelineChanged(timeline, reason);
        }

        @Override
        public void onTracksChanged(@NonNull Tracks tracks) {
            Player.Listener.super.onTracksChanged(tracks);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            LoggerWrapper.i(getTag(), String.valueOf(isLoading));
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE: {
                }
                break;

                case ExoPlayer.STATE_BUFFERING: {
                    binding.fragmentNewsAudioSeekBar.setEnabled(true);
                    binding.fragmentNewsAudioImageViewPlay.setEnabled(false);
                    binding.fragmentNewsAudioProgressBar.setVisibility(View.VISIBLE);
                }
                break;

                case ExoPlayer.STATE_READY: {
                    binding.fragmentNewsAudioSeekBar.setEnabled(true);
                    binding.fragmentNewsAudioImageViewPlay.setEnabled(true);
                    binding.fragmentNewsAudioProgressBar.setVisibility(View.GONE);
                    if (!isDurationSet) {
                        long duration = mPlayer.getDuration();
                        binding.fragmentNewsAudioSeekBar.setMax(MethodUtils.convertMilliSecondsToSeconds(duration));
                        binding.fragmentNewsAudioTextViewEndDuration.setText(MethodUtils.formatTime(duration));
                        isDurationSet = true;
                    }
                    getParentActivity().runOnUiThread(mNewsRunnable);
                }
                break;

                case ExoPlayer.STATE_ENDED: {
                    getParentActivity().onBackPressed();
                }
                break;
            }
        }


        @Override
        public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {

        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition,
                                            @NonNull Player.PositionInfo newPosition, int reason) {
            Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
        }


    };

    public static NewsAudioFragment newInstance(IntentInfo intentInfo) {
        NewsAudioFragment fragment = new NewsAudioFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    /****************************** {@link FeatureFragment} Callbacks *****************************/

    @Override
    public String createTag() {
        return NewsAudioFragment.class.getSimpleName();
    }

    /************************************** Fragment Callbacks ************************************/

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.NewsAudio(),  null);
        binding = FragmentNewsAudioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {
        NewsAudioComponent component =
                DaggerNewsAudioComponent
                        .builder()
                        .newsAudioModule(new NewsAudioModule(NewsAudioFragment.this))
                        .build();
        component.injectNewsAudioFragment(NewsAudioFragment.this);
    }

    @Override
    public void bindViews(View view) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeOtherComponents();
        initializePlayer();

        binding.fragmentNewsAudioImageViewPlay.setOnClickListener(view1 -> onPlayClicked());
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        featureFragmentCleanUps();
    }

    /************************************** private Methods ***************************************/

    private void initializeOtherComponents() {
        mNewsHandler = new Handler();
    }

    /********************************* {@link ExoPlayer} Methods **********************************/

    private void initializePlayer() {
        binding.fragmentNewsAudioSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        // Set-Up ExoPlayer.
        mPlayer = PlayVideoUtil.initialisePlayer(requireActivity(),mPlayer);
        mPlayer.addListener(mExoPlayerEventListener);
      //  mPlayerView.setPlayer(mPlayer);
        binding.fragmentNewsAudioSimpleExoPlayer.setUseArtwork(false);
    }

    @Override
    public void playPlayer() {
        mPlayer.setPlayWhenReady(true);
        binding.fragmentNewsAudioImageViewPlay.setImageResource(R.drawable.ic_pause_white_24dp);
    }

    @Override
    public void pausePlayer() {
        mPlayer.setPlayWhenReady(false);
        binding.fragmentNewsAudioImageViewPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
    }

    @Override
    public void stopPlayer() {
        mMediaSource = null;
        binding.fragmentNewsAudioSeekBar.setProgress(0);
        getParentActivity().onBackPressed();
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void setUpPlayerWithSource(String audioSourceURL) {
        mMediaSource = buildMediaSource(audioSourceURL);
        mPlayer.prepare(
                mMediaSource,
                true,
                false
        );
    }

    private MediaSource buildMediaSource(String audioSourceURL) {
        return PlayVideoUtil.buildMediaSource(requireActivity(), audioSourceURL);
    }

    /************************** {@link NewsAudioContract.View} Callbacks **************************/

    @Override
    public void showError(String errorCause) {
        respondToUser("Something went wrong");
        MethodUtils.showToastMessage(getActivity(), errorCause);
        getParentActivity().onBackPressed();
    }

    @Override
    public void playAudioNews(String audioSourceURL) {
        // Loads Media Source if null.
        if (mMediaSource == null) {
            setUpPlayerWithSource(audioSourceURL);
        }

        // Plays the Media.
        playPlayer();
    }

    /********************************* Listeners **********************************/

    void onPlayClicked() {
        if (mPlayer.getPlayWhenReady()) {
            pausePlayer();
        } else {
            playPlayer();
        }
    }

    void onParentClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }
}
