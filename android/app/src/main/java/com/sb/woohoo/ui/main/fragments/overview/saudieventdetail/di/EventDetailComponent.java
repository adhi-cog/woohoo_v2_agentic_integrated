package com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.di;

import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.EventDetailFragment;

import dagger.Component;

@Component(modules = EventDetailModule.class)
public interface EventDetailComponent {
    void injectEventDetailFragment(EventDetailFragment eventDetailFragment);

}

