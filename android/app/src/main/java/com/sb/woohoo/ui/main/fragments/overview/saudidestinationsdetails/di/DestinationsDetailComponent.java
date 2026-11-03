package com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.di;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.DestinationsDetailFragment;

import dagger.Component;

@Component(modules = DestinationsDetailModule.class)
public interface DestinationsDetailComponent {
    void injectDestinationsDetailFragment(DestinationsDetailFragment fragment);

}

