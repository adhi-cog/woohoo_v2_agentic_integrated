package com.sb.woohoo.ui.main.fragments.buysim.otpverification;

import java.util.ArrayList;

public interface OtpVerificationContract {

    interface Views {
        void showProgress();
        void hideProgress();
        void onSuccess();
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }

    interface Actions {
        void onSuccess();
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }
}
