package com.sb.woohoo.ui.main.fragments.surveillancemode.pinview;

import static android.view.View.VISIBLE;
import static com.sb.woohoo.utils.Constants.PINVIEW.IS_PIN_EXISTS;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentPinViewBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.di.DaggerPinViewComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.di.PinViewComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.di.PinViewModule;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.SurveillanceTriggerFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class PinViewFragment
        extends FeatureFragment<MainActivity>
        implements PinViewContracts.View {

    public static final int TURN_ON = 100;
    public static final int TURN_OFF = 101;
    private static final String KEY = "key";

    @Inject
    PinViewPresenter pinViewPresenter;
    @Inject
    AnalyticsHelper helper;

    private String mPin;
    private boolean isPinExists;
    private int key;
    private FragmentPinViewBinding binding;

    public static PinViewFragment newInstance(Bundle bundle, int key) {
        PinViewFragment pinViewFragment = new PinViewFragment();
        bundle.putInt(KEY, key);
        pinViewFragment.setArguments(bundle);
        return pinViewFragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        PinViewComponent pinViewComponent = DaggerPinViewComponent.builder()
                .pinViewModule(new PinViewModule(this))
                .build();
        pinViewComponent.injectPinViewFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.commonToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    private void getBundleDetails(Bundle arguments) {
        if (arguments != null && arguments.containsKey(IS_PIN_EXISTS)) {
            isPinExists = arguments.getBoolean(IS_PIN_EXISTS);
        }
        if (arguments != null && arguments.containsKey(KEY)) {
            key = arguments.getInt(KEY);
        }
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return pinViewPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.PinView(),  null);
        binding = FragmentPinViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHint(R.string.layout_pin_text, HINT_NO_CHANGE, VISIBLE);
        getBundleDetails(getArguments());
        initViews();
    }

    private void initViews() {
        binding.passcodeView.setKeyTextColor(Color.parseColor("#e5e7e8"));
        if (!isPinExists && key == TURN_ON) {
            binding.commonToolbar.commonToolbarTitle.setText(getString(R.string.set_woohoo_pin));
        }

        binding.passcodeView.setOnTextChangeListener(pin -> {
            binding.passcodeView.setEmptyDrawable(R.drawable.empty_field);
            if (pin.length() == 4) {
                this.mPin = pin;
                if (key == TURN_ON) {
                    if (isPinExists) {
                        pinViewPresenter.setHubToSurveillanceMode(pin, true);
                    } else {
                        pinViewPresenter.updateUserPinForSurveillance(pin);
                    }
                } else {
                    pinViewPresenter.setHubToSurveillanceMode(pin, false);
                }
            }
        });
    }

    @Override
    public void isHubModeInSurveillance(boolean isInSurveillance) {
        if (isInSurveillance) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PINVIEW.PIN, mPin);
            getParentActivity().startSurveillanceMode(true);
            getParentActivity().switchFragmentUsingAdd(SurveillanceTriggerFragment.newInstance(bundle));
        } else {
            getParentActivity().onBackPressed();
            FragmentManager fragmentManager = getParentActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag("SurveillanceFragment");
            if (currentFragment instanceof SurveillanceFragment) {
                ((SurveillanceFragment) currentFragment).stopSurveillance();
            }
        }
    }

    @Override
    public void onInCorrectPin() {
        binding.passcodeView.setError(true);
        binding.passcodeView.setEmptyDrawable(R.drawable.empty_error_field);
        setHint(R.string.layout_incorrect_pin_text, android.R.color.holo_red_dark, VISIBLE);
    }

    @Override
    public void onError(String error) {
        MethodUtils.showToastMessage(requireActivity(), error);
    }


    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onRootLayout() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        pinViewPresenter.presenterCleanUps();
    }
}
