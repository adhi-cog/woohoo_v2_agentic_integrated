package com.sb.woohoo.ui.main.fragments.home;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.content.Context;
import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsData;
import com.sb.woohoo.ui.main.pojo.Appstore;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Shrey on 09/03/18.
 */


public class HomePresenter
        extends BasePresenter
        implements HomeContract.Action {
    private HomeFragment mView;
    private HomeRepository mRepository;

    /*private BroadcastReceiver dateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mView.setDate();
        }
    };*/

    /*------------------------------------------------- Fetch Family Members ---------------------------------------------*/

    @Inject
    public HomePresenter(HomeFragment view, HomeRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(this);
    }

    /**
     * Consults the repository to fetch Family Members of User.
     */

    void fetchStaffMembers() {
        mView.showProgress();
        mRepository.fetchHotelContacts();
    }

    public void fetchHotelDetails() {
        mView.showProgress();
        //TODO ENABLE THIS AFTER THE API IS FIXED
        mRepository.fetchHotelDetails();
    }

    @Override
    public void onStoreListUpdate(List<Appstore.Data> list) {

        List<DrawerGridItems> itemsList = initListOfDrawerItems();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().toLowerCase().equalsIgnoreCase("zoom")) {
                itemsList.add(new DrawerGridItems(list.get(i), R.drawable.icon_zoom, list.get(i).getName()));
            } else if (list.get(i).getName().toLowerCase().equalsIgnoreCase("uber")) {
                itemsList.add(new DrawerGridItems(list.get(i), R.drawable.icon_uber, list.get(i).getName()));
            } else if (list.get(i).getName().toLowerCase().equalsIgnoreCase("outlook")) {
                itemsList.add(new DrawerGridItems(list.get(i), R.drawable.icon_outlook, list.get(i).getName()));
            } else {
                itemsList.add(new DrawerGridItems(list.get(i), R.drawable.icon_amadeus, list.get(i).getName()));
            }
        }

        Collections.sort(itemsList, new Comparator() {
            @Override
            public int compare(Object itemOne, Object itemTwo) {
                return ((DrawerGridItems) itemOne).getTitle().toLowerCase()
                        .compareTo(((DrawerGridItems) itemTwo).getTitle().toLowerCase());
            }
        });

        mView.showDrawerAsPerDomain(itemsList);
    }

    @Override
    public void getDeviceName() {
        mRepository.fetchDeviceNameApiRequest();
    }

    @Override
    public void updateDeviceName(String name) {
        mView.updateDeviceName(name);
    }

    @Override
    public void getSIPConfiguration() {
        mRepository.getSIPConfiguration();
    }

    @Override
    public void onSIPCredentialFetch() {
        mView.onSIPCredentialFetch();
    }

    public void onStartSessionSucess(StartSession appstoreRes) {
        mView.sessionStarted(appstoreRes);
    }

    public void onError(int msgResId) {
        mView.onError(msgResId);
    }

    @Override
    public void callSessionStart(DrawerGridItems drawerGridItems) {

        mRepository.startSessionApi(drawerGridItems);

    }
    @Override
    public void getAppStoreList() {
        mRepository.appStoreList();
    }

    @Override
    public List<DrawerGridItems> initListOfDrawerItems() {
        List<DrawerGridItems> list = new ArrayList<>();
        HomeFragment context = mView;
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        Log.e("domain_id", "initListOfDrawerItems - " + domainId);

        if (domainId.equals(Constants.GBTB_DOMAIN_ID)){
            list.add(new DrawerGridItems(null, R.drawable.ticket_gbtb, context.getString(R.string.ticket_purchase)));
            list.add(new DrawerGridItems(null, R.drawable.schedule, context.getString(R.string.time_slot_booking)));
            list.add(new DrawerGridItems(null, R.drawable.information, context.getString(R.string.information)));
        } else if (domainId.equals(Constants.RC_DOMAIN_ID)) {
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
            list.add(new DrawerGridItems(null, R.drawable.icon_fnb_new, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.restaurant_regent, context.getString(R.string.fragment_restaurant_title)));
        } else if (domainId.equals(Constants.CKS_DOMAIN_USER_ID) || domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID)) {
            list.add(new DrawerGridItems(null, R.drawable.in_room_dining, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amadeus_service_request)));
            list.add(new DrawerGridItems(null, R.drawable.smart_room, context.getString(R.string.control)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
            if (!domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID))
                list.add(new DrawerGridItems(null, R.drawable.icon_spotify, context.getString(R.string.spotify)));
            list.add(new DrawerGridItems(null, R.drawable.icon_weather, context.getString(R.string.weather)));
            list.add(new DrawerGridItems(null, R.drawable.icon_clock, context.getString(R.string.alarms)));
        } else if (domainId.equals(Constants.REGENT_DOMAIN_ID)) {
            list.add(new DrawerGridItems(null, R.drawable.in_room_dining, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amenities)));
            list.add(new DrawerGridItems(null, R.drawable.smart_room, context.getString(R.string.control)));
            list.add(new DrawerGridItems(null, R.drawable.restaurant_regent, context.getString(R.string.fragment_restaurant_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
        }else if (domainId.equals(Constants.POSTILLION_DOMAIN_ID)) {
            list.add(new DrawerGridItems(null, R.drawable.smart_room, context.getString(R.string.control)));
            list.add(new DrawerGridItems(null, R.drawable.in_room_dining, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amenities)));
            list.add(new DrawerGridItems(null, R.drawable.icon_weather, context.getString(R.string.weather)));
            list.add(new DrawerGridItems(null, R.drawable.icon_news, context.getString(R.string.fragment_news_text_view_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
            list.add(new DrawerGridItems(null, R.drawable.icon_clock, context.getString(R.string.alarms)));
            list.add(new DrawerGridItems(null, R.drawable.icon_faq, context.getString(R.string.faq)));

        } else if (domainId.equals(Constants.HOLIDAY_INN_DOMAIN_ID)) {
            list.add(new DrawerGridItems(null, R.drawable.in_room_dining, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.img_spa, context.getString(R.string.h_care_spa)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amenities)));
            list.add(new DrawerGridItems(null, R.drawable.information, context.getString(R.string.information)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
            list.add(new DrawerGridItems(null, R.drawable.icon_clock, context.getString(R.string.alarms)));
            list.add(new DrawerGridItems(null, R.drawable.icon_faq, context.getString(R.string.faq)));
            list.add(new DrawerGridItems(null, R.drawable.icon_weather, context.getString(R.string.weather)));
            list.add(new DrawerGridItems(null, R.drawable.icon_news, context.getString(R.string.fragment_news_text_view_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_reminders, context.getString(R.string.fragment_reminder_text_view_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_reservation, context.getString(R.string.reservation_app_tile)));

        }else if (domainId.equals(Constants.KHALIDIA_PALACE_DOMAIN_ID)){
            list.add(new DrawerGridItems(null, R.drawable.smart_room, context.getString(R.string.smart_room_control)));
            list.add(new DrawerGridItems(null, R.drawable.ic_fnb, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amenities)));
            list.add(new DrawerGridItems(null, R.drawable.restaurant_regent, context.getString(R.string.fragment_restaurant_title)));
//            list.add(new DrawerGridItems(null, R.drawable.information, context.getString(R.string.information)));
//            list.add(new DrawerGridItems(null, R.drawable.icon_reservation, context.getString(R.string.reservation_app_tile)));
            list.add(new DrawerGridItems(null, R.drawable.icon_weather, context.getString(R.string.weather)));
            list.add(new DrawerGridItems(null, R.drawable.icon_news, context.getString(R.string.fragment_news_text_view_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
//            list.add(new DrawerGridItems(null, R.drawable.icon_spotify, context.getString(R.string.spotify)));
            list.add(new DrawerGridItems(null, R.drawable.icon_clock, context.getString(R.string.alarms)));
            list.add(new DrawerGridItems(null, R.drawable.icon_faq, context.getString(R.string.faq)));
//            list.add(new DrawerGridItems(null, R.drawable.devices, context.getString(R.string.devices)));
            list.add(new DrawerGridItems(null, R.drawable.icon_reminders, context.getString(R.string.fragment_reminder_text_view_title)));
        }else {
            list.add(new DrawerGridItems(null, R.drawable.smart_room, context.getString(R.string.smart_room_control)));
            list.add(new DrawerGridItems(null, R.drawable.ic_fnb, context.getString(R.string.in_room_dining)));
            list.add(new DrawerGridItems(null, R.drawable.service_request, context.getString(R.string.amenities)));
            if (domainId.equals(Constants.SHERAA_HQ)){
                list.add(new DrawerGridItems(null, R.drawable.bg_buy_sim, context.getString(R.string.buy_sim)));
            }
            list.add(new DrawerGridItems(null, R.drawable.restaurant_regent, context.getString(R.string.fragment_restaurant_title)));
            list.add(new DrawerGridItems(null, R.drawable.information, context.getString(R.string.information)));
            list.add(new DrawerGridItems(null, R.drawable.icon_reservation, context.getString(R.string.reservation_app_tile)));
            list.add(new DrawerGridItems(null, R.drawable.icon_weather, context.getString(R.string.weather)));
            list.add(new DrawerGridItems(null, R.drawable.icon_news, context.getString(R.string.fragment_news_text_view_title)));
            list.add(new DrawerGridItems(null, R.drawable.icon_youtube, context.getString(R.string.youtube)));
            list.add(new DrawerGridItems(null, R.drawable.icon_spotify, context.getString(R.string.spotify)));
            list.add(new DrawerGridItems(null, R.drawable.icon_clock, context.getString(R.string.alarms)));

            if (!domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID) && !domainId.equalsIgnoreCase(Constants.PARK_ROYAL_PICKERING_DOMAIN_ID)) {
                list.add(new DrawerGridItems(null, R.drawable.icon_faq, context.getString(R.string.faq)));
                list.add(new DrawerGridItems(null, R.drawable.devices, context.getString(R.string.devices)));
                list.add(new DrawerGridItems(null, R.drawable.icon_reminders, context.getString(R.string.fragment_reminder_text_view_title)));
//                list.add(new DrawerGridItems(null, R.drawable.devices, context.getString(R.string.Translation)));
//                list.add(new DrawerGridItems(null, R.drawable.ic_covid, context.getString(R.string.drawer_menu_covid)));
                list.add(new DrawerGridItems(null, R.drawable.ic_feedback, context.getString(R.string.drawer_menu_feedback)));
            }
            if (domainId.equals(Constants.PARK_ROYAL_PICKERING_DOMAIN_ID)){
                list.add(new DrawerGridItems(null, R.drawable.ic_tv_remote, context.getString(R.string.tv_remote)));
            }
        }
        insertBeforeTitle(list,
                new DrawerGridItems(null, R.drawable.ic_relaxation_music, context.getString(R.string.relaxation_music)),
                context.getString(R.string.information));
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
            insertBeforeTitle(list,
                    new DrawerGridItems(null, R.drawable.ic_call, context.getString(R.string.call)),
                    context.getString(R.string.information));
        }
        return list;

    }

    private void insertBeforeTitle(List<DrawerGridItems> list, DrawerGridItems item, String title) {
        for (int i = 0; i < list.size(); i++) {
            if (title.equalsIgnoreCase(list.get(i).getTitle())) {
                list.add(i, item);
                return;
            }
        }
        list.add(item);
    }

    @Override
    public void onStaffMembersFetched(List<StaffMember> staffMembers) {
        mView.removeProgress();
        mView.setFamilyMembersToRecyclerView(replaceConciergeWithCall(staffMembers));
    }

    @Override
    public void onHotelDetailsFetched(HotelDetailsData hotelDetails) {
        mView.removeProgress();
        mView.setHotelDetails(hotelDetails);
    }

    @Override
    public void onStaffMembersNotFound() {
        mView.removeProgress();
        mView.setFamilyMembersToRecyclerView(setUpStaticStaffList());

    }

    private List<StaffMember> setUpStaticStaffList() {
        List<StaffMember> staffMembers = new ArrayList<>();
        staffMembers.add(new StaffMember(mView.getString(R.string.Reception), R.drawable.amadeus_reception));
        staffMembers.add(new StaffMember(mView.getString(R.string.room_service), R.drawable.amadeus_room_service));
        staffMembers.add(new StaffMember(mView.getString(R.string.Maintenance), R.drawable.amadeus_house_keeping));
        staffMembers.add(new StaffMember(mView.getString(R.string.Spa), R.drawable.amadeus_spa));
        staffMembers.add(new StaffMember(mView.getString(R.string.call), R.drawable.ic_call));
        return staffMembers;
    }

    private List<StaffMember> replaceConciergeWithCall(List<StaffMember> staffMembers) {
        String conciergeLabel = mView.getString(R.string.Concierge);
        String callLabel = mView.getString(R.string.call);
        for (int i = 0; i < staffMembers.size(); i++) {
            StaffMember member = staffMembers.get(i);
            if (member.getName() != null && member.getName().equalsIgnoreCase(conciergeLabel)) {
                staffMembers.set(i, new StaffMember(callLabel, R.drawable.ic_call));
                return staffMembers;
            }
        }
        staffMembers.add(new StaffMember(callLabel, R.drawable.ic_call));
        return staffMembers;
    }

    /*---------------------------------------------------- Common ---------------------------------------------------*/

    @Override
    public void onAPICallError(String error) {
        mView.removeProgress();
        if (error != null && error.toLowerCase().contains("timeout")) {
            mView.setFamilyMembersToRecyclerView(setUpStaticStaffList());
            return;
        }
        mView.onAPIError(error);
    }
    @Override
    public void registerBroadcastReceivers() {
        /*mView.getParentActivity().registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.HomeFragment.HOME_INTENT));*/
    }

    @Override
    public void unRegisterBroadcastReceivers() {
        /*mView.getParentActivity().unregisterReceiver(dateBroadcastReceiver);*/

    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}
