package com.sb.woohoo.ui.main.fragments.video;

import static com.sb.woohoo.ui.main.Categories.Controls.Player.PLAYER_PAUSE;
import static com.sb.woohoo.ui.main.Categories.Controls.Player.PLAYER_PLAY;
import static com.sb.woohoo.ui.main.Categories.Controls.Player.PLAYER_RESUME;
import static com.sb.woohoo.ui.main.Categories.Controls.Player.PLAYER_STOP;
import static com.sb.woohoo.ui.main.Categories.Skills.Video.VIDEO_CONTROLS_PAUSE;
import static com.sb.woohoo.ui.main.Categories.Skills.Video.VIDEO_CONTROLS_PLAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentMediaBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.video.pojo.Video;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.pojo.template.VideoTemplate;
import com.sb.woohoo.utils.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

public class VideoFragment extends FeatureFragment<MainActivity> implements NonPopFragment {

    private static final String VIDEO_KEY = "video";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_VIDEO;


    private FragmentMediaBinding binding;

    private YouTubePlayer youTubePlayer;
    private String videoId;
    private Video video;
    private int action;
    private boolean isPaused = false;
    private boolean isFragmentStopped = false;

//    private YouTubePlayerInitListener youTubePlayerInitListener = new YouTubePlayerInitListener() {
//
//        @Override
//        public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
//            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
//                @Override
//                public void onReady() {
//                    if (initializedYouTubePlayer != null && videoId != null) {
//                        initializedYouTubePlayer.loadVideo(videoId, 0);
//                        youTubePlayer = initializedYouTubePlayer;
//                    }
//                }
//
//                @Override
//                public void onError(@NonNull PlayerConstants.PlayerError error) {
//                    super.onError(error);
//                    LoggerWrapper.e("error", error.toString());
//                    FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
//                    Fragment fragment = fragmentManager.findFragmentByTag("VideoListFragment");
//                    if (fragment instanceof VideoListFragment) {
//                        if (binding.youtubePlayerView != null) {
//                            binding.youtubePlayerView.removeAllViews();
//                            binding.youtubePlayerView.release();
//                        }
//                        ((VideoListFragment) fragment).handleBackPressed();
//                    }
//                }
//
//                @Override
//                public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
//                    super.onStateChange(state);
//                    switch (state) {
//                        case ENDED:
//                            FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
//                            Fragment currentFragment = fragmentManager.findFragmentByTag("VideoListFragment");
//                            if (currentFragment instanceof VideoListFragment) {
//                                if (binding.youtubePlayerView  != null) {
//                                    binding.youtubePlayerView .removeAllViews();
//                                    binding.youtubePlayerView .release();
//                                }
//                                ((VideoListFragment) currentFragment).handleBackPressed();
//                            }
//                            break;
//
//                        case PAUSED:
//                            if (!(action == VIDEO_CONTROLS_PAUSE) && !isPaused) {
//                                play();
//                            }
//                            break;
//                    }
//                }
//
//                @Override
//                public void onCurrentSecond(float second) {
//                    currentSecond = second;
//                    super.onCurrentSecond(second);
//                }
//            });
//        }
//    };

