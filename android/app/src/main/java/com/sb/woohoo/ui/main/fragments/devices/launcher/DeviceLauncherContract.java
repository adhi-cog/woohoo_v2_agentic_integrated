package com.sb.woohoo.ui.main.fragments.devices.launcher;

import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

public interface DeviceLauncherContract {
    interface View {

        void updateAvailableDevicesToAdapter(List<Light> lights);

        void onItemClicked(IntentInfo intentInfo);

        void onClickLightSetting(final Light light);

        void showMessage(String message);

        void showEmptyLayout();

        void showProgress();

        void hideProgress();
    }

    interface Actions {

        void onAPIError(String message);

        void onAPISuccess(String message);

        void updateLightName(Light light, String newName);

        void deleteLight(Light light);

        void onLightDateFetchSuccess(List<Light> lightList);
    }
}
