package com.sb.woohoo.ui.main.fragments.buysim.qrscan.di;

import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.qrscan.QRScanFragment;

import dagger.Component;

@Component(modules = QRScanModule.class)
public interface QRScanComponent {
    void injectQRScanFragment(QRScanFragment fragment);
}
