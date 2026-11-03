package com.sb.woohoo.ui.main.fragments.reservation.confrim.di;

import com.sb.woohoo.ui.main.fragments.reservation.confrim.BookingConfirmFragment;

import dagger.Component;

@Component(modules = BookingConfirmModule.class)
public interface BookingConfirmComponent {

    void injectBookingConfirmFragment(BookingConfirmFragment bookingConfirmFragment);

}

