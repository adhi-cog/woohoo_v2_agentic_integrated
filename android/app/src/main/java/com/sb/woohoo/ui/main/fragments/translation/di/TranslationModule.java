package com.sb.woohoo.ui.main.fragments.translation.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.Interactors.HelperInteractor;
import com.sb.woohoo.ui.main.di.MainScope;
import com.sb.woohoo.ui.main.fragments.translation.TranslationFragment;
import com.sb.woohoo.ui.main.fragments.translation.TranslationPresenter;
import com.sb.woohoo.ui.main.fragments.translation.TranslationRepository;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * {@link Component} for {@link TranslationFragment}.
 * This needs to be updated
 *
 * @author subham satyam.
 */
@Module
public class TranslationModule {
    private TranslationFragment mFragment;

    public TranslationModule(TranslationFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public TranslationFragment getTranslationFragment() {
        return mFragment;
    }

    @Provides
    public TranslationPresenter getTranslationPresenter(
            TranslationRepository repository) {
        return new TranslationPresenter(mFragment, repository);
    }

    @Provides
    public WooHooApiCall provideWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}