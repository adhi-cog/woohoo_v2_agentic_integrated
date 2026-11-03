package com.sb.woohoo.ui.main.fragments.devices.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentDeviceDiscoveryBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.devices.discovery.di.DaggerDeviceDiscoveryComponent;
import com.sb.woohoo.ui.main.fragments.devices.discovery.di.DeviceDiscoveryComponent;
import com.sb.woohoo.ui.main.fragments.devices.discovery.di.DeviceDiscoveryModule;
import com.sb.woohoo.ui.main.fragments.devices.launcher.DeviceLauncherFragment;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

public class DeviceDiscoveryFragment extends FeatureFragment<MainActivity> implements DeviceDiscoveryContract.View {

    private final String FEATURE_CATEGORY = Categories.Devices.COMMAND_DEVICES_LIGHTS;

    @Inject
    DeviceDiscoveryPresenter mPresenter;

    @Inject
    LightDiscoveryAdapter mAdapter;

    @Inject
    AnalyticsHelper helper;
    private FragmentDeviceDiscoveryBinding binding;

    public DeviceDiscoveryFragment() {
    }

    public static DeviceDiscoveryFragment newInstance(IntentInfo intentInfo) {
        DeviceDiscoveryFragment fragment = new DeviceDiscoveryFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    public static DeviceDiscoveryFragment newInstance() {
        return new DeviceDiscoveryFragment();
    }

    @Override
    public String createTag() {
        return DeviceDiscoveryFragment.class.getSimpleName();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.DeviceDiscovery(),  null);
        binding = FragmentDeviceDiscoveryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        DeviceDiscoveryComponent component = DaggerDeviceDiscoveryComponent.builder()
                .deviceDiscoveryModule(new DeviceDiscoveryModule(this))
                .build();

        component.injectFragment(this);
    }

    @Override
    public void bindViews(View view) {
        initializeRecyclerView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void initializeRecyclerView() {
        binding.fragmentDeviceDiscoverList.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        binding.fragmentDeviceDiscoverList.setHasFixedSize(true);
        binding.fragmentDeviceDiscoverList.setAdapter(mAdapter);
        binding.fragmentDeviceDiscoverList.setEmptyView(binding.fragmentDeviceDiscoverLayout);
        binding.fragmentDeviceDiscoveryImgBack.setOnClickListener(view -> onBackPress());
    }

    @Override
    public void updateLightsAdapter(List<Light> devices) {
        mAdapter.updateDevices(devices);
    }

    @Override
    public void showErrorMessage(String message) {
        MethodUtils.showToastMessage(getContext(), message);
    }

    //This method is used when it is triggered by voice
    @Override
    public void onRegisterFinished() {
        getParentActivity().switchFragmentUsingAdd(DeviceLauncherFragment.newInstance());
    }

    //This method is used when it is triggered by clicking a list item
    @Override
    public void onItemClicked(Light light) {
        mPresenter.registerLight(light);
    }

    void clickOnRootView() { //Do nothing here
    }

    void onBackPress() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }
}
