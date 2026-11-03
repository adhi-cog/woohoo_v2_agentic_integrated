package com.sb.woohoo.ui.main.fragments.faq.categories.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FaqModule {

    private FaqCategoriesFragment faqCategoriesFragment;

    public FaqModule(FaqCategoriesFragment faqCategoriesFragment) {
        this.faqCategoriesFragment = faqCategoriesFragment;
    }

    @Provides
    FaqCategoriesFragment getFaqCategoriesFragment() {
        return faqCategoriesFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(faqCategoriesFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return faqCategoriesFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(faqCategoriesFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(faqCategoriesFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
