package com.sb.woohoo.ui.main.fragments.buysim.buypackage;

import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;

import java.util.ArrayList;

public interface BuySimContract {

    interface Views {
        void showProgress();
        void hideProgress();
        void onSuccess(ArrayList<GetEachPackageDetails> data);
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }

    interface Actions {
        void onSuccess(ArrayList<GetEachPackageDetails> data);
        void errorMessage(int emptyResultCode);
        void onErrorResponse(int apiFailErrorCode);
    }
}
