package com.sb.woohoo.ui.main.fragments.contactsprofile;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

/**
 * Presenter for {@link ContactsProfileFragment}.
 *
 * @author Ritwik Jamuar.
 */
public class ContactsProfilePresenter
        extends BasePresenter
        implements ContactsProfileContracts.Actions {
    private ContactsProfileContracts.View mView; // View.
    private ContactsProfileRepository mRepository; // Repository.

    public ContactsProfilePresenter(ContactsProfileFragment view, ContactsProfileRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        repository.onAttach(this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        getContactByName(intentInfo.getParameters().get("contact").toString());
    }

    public void getListOfAccessibleHubs(String contactId) {
        mRepository.getListOfAccessibleHubs(contactId);
    }

    public void getContactByName(String contactId) {
        mRepository.getContactByName(contactId);
    }

    public void getAudioListOfAccessibleHubs(String contactId) {
        mRepository.getAudioListOfAccessibleHubs(contactId);
    }

    @Override
    public void onAccessibleHubsEmpty() {
        mView.onEmptyHubs();
    }

    @Override
    public void onStaffMemberNotFound() {
       mView.onStaffMemberNotFound();
    }

    @Override
    public void onAccessibleHubsFound(List<Hub> hubList) {
        mView.onListOfHubsFetched(hubList);
    }

    @Override
    public void onAudioAccessibleHubsFound(List<Hub> hubList) {
        mView.onAudioListOfHubsFetched(hubList);
    }

    @Override
    public void onStaffMemberFound(StaffMember staffMember) {
        mView.onStaffMemberFetched(staffMember);
    }

    @Override
    public void onAPICallError(String errorMessage) {
        mView.onError(errorMessage);
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
    }


}