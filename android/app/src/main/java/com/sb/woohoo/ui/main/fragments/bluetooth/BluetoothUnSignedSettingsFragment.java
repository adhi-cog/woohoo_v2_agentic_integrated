package com.sb.woohoo.ui.main.fragments.bluetooth;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.BuildConfig;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.custom.RequestPermissionHandler;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentBluetoothSettingsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.bluetooth.di.BluetoothSettingsComponent;
import com.sb.woohoo.ui.main.fragments.bluetooth.di.BluetoothSettingsModule;
import com.sb.woohoo.ui.main.fragments.bluetooth.di.DaggerBluetoothSettingsComponent;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Header;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.sb.woohoo.utils.woohoblutooth.BluetoothA2DPRequester;
import com.sb.woohoo.utils.woohoblutooth.BluetoothBroadcastReceiver;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;


public class BluetoothUnSignedSettingsFragment extends FeatureFragment<MainActivity> implements RVBluetoothAdapter.BluetoothDeviceClickListener, NonPopFragment, BluetoothBroadcastReceiver.Callback {

    private final static int TURN_ON_BLUETOOTH = 1;
    private final static int DISCOVERABLE_BLUETOOTH_DURATION = 60; // In seconds
    private static final long AUTO_SCAN_DELAY = 1000 * 10;
    private String TAG = BluetoothUnSignedSettingsFragment.class.getSimpleName();
    private final static UUID DEFAULT_UUID = UUID.fromString("bf24c6d0-5f9f-11ed-9b6a-0242ac120002");
    private CompoundButton.OnCheckedChangeListener blueListener;

    private static final int BLUETOOTH_VISIBILITY_DURATION = 60; // Duration in seconds

    private static CountDownTimer visibilityTimer;

    @Inject
    RVBluetoothAdapter rvBluetoothAdapter;
    @Inject
    AnalyticsHelper helper;

    private FragmentBluetoothSettingsBinding binding;

    private BluetoothAdapter mBlueToothAdapter;
    private List listOfBluetoothDevices = new ArrayList(0);

    private Handler handler = new Handler();
    private BluetoothA2DPRequester bluetoothA2DPRequester;
    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;

    private static final int REQUEST_PERMISSION = 6001;

    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    /**
     * Broadcast Receiver that detects when other devices are available and checks for pairing status
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_FOUND:
                        onDeviceFound(intent);
                        break;
                    case ACTION_BOND_STATE_CHANGED:
                        onDeviceStateChanged(intent);
                        break;
                    case ACTION_SCAN_MODE_CHANGED:
                        LoggerWrapper.d(TAG, "Discoverable off: Your phone is not visible to nearby devices...");
//                        binding.bluetoothDiscoverableText.setVisibility(View.GONE);
                        // mDiscoverableImg.setImageResource(R.drawable.ic_bluetooth);
                      /*  if(mBlueToothAdapter.isEnabled()) {
                            MethodUtils.showToastMessage(getParentActivity(),
                                    "Click on discoverable to make your device visible to others");
                        }*/
                        break;

