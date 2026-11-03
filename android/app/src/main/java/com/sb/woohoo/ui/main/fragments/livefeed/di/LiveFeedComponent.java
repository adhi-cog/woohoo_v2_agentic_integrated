package com.sb.woohoo.ui.main.fragments.livefeed.di;

import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedFragment;

import dagger.Component;

/**
 * Created by admin on 20/03/18.
 */


@Component(modules = LiveFeedModule.class)
public interface LiveFeedComponent {
    void injectLiveFeedFragment(LiveFeedFragment liveFeedFragment);
}
