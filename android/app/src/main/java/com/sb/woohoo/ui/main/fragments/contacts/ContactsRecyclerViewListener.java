package com.sb.woohoo.ui.main.fragments.contacts;

import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;

/**
 * Listener for RemoteAlertModel Clicked on RecyclerView.
 *
 * @author Ritwik Jamuar.
 */
public interface ContactsRecyclerViewListener {
    /**
     * Invokes when a Family Member is clicked.
     *
     * @param member Instance of {@link StaffMember} representing the details of Family Member.
     */
    void onFamilyMemberClicked(StaffMember member);

    /**
     * Invokes when a Contact is clicked.
     *
     * @param contact Instance of {@link ContactsData} representing the details of Contact.
     */
    void onContactClicked(ContactsData contact);

    void onAllContactsClicked();
}