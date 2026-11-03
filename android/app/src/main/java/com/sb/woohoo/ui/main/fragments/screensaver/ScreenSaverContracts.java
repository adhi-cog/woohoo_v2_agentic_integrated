package com.sb.woohoo.ui.main.fragments.screensaver;

import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;

public interface ScreenSaverContracts {

    interface Views{

        void onError(String message);

        void onDetailsFetched(ScreenSaverMulti data);
    }

    interface Actions{

        void getScreenSaverDetails();

        void onDetailsFetchingSuccess(ScreenSaverMulti data);

        void onError(String message);
    }
}
