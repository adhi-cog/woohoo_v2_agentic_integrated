package com.sb.woohoo.ui.main.fragments.voipaudiocall.di;

import com.sb.woohoo.ui.main.fragments.voipaudiocall.AudioCallFragment;

import dagger.Component;

/**
 * {@link Component} for {@link AudioCallFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Component(modules = AudioCallModule.class)
@AudioCallScope
public interface AudioCallComponent {
    void injectAudioCallFragment(AudioCallFragment fragment);
}