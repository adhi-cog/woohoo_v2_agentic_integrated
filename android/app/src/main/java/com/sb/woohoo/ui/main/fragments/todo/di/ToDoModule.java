package com.sb.woohoo.ui.main.fragments.todo.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.todo.ToDoFragment;
import com.sb.woohoo.ui.main.fragments.todo.ToDoPresenter;
import com.sb.woohoo.ui.main.fragments.todo.ToDoRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link ToDoFragment}.
 */
@Module
public class ToDoModule {
    private ToDoFragment mFragment;

    public ToDoModule(ToDoFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public ToDoFragment getToDoFragment() {
        return mFragment;
    }

    @Provides
    public ToDoPresenter getToDoPresenter(ToDoRepository repository) {
        return new ToDoPresenter(mFragment, repository);
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
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