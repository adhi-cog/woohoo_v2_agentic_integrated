package com.sb.woohoo.ui.main.fragments.overview.itemDetail.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.overview.itemDetail.ItemDetailFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class ItemDetailModule {

    private ItemDetailFragment itemDetailFragment;

    public ItemDetailModule(ItemDetailFragment itemDetailFragment) {
        this.itemDetailFragment = itemDetailFragment;
    }

    @Provides
    ItemDetailFragment getItemDetailFragment() {
        return itemDetailFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(itemDetailFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return itemDetailFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(itemDetailFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(itemDetailFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
