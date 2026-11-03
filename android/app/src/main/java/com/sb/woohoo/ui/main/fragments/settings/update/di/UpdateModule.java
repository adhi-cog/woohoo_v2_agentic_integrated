package com.sb.woohoo.ui.main.fragments.settings.update.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateFragment;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdatePresenter;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateRepository;
import com.sb.woohoo.ui.main.helpers.UpdateHelper;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class UpdateModule {
    private UpdateFragment mFragment;

    public UpdateModule(UpdateFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    UpdateFragment providesUpdateFragment() {
        return mFragment;
    }

    /**************************** {@link UpdatePresenter} Dependencies ****************************/

    @Provides
    UpdatePresenter providesUpdatePresenter(UpdateRepository repository) {
        return new UpdatePresenter(mFragment, repository);
    }

    /**************************** {@link UpdateRepository} Dependencies ***************************/

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    UpdateHelper providesUpdateHelper() {
        return new UpdateHelper(mFragment.getParentActivity());
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}