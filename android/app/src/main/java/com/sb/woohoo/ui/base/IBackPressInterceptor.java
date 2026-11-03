package com.sb.woohoo.ui.base;

/**
 * Created by shreyas on 29-05-2016.
 */
public interface IBackPressInterceptor {

    void onBackPressed(BackCallback backCallback);


    abstract class BackCallback {

        public void backHandled() {
            backCompleted();
        }

        public abstract void backCompleted();
    }
}
