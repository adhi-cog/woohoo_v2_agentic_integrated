package com.sb.woohoo.ui.main.fragments.contactsprofile.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfilePresenter;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileRepository;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Module for {@link ContactsProfileFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Module
public class ContactsProfileModule {
    private ContactsProfileFragment mFragment;

    public ContactsProfileModule(ContactsProfileFragment fragment) {
        this.mFragment = fragment;
    }

    @Provides
    public ContactsProfileFragment getContactsProfileFragment() {
        return mFragment;
    }

    @Provides
    public ContactsProfilePresenter getContactsProfilePresenter(ContactsProfileRepository repository) {
        return new ContactsProfilePresenter(mFragment, repository);
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
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public SharedPrefsHelper getSharedHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getSharedPrefsHelper();
    }
}