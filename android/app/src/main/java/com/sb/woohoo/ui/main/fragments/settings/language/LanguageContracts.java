package com.sb.woohoo.ui.main.fragments.settings.language;

import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageData;

import java.util.ArrayList;

public interface LanguageContracts {

    interface Views{

        void onError(int errorCode);

        void onAllLocaleFetch(ArrayList<LanguageData> data);
    }

    interface Actions{

        void getAllLanguage();

        void onAllLocaleFetchSucess(ArrayList<LanguageData> data);

        void onError(int errorCode);
    }
}
