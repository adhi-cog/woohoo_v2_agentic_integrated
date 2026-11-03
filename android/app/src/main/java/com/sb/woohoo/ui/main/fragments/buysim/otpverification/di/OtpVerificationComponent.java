package com.sb.woohoo.ui.main.fragments.buysim.otpverification.di;

import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;

import dagger.Component;

@Component(modules = OtpVerificationModule.class)
public interface OtpVerificationComponent {
    void injectOtpVerificationFragment(OtpVerificationFragment otpVerificationFragment);
}
