package com.sb.woohoo.ui.main.fragments.buysim.cart.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class CartVerificationModule {

    private CartVerificationFragment cartVerificationFragment;

    public CartVerificationModule(CartVerificationFragment cartVerificationFragment) {
        this.cartVerificationFragment = cartVerificationFragment;
    }

    @Provides
    CartVerificationFragment getCartVerificationFragment() {
        return cartVerificationFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(cartVerificationFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return cartVerificationFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(cartVerificationFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(cartVerificationFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }
}
