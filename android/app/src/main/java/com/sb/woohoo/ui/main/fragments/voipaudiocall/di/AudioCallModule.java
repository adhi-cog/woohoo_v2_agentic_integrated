package com.sb.woohoo.ui.main.fragments.voipaudiocall.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.AudioCallFragment;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.AudioCallPresenter;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.AudioCallRepository;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * {@link Component} for {@link AudioCallFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Module
public class AudioCallModule {
    private AudioCallFragment mFragment;

    public AudioCallModule(AudioCallFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public AudioCallFragment getAudioCallFragment() {
        return mFragment;
    }

    @Provides
    public AudioCallPresenter getAudioCallPresenter(AudioCallRepository repository) {
        return new AudioCallPresenter(mFragment, repository);
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}