package com.sb.woohoo.ui.main.fragments.fnb.history;

import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryData;

import java.util.ArrayList;

public class FnBHistoryContract {

    interface Views {

        void errorMessage();

        void onSuccess(String successMessage, ArrayList<FnBHistoryData> orders);

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onSuccess(String successMessage, ArrayList<FnBHistoryData> orders);

        void errorMessage();
    }
}
