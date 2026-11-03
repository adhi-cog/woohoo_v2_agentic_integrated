package com.sb.woohoo.ui.main.fragments.newstext.headline.di;

import com.sb.woohoo.ui.main.fragments.newstext.headline.NewsHeadlineFragment;

import dagger.Component;

@Component(modules = NewsHeadlineModule.class)
public interface NewsHeadlineComponent {

    void injectNewsHeadlineFragment(NewsHeadlineFragment newsHeadlineFragment);

}
