package com.sb.woohoo.ui.main.fragments.interel;

import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtility;
import com.sb.woohoo.ui.main.fragments.interel.pojo.InterelUtilityData;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UtilityUpdationResponse;

import java.util.List;

public interface InterelUtilityContract {

    interface Views {

        void showProgressLayout(boolean show);

        void onUtilityFetched(InterelUtilityData interelUtilityData);

        void updateUtilityModeUi(String message);

        void getDynamicUtilities(List<DynamicUtility> dynamicUtilityList);

        void onTemperatureUpdated(String message);
    }

    interface Actions {

        void updateUtilityMode(String utilityType, boolean status);

        void getRoomState();

        void onAPICallError(String message);

        void onUtilityStatusFetched(InterelUtilityData interelUtilityData);

        void onUpdateModeSuccess(String message);

        void setThermostatPoint(Float tempPoint);

        void setFanMode(String fanMode, int id);

        void getDynamicUtilities();

        void getDynamicUtilitiesResponse(List<DynamicUtility> dynamicUtilityList);

        void updateDynamicUtility(String currentStateId);

        void updateTemperature(String currentStateId, int temperature);

        void onTemperatureUpdated(String message);
    }

    interface CONSTANT {

        String BathroomOn = "BathroomOn";
        String BathroomOff = "BathroomOff";
        String BedroomOn = "BedroomOn";
        String BedroomOff = "BedroomOff";
        String BedSideOn = "BedSideOn";
        String BedSideOff = "BedSideOff";
        String FoyerOn = "FoyerOn";
        String FoyerOff = "FoyerOff";
        String Activate = "Activate";
        String Deactivate = "Deactivate";
        String CurtainOpen = "CurtainOpen";
        String CurtainClose = "CurtainClose";
        String Open = "Open";
        String Close = "Close";
        String MasterOn = "MasterOn";
        String MasterOff = "MasterOff";
        String BedroomStripLightOn = "BedroomStripLightOn";
        String BedroomStripLightOff = "BedroomStripLightOff";


    }
}
