package com.sb.woohoo.ui.main.fragments.livefeedowndevice;

import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubListModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubData;

import java.util.List;

public interface OwnDeviceContracts {

    interface Views {

        void showProgress();

        void removeProgress();

        void setHubsToRecyclerView(List hubList);

        void showHubsNotFound();

        void onAPIError(Object errorMessage);

        void showListOfHubs(List<OthersHubData> othersHubListModelData);
    }

    interface Actions {
        void onHubListFetched(HubListModel hubListModel);

        void onHubsNotFound();

        void onAPICallError(Object error);

        void fetchListOfHubs();

        void onOtherHubListFetched(OtherFamilyModel otherFamilyModel);

        void getHubListOfOtherFamily(String id);

        void onListOfHubsFetchedSuccess(List<OthersHubData> othersHubListModelData);

    }
}
