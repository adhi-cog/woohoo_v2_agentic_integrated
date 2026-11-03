package com.sb.woohoo.ui.main.fragments.devices.huecontrol;

import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.base.BaseRepository;
import com.sb.woohoo.ui.main.fragments.devices.LightControl;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * APIs
 * <p>
 * 1. Update light state
 */
public class PhilipsHueRepository extends BaseRepository<PhilipsHuePresenter> implements LightControl.LightControlCallback {

    private WooHooApiCall mApiCall;
    private CompositeDisposable compositeDisposable;

    @Inject
    PhilipsHueRepository(WooHooApiCall mApiCall, CompositeDisposable compositeDisposable) {
        this.mApiCall = mApiCall;
        this.compositeDisposable = compositeDisposable;
    }

    void makeApiCall(Light light, int mode) {
        LightControl lightControl = new LightControl(this);
        lightControl.changeLightState(light, mode, compositeDisposable, mApiCall);
    }

    @Override
    public void repositoryCleanUps() {
        super.repositoryCleanUps();
    }

    @Override
    public void onSuccess() {
        //TODO do on success
    }

    @Override
    public void onError(String message) {
        getActions().onAPIError(message);
    }
}
