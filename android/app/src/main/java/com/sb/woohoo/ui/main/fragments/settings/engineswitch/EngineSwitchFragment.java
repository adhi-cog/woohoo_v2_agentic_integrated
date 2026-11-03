package com.sb.woohoo.ui.main.fragments.settings.engineswitch;

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

public class EngineSwitchFragment extends FeatureFragment<MainActivity> {

    public static int DIALOGFLOW_ENGINE = 100;
//    public static String DIALOGFLOW_NAME = "Consumer Smartflow";
//    public static String SMARTFLOW_NAME = "Enterprise SmartFlow";
    public static int SMARTFLOW_ENGINE = 101;

    private FragmentEngineSwitchBinding binding;

    public static EngineSwitchFragment newInstance() {
        return new EngineSwitchFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
    }

    @Override
    public void bindViews(View view) {
        binding.commonToolbarLayout.commonToolbarTitle.setText(getString(R.string.switch_engine));
        binding.commonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.smartFlow.setOnClickListener(view1 -> onSmartFlowClicked());
        binding.dialogFlow.setOnClickListener(view1 -> onDialogFlowClicked());
    }

    @Override
    public String getCategory() {
        return EngineSwitchFragment.class.getSimpleName();
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
        int engine = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);

        binding.smartflowText.setText(getString(R.string.enterprise_smartflow));
        binding.dialogflowText.setText(getString(R.string.consumer_smartflow));

        if (engine == DIALOGFLOW_ENGINE) {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, DIALOGFLOW_ENGINE);
            binding.dialogFlowRadio.setChecked(true);
            binding.smartFlowRadio.setChecked(false);
        } else {
            WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);
            binding.dialogFlowRadio.setChecked(false);
            binding.smartFlowRadio.setChecked(true);
        }

        binding.dialogFlowRadio.setOnClickListener(view -> dialogFlowClicked());

        binding.smartFlowRadio.setOnClickListener(view -> smartFlowClicked());
    }

    private void dialogFlowClicked() {
        binding.dialogFlowRadio.setChecked(true);
        binding.smartFlowRadio.setChecked(false);
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, DIALOGFLOW_ENGINE);

        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onEngineChanged(getString(R.string.consumer_smartflow));
        }
    }

    private void smartFlowClicked() {
        binding.dialogFlowRadio.setChecked(false);
        binding.smartFlowRadio.setChecked(true);
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, SMARTFLOW_ENGINE);

        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onEngineChanged(getString(R.string.enterprise_smartflow));
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

