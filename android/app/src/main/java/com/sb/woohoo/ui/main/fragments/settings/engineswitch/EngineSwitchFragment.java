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
import com.sb.woohoo.ui.main.agentic.AgenticEngine;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

public class EngineSwitchFragment extends FeatureFragment<MainActivity> {

    public static int DIALOGFLOW_ENGINE = 100;
//    public static String DIALOGFLOW_NAME = "Consumer Smartflow";
//    public static String SMARTFLOW_NAME = "Enterprise SmartFlow";
    public static int SMARTFLOW_ENGINE = 101;
    /** The agentic platform. Selectable only in builds that have a gateway compiled in. */
    public static int AGENTIC_ENGINE = AgenticEngine.AGENTIC_ENGINE;

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
        binding.agentic.setOnClickListener(view1 -> agenticClicked());
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
        binding.smartflowText.setText(getString(R.string.enterprise_smartflow));
        binding.dialogflowText.setText(getString(R.string.consumer_smartflow));
        binding.agenticText.setText(getString(R.string.agentic_assistant));

        // Hidden rather than shown-and-disabled when no gateway is compiled in: an option
        // that cannot work is worse than an option that is not offered, because someone will
        // select it and then report that voice is broken.
        boolean agenticAvailable = AgenticEngine.isConfigured();
        binding.agentic.setVisibility(agenticAvailable ? View.VISIBLE : View.GONE);

        int engine = AgenticEngine.selectedEngine();
        if (engine == AGENTIC_ENGINE && !agenticAvailable) {
            engine = SMARTFLOW_ENGINE;
        }
        select(engine);

        binding.dialogFlowRadio.setOnClickListener(view -> dialogFlowClicked());
        binding.smartFlowRadio.setOnClickListener(view -> smartFlowClicked());
        binding.agenticRadio.setOnClickListener(view -> agenticClicked());
    }

    /**
     * Store the choice and make the radios agree with it.
     *
     * Exactly one is checked, always. Three independent radios in a LinearLayout are not a
     * RadioGroup, so nothing enforces that for us.
     */
    private void select(int engine) {
        WooHooApp.getSharedPrefsHelper().put(SharedPrefsKey.PREF_KEY_ENGINE, engine);
        binding.dialogFlowRadio.setChecked(engine == DIALOGFLOW_ENGINE);
        binding.smartFlowRadio.setChecked(engine == SMARTFLOW_ENGINE);
        binding.agenticRadio.setChecked(engine == AGENTIC_ENGINE);
    }

    private void notifySettings(String label) {
        FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (currentFragment instanceof SettingsFragment) {
            ((SettingsFragment) currentFragment).onEngineChanged(label);
        }
    }

    private void agenticClicked() {
        select(AGENTIC_ENGINE);
        notifySettings(getString(R.string.agentic_assistant));
    }

    private void dialogFlowClicked() {
        select(DIALOGFLOW_ENGINE);
        notifySettings(getString(R.string.consumer_smartflow));
    }

    private void smartFlowClicked() {
        select(SMARTFLOW_ENGINE);
        notifySettings(getString(R.string.enterprise_smartflow));
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

