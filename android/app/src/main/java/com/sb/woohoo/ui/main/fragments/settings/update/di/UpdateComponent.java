package com.sb.woohoo.ui.main.fragments.settings.update.di;

import com.sb.woohoo.ui.main.fragments.settings.update.UpdateFragment;

import dagger.Component;

@UpdateScope
@Component(modules = UpdateModule.class)
public interface UpdateComponent {
    void injectUpdateFragment(UpdateFragment fragment);
}