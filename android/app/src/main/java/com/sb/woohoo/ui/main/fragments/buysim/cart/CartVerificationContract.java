package com.sb.woohoo.ui.main.fragments.buysim.cart;

import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.RedirectUrl;

public interface CartVerificationContract {

    interface Views {
        void showProgress();
        void hideProgress();
        void onSuccess(RedirectUrl data);
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }

    interface Actions {
        void onSuccess(RedirectUrl data);
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }
}