    public static VideoFragment newInstance(Video video) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(VIDEO_KEY, video);
        args.putSerializable("type", "1");
        fragment.setArguments(args);
        return fragment;
    }

    public static VideoFragment newInstance(TemplateData video) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(VIDEO_KEY, video);
        args.putSerializable("type", "2");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void processTemplates(TemplateData templateData) {
        showVideo(templateData);
    }


    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    public void processActions(IntentInfo intentInfo) {
        action = intentInfo.getAction();

        switch (action) {
            case VIDEO_CONTROLS_PLAY:
            case PLAYER_RESUME:
            case PLAYER_PLAY:
                isPaused = false;
                play();
                break;
            case VIDEO_CONTROLS_PAUSE:
            case PLAYER_PAUSE:
                isPaused = true;
                pause();
                break;
            case PLAYER_STOP:
                FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag("VideoListFragment");
                if (fragment instanceof VideoListFragment) {
                    if (binding.youtubePlayerView  != null) {
                        binding.youtubePlayerView .removeAllViews();
                        binding.youtubePlayerView .release();
                    }
                    ((VideoListFragment) fragment).handleBackPressed();
                }
        }
    }

    @Override
    public String createTag() {
        return VideoFragment.class.getSimpleName();
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        hookWebView();
        binding = FragmentMediaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((MainActivity)context).enableFullWidthMode();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getParentActivity().disableFullWidthMode();
        }
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.wholeScreen.setOnClickListener(view12 -> onWholeScreenClicked());
        binding.rightSide.setOnClickListener(view12 -> onForwardClick());
        binding.leftSide.setOnClickListener(view12 -> onBackwardClick());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getParentActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        getLifecycle().addObserver(binding.youtubePlayerView );
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String st = (String) bundle.getSerializable("type");
            if (st.equalsIgnoreCase("1")) {
                video = (Video) bundle.getSerializable(VIDEO_KEY);
            } else {
                TemplateData<VideoTemplate> imageTemplateTemplateData = (TemplateData<VideoTemplate>) bundle.getSerializable(VIDEO_KEY);
                if (imageTemplateTemplateData != null) {
                    showVideo(imageTemplateTemplateData);
                }
            }
            if (video != null) {
                videoId = video.getId();
                binding.videoTitleTextView.setText(video.getTitle());
            }
        }

       /* binding.youtubePlayerView .enterFullScreen();
        binding.youtubePlayerView .getPlayerUIController().showYouTubeButton(false);
        binding.youtubePlayerView .getPlayerUIController().showFullscreenButton(false);
        binding.youtubePlayerView .getPlayerUIController().showSeekBar(true);
        binding.youtubePlayerView .getPlayerUIController().showMenuButton(false);
        binding.youtubePlayerView .getPlayerUIController().enableLiveVideoUI(false);
        binding.youtubePlayerView .initialize(youTubePlayerInitListener, true);*/

        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (videoId != null) {
                    youTubePlayer.loadVideo(videoId, 0);
                }

                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(YouTubePlayer initializedYouTubePlayer) {
                        if (videoId != null) {
                            initializedYouTubePlayer.loadVideo(videoId, 0);
                        }
                    }

                    @Override
                    public void onError(YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                        LoggerWrapper.e("error", error.toString());

                        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentByTag("VideoListFragment");

                        if (fragment instanceof VideoListFragment) {
                            ((VideoListFragment) fragment).showVideoListAfterPlaybackError();
                            if (binding.youtubePlayerView != null) {
                                binding.youtubePlayerView.removeAllViews();
                                binding.youtubePlayerView.release();
                            }
                            getParentActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onStateChange(YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        switch (state) {
                            case ENDED:
                                FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
                                Fragment currentFragment = fragmentManager.findFragmentByTag("VideoListFragment");

                                if (currentFragment instanceof VideoListFragment) {
                                    if (binding.youtubePlayerView != null) {
                                        binding.youtubePlayerView.removeAllViews();
                                        binding.youtubePlayerView.release();
                                    }
                                    ((VideoListFragment) currentFragment).handleBackPressed();
                                }
                                break;

                            case PAUSED:
                                if (!(action == VIDEO_CONTROLS_PAUSE) && !isPaused) {
                                    play();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                        currentSecond = second;
                    }
                });
            }
        });

