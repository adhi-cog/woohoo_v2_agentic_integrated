package com.sb.woohoo.ui.main.fragments.reservation.upcoming.di;

import com.sb.woohoo.ui.main.fragments.reservation.upcoming.UpcomingServiceFragment;

import dagger.Component;

@Component(modules = UpcomingServiceModule.class)
public interface UpcomingServiceComponent {

    void injectUpcomingServiceFragment(UpcomingServiceFragment upcomingServiceFragment);

}

