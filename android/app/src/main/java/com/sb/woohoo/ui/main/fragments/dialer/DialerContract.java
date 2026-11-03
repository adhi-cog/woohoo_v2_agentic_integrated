package com.sb.woohoo.ui.main.fragments.dialer;

import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsData;

import java.util.List;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link DialerFragment}.
 *
 * @author Ritwik Jamuar
 */
public interface DialerContract {
    interface View {
        void showProgress();

        void removeProgress();

        void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers);


        void onAPIError(String errorMessage);
    }

    interface Actions {

        void onStaffMembersFetched(List<StaffMember> staffMembers);

        void onStaffMembersNotFound();

        void onAPICallError(String error);
    }
}