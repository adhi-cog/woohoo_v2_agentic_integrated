package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentRelaxationMusicBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.IBackPressInterceptor;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsResponse;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicTracksResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RelaxationMusicFragment extends FeatureFragment<MainActivity>
        implements IBackPressInterceptor {
    private static final String TAG = RelaxationMusicFragment.class.getSimpleName();
    private static final long FADE_OUT_MS = 5000L;
    private static final int[] TIMER_OPTIONS = {3, 5, 10, 15, 30};
    private static final int NAV_EXPLORE = 0;
    private static final int NAV_SLEEP = 1;
    private static final int NAV_TIMER = 2;
    private static final String ARG_VOICE_SLOTS = "voice_slots";

    private FragmentRelaxationMusicBinding binding;
    private final Handler handler = new Handler();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<RelaxationCategory> builtInCategories;
    private List<RelaxationCategory> categories;
    private RelaxationCategory selectedCategory;
    private RelaxationTrack selectedTrack;
    private int selectedTrackIndex;
    private int selectedTimerMinutes = 10;
    private ExoPlayer player;
    private long playbackEndsAt;
    private boolean fadingOut;

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimerLabel();
            handler.postDelayed(this, 1000);
        }
    };

    public static RelaxationMusicFragment newInstance() {
        return new RelaxationMusicFragment();
    }

    public static RelaxationMusicFragment newInstance(HashMap<String, Object> slots) {
        RelaxationMusicFragment fragment = new RelaxationMusicFragment();
        if (slots != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_VOICE_SLOTS, slots);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRelaxationMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void bindViews(View view) {
        builtInCategories = RelaxationMusicCatalog.categories();
        categories = new ArrayList<>(builtInCategories);
        binding.commonToolbar.commonToolbarTitle.setText(R.string.relaxation_music);
        binding.commonToolbar.commonToolbarBack.setOnClickListener(v -> handleBack());
        binding.playerDisc.setPlaying(false);
        setupVolume();
        setupBottomNavigation();
        renderDashboard();
        loadRemoteCatalog();
        handleVoiceSlotsFromArguments();
    }

    @Override
    public String getCategory() {
        return Categories.Skills.COMMAND_SKILL_RELAXATION_MUSIC;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onBackPressed(BackCallback callback) {
        if (binding.playerContainer.getVisibility() == View.VISIBLE) {
            stopPlayback();
            renderCategory(selectedCategory);
        } else if (binding.categoryContainer.getVisibility() == View.VISIBLE) {
            renderDashboard();
        } else {
            callback.backCompleted();
        }
    }

    public void stopPlaybackAndClearCache() {
        stopPlayback();
        if (getContext() != null) {
            RelaxationMusicCache.clear(getContext());
        }
    }

    public void handleVoiceSlots(HashMap<String, Object> slots) {
        if (binding == null) {
            return;
        }

        String action = getSlotValue(slots, "_voice_action");
        if (Categories.Skills.RelaxationMusic.COMMAND_ACTION_NEXT.equals(action)) {
            playRelative(1);
            return;
        }
        if (Categories.Skills.RelaxationMusic.COMMAND_ACTION_PREVIOUS.equals(action)) {
            playRelative(-1);
            return;
        }
        if (Categories.Skills.RelaxationMusic.COMMAND_ACTION_PAUSE.equals(action)) {
            if (player != null) {
                player.setPlayWhenReady(false);
            }
            return;
        }
        if (Categories.Skills.RelaxationMusic.COMMAND_ACTION_RESUME.equals(action)) {
            if (player != null) {
                player.setPlayWhenReady(true);
            }
            return;
        }

        String categoryName = firstSlot(slots, "category_name", "music_type", "type", "category");
        String trackName = firstSlot(slots, "track_name", "song_name", "title", "name");
        String trackOrdinal = firstSlot(slots, "track_ordinal", "ordinal", "number");

        RelaxationCategory category = findCategoryByVoice(categoryName);
        if (category == null && selectedCategory != null) {
            category = selectedCategory;
        }
        if (category == null && !isBlank(trackName)) {
            category = findCategoryForTrack(trackName);
        }

        if (category != null) {
            renderCategory(category);
        } else {
            renderDashboard();
            return;
        }

        if (!isBlank(trackName)) {
            int trackIndex = findTrackIndex(category, trackName);
            if (trackIndex >= 0) {
                playTrack(trackIndex);
            }
        } else if (!isBlank(trackOrdinal)) {
            int trackIndex = trackIndexFromOrdinal(trackOrdinal, category.getTracks().size());
            if (trackIndex >= 0) {
                playTrack(trackIndex);
            }
        }
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        stopPlayback();
        super.onDestroyView();
        binding = null;
    }

    private void handleBack() {
        if (binding.playerContainer.getVisibility() == View.VISIBLE) {
            stopPlayback();
            renderCategory(selectedCategory);
        } else if (binding.categoryContainer.getVisibility() == View.VISIBLE) {
            renderDashboard();
        } else {
            getParentActivity().onBackPressed();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleVoiceSlotsFromArguments() {
        if (getArguments() == null) {
            return;
        }
        Object slots = getArguments().getSerializable(ARG_VOICE_SLOTS);
        if (slots instanceof HashMap) {
            handleVoiceSlots((HashMap<String, Object>) slots);
        }
    }

    private void renderDashboard() {
        stopPlayback();
        binding.dashboardContainer.setVisibility(View.VISIBLE);
        binding.categoryContainer.setVisibility(View.GONE);
        binding.playerContainer.setVisibility(View.GONE);
        binding.commonToolbar.commonToolbarTitle.setText(R.string.relaxation_music);
        binding.categoryGrid.removeAllViews();
        updateBottomNavState(NAV_EXPLORE);

        if (categories == null || categories.isEmpty()) {
            TextView empty = label("No soundscapes available", 18, Color.parseColor("#D6C9D9"), Typeface.BOLD);
            empty.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(120));
            params.setMargins(dp(24), dp(10), dp(24), dp(10));
            binding.categoryGrid.addView(empty, params);
            return;
        }

        for (RelaxationCategory category : categories) {
            binding.categoryGrid.addView(createCategoryCard(category));
        }
    }

    private View createCategoryCard(RelaxationCategory category) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(22), dp(20), dp(22), dp(20));
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackground(cardBackground(category.getAccentColor(), false));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(24), dp(10), dp(24), dp(10));
        card.setLayoutParams(params);
        card.setMinimumHeight(dp(176));

        View icon = categoryIconView(category);
        TextView title = label(category.getTitle(), 24, Color.WHITE, Typeface.BOLD);
        TextView subtitle = label(category.getSubtitle(), 15, Color.parseColor("#D7CADB"), Typeface.NORMAL);
        TextView count = label(category.getTracks().size() + " TRACKS", 13, Color.parseColor("#F0DDE7"), Typeface.BOLD);
        title.setMaxLines(2);
        title.setEllipsize(TextUtils.TruncateAt.END);
        subtitle.setMaxLines(2);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);

        card.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(58)));
        addSpacer(card, 8);
        card.addView(title);
        card.addView(subtitle);
        addSpacer(card, 5);
        card.addView(count);
        card.setOnClickListener(v -> renderCategory(category));
        return card;
    }

    private void renderCategory(RelaxationCategory category) {
        selectedCategory = category;
        binding.dashboardContainer.setVisibility(View.GONE);
        binding.categoryContainer.setVisibility(View.VISIBLE);
        binding.playerContainer.setVisibility(View.GONE);
        binding.commonToolbar.commonToolbarTitle.setText(category.getTitle());
        updateBottomNavState(NAV_SLEEP);
        binding.categoryHero.setBackground(cardBackground(category.getAccentColor(), true));
        binding.categoryHeroKicker.setText("CURATED FOR REST");
        binding.categoryHeroTitle.setText(category.getTitle().equals("Deep Sleep")
                ? "Midnight\nSanctuaries" : category.getTitle());
        binding.trackList.removeAllViews();
        for (int i = 0; i < category.getTracks().size(); i++) {
            binding.trackList.addView(createTrackRow(category.getTracks().get(i), i));
        }
    }

    private View createTrackRow(RelaxationTrack track, int index) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));
        row.setBackground(cardBackground(selectedCategory.getAccentColor(), false));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(92));
        params.setMargins(dp(24), dp(8), dp(24), dp(8));
        row.setLayoutParams(params);

        TextView play = label("▷", 28, Color.parseColor("#F7DDE5"), Typeface.BOLD);
        play.setGravity(Gravity.CENTER);
        play.setBackground(circle(Color.parseColor("#4D365F")));
        row.addView(play, new LinearLayout.LayoutParams(dp(58), dp(58)));

        LinearLayout text = new LinearLayout(requireContext());
        text.setOrientation(LinearLayout.VERTICAL);
        text.setPadding(dp(18), 0, dp(10), 0);
        TextView title = label(track.getTitle(), 20, Color.WHITE, Typeface.BOLD);
        TextView subtitle = label(track.getSubtitle(), 15, Color.parseColor("#BCAFC6"), Typeface.NORMAL);
        text.addView(title);
        text.addView(subtitle);
        row.addView(text, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView duration = label(track.getDurationLabel(), 17, Color.parseColor("#E9DCE8"), Typeface.BOLD);
        row.addView(duration);
        row.setOnClickListener(v -> playTrack(index));
        return row;
    }

    private View categoryIconView(RelaxationCategory category) {
        if (!isBlank(category.getImageUrl())) {
            ImageView image = new ImageView(requireContext());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackground(circle(withAlpha(category.getAccentColor(), 70)));
            image.setClipToOutline(false);
            Picasso.get().load(category.getImageUrl()).fit().centerCrop().into(image);
            return image;
        }
        TextView icon = label(category.getIcon(), 30, category.getAccentColor(), Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        return icon;
    }

    private void playTrack(int index) {
        selectedTrackIndex = index;
        selectedTrack = selectedCategory.getTracks().get(index);
        binding.dashboardContainer.setVisibility(View.GONE);
        binding.categoryContainer.setVisibility(View.GONE);
        binding.playerContainer.setVisibility(View.VISIBLE);
        binding.commonToolbar.commonToolbarTitle.setText(R.string.relaxation_music);
        binding.playerTitle.setText(selectedTrack.getTitle());
        binding.playerCategory.setText(selectedTrack.getCategoryName().toUpperCase(Locale.US));
        updateBottomNavState(NAV_TIMER);
        renderTimerOptions();
        preparePlayer(selectedTrack);
    }

    private void renderTimerOptions() {
        binding.durationSelector.removeAllViews();
        for (int minutes : TIMER_OPTIONS) {
            TextView option = label(minutes + "m", 16,
                    minutes == selectedTimerMinutes ? Color.parseColor("#3B1A25") : Color.WHITE,
                    Typeface.BOLD);
            option.setGravity(Gravity.CENTER);
            option.setBackground(timerBackground(minutes == selectedTimerMinutes));
            option.setOnClickListener(v -> {
                selectedTimerMinutes = minutes;
                if (player != null) {
                    startSleepTimer();
                }
                renderTimerOptions();
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(46), 1f);
            binding.durationSelector.addView(option, params);
        }
    }

    private void preparePlayer(RelaxationTrack track) {
        releasePlayer();
        player = new ExoPlayer.Builder(requireContext()).build();
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setVolume(1f);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updatePlayState(isPlaying);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                LoggerWrapper.e(TAG, "Relaxation playback failed: " + error.getMessage());
                Toast.makeText(getContext(), R.string.relaxation_music_playback_error, Toast.LENGTH_SHORT).show();
                updatePlayState(false);
            }
        });
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(
                RelaxationMusicCache.createDataSourceFactory(requireContext()))
                .createMediaSource(MediaItem.fromUri(Uri.parse(track.getStreamUrl())));
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        startSleepTimer();
        binding.playerStatus.setText(R.string.relaxation_music_streaming);
    }

    private void startSleepTimer() {
        fadingOut = false;
        handler.removeCallbacksAndMessages(null);
        playbackEndsAt = System.currentTimeMillis() + selectedTimerMinutes * 60L * 1000L;
        handler.post(progressRunnable);
        long fadeDelay = Math.max(0, selectedTimerMinutes * 60L * 1000L - FADE_OUT_MS);
        handler.postDelayed(this::beginFadeOut, fadeDelay);
    }

    private void beginFadeOut() {
        if (player == null || fadingOut) {
            return;
        }
        fadingOut = true;
        final int steps = 20;
        for (int i = 0; i <= steps; i++) {
            final int step = i;
            handler.postDelayed(() -> {
                if (player == null) {
                    return;
                }
                float volume = Math.max(0f, 1f - (step / (float) steps));
                player.setVolume(volume);
                if (step == steps) {
                    stopPlayback();
                    binding.playerStatus.setText(R.string.relaxation_music_timer_complete);
                }
            }, step * (FADE_OUT_MS / steps));
        }
    }

    private void updatePlayState(boolean isPlaying) {
        binding.playerDisc.setPlaying(isPlaying);
        binding.playPauseButton.setText(isPlaying ? "Ⅱ" : "▶");
        binding.playerStatus.setText(isPlaying
                ? R.string.relaxation_music_playing_now : R.string.relaxation_music_paused);
    }

    private void updateTimerLabel() {
        long remaining = Math.max(0L, playbackEndsAt - System.currentTimeMillis());
        long minutes = remaining / 60000L;
        long seconds = (remaining / 1000L) % 60L;
        binding.timerRemaining.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    private void setupVolume() {
        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        binding.volumeSeek.setMax(max);
        binding.volumeSeek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        binding.volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.playPauseButton.setOnClickListener(v -> togglePlayPause());
        binding.nextTrackButton.setOnClickListener(v -> playRelative(1));
        binding.previousTrackButton.setOnClickListener(v -> playRelative(-1));
        binding.repeatButton.setOnClickListener(v -> {
            if (player != null) {
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
                Toast.makeText(getContext(), R.string.relaxation_music_repeat_enabled, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavExplore.setOnClickListener(v -> renderDashboard());
        binding.bottomNavSleep.setOnClickListener(v -> openSleepSection());
        binding.bottomNavTimer.setOnClickListener(v -> {
            if (binding.playerContainer.getVisibility() == View.VISIBLE) {
                updateBottomNavState(NAV_TIMER);
            } else {
                Toast.makeText(getContext(), R.string.relaxation_music_select_track_for_timer,
                        Toast.LENGTH_SHORT).show();
            }
        });
        binding.bottomNavSettings.setOnClickListener(v -> getParentActivity().onSettingsPressed());
    }

    private void openSleepSection() {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        RelaxationCategory category = selectedCategory;
        if (category == null || !categories.contains(category)) {
            category = findCategoryByTitle("sleep", categories.get(0));
        }
        renderCategory(category);
    }

    private RelaxationCategory findCategoryByTitle(String titlePart, RelaxationCategory fallback) {
        for (RelaxationCategory category : categories) {
            if (category.getTitle() != null
                    && category.getTitle().toLowerCase(Locale.US).contains(titlePart)) {
                return category;
            }
        }
        return fallback;
    }

    private RelaxationCategory findCategoryByVoice(String categoryName) {
        if (isBlank(categoryName) || categories == null) {
            return null;
        }
        String needle = normalizeVoiceText(categoryName);
        for (RelaxationCategory category : categories) {
            String title = normalizeVoiceText(category.getTitle());
            String id = normalizeVoiceText(category.getId());
            if (title.contains(needle) || needle.contains(title) || id.contains(needle) || needle.contains(id)) {
                return category;
            }
        }
        return null;
    }

    private RelaxationCategory findCategoryForTrack(String trackName) {
        if (isBlank(trackName) || categories == null) {
            return null;
        }
        for (RelaxationCategory category : categories) {
            if (findTrackIndex(category, trackName) >= 0) {
                return category;
            }
        }
        return null;
    }

    private int findTrackIndex(RelaxationCategory category, String trackName) {
        if (category == null || isBlank(trackName)) {
            return -1;
        }
        String needle = normalizeVoiceText(trackName);
        for (int i = 0; i < category.getTracks().size(); i++) {
            RelaxationTrack track = category.getTracks().get(i);
            String title = normalizeVoiceText(track.getTitle());
            if (title.contains(needle) || needle.contains(title)) {
                return i;
            }
        }
        return -1;
    }

    private int trackIndexFromOrdinal(String ordinal, int trackCount) {
        try {
            int index = Integer.parseInt(ordinal.replaceAll("[^0-9]", "")) - 1;
            return index >= 0 && index < trackCount ? index : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void updateBottomNavState(int activeTab) {
        int active = Color.parseColor("#FFA3B5");
        int inactive = Color.parseColor("#9D91AC");
        binding.bottomNavExplore.setTextColor(activeTab == NAV_EXPLORE ? active : inactive);
        binding.bottomNavSleep.setTextColor(activeTab == NAV_SLEEP ? active : inactive);
        binding.bottomNavTimer.setTextColor(activeTab == NAV_TIMER ? active : inactive);
        binding.bottomNavSettings.setTextColor(inactive);
    }

    private void loadRemoteCatalog() {
        if (getActivity() == null || !MethodUtils.isNetworkAvailable(requireContext())) {
            return;
        }

        WooHooApiCall apiCall = WooHooApp.get(requireActivity()).getWooHooApiCall();
        String deviceId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");

        compositeDisposable.add(fetchPropertyId(apiCall, deviceId)
                .flatMap(propertyId -> fetchCatalog(apiCall, propertyId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RemoteCatalogPayload>() {
                    @Override
                    public void onSuccess(RemoteCatalogPayload payload) {
                        List<RelaxationCategory> remoteCategories = RelaxationMusicCatalog.fromApi(
                                payload.defaultCategories.getData(),
                                payload.defaultTracks.getData(),
                                payload.hotelCategories.getData(),
                                payload.hotelTracks.getData()
                        );
                        if (remoteCategories == null || remoteCategories.isEmpty()) {
                            return;
                        }
                        categories = mergeCatalogs(builtInCategories, remoteCategories);
                        if (binding != null && binding.dashboardContainer.getVisibility() == View.VISIBLE) {
                            renderDashboard();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoggerWrapper.e(TAG, "Relaxation music catalog failed: " + e.getMessage());
                    }
                }));
    }

    private List<RelaxationCategory> mergeCatalogs(List<RelaxationCategory> defaultCategories,
                                                   List<RelaxationCategory> remoteCategories) {
        List<RelaxationCategory> merged = new ArrayList<>();
        addUniqueCategories(merged, defaultCategories);
        addUniqueCategories(merged, remoteCategories);
        return merged;
    }

    private void addUniqueCategories(List<RelaxationCategory> target, List<RelaxationCategory> source) {
        if (source == null) {
            return;
        }
        for (RelaxationCategory category : source) {
            if (category != null && !containsCategoryId(target, category.getId())) {
                target.add(category);
            }
        }
    }

    private boolean containsCategoryId(List<RelaxationCategory> categories, String id) {
        for (RelaxationCategory category : categories) {
            if (category.getId() != null && category.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Single<Integer> fetchPropertyId(WooHooApiCall apiCall, String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return Single.just(0);
        }
        return apiCall.getHotelDetails(deviceId)
                .map(this::extractPropertyId)
                .onErrorReturnItem(0);
    }

    private int extractPropertyId(HotelDetailsResponse response) {
        if (response != null && response.getStatus() != null && response.getStatus() == 200
                && response.getData() != null) {
            return response.getData().getId();
        }
        return 0;
    }

    private Single<RemoteCatalogPayload> fetchCatalog(WooHooApiCall apiCall, int propertyId) {
        Single<RelaxationMusicCategoriesResponse> defaultCategories =
                apiCall.getDefaultRelaxationMusicCategories()
                        .onErrorReturnItem(RelaxationMusicCategoriesResponse.empty());
        Single<RelaxationMusicTracksResponse> defaultTracks =
                apiCall.getDefaultRelaxationMusicTracks()
                        .onErrorReturnItem(RelaxationMusicTracksResponse.empty());
        Single<RelaxationMusicCategoriesResponse> hotelCategories = propertyId > 0
                ? apiCall.getHotelRelaxationMusicCategories(propertyId)
                    .onErrorReturnItem(RelaxationMusicCategoriesResponse.empty())
                : Single.just(RelaxationMusicCategoriesResponse.empty());
        Single<RelaxationMusicTracksResponse> hotelTracks = propertyId > 0
                ? apiCall.getHotelRelaxationMusicTracks(propertyId)
                    .onErrorReturnItem(RelaxationMusicTracksResponse.empty())
                : Single.just(RelaxationMusicTracksResponse.empty());

        return Single.zip(defaultCategories, defaultTracks, hotelCategories, hotelTracks,
                RemoteCatalogPayload::new);
    }

    private static final class RemoteCatalogPayload {
        private final RelaxationMusicCategoriesResponse defaultCategories;
        private final RelaxationMusicTracksResponse defaultTracks;
        private final RelaxationMusicCategoriesResponse hotelCategories;
        private final RelaxationMusicTracksResponse hotelTracks;

        private RemoteCatalogPayload(RelaxationMusicCategoriesResponse defaultCategories,
                                     RelaxationMusicTracksResponse defaultTracks,
                                     RelaxationMusicCategoriesResponse hotelCategories,
                                     RelaxationMusicTracksResponse hotelTracks) {
            this.defaultCategories = defaultCategories;
            this.defaultTracks = defaultTracks;
            this.hotelCategories = hotelCategories;
            this.hotelTracks = hotelTracks;
        }
    }

    private void togglePlayPause() {
        if (player == null) {
            return;
        }
        player.setPlayWhenReady(!player.isPlaying());
    }

    private void playRelative(int offset) {
        if (selectedCategory == null) {
            openSleepSection();
        }
        if (selectedCategory == null) {
            return;
        }
        int size = selectedCategory.getTracks().size();
        int nextIndex = (selectedTrackIndex + offset + size) % size;
        playTrack(nextIndex);
    }

    private void stopPlayback() {
        handler.removeCallbacksAndMessages(null);
        releasePlayer();
        if (binding != null) {
            binding.playerDisc.setPlaying(false);
            binding.playPauseButton.setText("▶");
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    private TextView label(String text, int sp, int color, int style) {
        TextView label = new TextView(requireContext());
        label.setText(text);
        label.setTextSize(sp);
        label.setTextColor(color);
        label.setTypeface(Typeface.DEFAULT, style);
        label.setIncludeFontPadding(true);
        return label;
    }

    private GradientDrawable cardBackground(int accentColor, boolean prominent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{prominent ? Color.parseColor("#37224F") : Color.parseColor("#261640"),
                        Color.parseColor("#1B1236")});
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), withAlpha(accentColor, 95));
        return drawable;
    }

    private GradientDrawable timerBackground(boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(selected ? Color.parseColor("#FFA3B5") : Color.TRANSPARENT);
        drawable.setCornerRadius(dp(24));
        if (!selected) {
            drawable.setStroke(dp(1), Color.parseColor("#49346B"));
        }
        return drawable;
    }

    private GradientDrawable circle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private void addSpacer(LinearLayout layout, int heightDp) {
        View spacer = new View(requireContext());
        layout.addView(spacer, new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getSlotValue(HashMap<String, Object> slots, String key) {
        if (slots == null || !slots.containsKey(key) || slots.get(key) == null) {
            return null;
        }
        String value = String.valueOf(slots.get(key)).trim();
        return value.isEmpty() || "null".equalsIgnoreCase(value) ? null : value;
    }

    private String firstSlot(HashMap<String, Object> slots, String... keys) {
        for (String key : keys) {
            String value = getSlotValue(slots, key);
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String normalizeVoiceText(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.US)
                .replace("&", "and")
                .replace("/", " ")
                .replace("-", " ")
                .replace("_", " ")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
