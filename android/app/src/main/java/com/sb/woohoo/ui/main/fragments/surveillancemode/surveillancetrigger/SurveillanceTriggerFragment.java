package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSurveillanceTriggerBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceon.SurveillanceFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.di.DaggerSurveillanceTriggerComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.di.SurveillanceTriggerComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillancetrigger.di.SurveillanceTriggerModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import javax.inject.Inject;

public class SurveillanceTriggerFragment
        extends FeatureFragment<MainActivity>
        implements SurveillanceTriggerContract.Views, VideoCalling {

    private static final int COUNT_DOWN_TIME = 5;
    @Inject
    SurveillanceTriggerPresenter surveillanceTriggerPresenter;
    @Inject
    AnalyticsHelper helper;
    private int countDownTimer = COUNT_DOWN_TIME;

    private String mPin;
    private Handler handler = new Handler();
    private Runnable setSurveillanceModeRunnable = new Runnable() {
        @Override
        public void run() {
            countDownTimer = countDownTimer - 1;
            if (countDownTimer == 0) {
                getParentActivity().switchFragmentUsingAdd(SurveillanceFragment.newInstance());
                handler.removeCallbacks(setSurveillanceModeRunnable);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };
    private FragmentSurveillanceTriggerBinding binding;

    public static SurveillanceTriggerFragment newInstance(Bundle bundle) {
        SurveillanceTriggerFragment surveillanceTriggerFragment = new SurveillanceTriggerFragment();
        surveillanceTriggerFragment.setArguments(bundle);
        return surveillanceTriggerFragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        SurveillanceTriggerComponent surveillanceTriggerComponent = DaggerSurveillanceTriggerComponent
                .builder()
                .surveillanceTriggerModule(new SurveillanceTriggerModule(this))
                .build();

        surveillanceTriggerComponent.injectFragment(this);
    }

    @Override
    public void bindViews(View view) {
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
        helper.track(new Screen.SurveillanceTrigger(),  null);
        binding = FragmentSurveillanceTriggerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        binding.tvCancelSurveillance.setOnClickListener(view1 -> onCancelPressed());
    }

    private void init() {
        setHint(R.string.surveillance_five_mins_message, HINT_NO_CHANGE, VISIBLE);
        mPin = getArguments() != null ? getArguments().getString(Constants.PINVIEW.PIN) : null;
        handler.postDelayed(setSurveillanceModeRunnable, 1000);
    }

    @Override
    public void onError(String errorMessage) {
        MethodUtils.showToastMessage(requireContext(), errorMessage);
    }

    @Override
    public void cancelSurveillanceMode() {
        getParentActivity().startSurveillanceMode(false);
    }

    void onCancelPressed() {
        handler.removeCallbacks(setSurveillanceModeRunnable);
        surveillanceTriggerPresenter.cancelSurveillanceMode(mPin);
    }


    void onRootLayout() {

    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        surveillanceTriggerPresenter.presenterCleanUps();
    }
}
