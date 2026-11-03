package com.sb.woohoo.ui.onboarding.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 1/20/18.
 */

public class LanguageRepository {

    private static final String ENG_US = "English (US)";
    private static final String ENG_UK = "English (UK)";

    public static List<String> getLanguages() {
        List<String> languageList = new ArrayList<>();
        languageList.add(ENG_US);
        languageList.add(ENG_UK);
        return languageList;
    }
}
