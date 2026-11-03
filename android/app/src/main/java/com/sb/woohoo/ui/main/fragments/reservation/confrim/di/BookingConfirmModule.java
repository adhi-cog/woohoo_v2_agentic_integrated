package com.sb.woohoo.ui.main.fragments.reservation.confrim.di;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.BookingConfirmFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
public class BookingConfirmModule {

    private BookingConfirmFragment bookingConfirmFragment;

    public BookingConfirmModule(BookingConfirmFragment bookingConfirmFragment) {
        this.bookingConfirmFragment = bookingConfirmFragment;
    }

    @Provides
    BookingConfirmFragment getBookingConfirmFragment() {
        return bookingConfirmFragment;
    }

    @Provides
    public WooHooApiCall providesWooHooApiCall() {
        return WooHooApp.get(bookingConfirmFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    Fragment getFragment() {
        return bookingConfirmFragment;
    }

    @Provides
    public Picasso getPicasso() {
        return WooHooApp.get(bookingConfirmFragment.getParentActivity()).getWooHooAppComponent().getPicasso();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(bookingConfirmFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
