package com.sb.woohoo.ui.main.fragments.contacts;

import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CustomItemDecorator;
import com.sb.woohoo.custom.RecyclerViewEmptySupport;
import com.sb.woohoo.databinding.FragmentContactsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.contacts.di.ContactsComponent;
import com.sb.woohoo.ui.main.fragments.contacts.di.ContactsModule;
import com.sb.woohoo.ui.main.fragments.contacts.di.DaggerContactsComponent;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Shows User Contacts ( both Family and Normal Contacts ).
 *
 * @author Ritwik Jamuar.
 */
public class ContactsFragment
        extends FeatureFragment<MainActivity>
        implements ContactsContract.View,
        ContactsRecyclerViewListener, PopFragment {
    // Constant.
    private static String KEY_CONTACT_INFO = "contactInfo";
    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;

    // Adapters.
    @Inject
    FamilyContactsRecyclerAdapter mRAFamily;
    @Inject
    ContactsRecyclerViewAdapter mRAContacts;
    // Presenter
    @Inject
    ContactsPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;

    private FragmentContactsBinding binding;
    public ContactsFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link ContactsFragment} using the provided parameters.
     *
     * @return A new instance of {@link ContactsFragment}.
     */
    public static ContactsFragment newInstance(IntentInfo<ContactsData> contactsDataIntentInfo) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putSerializable("contact", contactsDataIntentInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return ContactsFragment.class.getSimpleName();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects the {@link ContactsFragment} to {@link ContactsComponent}.
     */
    @Override
    public void initDagger() {
        ContactsComponent component = DaggerContactsComponent
                .builder()
                .contactsModule(new ContactsModule(ContactsFragment.this)).build();

        component.injectContactsFragment(ContactsFragment.this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link ContactsFragment}.
     */
    @Override
    public void bindViews(View view) {
        setUpRecyclerViews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.Contacts(),  null);
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callFetchFamilyMembersAPI();
        callFetchContactsAPI();
        binding.fragmentCommonToolbar.commonToolbarTitle.setText(R.string.fragment_contacts_title);
        attachParent((MainActivity) getActivity());
        binding.interceptorLayout.setContext(getParentActivity());

        binding.fragmentCommonToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        featureFragmentCleanUps();
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    /**
     * Tells the {@link ContactsPresenter} to make API call to fetch list of Family Members.
     */
    private void callFetchFamilyMembersAPI() {
        mPresenter.fetchFamilyMembers();
    }

    /**
     * Tells the {@link ContactsPresenter} to make API call to fetch list of Contacts.
     */
    private void callFetchContactsAPI() {
        mPresenter.fetchContacts();
    }

    /**
     * Configures the {@link RecyclerViewEmptySupport} for Use.
     */
    private void setUpRecyclerViews() {
        // Set Layout Manager and Recycler View Adapter to Family Contacts Recycler View.
        LinearLayoutManager familyLLM = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.fragmentContactsRecyclerViewFamily.setLayoutManager(familyLLM);
        binding.fragmentContactsRecyclerViewFamily.setEmptyView(binding.fragmentContactsTextViewNoFamilyMembers);
        binding.fragmentContactsRecyclerViewFamily.setAdapter(mRAFamily);

        // Set Layout Manager and Recycler View Adapter to Contacts Recycler View.
        LinearLayoutManager contactLLM = new LinearLayoutManager(getActivity());
        binding.fragmentContactsRecyclerViewAllContacts.setLayoutManager(contactLLM);
        binding.fragmentContactsRecyclerViewAllContacts.addItemDecoration(new CustomItemDecorator(getResources().getDrawable(R.drawable.line_divider)));
        binding.fragmentContactsRecyclerViewAllContacts.setEmptyView(binding.fragmentContactsTextViewNoContacts);
        binding.fragmentContactsRecyclerViewAllContacts.setAdapter(mRAContacts);
    }

    @Override
    public void onFamilyMemberClicked(StaffMember member) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(member));

        // FIXME : Remove redundant code.
        ContactsProfileFragment fragment = new ContactsProfileFragment();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    /**
     * Builds POJO Model {@link ContactInfo} to be fed to {@link com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment}
     * for showing details of a Contact.
     *
     * @param member {@link StaffMember}.
     * @return Instance of {@link ContactInfo}.
     */
    private ContactInfo convertToContactInfo(StaffMember member) {
        ContactInfo info = new ContactInfo();
        info.setId(String.valueOf(member.getId()));
        info.setEmail(member.getEmail());
        info.setImageURL(member.getImageUrl());
        info.setName(member.getName());
        info.setUser_id(String.valueOf(member.getUserId()));
        info.setLiveFeedAccess(member.getHub() != null ? member.getHub().getLiveFeedAccess() : true);
        return info;
    }

    @Override
    public void onContactClicked(ContactsData contact) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CONTACT_INFO, convertToContactInfo(contact));
        ContactsProfileFragment fragment = new ContactsProfileFragment();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    @Override
    public void onAllContactsClicked() {

    }

    /**
     * Builds POJO Model {@link ContactInfo} to be fed to {@link com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment}
     * for showing details of a Contact.
     *
     * @param contact {@link ContactsData}.
     * @return Instance of {@link ContactInfo}.
     */
    private ContactInfo convertToContactInfo(ContactsData contact) {
        ContactInfo info = new ContactInfo();
        info.setId(contact.getId());
        info.setEmail(contact.getEmail());
        info.setImageURL(IMAGE_BASE_URL.concat(contact.getImageUrl()));
        info.setName(contact.getName());
        info.setUser_id(contact.getUserId());
        info.setPhoneNumbers(contact.getPhoneNos());
        info.setLiveFeedAccess(contact.getLiveFeedAccess());
        return info;
    }

    @Override
    public void showProgress() {
        binding.fragmentContactsProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeProgress() {
        binding.fragmentContactsProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setFamilyMembersToRecyclerView(List<StaffMember> staffMembers) {
        toggleFamilyMembersVisibility(true);
        mRAFamily.updateFamilyMembers(staffMembers);
    }

    @Override
    public void showFamilyMemberNotFound() {
        toggleFamilyMembersVisibility(false);
        callFetchContactsAPI();
    }

    private void toggleFamilyMembersVisibility(boolean isFamilyMemberToBeVisible) {
        if (isFamilyMemberToBeVisible) {
            binding.fragmentContactsRecyclerViewFamily.setVisibility(View.VISIBLE);
            binding.fragmentContactsTextViewNoFamilyMembers.setVisibility(View.GONE);
        } else {
            binding.fragmentContactsRecyclerViewFamily.setVisibility(View.GONE);
            binding.fragmentContactsTextViewNoFamilyMembers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setContactsToRecyclerView(List<ContactsData> contacts) {
        toggleContactsVisibility(true);
        mRAContacts.updateContactList(contacts);
    }

    @Override
    public void showContactsNotFound() {
        toggleContactsVisibility(false);
    }

    private void toggleContactsVisibility(boolean isContactToBeVisible) {
        if (isContactToBeVisible) {
            binding.fragmentContactsRecyclerViewAllContacts.setVisibility(View.VISIBLE);
            binding.fragmentContactsTextViewNoContacts.setVisibility(View.INVISIBLE);
        } else {
            binding.fragmentContactsRecyclerViewAllContacts.setVisibility(View.INVISIBLE);
            binding.fragmentContactsTextViewNoContacts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAPIError(String errorMessage) {
        MethodUtils.showToastMessage(getActivity(), errorMessage);
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onParentClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }
}