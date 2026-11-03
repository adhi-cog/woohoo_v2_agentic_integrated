package com.sb.woohoo.ui.onboarding.intro;

/**
 * Created by Shrey on 26/02/18.
 */

public class WoohooIntroPresenter implements WoohooIntroContract.Actions {


    private WoohooIntroContract.View mView;

    public WoohooIntroPresenter(WoohooIntroContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void loadVideo() {

    }
}
