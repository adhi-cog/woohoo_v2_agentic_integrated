package com.sb.woohoo.ui.main.fragments.interel;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtility;
import com.sb.woohoo.ui.main.fragments.interel.pojo.InterelUtilityData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

public class InterelUtilityPresenter extends BasePresenter implements InterelUtilityContract.Actions {

    private InterelUtilityRepository interelUtilityRepository;
    private InterelUtilityContract.Views views;

    @Inject
    public InterelUtilityPresenter(InterelUtilityFragment interelUtilityFragment, InterelUtilityRepository interelUtilityRepository) {
        this.views = interelUtilityFragment;
        this.interelUtilityRepository = interelUtilityRepository;
        interelUtilityRepository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void getRoomState() {
        views.showProgressLayout(true);
        interelUtilityRepository.getRoomState();
    }

    @Override
    public void onUtilityStatusFetched(InterelUtilityData interelUtilityData) {
        views.showProgressLayout(false);
        views.onUtilityFetched(interelUtilityData);
    }

    @Override
    public void onAPICallError(String message) {
        views.showProgressLayout(false);
    }

    @Override
    public void updateUtilityMode(String utilitySkill, boolean status) {
        if (getView().getString(R.string.do_not_disturb).equals(utilitySkill)) {
            interelUtilityRepository.setDNDMode(status);
        } else if (getView().getString(R.string.make_up_room).equals(utilitySkill)) {
            interelUtilityRepository.setMURMode(status);
        }
//        else if (getView().getString(R.string.open_close_curtain).equals(utilitySkill)) {
//            interelUtilityRepository.setCurtain(status);
//        } else if (getView().getString(R.string.blind_state).equals(utilitySkill)) {
//            interelUtilityRepository.setBlind(status);
//        } else if (getView().getString(R.string.master_on_off).equals(utilitySkill)) {
//            interelUtilityRepository.setMasterOnOff(status);
//        } else if (getView().getString(R.string.toggle_lights).equals(utilitySkill)) {
//            interelUtilityRepository.setToggleLight(status);
//        } else if (getView().getString(R.string.bathroom_light).equals(utilitySkill)) {
//            interelUtilityRepository.setBathroomLightOnOff(status);
//        }
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "1");
        if(domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equalsIgnoreCase(Constants.REGENT_DOMAIN_ID) ||
                domainId.equalsIgnoreCase(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            if (getView().getString(R.string.bathroom_light).equals(utilitySkill)) {
                interelUtilityRepository.setMasterOnOff(status, InterelUtilityContract.CONSTANT.BathroomOn,InterelUtilityContract.CONSTANT.BathroomOff);
            } else if (getView().getString(R.string.toggle_lights).equals(utilitySkill)) {
                interelUtilityRepository.setMasterOnOff(status,InterelUtilityContract.CONSTANT.BedroomOn,InterelUtilityContract.CONSTANT.BedroomOff);
            } else if (getView().getString(R.string.bedside).equals(utilitySkill)) {
                interelUtilityRepository.setMasterOnOff(status,InterelUtilityContract.CONSTANT.BedSideOn,InterelUtilityContract.CONSTANT.BedSideOff);
            } else if (getView().getString(R.string.entrance_light).equals(utilitySkill)) {
                interelUtilityRepository.setMasterOnOff(status,InterelUtilityContract.CONSTANT.FoyerOn,InterelUtilityContract.CONSTANT.FoyerOff);
            }
        }else{
             if (getView().getString(R.string.open_close_curtain).equals(utilitySkill)) {
                interelUtilityRepository.setCurtain(status);
            } else if (getView().getString(R.string.blind_state).equals(utilitySkill)) {
                interelUtilityRepository.setBlind(status);
            } else if (getView().getString(R.string.master_on_off).equals(utilitySkill)) {
                interelUtilityRepository.setMasterOnOff(status);
            } else if (getView().getString(R.string.toggle_lights).equals(utilitySkill)) {
                interelUtilityRepository.setToggleLight(status);
            } else if (getView().getString(R.string.bathroom_light).equals(utilitySkill)) {
                interelUtilityRepository.setBathroomLightOnOff(status);
            }
        }

    }

    @Override
    public void setThermostatPoint(Float tempPoint) {
        views.showProgressLayout(true);
        interelUtilityRepository.changeSetPoint(tempPoint);
    }

    @Override
    public void setFanMode(String fanMode,int id) {
        interelUtilityRepository.changeFanMode(fanMode, id);
    }

    @Override
    public void getDynamicUtilities() {
        views.showProgressLayout(true);
        interelUtilityRepository.getDynamicUtilities();
    }

    @Override
    public void updateTemperature(String currentStateId, int temperature) {
        interelUtilityRepository.updateTemperature(currentStateId, temperature);
    }

    @Override
    public void onUpdateModeSuccess(String message) {
        views.showProgressLayout(false);
        views.updateUtilityModeUi(message);
        // getRoomState();
    }

    @Override
    public void onTemperatureUpdated(String message) {
        views.showProgressLayout(false);
        views.onTemperatureUpdated(message);
    }

    @Override
    public void getDynamicUtilitiesResponse(List<DynamicUtility> dynamicUtilityList) {
        views.showProgressLayout(false);
        views.getDynamicUtilities(dynamicUtilityList);
    }

    @Override
    public void updateDynamicUtility(String currentStateId) {
        views.showProgressLayout(true);
        interelUtilityRepository.updateDynamicUtility(currentStateId);
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        interelUtilityRepository.repositoryCleanUps();
    }
}
