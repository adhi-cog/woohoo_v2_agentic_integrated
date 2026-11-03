package com.sb.woohoo.ui.base;


public interface BaseContract {

    interface Views {

        FeatureFragment getFragmentView();

    }

    interface Actions {

        void attachView(FeatureFragment view);

    }

}
