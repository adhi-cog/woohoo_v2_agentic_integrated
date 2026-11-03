package com.sb.woohoo.ui.main.fragments.livefeedowndevice;

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
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RecyclerViewDivider;
import com.sb.woohoo.databinding.FragmentOwnDeviceBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.di.DaggerOwnDeviceComponent;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.di.OwnDeviceComponent;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.di.OwnDeviceModule;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyData;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

public class LiveFeedOwnDeviceFragment
        extends FeatureFragment<MainActivity>
        implements OwnDeviceContracts.Views, OwnerHubListAdapter.HubClickListener {

    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;


    @Inject
    OwnerHubListAdapter ownerHubListAdapter;

    @Inject
    OwnDevicePresenter ownDevicePresenter;

    @Inject
    AnalyticsHelper helper;
    private FragmentOwnDeviceBinding binding;

    public LiveFeedOwnDeviceFragment() {
        /*
         Required empty public constructor
         **/
    }

    /**
     * Use this factory method to create a new instance of {@link LiveFeedOwnDeviceFragment} using the provided parameters.
     *
     * @return A new instance of {@link LiveFeedOwnDeviceFragment}.
     */

    public static LiveFeedOwnDeviceFragment newInstance() {
        return new LiveFeedOwnDeviceFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        OwnDeviceComponent ownDeviceComponent = DaggerOwnDeviceComponent
                .builder()
                .ownDeviceModule(new OwnDeviceModule(this)).build();

        ownDeviceComponent.injectOwnDeviceFragment(this);
    }

    @Override
    public void bindViews(View view) {
        setUpRecyclerViews();
        binding.commonToolbar.commonToolbarTitle.setText(R.string.fragment_own_device_fragment);
        binding.interceptorLayout.setContext(getParentActivity());
    }

    @Override
    public String createTag() {
        return LiveFeedOwnDeviceFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.LiveFeedOwnDevice(),  null);
        binding = FragmentOwnDeviceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchListOfHubs();
        setClickEvents();
    }

    @Override
    public BasePresenter getPresenter() {
        return ownDevicePresenter;
    }


    private void setUpRecyclerViews() {
        // Set Layout Manager and Recycler View Adapter to Hub List Recycler View.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.fragmentContentContacts.fragmentRecyclerViewOwnDevices.setLayoutManager(layoutManager);
        binding.fragmentContentContacts.fragmentRecyclerViewOwnDevices.setAdapter(ownerHubListAdapter);
        binding.fragmentContentContacts.fragmentRecyclerViewOwnDevices.addItemDecoration(new RecyclerViewDivider(getResources().getDrawable(R.drawable.line_divider)));

    }

    private void fetchListOfHubs() {
        ownDevicePresenter.fetchListOfHubs();

    }

    @Override
    public void showProgress() {
        binding.fragmentContentContacts.fragmentOwnDeviceProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeProgress() {
        binding.fragmentContentContacts.fragmentOwnDeviceProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setHubsToRecyclerView(List hubList) {
        ownerHubListAdapter.updateList(hubList);
        toggleListOfHubsVisibility(true);
    }

    @Override
    public void showHubsNotFound() {
        toggleListOfHubsVisibility(false);
    }

    private void toggleListOfHubsVisibility(boolean isHubsVisible) {
        if (isHubsVisible) {
            binding.fragmentContentContacts.tvEmptyView.setVisibility(View.GONE);
            binding.fragmentContentContacts.tvTitleText.setVisibility(View.VISIBLE);
            binding.fragmentContentContacts.fragmentRecyclerViewOwnDevices.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentContentContacts.tvEmptyView.setVisibility(View.VISIBLE);
            binding.fragmentContentContacts.tvTitleText.setVisibility(View.GONE);
            binding.fragmentContentContacts.fragmentRecyclerViewOwnDevices.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAPIError(Object errorMessage) {
        String msgString;
        if (errorMessage instanceof Integer)
            msgString = getString((Integer) errorMessage);
        else
            msgString = (String) errorMessage;
        MethodUtils.showToastMessage(getActivity(), msgString);
    }

    private void setClickEvents() {
        binding.commonToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.ivAllContacts.setOnClickListener(view -> onContactsClicked());
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onContactsClicked() {
        ContactsFragment fragment = new ContactsFragment();
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    /************************{ADAPTER_CALLBACK}****************************************/
    @Override
    public void onItemClicked(Hub hub) {
        if (getParentActivity() != null && hub != null) {
            getParentActivity().startLiveFeedToOwnHub(hub);
        }
    }

    @Override
    public void onOtherHubItemClicked(OtherFamilyData otherFamilyData) {
        ownDevicePresenter.getHubListOfOtherFamily(otherFamilyData.getId());
    }

    @Override
    public void showListOfHubs(List<OthersHubData> othersHubListModelData) {
        LoggerWrapper.i("ListOfHubs", "showListOfHubs: " + othersHubListModelData.toString());
    }
}
