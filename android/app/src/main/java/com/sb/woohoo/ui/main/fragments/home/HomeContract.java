package com.sb.woohoo.ui.main.fragments.home;

import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsData;
import com.sb.woohoo.ui.main.pojo.Appstore;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.StartSession;

import java.util.List;

/**
 * Created by Shrey on 18/03/18.
 */

public interface HomeContract {

    interface View {
        void showProgress();

        void removeProgress();

        void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers);

        void setHotelDetails(HotelDetailsData hotelDetails);

        void showFamilyMemberNotFound();

        void onAPIError(String errorMessage);

        void sessionStarted(StartSession appstoreRes);

        void onError(int msg);

        void updateDeviceName(String name);

        void onSIPCredentialFetch();
    }

    interface Action {

        void getSIPConfiguration();

        void onStaffMembersFetched(List<StaffMember> staffMembers);

        void onHotelDetailsFetched(HotelDetailsData hotelDetails);

        void onStaffMembersNotFound();

        void onAPICallError(String error);

        void registerBroadcastReceivers();

        void unRegisterBroadcastReceivers();

        List<DrawerGridItems> initListOfDrawerItems();

        void callSessionStart(DrawerGridItems drawerGridItems);

        void getAppStoreList();

        void onStoreListUpdate(List<Appstore.Data> list);

        void getDeviceName();

        void updateDeviceName(String name);

        void onSIPCredentialFetch();

    }
}