                    case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                            handleParingReq(context, intent);
                        }
                        break;
                }
            }
        }
    };

    private void handleParingReq(Context context, Intent intent)
    {
        try {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String pin = Integer.toString(
                    intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));

            assert device != null;
            byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            {
                String deviceName = String.format("%s (%s)", device.getName(), device.getAddress());
                LoggerWrapper.d(getTag(), deviceName);
                LoggerWrapper.d(getTag(), String.format("Pin Code is %s", pin));
                device.setPin(pinBytes);
                device.setPairingConfirmation(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeviceFound(Intent intent) {
        LoggerWrapper.d(TAG, " onDevice Found : ");
        /**
         * Stop Scan Animation
         */
        setStopScanAnimation();
        /**
         * retrieve the BluetoothDevice object and
         * its EXTRA_DEVICE field, which contains information about the device’s characteristics and capabilities
         */
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        /**
         * You’ll usually want to display information about any devices you discover, and show it in a list
         */
        updateAdapter(device);
    }

    private void requestPermissions() {
        LoggerWrapper.d(TAG, "Inside requestPermissions : called");
        RequestPermissionHandler mRequestPermissionHandler = new RequestPermissionHandler();
        mRequestPermissionHandler.requestPermission(requireActivity(), ANDROID_12_BLE_PERMISSIONS, REQUEST_PERMISSION, mRequestPermissionListener);
    }

    private boolean isPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int lBLUETOOTH_SCAN = ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_SCAN);
            int lBLUETOOTH_CONNECT = ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT);
            return lBLUETOOTH_SCAN == PackageManager.PERMISSION_GRANTED && lBLUETOOTH_CONNECT == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


    private void onDeviceStateChanged(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            switch (device.getBondState()) {
                case BOND_BONDED:
                case BOND_BONDING:
                case BOND_NONE:
                    //rvBluetoothAdapter.updateDeviceStatus(device);
                    scanForDevices();
                    break;
            }
        }
    }


    public static BluetoothUnSignedSettingsFragment newInstance() {
        return new BluetoothUnSignedSettingsFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        BluetoothSettingsComponent bluetoothSettingsComponent = DaggerBluetoothSettingsComponent.builder().bluetoothSettingsModule(new BluetoothSettingsModule(this)).build();
        bluetoothSettingsComponent.injectBluetoothUnSignedSettingsFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.bluetooth));
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
        setBluetoothAdapter();
        String lDeviceName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DEVICE_NAME, "");
        if(lDeviceName.isEmpty())
        {
            if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                binding.deviceNameText.setText(mBlueToothAdapter != null ? mBlueToothAdapter.getName() : "");
            }else{
                if(mBlueToothAdapter != null)
                {
                    mBlueToothAdapter.setName("Woohoo Device");
                    binding.deviceNameText.setText(mBlueToothAdapter != null ? mBlueToothAdapter.getName() : "Woohoo Device");
                }
            }
        } else{
            if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if(mBlueToothAdapter != null)
                {
                    mBlueToothAdapter.setName(lDeviceName);
                }
            }
            binding.deviceNameText.setText(lDeviceName);
        }
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 989);
    }

    private void setBluetoothAdapter() {
        BluetoothManager lBluetoothManager = (BluetoothManager) getParentActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (lBluetoothManager != null) {
            mBlueToothAdapter = lBluetoothManager.getAdapter();
        }

        if (mBlueToothAdapter == null) {
            mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.Bluetooth(), null);
        binding = FragmentBluetoothSettingsBinding.inflate(inflater, container, false);
        requestPermissions();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUIViews();
        registerListener();

        if (isPermission()) {
            scanForDevices();
        }
    }

    private void requestBluetoothPrivPer()
    {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.BLUETOOTH_PRIVILEGED)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(requireContext(),
                    "No BLUETOOTH_PRIVILEGED Permission", Toast.LENGTH_LONG).show();
            RequestPermissionHandler lRequestHandler = new RequestPermissionHandler();
            RequestPermissionHandler.RequestPermissionListener lRequestPermissionListener =
                    new RequestPermissionHandler.RequestPermissionListener() {
                @Override
                public void onSuccess() {
                    LoggerWrapper.d(TAG, "Inside requestResult : onSuccess");
                }

                @Override
                public void onFailed() {
                    LoggerWrapper.e(TAG, "Inside requestResult : onFailed");
                }
            };

            lRequestHandler.requestPermission(requireActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED},
                    REQUEST_PERMISSION, lRequestPermissionListener);
        } else {
            Toast.makeText(requireContext(),
                    "YES BLUETOOTH_PRIVILEGED Permission Granted", Toast.LENGTH_LONG).show();
        }
    }

    private void registerListener() {
        registerReceiver();
        registerBluetoothReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestPermissionListener = null;
        listOfBluetoothDevices = null;
        mBlueToothAdapter = null;
        bluetoothA2DPRequester = null;
        mBluetoothBroadcastReceiver = null;
        unRegisterListener();

    }

    private void unRegisterListener() {
        try {
            if(broadcastReceiver != null){
                getParentActivity().unregisterReceiver(broadcastReceiver);
            }
            if(mBluetoothBroadcastReceiver != null){
                getParentActivity().unregisterReceiver(mBluetoothBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RequestPermissionHandler.RequestPermissionListener mRequestPermissionListener = new RequestPermissionHandler.RequestPermissionListener() {
        @Override
        public void onSuccess() {
            LoggerWrapper.d(TAG, "Inside requestResult : onSuccess");
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                requestBluetoothPrivPer();
            }
            enableScanning(true);
        }

        @Override
        public void onFailed() {
            LoggerWrapper.e(TAG, "Inside requestResult : onFailed");
            if (BuildConfig.DEBUG) {
                MethodUtils.showToastMessage(getParentActivity(), getString(R.string.request_permission_failed));
            } else {
                MethodUtils.showToastMessage(getParentActivity(), getString(R.string.warning_missing_app_permissions));
            }
            onBackPressed();
        }
    };

    private void registerBluetoothReceiver() {
        LoggerWrapper.d(TAG, "Inside registerBluetoothReceiver");
        if (mBluetoothBroadcastReceiver == null) {
            mBluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getParentActivity().registerReceiver(mBluetoothBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            this.getParentActivity().registerReceiver(mBluetoothBroadcastReceiver, intentFilter);
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_FOUND);
        intentFilter.addAction(ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(ACTION_SCAN_MODE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getParentActivity().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            this.getParentActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private Runnable stopScanAnimation = new Runnable() {
        @Override
        public void run() {
            LoggerWrapper.e(TAG, "Inside stopScanAnimation : called");
            if (ActivityCompat.checkSelfPermission(getParentActivity().getApplicationContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (mBlueToothAdapter != null) {
                    mBlueToothAdapter.cancelDiscovery();
                }
                if (binding.reload != null) {
                    binding.reload.clearAnimation();
                }
            }
        }
    };

    private void setStopScanAnimation() {
        handler.removeCallbacks(stopScanAnimation);
        handler.postDelayed(stopScanAnimation, AUTO_SCAN_DELAY);
    }

    private void initUIViews() {
        if (visibilityTimer != null) {
            visibilityTimer.cancel();
        }

        binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);


        enableScanning(isPermission());


        binding.listBluetooth.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        binding.listBluetooth.setAdapter(rvBluetoothAdapter);
        setClickEvents();
    }


    void onBluetoothSwitchToggle(boolean isChecked) {
        toggleBluetoothSwitch(isChecked);
    }

    private void toggleBluetoothSwitch(boolean isEnabled) {
        if (isEnabled) {

            registerListener();
            boolean lPermission = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                lPermission = ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
            }
            if (mBlueToothAdapter == null) {
                Toast.makeText(getParentActivity(), "Device Not Support Bluetooth", Toast.LENGTH_SHORT).show();
            } /*else if (lPermission && mBlueToothAdapter.isEnabled()) {
                Toast.makeText(getParentActivity(), "Bluetooth disabled!", Toast.LENGTH_SHORT).show();
                getParentActivity().updateBluetoothIconInHomePage(false);
                mBlueToothAdapter.disable();
                getParentActivity().drawerItemDisEnable();
            } */ else if (lPermission && !mBlueToothAdapter.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                someActivityResultLauncher.launch(turnOn);
            }
        } else {
            if (visibilityTimer != null) {
                visibilityTimer.cancel();
            }
            getParentActivity().setTimeLeft(0);
            binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);

            Toast.makeText(getParentActivity(), "Bluetooth disabled!", Toast.LENGTH_SHORT).show();
            getParentActivity().updateBluetoothIconInHomePage(false);
            mBlueToothAdapter.disable();
            getParentActivity().drawerItemDisEnable();
            unRegisterListener();
            listOfBluetoothDevices.clear();
            rvBluetoothAdapter.updateList(listOfBluetoothDevices);
            enableScanning(false);


        }
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                getParentActivity().updateBluetoothIconInHomePage(true);
                getParentActivity().drawerItemEnable();
                enableScanning(true);
                scanForDevices();

            } else if (result.getResultCode() == RESULT_CANCELED) {
                getParentActivity().updateBluetoothIconInHomePage(false);
                getParentActivity().drawerItemDisEnable();
                binding.bluetoothSwitch.setOnCheckedChangeListener(null);
                binding.bluetoothSwitch.setChecked(false);
                binding.bluetoothSwitch.setOnCheckedChangeListener(blueListener);
                enableScanning(false);

            }
        }
    });

    private void enableScanning(boolean isPermissionGranted) {
        binding.rlScan.setEnabled(isPermissionGranted);
        binding.availableTextView.setTextColor(isPermissionGranted ? MethodUtils.getColorFromTheme(getParentActivity(), R.attr.text_color_tertiary) : getResources().getColor(R.color.hintTextColor));
        if (isPermissionGranted && (mBlueToothAdapter != null && mBlueToothAdapter.isEnabled())) {
            binding.bluetoothSwitch.setChecked(true);
            binding.deviceNameHeader.setVisibility(View.VISIBLE);
            binding.deviceNameText.setVisibility(View.VISIBLE);
            binding.rlScan.setVisibility(View.VISIBLE);
            startReloadAnimation();
            long timeLeft = getParentActivity().getTimeLeft();

            if (timeLeft > 0) {
                startVisibilityTimer();
            }

        } else {
            getParentActivity().setTimeLeft(0);
            binding.bluetoothSwitch.setChecked(false);
            binding.deviceNameHeader.setVisibility(View.INVISIBLE);
            binding.deviceNameText.setVisibility(View.INVISIBLE);
            binding.rlScan.setVisibility(View.INVISIBLE);
            setStopScanAnimation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TURN_ON_BLUETOOTH) {
                enableScanning(true);
                scanForDevices();
                getParentActivity().drawerItemEnable();
                getParentActivity().updateBluetoothIconInHomePage(true);
            }
        } else if (resultCode == DISCOVERABLE_BLUETOOTH_DURATION) {
            LoggerWrapper.d(TAG, "onActivityResult: Your phone is visible to nearby devices...");
            //mDiscoverableImg.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24);
        } else {
            binding.bluetoothSwitch.setChecked(false);
            getParentActivity().updateBluetoothIconInHomePage(false);
            getParentActivity().drawerItemDisEnable();
            MethodUtils.showToastMessage(requireActivity(), getString(R.string.error_occured_enable_bluetooth));
        }
    }

    void onBackPressed() {
        getParentFragmentManager().popBackStack();
    }

    private void scanForDevices() {
        LoggerWrapper.d(TAG, " Inside scanForDevices : ");

        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
        {
            LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT == true");

            listOfBluetoothDevices.clear();
            if (mBlueToothAdapter != null) {
                LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT true : mBlueToothAdapter != null");
                if (getParentActivity().getTimeLeft() <= 0 && binding.bluetoothSwitch.isChecked())
                    makeDeviceDiscoverable();
                mBlueToothAdapter.startDiscovery();
                Set<BluetoothDevice> pairedDevices = mBlueToothAdapter.getBondedDevices();
                // If there’s 1 or more paired devices...//
                if (pairedDevices.size() > 0) {
                    Header header = new Header();
                    header.setHeader(getString(R.string.paired_devices));
                    listOfBluetoothDevices.add(header);
                    // Retrieve each device’s public identifier and MAC address.
                    // Add each device’s name and address to an ArrayAdapter, ready to incorporate into a
                    //ListView
                    listOfBluetoothDevices.addAll(pairedDevices);
                }
                Header header = new Header();
                header.setHeader(getString(R.string.available_devices));
                listOfBluetoothDevices.add(header);
            }else{
                LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT true : mBlueToothAdapter == null");
            }
        }else{
            LoggerWrapper.d(TAG, " Inside scanForDevices : BLUETOOTH_CONNECT == false");
        }
    }

    private void updateAdapter(BluetoothDevice device) {
        LoggerWrapper.d(TAG, " Inside updateAdapter device : " + device);
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            LoggerWrapper.d(TAG, " Inside checkSelfPermission  : true");
            LoggerWrapper.d(TAG, " Inside updateAdapter device : " + device.getName());
            if (listOfBluetoothDevices != null && listOfBluetoothDevices.size() > 0) {
                if (!listOfBluetoothDevices.contains(device) && device != null && device.getName() != null && !device.getName().isEmpty() && device.getAddress() != null && !device.getAddress().isEmpty()) {
                    LoggerWrapper.d(TAG, " Not contains :  " + device.getName());
                    listOfBluetoothDevices.add(device);
                }
            } else {
                LoggerWrapper.d(TAG, " availableDeviceList == null else ");
                if (device.getBondState() != BOND_BONDED) {
                    if (!listOfBluetoothDevices.contains(device) && device.getName() != null && !device.getName().isEmpty() && device.getAddress() != null && !device.getAddress().isEmpty()) {
                        LoggerWrapper.d(TAG, " Not contains :  " + device.getName());
                        listOfBluetoothDevices.add(device);
                    }
                }
            }
            rvBluetoothAdapter.updateList(listOfBluetoothDevices);
        } else {
            requestPermissions();
        }
    }

    private void setClickEvents() {
        binding.rlScan.setOnClickListener(view -> onRefreshClicked());
        //binding.deviceNameText.setOnClickListener(view -> onDeviceNameClicked());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        blueListener = (buttonView, isChecked) -> onBluetoothSwitchToggle(isChecked);
        binding.bluetoothSwitch.setOnCheckedChangeListener(blueListener);
    }

    void onRefreshClicked() {
        startReloadAnimation();
        scanForDevices();
    }

    void onDeviceNameClicked() {
        showBottomSheetChangeName();
    }

    private void showBottomSheetChangeName() {
        DialogInterface lListener = new DialogInterface() {
            @Override
            public void cancel() {
            }

            @Override
            public void dismiss() {
                if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    binding.deviceNameText.setText(mBlueToothAdapter != null ? mBlueToothAdapter.getName() : "");
                }
            }
        };
