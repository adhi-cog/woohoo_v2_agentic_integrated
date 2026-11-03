package com.sb.woohoo.ui.main.fragments.screensaver;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class ScreenSaverPresenter
        extends BasePresenter
        implements ScreenSaverContracts.Actions{

    private ScreenSaverContracts.Views views;
    private ScreenSaverRepository screenSaverRepisotory;

    @Inject
    public ScreenSaverPresenter(ScreenSaverFragment views, ScreenSaverRepository screenSaverRepisotory) {
        this.views = views;
        this.screenSaverRepisotory = screenSaverRepisotory;
        this.screenSaverRepisotory.onAttach(this);
    }

    @Override
    public void getScreenSaverDetails() {
        screenSaverRepisotory.getScreenSaverDetails(0);
    }


    @Override
    public void onDetailsFetchingSuccess(ScreenSaverMulti data) {
        views.onDetailsFetched(data);
    }

    @Override
    public void onError(String message) {
        views.onError(message);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        screenSaverRepisotory.repositoryCleanUps();
    }
}
