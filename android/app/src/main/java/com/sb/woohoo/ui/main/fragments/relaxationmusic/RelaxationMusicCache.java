package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.sb.woohoo.custom.LoggerWrapper;

import java.io.File;

public final class RelaxationMusicCache {
    private static final String TAG = RelaxationMusicCache.class.getSimpleName();
    private static final String CACHE_DIR = "relaxation_music_cache";
    private static final long MAX_CACHE_SIZE_BYTES = 80L * 1024L * 1024L;
    private static SimpleCache cache;

    private RelaxationMusicCache() {
    }

    public static synchronized Cache getCache(Context context) {
        if (cache == null) {
            File cacheDir = getCacheDir(context);
            cache = new SimpleCache(
                    cacheDir,
                    new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE_BYTES),
                    new ExoDatabaseProvider(context.getApplicationContext())
            );
        }
        return cache;
    }

    public static CacheDataSource.Factory createDataSourceFactory(Context context) {
        DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(8000)
                .setReadTimeoutMs(12000)
                .setUserAgent("WooHoo-Hub-RelaxationMusic");

        DefaultDataSource.Factory upstreamFactory =
                new DefaultDataSource.Factory(context.getApplicationContext(), httpFactory);

        return new CacheDataSource.Factory()
                .setCache(getCache(context))
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    public static synchronized void clear(Context context) {
        File cacheDir = getCacheDir(context);
        try {
            if (cache != null) {
                cache.release();
                cache = null;
            }
            SimpleCache.delete(cacheDir, new ExoDatabaseProvider(context.getApplicationContext()));
        } catch (Exception e) {
            LoggerWrapper.e(TAG, "Failed to delete ExoPlayer cache metadata: " + e.getMessage());
        }
        deleteRecursively(cacheDir);
    }

    private static File getCacheDir(Context context) {
        return new File(context.getApplicationContext().getCacheDir(), CACHE_DIR);
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        if (!file.delete()) {
            LoggerWrapper.e(TAG, "Unable to delete " + file.getAbsolutePath());
        }
    }
}
