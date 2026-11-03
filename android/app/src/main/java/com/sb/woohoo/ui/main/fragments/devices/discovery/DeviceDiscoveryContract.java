package com.sb.woohoo.ui.main.fragments.devices.discovery;

import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDevice;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueLightContainer;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

import java.util.List;

public interface DeviceDiscoveryContract {
    interface View {
        void updateLightsAdapter(List<Light> devices);

        void showErrorMessage(String message);

        void onRegisterFinished();

        void onItemClicked(Light tag);
    }

    interface Actions {

        void onAPIError(String message);

        void onPhilipsHueLightDataFetched(PhilipsHueLightContainer lightContainer,
                                          PhilipsHueDevice philipsHueDevice,
                                          String username);
    }
}
