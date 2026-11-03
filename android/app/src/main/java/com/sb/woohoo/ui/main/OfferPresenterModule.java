package com.sb.woohoo.ui.main;

import com.sb.woohoo.ui.main.secondarydisplay.offers.SecondDisplay;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class OfferPresenterModule {

    private SecondDisplay offerPresentation;

    public OfferPresenterModule(SecondDisplay offerPresentation) {
        this.offerPresentation = offerPresentation;
    }

    @Provides
    SecondDisplay getOfferPresentation() {
        return offerPresentation;
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
