package com.sb.woohoo.ui.main.di;

import com.sb.woohoo.di.WooHooAppComponent;
import com.sb.woohoo.ui.main.MainActivity;

import dagger.Component;

/**
 * Created by Shrey on 10/03/18.
 */

@MainScope
@Component(modules = MainModule.class, dependencies = WooHooAppComponent.class)
public interface MainComponent {
    void injectMainActivity(MainActivity mainActivity);
}