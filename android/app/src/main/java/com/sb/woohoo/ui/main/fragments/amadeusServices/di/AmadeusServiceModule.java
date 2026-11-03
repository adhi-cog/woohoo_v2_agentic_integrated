package com.sb.woohoo.ui.main.fragments.amadeusServices.di;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.amadeusServices.AmadeusServicesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class AmadeusServiceModule {

    private AmadeusServicesFragment amadeusServicesFragment;

    public AmadeusServiceModule(AmadeusServicesFragment amadeusServicesFragment) {
        this.amadeusServicesFragment = amadeusServicesFragment;
    }

    @Provides
    AmadeusServicesFragment provideAmadeusServiceFragment() {
        return amadeusServicesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(amadeusServicesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public WooHooApiCall getWoohooApiCall() {
        return WooHooApp.get(amadeusServicesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(amadeusServicesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
