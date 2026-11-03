package com.sb.woohoo.ui.main.fragments.newstext.detail.di;

import com.sb.woohoo.ui.main.fragments.newstext.detail.NewsDetailFragment;

import dagger.Component;

@Component(modules = NewsDetailModule.class)
public interface NewsDetailComponent {
    void injectNewsDetailFragment(NewsDetailFragment newsDetailFragment);
}
