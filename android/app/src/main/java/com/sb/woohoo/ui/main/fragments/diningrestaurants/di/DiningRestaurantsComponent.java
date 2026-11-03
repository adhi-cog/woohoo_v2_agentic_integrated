package com.sb.woohoo.ui.main.fragments.diningrestaurants.di;

import com.sb.woohoo.ui.main.fragments.diningrestaurants.DiningRestaurantsFragment;

import dagger.Component;

@Component(modules = DiningRestaurantsModule.class)
public interface DiningRestaurantsComponent {
    void injectDiningRestaurantsFragment(DiningRestaurantsFragment diningRestaurantsFragment);
}
