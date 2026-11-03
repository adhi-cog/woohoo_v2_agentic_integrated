package com.sb.woohoo.ui.main.fragments.faq.questions.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.faq.categories.FaqCategoriesFragment;
import com.sb.woohoo.ui.main.fragments.faq.questions.FaqQnAFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class FaqQnAModule {

    private FaqQnAFragment faqQnAFragment;

    public FaqQnAModule(FaqQnAFragment faqQnAFragment) {
        this.faqQnAFragment = faqQnAFragment;
    }

    @Provides
    FaqQnAFragment getFaqCategoriesFragment() {
        return faqQnAFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(faqQnAFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return faqQnAFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(faqQnAFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(faqQnAFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
