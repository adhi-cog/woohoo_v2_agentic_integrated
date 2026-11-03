package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.di;

import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.PinViewFragment;

import dagger.Component;

@Component(modules = PinViewModule.class)
public interface PinViewComponent {

    void injectPinViewFragment(PinViewFragment pinViewFragment);
}