// UI Customization
        binding.youtubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
            if (videoId != null) {
                youTubePlayer.loadVideo(videoId, 0);
            }


        });
        binding.youtubePlayerView .setLayerType(View.LAYER_TYPE_HARDWARE, null);

        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals( Constants.RC_DOMAIN_ID)) {
            binding.hintBar.setVisibility(View.INVISIBLE);
        }

        //wholeScreen.setOnTouchListener(onTouchListener);
    }

    private void showVideo(TemplateData<VideoTemplate> imageTemplateTemplateData) {
        String title = (String) imageTemplateTemplateData.getDisplayTemplate().get("title");
        String videoUrl = imageTemplateTemplateData.getTemplate().
                getMedia().getVideoUrl().replace("https://www.youtube.com/watch?v=", "");

        video = new Video();
        video.setTitle(title);
        video.setId(videoUrl);
    }

    private void play() {
        if (youTubePlayer != null && !isFragmentStopped) {
            youTubePlayer.play();
            Toast.makeText(getParentActivity(), "Resumed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pause() {
        if (youTubePlayer != null && !isFragmentStopped) {
            youTubePlayer.pause();
            Toast.makeText(getParentActivity(), "Paused!", Toast.LENGTH_SHORT).show();
        }
    }

    void playVideo(Video video) {
        binding.videoTitleTextView.setText(video.getTitle());
        if (youTubePlayer != null && video != null && video.getId() != null)
            youTubePlayer.loadVideo(video.getId(), 0);
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding.youtubePlayerView  != null) {
            binding.youtubePlayerView .removeAllViews();
            binding.youtubePlayerView .release();
        }
    }

    void onRootClicked() {
    }

    void onBottomFrameClicked() {

    }

    private float currentSecond = 0;
    private long startMillis = 0;
    boolean IS_LEFT_CLICK = false, IS_RIGHT_CLICK = false;


    void onWholeScreenClicked() {
        if (!isPaused) {
            pause();
            isPaused = true;
        } else {
            play();
            isPaused = false;
        }

        IS_LEFT_CLICK = false;
        IS_RIGHT_CLICK = false;
    }

    void onBackwardClick() {
        long time= System.currentTimeMillis();
        if (startMillis == 0 || (time - startMillis > 1000)) {
            startMillis = time;
            IS_LEFT_CLICK = true;
            IS_RIGHT_CLICK = false;
            // TODO Handler
        } else {
            if(IS_RIGHT_CLICK){
                IS_RIGHT_CLICK = false;
                IS_LEFT_CLICK = true;
            } {
                if(IS_LEFT_CLICK) {
                    youTubePlayer.seekTo(currentSecond - 10);
                    Toast.makeText(getParentActivity(), "-10 Sec", Toast.LENGTH_SHORT).show();
                    binding.imgBackward.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.imgBackward.setVisibility(View.GONE);
                        }
                    }, 500);
                }
                else {
                    IS_LEFT_CLICK = true;
                    IS_RIGHT_CLICK = false;
                }
            }
        }
    }

    void onForwardClick() {
        long time= System.currentTimeMillis();
        if (startMillis == 0 || (time - startMillis > 1000)) {
            startMillis = time;
            IS_LEFT_CLICK = false;
            IS_RIGHT_CLICK = true;
        } else {
            if(IS_LEFT_CLICK){
                IS_RIGHT_CLICK = true;
                IS_LEFT_CLICK = false;
            } {
                if(IS_RIGHT_CLICK) {
                    youTubePlayer.seekTo(currentSecond + 10);
                    Toast.makeText(getParentActivity(), "+10 Sec", Toast.LENGTH_SHORT).show();
                    binding.imgForward.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.imgForward.setVisibility(View.GONE);
                        }
                    }, 500);
                }
                else {
                    IS_LEFT_CLICK = false;
                    IS_RIGHT_CLICK = true;
                }
            }
        }
    }

    void onBackPressed() {
        if (binding.youtubePlayerView  != null) {
            binding.youtubePlayerView .removeAllViews();
            binding.youtubePlayerView .release();
        }
        handleBackPressed();
    }

    void handleBackPressed() {
            getParentActivity().onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        isFragmentStopped = true;
        pause();
        isPaused = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        isFragmentStopped = false;
    }

    private static String TAG = "hookWebView";
    public static void hookWebView(){
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i(TAG,"sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i(TAG,"Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if(sdkInt < 26){
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                @SuppressLint("SoonBlockedPrivateApi") Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory!=null){
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null){
                field.set("sProviderInstance", sProviderInstance);
                Log.i(TAG,"Hook success!");
            } else {
                Log.i(TAG,"Hook failed!");
            }
        } catch (Throwable e) {
            Log.w(TAG,e);
        }
    }
}
