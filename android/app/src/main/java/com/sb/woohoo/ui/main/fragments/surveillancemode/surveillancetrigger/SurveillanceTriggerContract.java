package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger;

public interface SurveillanceTriggerContract {

    interface Views {

        void onError(String errorMessage);

        void cancelSurveillanceMode();

    }

    interface Actions {

        void cancelSurveillanceMode(String mPin);

    }
}
