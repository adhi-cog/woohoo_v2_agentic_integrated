package com.sb.woohoo.ui.main.fragments.diningrestaurants.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.DiningRestaurantsFragment;
import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class DiningRestaurantsModule {

    private DiningRestaurantsFragment diningRestaurantsFragment;

    public DiningRestaurantsModule(DiningRestaurantsFragment diningRestaurantsFragment) {
        this.diningRestaurantsFragment = diningRestaurantsFragment;
    }

    @Provides
    DiningRestaurantsFragment getDiningRestaurantsFragment() {
        return diningRestaurantsFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(diningRestaurantsFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return diningRestaurantsFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(diningRestaurantsFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(diningRestaurantsFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
