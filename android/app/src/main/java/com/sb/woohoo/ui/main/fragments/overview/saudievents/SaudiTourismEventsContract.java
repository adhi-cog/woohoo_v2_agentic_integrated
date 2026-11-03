package com.sb.woohoo.ui.main.fragments.overview.saudievents;

import com.sb.woohoo.ui.main.fragments.overview.Category;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.Data;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.DataList;

import java.util.List;

public class SaudiTourismEventsContract {

    interface Views {
        void onCategoriesResponse(List<DataList> list);
        void onErrorResponse(int errorMessage);
        void showProgress();
        void hideProgress();
    }

    interface Actions {
        void onSuccess(List<DataList> categories);
        void errorMessage(int error);
    }
}
