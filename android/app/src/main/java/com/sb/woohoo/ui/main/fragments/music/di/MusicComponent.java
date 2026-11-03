package com.sb.woohoo.ui.main.fragments.music.di;

import com.sb.woohoo.ui.main.fragments.music.MusicFragment;

import dagger.Component;

/**
 * {@link Component} for {@link MusicFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = MusicModule.class)
@MusicScope
public interface MusicComponent {
    void injectMusicFragment(MusicFragment fragment);
}