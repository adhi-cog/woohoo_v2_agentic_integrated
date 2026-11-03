package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial;

public interface SurveillanceInitContract {

    interface Views {

        void onUserPinExists(boolean isAvailable);

    }

    interface Actions {
        void verifyIfUserPinExists();
    }
}
