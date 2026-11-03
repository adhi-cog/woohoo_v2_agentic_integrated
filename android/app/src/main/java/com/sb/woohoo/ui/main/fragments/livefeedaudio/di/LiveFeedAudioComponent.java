package com.sb.woohoo.ui.main.fragments.livefeedaudio.di;

import com.sb.woohoo.ui.main.fragments.livefeedaudio.LiveFeedAudioFragment;

import dagger.Component;

/**
 * Created by admin on 20/03/18.
 */


@Component(modules = LiveFeedAudioModule.class)
public interface LiveFeedAudioComponent {
    void injectLiveFeedAudioFragment(LiveFeedAudioFragment liveFeedAudioFragment);
}
