package com.sb.woohoo.ui.main.fragments.reservation.booking.di;

import com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment;

import dagger.Component;

@Component(modules = ReservationModule.class)
public interface ReservationComponent {

    void injectReservationFragment(ReservationFragment reservationFragment);

}

