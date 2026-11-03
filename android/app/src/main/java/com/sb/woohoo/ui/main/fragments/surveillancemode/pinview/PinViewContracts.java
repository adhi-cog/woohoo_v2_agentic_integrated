package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview;

public interface PinViewContracts {

    interface View {

        void onError(String error);

        void isHubModeInSurveillance(boolean isInSurveillance);

        void onInCorrectPin();
    }

    interface Actions {

        void onAPICallError(String errorMessage);

        void onPinUpdateError(String error);

        void onHubModeChangeFailed();

        void onHubModeInSurveillanceSuccess(boolean surveillanceMode);
    }
}
