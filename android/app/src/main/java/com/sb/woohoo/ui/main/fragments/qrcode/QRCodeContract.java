package com.sb.woohoo.ui.main.fragments.qrcode;

import com.sb.woohoo.ui.main.fragments.covid.pojo.Question;
import com.sb.woohoo.ui.main.fragments.qrcode.pojo.QRCodeResponse;

import java.util.List;

public interface QRCodeContract {

    interface Views {

        void qrCodeMessage(String errorMessage);

        void qrCodeResult(QRCodeResponse qrCode);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void qrCodeSuccess(QRCodeResponse response);

        void qrCodeMessage(String error);
    }
}
