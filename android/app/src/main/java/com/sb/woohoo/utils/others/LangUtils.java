package com.sb.woohoo.utils.others;

import com.sb.woohoo.utils.Constants;

import java.util.HashMap;
import java.util.Objects;

public class LangUtils {

    public HashMap<String, String> allLang = new HashMap<>();
    public static String DEFAULT_LANG = "en";

    public LangUtils() {
        allLang.put("english", "en");
        allLang.put("hindi", "hi");
        allLang.put("irish", "ga");
        allLang.put("italian", "it");
        allLang.put("arabic", "ar");
        allLang.put("japanese", "ja");
        allLang.put("korean", "ko");
        allLang.put("kannada", "kn");
        allLang.put("bengali", "bn");
        allLang.put("latin", "la");
        allLang.put("chinese", "zh-CN");
        allLang.put("malay", "ms");
        allLang.put("norwegian", "no");
        allLang.put("czech", "cs");
        allLang.put("persian", "fa");
        allLang.put("danish", "da");
        allLang.put("polish", "pl");
        allLang.put("dutch", "nl");
        allLang.put("portuguese", "pt");
        allLang.put("romanian", "ro");
        allLang.put("esperanta", "eo");
        allLang.put("russian", "ru");
        allLang.put("estonian", "et");
        allLang.put("serbian", "sr");
        allLang.put("filipino", "tl");
        allLang.put("finnish", "fi");
        allLang.put("french", "fr");
        allLang.put("spanish", "es");
        allLang.put("swedish", "sv");
        allLang.put("german", "de");
        allLang.put("tamil", "ta");
        allLang.put("greek", "el");
        allLang.put("telugu", "te");
        allLang.put("gujarati", "gu");
        allLang.put("thai", "th");
        allLang.put("turkish", "tr");
        allLang.put("hebrew", "iw");
        allLang.put("ukrainian", "uk");
        allLang.put("urdu", "ur");
        allLang.put("vietnamese", "vi");
        allLang.put("hungarian", "hu");
        allLang.put("indonesian", "id");
    }

    public static String getLanguageName(String lang) {
        if (Objects.equals(lang, Constants.LANGUAGE.ARABIC))
            return ("عربي");
        else if (Objects.equals(lang, Constants.LANGUAGE.CHINESH))
            return ("中文");
        else if (Objects.equals(lang, Constants.LANGUAGE.DUTCH))
            return ("Dutch");
        else if (Objects.equals(lang, Constants.LANGUAGE.FRENCH))
            return ("French");
        else
            return ("English");

    }

}
