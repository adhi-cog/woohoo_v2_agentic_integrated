package com.sb.woohoo.ui.onboarding.lang;

import java.util.List;

/**
 * Created by admin on 1/20/18.
 */

public interface LanguageContract {
    interface View {
        void showLanguages(List<String> languageList);

        void showEmptyText();

        void hideEmptyText();

        void showMessage(String message);

    }

    interface Actions {
        void loadLanguages();

        void loadLanguagesError(String msg);

    }
}
