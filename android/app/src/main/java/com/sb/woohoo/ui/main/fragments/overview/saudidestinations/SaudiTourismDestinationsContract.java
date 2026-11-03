package com.sb.woohoo.ui.main.fragments.overview.saudidestinations;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.Data;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.DataList;

import java.util.List;

public class SaudiTourismDestinationsContract {

    interface Views {
        void onDestinationsResponse(List<Data> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<Data> categories);
        void errorMessage(int error);
    }
}
