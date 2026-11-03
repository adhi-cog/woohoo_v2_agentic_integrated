package com.sb.woohoo.ui.main.fragments.livefeedhub.di;

import com.sb.woohoo.ui.main.fragments.livefeedhub.LiveFeedToHubFragment;

import dagger.Component;


@Component(modules = LiveFeedToHubModule.class)
public interface LiveFeedToHubComponent {
    void injectLiveFeedToHubFragment(LiveFeedToHubFragment liveFeedToHubFragment);
}
