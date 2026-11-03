package com.sb.woohoo.ui.main.fragments.buysim.phonenumber;

import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;

import java.util.ArrayList;

public interface PhoneNumberContract {

    interface Views {
        void showProgress();
        void hideProgress();

        void onSuccess(ArrayList<GetPhoneDetails> data);
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }

    interface Actions {
        void onSuccess(ArrayList<GetPhoneDetails> data);

        void errorMessage(int emptyResultCode);

        void onErrorResponse(int apiFailErrorCode);
    }
}
