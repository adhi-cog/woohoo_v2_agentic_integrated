package com.sb.woohoo.ui.main.fragments.settings.wakewordswitch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentEngineSwitchBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.settings.SettingsFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

public class WakeWordSwitchFragment extends FeatureFragment<MainActivity> {

    public static int GLOBAL_WAKEWORD = 100;
//    public static String GLOBALWAKEWORD_NAME = "Global Wakeword";
//    public static String PRIVATEWAKEWORD_NAME = "Private Wakeword";
    private static int PRIVATE_WAKEWORD = 101;

    private FragmentEngineSwitchBinding binding;

    public static WakeWordSwitchFragment newInstance() {
        return new WakeWordSwitchFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
    }

    @Override
    public void bindViews(View view) {
        binding.commonToolbarLayout.commonToolbarTitle.setText(getString(R.string.switch_wakeword));
        binding.commonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.dialogFlow.setOnClickListener(view1 -> onDialogFlowClicked());
        binding.smartFlow.setOnClickListener(view1 -> onSmartFlowClicked());
    }

    @Override
    public String getCategory() {
        return WakeWordSwitchFragment.class.getSimpleName();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEngineSwitchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    private void initViews() {
        int engine = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD);

        binding.smartflowText.setText(getString(R.string.private_wakeword));
        binding.dialogflowText.setText(getString(R.string.global_wakeword));

        if (engine == GLOBAL_WAKEWORD) {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD);
            binding.dialogFlowRadio.setChecked(true);
            binding.smartFlowRadio.setChecked(false);
        } else {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_WAKEWORD, PRIVATE_WAKEWORD);
            binding.dialogFlowRadio.setChecked(false);
            binding.smartFlowRadio.setChecked(true);
        }

        binding.dialogFlowRadio.setOnClickListener(view -> dialogFlowClicked());

        binding.smartFlowRadio.setOnClickListener(view -> smartFlowClicked());
    }

    private void dialogFlowClicked() {
        binding.dialogFlowRadio.setChecked(true);
        binding.smartFlowRadio.setChecked(false);
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_WAKEWORD, GLOBAL_WAKEWORD);

        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onWakeWordChanged(getString(R.string.global_wakeword));
        }
    }

    private void smartFlowClicked() {
        binding.dialogFlowRadio.setChecked(false);
        binding.smartFlowRadio.setChecked(true);
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_WAKEWORD, PRIVATE_WAKEWORD);

        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onWakeWordChanged(getString(R.string.private_wakeword));
        }
    }


    void onDialogFlowClicked() {
        dialogFlowClicked();
    }


    void onSmartFlowClicked() {
        smartFlowClicked();
    }

    void onRootClicked() {

    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }
}

