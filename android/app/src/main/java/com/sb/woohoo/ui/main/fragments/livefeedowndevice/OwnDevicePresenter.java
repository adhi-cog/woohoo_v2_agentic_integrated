package com.sb.woohoo.ui.main.fragments.livefeedowndevice;

import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Header;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubListModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyData;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_DEVICE_ID;

public class OwnDevicePresenter
        extends BasePresenter
        implements OwnDeviceContracts.Actions {

    private OwnDeviceRepository deviceRepository;
    private OwnDeviceContracts.Views views;
    private List listOfHubs = new ArrayList(0);

    @Inject
    public OwnDevicePresenter(LiveFeedOwnDeviceFragment liveFeedOwnDeviceFragment, OwnDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.views = liveFeedOwnDeviceFragment;
        deviceRepository.onAttach(this);
    }

    @Override
    public void onHubListFetched(HubListModel hubListModel) {
        for (Hub hub : hubListModel.getData()) {
            if (!hub.getId().equals(WooHooApp.getSharedPrefsHelper().get(PREF_KEY_DEVICE_ID, null))) {
                listOfHubs.add(hub);
            }
        }
        deviceRepository.fetchHubsWithAccess();
    }

    @Override
    public void onHubsNotFound() {
        views.showHubsNotFound();
    }

    @Override
    public void onAPICallError(Object error) {
        views.removeProgress();
        views.onAPIError(error);
    }

    @Override
    public void fetchListOfHubs() {
        views.showProgress();
        deviceRepository.fetchListOfHubs();
    }

    @Override
    public void onOtherHubListFetched(OtherFamilyModel otherFamilyModel) {
        views.removeProgress();
        if (otherFamilyModel.getData().size() > 0) {
            Header header = new Header();
            header.setHeader("You can Video Conference to following users");
            listOfHubs.add(header);
            for (OtherFamilyData otherFamilyData : otherFamilyModel.getData()) {
                listOfHubs.add(otherFamilyData);
            }
        }
        if (listOfHubs.size() > 0) {
            views.setHubsToRecyclerView(listOfHubs);
        } else {
            onHubsNotFound();
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void getHubListOfOtherFamily(String id) {
        deviceRepository.getHubListOfOtherFamily(id);
    }

    @Override
    public void onListOfHubsFetchedSuccess(List<OthersHubData> othersHubListModelData) {
        if (othersHubListModelData.size() > 0) {
            views.showListOfHubs(othersHubListModelData);
        }
    }
}
