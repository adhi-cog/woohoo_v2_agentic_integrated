package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import android.graphics.Color;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicCategoryData;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicTrackData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RelaxationMusicCatalog {
    private static final String AUDIO_BASE_URL =
            BuildConfig.WOOHOO_BASE_URL + "relaxationMusic/tracks/";

    private RelaxationMusicCatalog() {
    }

    public static List<RelaxationCategory> categories() {
        List<RelaxationCategory> categories = new ArrayList<>();
        categories.add(category("deep_sleep", "Deep Sleep", "Midnight sanctuaries for restorative rest",
                Color.parseColor("#FFA3B5"), "☾", tracks("deep_sleep", "Deep Sleep",
                        "moonlight-serenade.mp3", "Moonlight Serenade", "Slow lunar pads and soft air", "1:42",
                        "ethereal-clouds.mp3", "Ethereal Clouds", "Drift into weightlessness", "1:38",
                        "submerged-pulse.mp3", "Submerged Pulse", "Oceanic deep frequencies", "1:51",
                        "lunar-rain.mp3", "Lunar Rain", "Soft patter on glass", "1:34",
                        "silk-and-shadow.mp3", "Silk & Shadow", "Velvet harmonic resonance", "1:47")));
        categories.add(category("zen_garden", "Zen Garden", "Gentle textures for stillness",
                Color.parseColor("#55E6D0"), "♧", tracks("zen_garden", "Zen Garden",
                        "bamboo-drift.mp3", "Bamboo Drift", "Quiet chimes and soft wind", "1:36",
                        "jade-stream.mp3", "Jade Stream", "Flowing water over stone", "1:44",
                        "lotus-dawn.mp3", "Lotus Dawn", "Warm bells and morning air", "1:40",
                        "temple-mist.mp3", "Temple Mist", "Distant bowls in haze", "1:49",
                        "bonsai-rain.mp3", "Bonsai Rain", "Fine rainfall and leaves", "1:33")));
        categories.add(category("mindful_meditation", "Mindful Meditation", "Guided calm without distraction",
                Color.parseColor("#C7B7FF"), "♟", tracks("mindful_meditation", "Mindful Meditation",
                        "breath-anchor.mp3", "Breath Anchor", "Slow pulses for presence", "1:45",
                        "inner-lake.mp3", "Inner Lake", "Still water and low pads", "1:52",
                        "quiet-count.mp3", "Quiet Count", "Minimal rhythm for grounding", "1:31",
                        "open-sky.mp3", "Open Sky", "Airy tones and long release", "1:43",
                        "center-point.mp3", "Center Point", "Balanced bell harmonics", "1:48")));
        categories.add(category("nature_escape", "Nature Escape", "Natural soundscapes for recovery",
                Color.parseColor("#52E5C0"), "♜", tracks("nature_escape", "Nature Escape",
                        "forest-canopy.mp3", "Forest Canopy", "Leaves, birds, and hush", "1:46",
                        "rainforest-hum.mp3", "Rainforest Hum", "Layered rain and insects", "1:50",
                        "coastal-dusk.mp3", "Coastal Dusk", "Slow surf and evening air", "1:42",
                        "mountain-brook.mp3", "Mountain Brook", "Clean stream over rocks", "1:39",
                        "night-meadow.mp3", "Night Meadow", "Crickets under soft wind", "1:54")));
        categories.add(category("focus_lofi", "Focus/Lo-Fi", "Low-pressure loops for concentration",
                Color.parseColor("#F8A6B6"), "♫", tracks("focus_lofi", "Focus/Lo-Fi",
                        "paper-lamp.mp3", "Paper Lamp", "Soft keys and vinyl air", "1:41",
                        "late-checkout.mp3", "Late Checkout", "Warm Rhodes and hush", "1:37",
                        "city-rain-notes.mp3", "City Rain Notes", "Muted beat and drizzle", "1:48",
                        "quiet-desk.mp3", "Quiet Desk", "Dusty loop for reading", "1:35",
                        "neon-window.mp3", "Neon Window", "Night chords and calm bass", "1:53")));
        categories.add(category("healing_tones", "Healing Tones", "Harmonic frequencies for deep ease",
                Color.parseColor("#57E1C4"), "≋", tracks("healing_tones", "Healing Tones",
                        "crystal-breath.mp3", "Crystal Breath", "Shimmering high resonance", "1:44",
                        "amber-bowl.mp3", "Amber Bowl", "Low singing bowl waves", "1:50",
                        "soft-528.mp3", "Soft 528", "Warm tuned drone", "1:46",
                        "violet-harmonics.mp3", "Violet Harmonics", "Layered overtone field", "1:58",
                        "deep-om.mp3", "Deep Om", "Grounded meditative tone", "1:40")));
        return categories;
    }

    public static List<RelaxationCategory> fromApi(List<RelaxationMusicCategoryData> defaultCategories,
                                                   List<RelaxationMusicTrackData> defaultTracks,
                                                   List<RelaxationMusicCategoryData> hotelCategories,
                                                   List<RelaxationMusicTrackData> hotelTracks) {
        List<RelaxationCategory> categories = new ArrayList<>();
        categories.addAll(toCategories(defaultCategories, defaultTracks));
        categories.addAll(toCategories(hotelCategories, hotelTracks));
        return categories;
    }

    private static List<RelaxationCategory> toCategories(List<RelaxationMusicCategoryData> categoryData,
                                                        List<RelaxationMusicTrackData> trackData) {
        List<RelaxationCategory> categories = new ArrayList<>();
        if (categoryData == null || categoryData.isEmpty()) {
            return categories;
        }

        Map<Integer, List<RelaxationMusicTrackData>> tracksByCategory = new HashMap<>();
        if (trackData != null) {
            for (RelaxationMusicTrackData track : trackData) {
                if (track == null || Boolean.FALSE.equals(track.getActive()) || isBlank(track.getAudioUrl())) {
                    continue;
                }
                List<RelaxationMusicTrackData> tracks = tracksByCategory.get(track.getCategoryId());
                if (tracks == null) {
                    tracks = new ArrayList<>();
                    tracksByCategory.put(track.getCategoryId(), tracks);
                }
                tracks.add(track);
            }
        }

        for (RelaxationMusicCategoryData category : categoryData) {
            if (category == null || Boolean.FALSE.equals(category.getActive())) {
                continue;
            }
            List<RelaxationMusicTrackData> apiTracks = tracksByCategory.get(category.getId());
            if (apiTracks == null || apiTracks.isEmpty()) {
                continue;
            }
            String categoryName = valueOrFallback(category.getName(), "Relaxation");
            List<RelaxationTrack> tracks = new ArrayList<>();
            for (RelaxationMusicTrackData track : apiTracks) {
                tracks.add(toTrack(track, categoryName));
            }
            categories.add(new RelaxationCategory(
                    String.valueOf(category.getId()),
                    categoryName,
                    valueOrFallback(category.getDescription(), tracks.size() + " guided tracks"),
                    accentFor(category.getSlug(), category.getName()),
                    iconFor(category.getSlug(), category.getName()),
                    category.getImageUrl(),
                    tracks
            ));
        }
        return categories;
    }

    private static RelaxationTrack toTrack(RelaxationMusicTrackData track) {
        return toTrack(track, "");
    }

    private static RelaxationTrack toTrack(RelaxationMusicTrackData track, String categoryName) {
        String categoryId = String.valueOf(track.getCategoryId());
        return new RelaxationTrack(
                String.valueOf(track.getId()),
                valueOrFallback(track.getTitle(), "Relaxation Track"),
                valueOrFallback(track.getArtist(), "WooHoo"),
                categoryId,
                categoryName,
                track.getAudioUrl(),
                durationLabel(track.getDurationSeconds())
        );
    }

    private static RelaxationCategory category(String id, String title, String subtitle, int accentColor,
                                               String icon, List<RelaxationTrack> tracks) {
        return new RelaxationCategory(id, title, subtitle, accentColor, icon, tracks);
    }

    private static List<RelaxationTrack> tracks(String categoryId, String categoryName, String... values) {
        List<RelaxationTrack> tracks = new ArrayList<>();
        for (int i = 0; i < values.length; i += 4) {
            String fileName = values[i];
            tracks.add(new RelaxationTrack(
                    categoryId + "_" + (i / 4 + 1),
                    values[i + 1],
                    values[i + 2],
                    categoryId,
                    categoryName,
                    AUDIO_BASE_URL + categoryId + "/" + fileName,
                    values[i + 3]
            ));
        }
        return tracks;
    }

    private static String durationLabel(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) {
            return "--:--";
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private static String valueOrFallback(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static int accentFor(String slug, String title) {
        String value = (slug == null ? title : slug).toLowerCase();
        if (value.contains("zen") || value.contains("nature") || value.contains("healing")) {
            return Color.parseColor("#55E6D0");
        }
        if (value.contains("meditation")) {
            return Color.parseColor("#C7B7FF");
        }
        return Color.parseColor("#FFA3B5");
    }

    private static String iconFor(String slug, String title) {
        String value = (slug == null ? title : slug).toLowerCase();
        if (value.contains("zen")) {
            return "♧";
        }
        if (value.contains("meditation")) {
            return "♟";
        }
        if (value.contains("nature")) {
            return "♜";
        }
        if (value.contains("focus") || value.contains("lofi")) {
            return "♫";
        }
        if (value.contains("healing")) {
            return "≋";
        }
        return "☾";
    }
}
