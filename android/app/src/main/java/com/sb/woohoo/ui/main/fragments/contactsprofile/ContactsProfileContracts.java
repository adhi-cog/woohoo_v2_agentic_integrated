package com.sb.woohoo.ui.main.fragments.contactsprofile;

import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;

import java.util.List;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link ContactsProfileFragment}.
 *
 * @author Ritwik Jamuar
 */
public interface ContactsProfileContracts {
    interface View {

        void onError(String errorMessage);

        void onEmptyHubs();

        void onStaffMemberNotFound();

        void onListOfHubsFetched(List<Hub> hubList);

        void onAudioListOfHubsFetched(List<Hub> hubList);

        void onStaffMemberFetched(StaffMember staffMember);

    }

    interface Actions {

        void onAPICallError(String errMessage);

        void onAccessibleHubsEmpty();

        void onStaffMemberNotFound();

        void onAccessibleHubsFound(List<Hub> hubList);

        void onAudioAccessibleHubsFound(List<Hub> hubList);

        void onStaffMemberFound(StaffMember staffMember);
    }
}