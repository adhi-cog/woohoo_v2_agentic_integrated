package com.sb.woohoo.ui.main.fragments.devices.huecontrol.di;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.custom.Colors;
import com.sb.woohoo.data.network.WooHooApiCall;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.PhilipsHueFragment;
import com.sb.woohoo.utils.Constants;

import java.net.URISyntaxException;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.socket.client.IO;
import io.socket.client.Socket;

@Module
public class PhilipsHueModule {

    private PhilipsHueFragment mFragment;

    public PhilipsHueModule(PhilipsHueFragment philipsHueFragment) {
        this.mFragment = philipsHueFragment;
    }

    @Provides
    public PhilipsHueFragment getPhilipsHueFragment() {
        return mFragment;
    }

    @Provides
    public WooHooApiCall getWooHooApiCall() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getWooHooApiCall();
    }

    @Provides
    public Colors getColors() {
        return new Colors();
    }

    @Provides
    public Gson getGson() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getGson();
    }

    @Provides
    public JsonParser getJsonParser() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getJsonParser();
    }

    @Provides
    Socket getSocket() {
        Socket socket = null;
        try {
            socket = IO.socket(Constants.PhillipsHue.SOCKET_URL_DEVICES);
        } catch (URISyntaxException uriException) {
            uriException.printStackTrace();
        }
        return socket;
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public AnalyticsHelper getAnalyticsHelper() {
        return WooHooApp.get(mFragment.getParentActivity()).getWooHooAppComponent().getAnalyticsHelper();
    }

}
