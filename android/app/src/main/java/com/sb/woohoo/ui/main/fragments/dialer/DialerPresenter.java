package com.sb.woohoo.ui.main.fragments.dialer;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Presenter for {@link DialerFragment}.
 *
 * @author Ritwik Jamuar.
 */
public class DialerPresenter
        extends BasePresenter
        implements DialerContract.Actions {
    private DialerContract.View mView; // View.
    private DialerRepository mRepository; // Repository.

    @Inject
    public DialerPresenter(DialerContract.View view, DialerRepository repository) {
        this.mView = view;
        this.mRepository = repository;
        this.mRepository.onAttach(DialerPresenter.this);
    }

    @Override
    public void processActions(IntentInfo intentInfo) {

    }
    @Override
    public void onAPICallError(String error) {
        mView.removeProgress();
        mView.onAPIError(error);
    }

    void fetchStaffMembers() {
        mView.showProgress();
        mRepository.fetchHotelContacts();
    }

    @Override
    public void onStaffMembersFetched(List<StaffMember> staffMembers) {
        mView.removeProgress();
        mView.setFamilyMembersToRecyclerView(filterMembersWithPhoneNumber(staffMembers));
    }

    @Override
    public void onStaffMembersNotFound() {
        mView.removeProgress();
        mView.setFamilyMembersToRecyclerView(Collections.emptyList());

    }

    private List<StaffMember> filterMembersWithPhoneNumber(List<StaffMember> staffMembers) {
        List<StaffMember> membersWithPhoneNumber = new ArrayList<>();
        if (staffMembers == null) {
            return membersWithPhoneNumber;
        }
        for (StaffMember member : staffMembers) {
            String phoneNumber = member.getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                membersWithPhoneNumber.add(member);
            }
        }
        return membersWithPhoneNumber;
    }

}
