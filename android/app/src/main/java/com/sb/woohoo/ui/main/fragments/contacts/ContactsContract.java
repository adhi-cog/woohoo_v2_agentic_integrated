package com.sb.woohoo.ui.main.fragments.contacts;

import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;

import java.util.List;

/**
 * MVP Interface for Providing Methods across Model, View and Presenter in {@link ContactsFragment}.
 *
 * @author Ritwik Jamuar
 */
public interface ContactsContract {
    interface View {
        void showProgress();

        void removeProgress();

        void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers);

        void showFamilyMemberNotFound();

        void setContactsToRecyclerView(List<ContactsData> contacts);

        void showContactsNotFound();

        void onAPIError(String errorMessage);
    }

    interface Actions {
        void onStaffMembersFetched(List<StaffMember> staffMembers);

        void onStaffMembersNotFound();

        void onContactsFetched(List<ContactsData> contacts);

        void onContactsNotFound();

        void onAPICallError(String error);
    }
}