//        ChangeNameBottomSheet lDialog = new ChangeNameBottomSheet();
//        lDialog.setListener(lListener);
//        lDialog.show(getChildFragmentManager(), "ChangeNameBottomSheet");
    }

    private void startReloadAnimation() {
        binding.reload.startAnimation(AnimationUtils.loadAnimation(getParentActivity(), R.anim.rotate));
    }

    private void resetRVAdapter() {
        rvBluetoothAdapter.updateList(new ArrayList(0));
        setStopScanAnimation();
    }

    @Override
    public void onClicked(BluetoothDevice bluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (mBlueToothAdapter != null && mBlueToothAdapter.isDiscovering()) {
                mBlueToothAdapter.cancelDiscovery();
            }
            if (bluetoothDevice.getBondState() != BOND_BONDED) {
                if (bluetoothDevice.createBond()) {
                    rvBluetoothAdapter.updateDeviceStatus(bluetoothDevice);
                }
            } else if (bluetoothDevice.getBondState() == BOND_BONDED) {
                if (bluetoothA2DPRequester == null)
                    bluetoothA2DPRequester = new BluetoothA2DPRequester();
                bluetoothA2DPRequester.request(getParentActivity(), mBlueToothAdapter, bluetoothDevice);
            } else if (bluetoothDevice.getBondState() == BOND_BONDING) {
                MethodUtils.showToastMessage(getParentActivity(), "Connecting!");
            }
        }
    }

    @Override
    public void onUnPairClicked(BluetoothDevice bluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothDevice.getBondState() == BOND_BONDED) {
                BluetoothA2DPRequester.unpairDevice(bluetoothDevice);
            }
        }
    }

    public boolean unpairAllDevice() {
        boolean isSuccess = false;
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Set<BluetoothDevice> pairedDevices = mBlueToothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    try {
                        Method m = device.getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                        isSuccess = true;
                    } catch (Exception e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBluetoothError() {
        LoggerWrapper.e(TAG, "There was an error enabling the Bluetooth Adapter.");
    }

    @Override
    public void onBluetoothConnected(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (device != null && (device.getBondState() == BOND_BONDED || device.getBondState() == BOND_BONDING)) {
                MethodUtils.showToastMessage(getParentActivity(), device.getName() + getParentActivity().getString(R.string.bt_error_connected));
                rvBluetoothAdapter.updateDeviceStatus(device);
                playConnectedAudio();
            } else {
                MethodUtils.showToastMessage(getParentActivity(), getParentActivity().getString(R.string.bt_error_disable));
            }
        }
    }

    private void playConnectedAudio() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getParentActivity(), notification);
        r.stop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            r.setHapticGeneratorEnabled(true);
        }
        r.play();
    }

    @Override
    public void onBluetoothDisconnected(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(getParentActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (device != null) {
                rvBluetoothAdapter.updateDeviceStatus(device);
                playConnectedAudio();
                setStopScanAnimation();
            }
        }
    }

    private void makeDeviceDiscoverable() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_VISIBILITY_DURATION);
        someActivityResultDiscovery.launch(i);
    }


    private void startVisibilityTimer() {
        long timeLeft = getParentActivity().getTimeLeft();
        if (timeLeft <= 0)
            timeLeft = BLUETOOTH_VISIBILITY_DURATION * 1000;
        if (visibilityTimer != null)
            visibilityTimer.cancel();
        visibilityTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getParentActivity().setTimeLeft(millisUntilFinished);
                if (binding.bluetoothDiscoverableText != null) {
                    binding.bluetoothDiscoverableText.setVisibility(View.VISIBLE);
                    binding.bluetoothDiscoverableText.setText(getParentActivity().getString(R.string.bt_discovery_text) + " " + (millisUntilFinished / 1000) + " " + getParentActivity().getString(R.string.bluetooth_sec));
                }
            }

            @Override
            public void onFinish() {
                getParentActivity().setTimeLeft(0);
                if (binding.bluetoothDiscoverableText != null)
                    binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);

            }
        };
        visibilityTimer.start();
    }


    ActivityResultLauncher<Intent> someActivityResultDiscovery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_CANCELED) {
                binding.bluetoothDiscoverableText.setVisibility(View.INVISIBLE);
            } else {
                startVisibilityTimer();
            }
        }
    });

}
