package com.sb.woohoo.ui.main.fragments.video.di;

import com.sb.woohoo.ui.main.fragments.video.VideoListFragment;

import dagger.Component;

@Component(modules = VideoSearchModule.class)
public interface VideoSearchComponent {

    void injectVideoSearchFragment(VideoListFragment videoListFragment);

}
