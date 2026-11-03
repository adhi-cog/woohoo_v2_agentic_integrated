package com.sb.woohoo.ui.main.fragments.livefeedhubaudio.di;

import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.LiveFeedToHubAudioFragment;

import dagger.Component;


@Component(modules = LiveFeedToHubAudioModule.class)
public interface LiveFeedToHubAudioComponent {
    void injectLiveFeedToHubAudioFragment(LiveFeedToHubAudioFragment liveFeedToHubAudioFragment);
}
