package com.sb.woohoo.ui.main.fragments.buysim.phonenumber.di;

import com.sb.woohoo.ui.main.fragments.buysim.buypackage.BuySimFragment;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.PhoneNumberFragment;

import dagger.Component;

@Component(modules = PhoneNumberModule.class)
public interface PhoneNumberComponent {
    void injectPhoneNumberFragment(PhoneNumberFragment phoneNumberFragment);
}
