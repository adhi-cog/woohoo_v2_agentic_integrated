package com.sb.woohoo.ui.main.fragments.screensaver;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentScreenSaverBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.screensaver.di.DaggerScreenSaverComponent;
import com.sb.woohoo.ui.main.fragments.screensaver.di.ScreenSaverComponent;
import com.sb.woohoo.ui.main.fragments.screensaver.di.ScreenSaverModule;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayImage;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.PlayVideoUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.inject.Inject;

public class ScreenSaverFragment
        extends FeatureFragment<MainActivity>
        implements ScreenSaverContracts.Views {

    private static final String TAG = ScreenSaverFragment.class.getSimpleName();


    ExoPlayer player = null;
    Handler slideHandler = new Handler();

    @Inject
    ScreenSaverPresenter mScreenSaverPresenter;

    @Inject
    AnalyticsHelper helper;

    private Handler handler = new Handler();
    private ScreenSaverMulti screenSaverMulti;
    private FragmentScreenSaverBinding binding;

    public static ScreenSaverFragment newInstance() {
        return new ScreenSaverFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.ScreenSaver(), null);
        binding = FragmentScreenSaverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public String createTag() {
        return ScreenSaverFragment.class.getSimpleName();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHint(R.string.screen_saver_hint_text, R.color.black, View.GONE);

        showTime();

        showDate();

        callScreenSaverDetailsAPI();

//        for ritz remove wakeword text for both landscape and portrait. Previously it was only for landscape
        if (checkIfDomainIsRCDomain()) {
            binding.textVoiceGenie.setVisibility(View.GONE);
            binding.title.setVisibility(View.GONE);
            binding.description.setVisibility(View.GONE);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.textVoiceGenie.setVisibility(View.GONE);
            } else {
                binding.textVoiceGenie.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getParentActivity().disableFullWidthMode();
        }
        ((MainActivity) getActivity()).changeAudioStateSecondDisplay(false);
        super.onDestroyView();
    }

    private Timer timer;
    int itemIndex;
    long intevalPeriod;
    List<String> slideShowImages;

    @Override
    public void onDetailsFetched(ScreenSaverMulti data) {
        onScreenSaverUpdate(data);
    }

    Player.Listener playerEventListener = new Player.Listener() {

        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {

        }

        @Override
        public void onTracksChanged(@NonNull Tracks tracks) {

        }

        @Override
        public void onIsLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                doNext();
            }
        }

        @Override
        public void onPlayerStateChanged(boolean b, int i) {
            if (i == ExoPlayer.STATE_ENDED) {
                doNext();
            }
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(
                @NonNull Player.PositionInfo oldPosition,
                @NonNull Player.PositionInfo newPosition, int reason)
        { }

        @Override
        public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {

        }
    };

    private void loadView(String mediaItemUrl) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopSlideShow();
                SecondaryDisplayImage secondaryDisplayImage = new SecondaryDisplayImage(mediaItemUrl, binding.title.getText().toString(), binding.description.getText().toString());
                ((MainActivity) getActivity()).updateSecondDisplay(secondaryDisplayImage);

                //For now, let's keep the second display remain muted so only primary display will play the audio.
                ((MainActivity) getActivity()).changeAudioStateSecondDisplay(false);
                if (mediaItemUrl.endsWith(".mp4") || mediaItemUrl.endsWith(".MP4")) {
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.wallpaper.setVisibility(View.GONE);
                    stopSlideShow();
                    player = PlayVideoUtil.initialisePlayer(getParentActivity(), player);
                    player.addListener(playerEventListener);
                    binding.videoView.setPlayer(player);
                    PlayVideoUtil.playVideo(getParentActivity(), mediaItemUrl, player);
                } else {
                    binding.videoView.setVisibility(View.GONE);
                    binding.wallpaper.setVisibility(View.VISIBLE);
                    if (player != null) {
                        releasePlayer();
                    }

                    Picasso.get()
                            .load(mediaItemUrl)
                            .fit()
                            .into(binding.wallpaper);

                    if (slideShowImages != null && slideShowImages.size() > 1) {
                        if (slideHandler == null) {
                            slideHandler = new Handler();
                        }
                        Log.i("ScreenSaversFragment", "ScreenSaversFragment::set timer");

                        slideHandler.postDelayed(slideShowRunnable, intevalPeriod);
                    }
                }
            }
        });
    }

    private void doNext() {
        if (slideShowImages != null && slideShowImages.size() > 0) {
            if (itemIndex >= slideShowImages.size()) {
                itemIndex = 0;
            }
            loadView(slideShowImages.get(itemIndex));
            itemIndex++;
        } else if (!TextUtils.isEmpty(screenSaverMulti.getImageUrl())) {
            loadView(screenSaverMulti.getImageUrl());
        }
    }


    public void onScreenSaverUpdate(ScreenSaverMulti data) {
        this.screenSaverMulti = data;

        stopSlideShow();
        getParentActivity().updateScreenSaverTime(data.getScreenSaverTime());
        if (data.getSlideShow() && data.getSlideShowImages() != null && data.getSlideShowImages().size() > 0) {
            intevalPeriod = data.getSlideShowTimeInMillis();
            itemIndex = 0;
            slideShowImages = data.getSlideShowImages();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doNext();
                }
            });
        } else if (!TextUtils.isEmpty(data.getImageUrl())) {
            loadView(data.getImageUrl());
        }

        if (!checkIfDomainIsRCDomain()) {
            if (!TextUtils.isEmpty(data.getTitle())) {
                binding.title.setVisibility(View.VISIBLE);
                binding.title.setText(data.getTitle());
            } else {
                binding.title.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(data.getDescription())) {
                binding.description.setVisibility(View.VISIBLE);
                binding.description.setText(data.getDescription());
            } else {
                binding.description.setVisibility(View.GONE);
            }
        } else {
            binding.title.setVisibility(View.GONE);
            binding.description.setVisibility(View.GONE);
        }
    }

    private boolean checkIfDomainIsRCDomain() {
        return WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "").equalsIgnoreCase(Constants.RC_DOMAIN_ID);
    }

    @Override
    public void onError(String message) {
        // MethodUtils.showToastMessage(requireActivity(), message);
        // callScreenSaverDetailsAPI();
    }

    private int getImageUrlByTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 7 && timeOfDay < 12) {
            return R.drawable.morning;
        } else if (timeOfDay >= 12 && timeOfDay < 20) {
            return R.drawable.evening;
        } else {
            return R.drawable.night;
        }
    }

    private void showDate() {
        Date date = new Date();
        SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM yyyy");
        binding.date.setText(String.valueOf(writeDate.format(date)/* + " · 25° C"*/));
        runDateThread();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ScreenSaverComponent screenSaverComponent = DaggerScreenSaverComponent
                .builder()
                .screenSaverModule(new ScreenSaverModule(this))
                .build();
        screenSaverComponent.injectScreenSaverFragment(this);
    }

    private void showTime() {
        Date date = new Date();
        SimpleDateFormat writeTime = new SimpleDateFormat("hh:mm aa");
        binding.time.setText(writeTime.format(date));
        runTimeThread();
    }

    private void runTimeThread() {
        LoggerWrapper.i(ScreenSaverFragment.class.getSimpleName(), "This time thread should be closed if the screensaver isn't in the view.");
        handler.postDelayed(this::showTime, 60 * 1000);
    }

    private Runnable slideShowRunnable = () -> doNext();

    private void runDateThread() {
        LoggerWrapper.i(ScreenSaverFragment.class.getSimpleName(), "This date thread should be closed if the screensaver isn't in the view.");
        handler.postDelayed(this::showDate, 24 * 60 * 60 * 1000);
    }

    @Override
    public void bindViews(View view) {
        binding.screenSaverRoot.setOnClickListener(view1 -> onRootClicked());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    void onRootClicked() {
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "-1").equals(Constants.GBTB_DOMAIN_ID)) {
            getParentActivity().isTransparentActivityEnabled = true;
            getParentActivity().onRespondToUser(getString(R.string.welcome_to_gbtb));
        }
        getParentActivity().onBackPressed();
    }

    public void callScreenSaverDetailsAPI() {
        mScreenSaverPresenter.getScreenSaverDetails();
    }

    @Override
    public void onDetach() {
        mScreenSaverPresenter.presenterCleanUps();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        stopSlideShow();
        super.onDetach();
    }

    private void stopSlideShow() {
        if (slideHandler != null) {
            slideHandler.removeCallbacks(slideShowRunnable);
        }
        slideHandler = null;
        releasePlayer();
    }


    @Override
    public void onPause() {
        super.onPause();
        stopSlideShow();
    }

    void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
        }
        player = null;
    }
}
