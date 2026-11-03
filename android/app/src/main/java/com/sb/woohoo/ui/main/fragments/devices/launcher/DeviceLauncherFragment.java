package com.sb.woohoo.ui.main.fragments.devices.launcher;

import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.RenameDialog;
import com.sb.woohoo.databinding.FragmentDeviceLauncherBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.devices.discovery.DeviceDiscoveryFragment;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.PhilipsHueFragment;
import com.sb.woohoo.ui.main.fragments.devices.launcher.di.DaggerDeviceLauncherComponent;
import com.sb.woohoo.ui.main.fragments.devices.launcher.di.DeviceLauncherComponent;
import com.sb.woohoo.ui.main.fragments.devices.launcher.di.DeviceLauncherModule;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

public class DeviceLauncherFragment extends FeatureFragment<MainActivity> implements DeviceLauncherContract.View{

    private final String FEATURE_CATEGORY = Categories.Devices.COMMAND_DEVICES_LIGHTS;


    @Inject
    AvailableDeviceAdapter mAdapter;

    @Inject
    DeviceLauncherPresenter mPresenter;

    @Inject
    AnalyticsHelper helper;

    private RenameDialog dialogRename;

    private int position;
    private FragmentDeviceLauncherBinding binding;

    public DeviceLauncherFragment() { /*Required empty public constructor*/ }

    public static DeviceLauncherFragment newInstance(IntentInfo intentInfo) {
        DeviceLauncherFragment fragment = new DeviceLauncherFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    public static DeviceLauncherFragment newInstance() {
        return new DeviceLauncherFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return DeviceLauncherFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.DeviceLauncher(),  null);
        binding = FragmentDeviceLauncherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        DeviceLauncherComponent component = DaggerDeviceLauncherComponent.builder()
                .deviceLauncherModule(new DeviceLauncherModule(this))
                .build();

        component.injectFragment(this);
    }

    @Override
    public void bindViews(View view) {
        dialogRename = new RenameDialog(requireContext(), R.style.DialogTheme);
        initViews();
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

    private void initViews() {
        binding.fragmentCommonToolbar.commonToolbarTitle.setText(getString(R.string.fragment_devices));
        binding.fragmentDeviceLauncherRecyclerViewDeviceList.setAdapter(mAdapter);
        binding.fragmentDeviceLauncherRecyclerViewDeviceList.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        binding.fragmentDeviceLauncherRecyclerViewDeviceList.setEmptyView(binding.fragmentDeviceLauncherLinearLayoutNoDevice);
        binding.fragmentCommonToolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.fragmentDeviceLauncherTextViewDiscover.setOnClickListener(view -> onDiscoverClicked());
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onDiscoverClicked() {
        getParentActivity().switchFragmentUsingAdd(DeviceDiscoveryFragment.newInstance());
    }

    void onClickRootView() { /* Do nothing here  */ }

    @Override
    public void updateAvailableDevicesToAdapter(List<Light> lights) {
        if (lights != null) {
            mAdapter.addList(lights);
        }

        if (!(mAdapter.getItemCount() > 0)) {
            binding.fragmentDeviceLauncherLinearLayoutNoDevice.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
        hideProgress();
    }

    @Override
    public void showEmptyLayout() {
        hideProgress();
        binding.fragmentDeviceLauncherLinearLayoutNoDevice.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {
        if (binding.fragmentDevicesProgressBar.getVisibility() == View.GONE) {
            binding.fragmentDevicesProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (binding.fragmentDevicesProgressBar.getVisibility() == View.VISIBLE) {
            binding.fragmentDevicesProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getContext(), message);
    }

    @Override
    public void onItemClicked(IntentInfo intentInfo) {
        /*getParentActivity ().onSwitchFragment ( Constants.Fragments.FRAGMENT_DEVICES_CONTROL, intentInfo, false );*/
        fragmentCall(intentInfo);
    }

    private void fragmentCall(IntentInfo intentInfo){
        getParentActivity().switchFragmentUsingAdd(PhilipsHueFragment.newInstance(intentInfo));

    }
    @Override
    public void onClickLightSetting(final Light light) {

        dialogRename.show();

        dialogRename.binding.buttonRename.setOnClickListener(v -> {
            position = mAdapter.getLightList().indexOf(light);
            String newName = dialogRename.binding.renameEdittext.getText().toString();
            mPresenter.updateLightName(light, newName);
            MethodUtils.hideSoftInputKeyboard(dialogRename.binding.renameEdittext, getContext());
            dialogRename.dismiss();
        });

        dialogRename.binding.buttonCancel.setOnClickListener(v -> {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = getParentActivity().getWindow();
            if (window != null) {
                lp.copyFrom(window.getAttributes());
                lp.gravity = Gravity.TOP;
                //This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                setActivityFullScreen(window);

                getParentActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            MethodUtils.hideSoftInputKeyboard(dialogRename.binding.renameEdittext, getContext());
            dialogRename.dismiss();
        });

        dialogRename.binding.buttonDelete.setOnClickListener(v -> {
            position = mAdapter.getLightList().indexOf(light);
            mPresenter.deleteLight(light);
            MethodUtils.hideSoftInputKeyboard(dialogRename.binding.renameEdittext, getContext());
            dialogRename.dismiss();
        });
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    void updateItemFromRecyclerView(Light light, int pos) {
        mAdapter.updateItemInAdapter(light, pos);
    }

    void deleteItemFromRecyclerView(Light light) {
        mAdapter.deleteItemInAdapter(position);
    }

    void updateItemFromRecyclerView(Light light) {
        mAdapter.updateItemInAdapter(light, position);
    }

    public void callPhilipsFrag(IntentInfo lightIntentData) {
        fragmentCall(lightIntentData);
    }
}