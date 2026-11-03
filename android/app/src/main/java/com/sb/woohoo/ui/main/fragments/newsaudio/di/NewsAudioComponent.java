package com.sb.woohoo.ui.main.fragments.newsaudio.di;

import com.sb.woohoo.ui.main.fragments.newsaudio.NewsAudioFragment;

import dagger.Component;

@NewsAudioScope
@Component(modules = NewsAudioModule.class)
public interface NewsAudioComponent {
    void injectNewsAudioFragment(NewsAudioFragment fragment);
}