package com.sb.woohoo.ui.main.fragments.buysim.cart.di;

import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;

import dagger.Component;

@Component(modules = CartVerificationModule.class)
public interface CartVerificationComponent {
    void injectCartVerificationFragment(CartVerificationFragment cartVerificationFragment);
}
