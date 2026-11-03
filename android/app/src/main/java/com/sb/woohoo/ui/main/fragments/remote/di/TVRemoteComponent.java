package com.sb.woohoo.ui.main.fragments.remote.di;

import com.sb.woohoo.ui.main.fragments.remote.TVRemoteFragment;

import dagger.Component;

@Component(modules = TVRemoteModule.class)
public interface TVRemoteComponent {
    void injectTVRemoteFragment(TVRemoteFragment tvRemoteFragment);
}
