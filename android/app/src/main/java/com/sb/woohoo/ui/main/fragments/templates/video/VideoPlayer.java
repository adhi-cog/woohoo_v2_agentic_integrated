package com.sb.woohoo.ui.main.fragments.templates.video;

//public class VideoPlayer extends FeatureFragment<MainActivity> {

//    public static final String TEMPLATE_KEY = "template_data";
//
//    public static final int TIMER_TIMEOUT = 4;
//
////    @BindView(R.id.video_view)
////    PlayerView playerView;
//
//    @BindView(R.id.video_title)
//    TextView titleTextView;
//
////    @BindView(R.id.video_seek_bar)
////    SeekBar mSeekBar;
//
////    @BindView(R.id.play_icon)
////    ImageView playImage;
//
////    @BindView(R.id.pause_icon)
////    ImageView pauseImage;
//
//    @BindView(R.id.top_background)
//    FrameLayout topBackground;
//
//    @BindView(R.id.bottom_background)
//    FrameLayout bottomBackground;
//
//    @BindView(R.id.video_back_button)
//    ImageView backButton;
//
////    @BindView(R.id.video_count)
////    TextView countTextVIew;
//
//    @BindView(R.id.progress_bar)
//    ProgressBar progressBar;
//
////    @BindView(R.id.replay_icon)
////    ImageView replayImage;
//
//    private VideoPresenter videoPresenter;
//    private Handler mHandler;
//    private ComponentListener componentListener;
//    private DynamicConcatenatingMediaSource playlist;
//    private HashMap<MediaSource, VideoMedia> metadataMap = new HashMap<>(0);
//    private ExoPlayer player;
//
//    private boolean touch;
//    private boolean isVideoPlaying = false;
//    private boolean isError = false;
//
//    private int autoTextTime = TIMER_TIMEOUT;
//    //TODO initialize the action and currentAction variables
//    private int action;
//    private int currentAction;
//
//    public VideoPlayer() {
//    }
//
//    public static VideoPlayer newInstance(TemplateData templateData){
//        VideoPlayer videoPlayer = new VideoPlayer();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(TEMPLATE_KEY, templateData);
//        videoPlayer.setArguments(bundle);
//        return videoPlayer;
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_media, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        videoPresenter = new VideoPresenter();
//        mHandler = new Handler();
//        componentListener = new ComponentListener();
//        playlist = new DynamicConcatenatingMediaSource();
//
//        VideoMedia videoMedia;
//        if (getArguments() != null) {
//            videoMedia = (VideoMedia) getArguments().getSerializable(TEMPLATE_KEY);
//            initializePlayer(videoMedia);
//        }
//    }
//
//    public void initializePlayer(VideoMedia video) {
//
//        MediaSource mediaSource = buildMediaSource(Uri.parse(video.getVideoUrl()));
//
//        playlist.addMediaSource(mediaSource);
//
//        metadataMap.put(mediaSource, video);
//
//        setViews(View.VISIBLE);
//
//        player = ExoPlayerFactory.newSimpleInstance(
//                new DefaultRenderersFactory(getActivity()),
//                new DefaultTrackSelector(), new DefaultLoadControl());
//
//        playerView.setPlayer(player);
//
//        playerView.setUseController(false);
//
//        player.addListener(componentListener);
//
//        player.prepare(playlist);
//
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);
//
//        player.setPlayWhenReady(true);
//
//        setVideoDetails(video);
//
//        touch = true;
//
//        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
//
//        getParentActivity().runOnUiThread(videoDetailsRunnable);
//
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if (b) {
//                    player.seekTo(i * 1000);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        ((ViewGroup) playerView.getParent()).setOnClickListener(view -> {
//            if (!isError) {
//                if (touch) {
//                    playerView.getPlayer().setPlayWhenReady(!player.getPlayWhenReady());
//                    if (mHandler != null)
//                        mHandler.removeCallbacks(videoDetailsRunnable);
//                    autoTextTime = 4;
//                    if (player.getPlayWhenReady()) {
//                        getParentActivity().runOnUiThread(videoDetailsRunnable);
//                        playAnimation();
//                    } else {
//                        pauseAnimation();
//                    }
//                } else {
//                    touch = true;
//                    setViews(View.VISIBLE);
//                    getParentActivity().runOnUiThread(videoDetailsRunnable);
//                }
//            } else {
//                replayVideo();
//            }
//        });
//
//        if (action != Categories.Skills.Video.VIDEO_AUTOPLAY) {
//            fetchNextVideo();
//            fetchPreviousVideo();
//        } else {
//            player.setRepeatMode(Player.REPEAT_MODE_OFF);
//        }
//    }
//
//    private void replayVideo() {
//        isError = false;
//        replayImage.setVisibility(View.GONE);
//        isVideoPlaying = false;
//        int index = player.getCurrentWindowIndex();
//        long position = player.getCurrentPosition();
//        player.prepare(playlist);
//        player.seekTo(index, position);
//        player.setPlayWhenReady(true);
//    }
//
//    private MediaSource buildMediaSource(Uri uri) {
//        return new ExtractorMediaSource.Factory(
//                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
//                createMediaSource(uri);
//    }
//
//    private class ComponentListener extends Player.DefaultEventListener {
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {
//            if (error.getCause().getMessage().contains("None of the available extractors") || error.getCause().getMessage().contains("Response code")) {
//                respondToUser("Sorry, I am not able to play this video. Please try playing something else.");
//            } else {
//                isError = true;
//                showReplay();
//            }
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady,
//                                         int playbackState) {
//            switch (playbackState) {
//                case Player.STATE_IDLE:
//                    LoggerWrapper.e("video", "VideoIdle");
//                    break;
//                case Player.STATE_BUFFERING:
//                    LoggerWrapper.e("video", "Buffering");
//                    progressBar.setVisibility(View.VISIBLE);
//                    setViews(View.VISIBLE);
//                    mSeekBar.setVisibility(View.GONE);
//                    break;
//                case Player.STATE_READY:
//                    LoggerWrapper.e("video", "Ready");
//                    progressBar.setVisibility(View.INVISIBLE);
//                    mSeekBar.setVisibility(View.VISIBLE);
//                    isVideoPlaying = true;
//                    getParentActivity().runOnUiThread(seekBarRunnable);
//                    break;
//                case Player.STATE_ENDED:
//                    LoggerWrapper.e("video", "Ended");
//                    if (action == Categories.Skills.Video.VIDEO_AUTOPLAY && isVideoPlaying) {
//                        getParentActivity().onBackPressed();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        @Override
//        public void onPositionDiscontinuity(int reason) {
//            //THIS METHOD GETS CALLED FOR EVERY NEW SOURCE THAT IS PLAYED
//            int currentWindowIndex = player.getCurrentWindowIndex();
//            if (action != Categories.Skills.Video.VIDEO_AUTOPLAY) {
//                // item selected in playlist has changed, handle here
//                if (reason == 0 || currentAction == Categories.Skills.Video.VIDEO_CONTROLS_NEXT || currentAction == Categories.Controls.Player.PLAYER_NEXT) {
//                    setViews(View.VISIBLE);
//                    getParentActivity().runOnUiThread(videoDetailsRunnable);
//                    touch = true;
//                    VideoMedia video = metadataMap.get(playlist.getMediaSource(currentWindowIndex));
//                    setVideoDetails(video);
//                    if (((VideoListFragment) getParent()).isListCompleted()) {
//                        if (currentWindowIndex == playlist.getSize() - 1) {
//                            fetchNextVideo();
//                        }
//                    }
//                } else if (currentAction == Categories.Skills.Video.VIDEO_CONTROLS_PREVIOUS || currentAction == Categories.Controls.Player.PLAYER_PREVIOUS) {
//                    setViews(View.VISIBLE);
//                    getParentActivity().runOnUiThread(videoDetailsRunnable);
//                    touch = true;
//                    VideoMedia video = metadataMap.get(playlist.getMediaSource(currentWindowIndex));
//                    setVideoDetails(video);
//                    if (((VideoListFragment) getParent()).isListCompleted()) {
//                        if (currentWindowIndex == 0) {
//                            fetchPreviousVideo();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void showReplay() {
//            progressBar.setVisibility(View.INVISIBLE);
//            setViews(View.VISIBLE);
//            replayImage.setVisibility(View.VISIBLE);
//    }
//
//    private void fetchPreviousVideo() {
//        ((VideoListFragment) getParent()).addPreviousVideoToList();
//    }
//
//    private void fetchNextVideo() {
//        ((VideoListFragment) getParent()).addNextVideoToList();
//    }
//
//    public void addToPreviousPlaylist(VideoMedia video) {
//        MediaSource mediaSource = buildMediaSource(Uri.parse(video.getVideoUrl()));
//        metadataMap.put(mediaSource, video);
//        playlist.addMediaSource(0, mediaSource);
//    }
//
//    public void addToNextPlaylist(VideoMedia video) {
//        MediaSource mediaSource = buildMediaSource(Uri.parse(video.getVideoUrl()));
//        metadataMap.put(mediaSource, video);
//        playlist.addMediaSource(mediaSource);
//    }
//
//    private void skipNext() {
//        if (player != null && playlist != null) {
//            player.seekTo(player.getNextWindowIndex(), 0);
//            player.setPlayWhenReady(true);
//        }
//    }
//
//    private void skipPrevious() {
//        if (player != null && playlist != null && player.getCurrentWindowIndex() != 0) {
//            player.seekTo(player.getPreviousWindowIndex(), 0);
//            player.setPlayWhenReady(true);
//        }
//    }
//
//    private Runnable seekBarRunnable = new Runnable() {
//        @Override
//        public void run() {
//            LoggerWrapper.e("video", "video thread running");
//            if (player.getPlayWhenReady()) {
//                mSeekBar.setProgress((int) player.getCurrentPosition() / 1000);
//                mHandler.postDelayed(this, 1000);
//            }
//        }
//    };
//
//    private Runnable videoDetailsRunnable = new Runnable() {
//        @Override
//        public void run() {
//            LoggerWrapper.e("video", "video text thread running");
//            autoTextTime--;
//            mHandler.postDelayed(this, 1000);
//            if (autoTextTime < 0) {
//                setViews(View.INVISIBLE);
//                autoTextTime = 4;
//                touch = false;
//                mHandler.removeCallbacks(videoDetailsRunnable);
//            }
//        }
//    };
//
//    @SuppressLint("DefaultLocale")
//    public void setVideoDetails(VideoMedia videoDetails) {
//        //TODO SET THE LENGTH OF THE VIDEO
//        //mSeekBar.setMax();
//        //TODO SET THE TITLE OF THE VIDEO
//        //titleTextView.setText();
//        //TODO SET THE VIEWERS COUNT OF THE VIDEO
//        //countTextVIew.setText(String.valueOf(String.format("%,d", videoDetails.getMetadata().getViewCount()) + " " + "views"));
//    }
//
//    private void setViews(int view) {
//            backButton.setVisibility(view);
//            titleTextView.setVisibility(view);
//            countTextVIew.setVisibility(view);
//            mSeekBar.setVisibility(view);
//
//            Animation animation = new TranslateAnimation(0, 0, 1000, 0);
//            animation.setDuration(1000);
//
//            Animation animation2 = new TranslateAnimation(0, 0, -1000, 0);
//            animation2.setDuration(1000);
//
//            Animation animation3 = new TranslateAnimation(0, 0, 0, 1000);
//            animation3.setDuration(1000);
//
//            Animation animation4 = new TranslateAnimation(0, 0, 0, -1000);
//            animation4.setDuration(1000);
//
//            animation3.setFillEnabled(true);
//            animation3.setFillAfter(true);
//            animation4.setFillEnabled(true);
//            animation4.setFillAfter(true);
//
//            if (view == View.VISIBLE) {
//                bottomBackground.startAnimation(animation);
//                topBackground.startAnimation(animation2);
//            } else {
//                bottomBackground.startAnimation(animation3);
//                topBackground.startAnimation(animation4);
//            }
//        }
//
//    private void playAnimation() {
//        playImage.setVisibility(View.VISIBLE);
//        pauseImage.setVisibility(View.GONE);
//        Animation animation5 = AnimationUtils.loadAnimation(getContext(), R.anim.popout);
//        animation5.setFillAfter(true);
//        animation5.setFillEnabled(true);
//        animation5.reset();
//        playImage.setAnimation(animation5);
//    }
//
//    private void pauseAnimation() {
//        playImage.setVisibility(View.GONE);
//        pauseImage.setVisibility(View.VISIBLE);
//        Animation animation6 = AnimationUtils.loadAnimation(getContext(), R.anim.popout);
//        animation6.setFillAfter(true);
//        animation6.setFillEnabled(true);
//        animation6.reset();
//        pauseImage.setAnimation(animation6);
//    }
//
//    @Override
//    public String createTag() {
//        return VideoPlayer.class.getSimpleName();
//    }
//
//    @Override
//    public void onIntentCreate(IntentInfo intentInfo) {
//
//    }
//
//    @Override
//    public void initDagger() {
//
//    }
//
//    @Override
//    public void bindViews(View view) {
//        ButterKnife.bind(this, view);
//    }
//
//    @Override
//    public String getCategory() {
//        return null;
//    }
//
//    @Override
//    public BasePresenter getPresenter() {
//        return videoPresenter;
//    }
//
//    @OnClick(R.id.video_back_button)
//    void goBack() {
//        getParentActivity().onBackPressed();
//    }
//
//    @OnClick(R.id.video_background)
//    void onRootClicked() {
//    }
//}
