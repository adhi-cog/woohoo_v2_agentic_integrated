package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial;

import static android.view.View.VISIBLE;
import static com.sb.woohoo.utils.Constants.PINVIEW.IS_PIN_EXISTS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSurveillanceInitialBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pinview.PinViewFragment;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.di.DaggerSurveillanceInitialComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.di.SurveillanceInitialComponent;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.di.SurveillanceInitialModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class SurveillanceInitialFragment
        extends FeatureFragment<MainActivity>
        implements SurveillanceInitContract.Views {


    @Inject
    AnalyticsHelper helper;

    @Inject
    SurveillanceInitPresenter surveillanceInitPresenter;
    private FragmentSurveillanceInitialBinding binding;

    public SurveillanceInitialFragment() {
        /*
        Required Empty Constructor
         */
    }

    public static SurveillanceInitialFragment newInstance() {
        return new SurveillanceInitialFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        SurveillanceInitialComponent surveillanceInitialComponent = DaggerSurveillanceInitialComponent
                .builder()
                .surveillanceInitialModule(new SurveillanceInitialModule(this))
                .build();
        surveillanceInitialComponent.injectInitialSurveillanceFragment(this);
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
        return surveillanceInitPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.SurveillanceInit(),  null);
        binding = FragmentSurveillanceInitialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHint(R.string.turn_on_surveillance_hint, HINT_NO_CHANGE, VISIBLE);
        binding.commonToolbar.commonToolbarTitle.setText(R.string.surveillance_mode);
        binding.commonToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.tvTurnOnSurveillance.setOnClickListener(view1 -> turnOnSurveillanceMode());
    }

    @Override
    public void onUserPinExists(boolean isPinExists) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_PIN_EXISTS, isPinExists);
        getParentActivity().switchFragmentUsingAdd(PinViewFragment.newInstance(bundle, PinViewFragment.TURN_ON));
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        surveillanceInitPresenter.presenterCleanUps();
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void turnOnSurveillanceMode() {
        surveillanceInitPresenter.verifyIfUserPinExists();
    }

    void onRootLayout() {

    }
}
