package com.sb.woohoo.ui.main.fragments.qrcode.di;

import com.sb.woohoo.ui.main.fragments.qrcode.QRCodeFragment;

import dagger.Component;

@Component(modules = QRCodeModule.class)
public interface QRCodeComponent {

    void injectQRCodeFragment(QRCodeFragment qrCodeFragment);

}
