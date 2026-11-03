package com.sb.woohoo.ui.main.fragments.video.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.video.VideoListFragment;

import androidx.fragment.app.Fragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module(includes = NetworkModule.class)
public class VideoSearchModule {

    private VideoListFragment videoListFragment;

    public VideoSearchModule(VideoListFragment videoListFragment) {
        this.videoListFragment = videoListFragment;
    }

    @Provides
    VideoListFragment getVideoFragment() {
        return videoListFragment;
    }

    @Provides
    Fragment getFragment() {
        return videoListFragment;
    }

    @Provides
//    @MainScope
    WooHooApiCall providesWoohooApiCall() {
        return WooHooApp.get(videoListFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}