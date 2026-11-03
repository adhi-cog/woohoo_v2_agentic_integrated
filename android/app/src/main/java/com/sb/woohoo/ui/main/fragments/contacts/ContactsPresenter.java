package com.sb.woohoo.ui.main.fragments.contacts;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

/**
 * Presenter for {@link ContactsFragment}.
 *
 * @author Ritwik Jamuar.
 */
public class ContactsPresenter
        extends BasePresenter
        implements ContactsContract.Actions {
    private ContactsContract.View mView;
    private ContactsRepository mRepository;

    @Inject
    public ContactsPresenter(ContactsContract.View view, ContactsRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(ContactsPresenter.this);
    }

    /*------------------------------------------------- Fetch Family Members ---------------------------------------------*/

    /**
     * Consults the repository to fetch Family Members of User.
     */
    public void fetchFamilyMembers() {
        mView.showProgress();
        mRepository.fetchFamilyContacts();
    }

    @Override
    public void onStaffMembersFetched(List<StaffMember> staffMembers) {
        mView.removeProgress();
        mView.setFamilyMembersToRecyclerView(staffMembers);
    }

    @Override
    public void onStaffMembersNotFound() {
        mView.removeProgress();
        mView.showFamilyMemberNotFound();
    }

    /*------------------------------------------------- Fetch Contacts ---------------------------------------------*/

    /**
     * Consults the repository to fetch Contacts of User.
     */
    void fetchContacts() {
        mView.showProgress();
        mRepository.fetchContacts();
    }

    @Override
    public void onContactsFetched(List<ContactsData> contacts) {
        mView.removeProgress();
        mView.setContactsToRecyclerView(contacts);
    }

    @Override
    public void onContactsNotFound() {
        mView.removeProgress();
        mView.showContactsNotFound();
    }

    /*---------------------------------------------------- Common ---------------------------------------------------*/

    @Override
    public void onAPICallError(String error) {
        mView.removeProgress();
        mView.onAPIError(error);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        fetchFamilyMembers();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        mRepository.repositoryCleanUps();
    }
}