package com.sb.woohoo.ui.main.fragments.translation.di;

import com.sb.woohoo.ui.main.di.MainScope;
import com.sb.woohoo.ui.main.fragments.translation.TranslationFragment;

import dagger.Component;

/**
 * {@link Component} for {@link TranslationFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Component(modules = TranslationModule.class)
public interface TranslationComponent {
    void injectTranslationFragment(TranslationFragment fragment);
}