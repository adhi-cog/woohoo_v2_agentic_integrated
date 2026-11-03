package com.sb.woohoo.ui.main.fragments.buysim.emailverification.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.EmailVerificationFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class EmailVerificationModule {

    private EmailVerificationFragment emailVerificationFragment;

    public EmailVerificationModule(EmailVerificationFragment emailVerificationFragment) {
        this.emailVerificationFragment = emailVerificationFragment;
    }

    @Provides
    EmailVerificationFragment getEmailVerificationFragment() {
        return emailVerificationFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(emailVerificationFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return emailVerificationFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(emailVerificationFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(emailVerificationFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
