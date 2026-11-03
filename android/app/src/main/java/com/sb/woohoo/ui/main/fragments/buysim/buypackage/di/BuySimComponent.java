package com.sb.woohoo.ui.main.fragments.buysim.buypackage.di;

import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;

import dagger.Component;

@Component(modules = BuySimModule.class)
public interface BuySimComponent {
    void injectBuySimFragment(BuySimFragment buySimFragment);
}
