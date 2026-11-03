package com.sb.woohoo.ui.main.fragments.translation;

import android.content.Context;

import com.sb.woohoo.ui.main.pojo.translate.TranslateResponse;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link TranslationFragment}.
 * This needs to be updated
 *
 * @author subham satyam
 */
public interface TranslationContracts {
    interface View {

        void onTraslateResult(TranslateResponse translateResponse);
    }

    interface Actions {

        void onTranslateResult(TranslateResponse translateResponse);

    }
}