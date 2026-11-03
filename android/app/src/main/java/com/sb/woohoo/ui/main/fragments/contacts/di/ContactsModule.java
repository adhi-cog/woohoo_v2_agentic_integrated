package com.sb.woohoo.ui.main.fragments.contacts.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsPresenter;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link ContactsComponent}.
 *
 * @author Ritwik Jamuar.
 */
@Module
public class ContactsModule {
    private ContactsFragment mFragment;

    public ContactsModule(ContactsFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public ContactsFragment getContactsFragment() {
        return mFragment;
    }

    @Provides
    public ContactsPresenter getContactsPresenter(ContactsRepository repository) {
        return new ContactsPresenter(mFragment, repository);
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