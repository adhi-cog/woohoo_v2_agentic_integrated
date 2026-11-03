package com.sb.woohoo.ui.main.fragments.voipaudiocall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentAudioCallBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.di.AudioCallComponent;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.di.AudioCallModule;
import com.sb.woohoo.ui.main.fragments.voipaudiocall.di.DaggerAudioCallComponent;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

/**
 * Audio Call.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class AudioCallFragment
        extends FeatureFragment<MainActivity>
        implements AudioCallContracts.View, PopFragment {


    // Presenter.
    @Inject
    AudioCallPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    private FragmentAudioCallBinding binding;


    public AudioCallFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link AudioCallFragment} using the provided parameters.
     *
     * @return A new instance of {@link AudioCallFragment}.
     */
    public static AudioCallFragment newInstance(Bundle args) {
        AudioCallFragment fragment = new AudioCallFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public String createTag() {
        return AudioCallFragment.class.getSimpleName();
    }

    /*********************************************** Fragment Callback Methods ***********************************************/

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects the {@link AudioCallFragment} to {@link AudioCallComponent}.
     */
    @Override
    public void initDagger() {
        AudioCallComponent component =
                DaggerAudioCallComponent
                        .builder()
                        .audioCallModule(new AudioCallModule(AudioCallFragment.this))
                        .build();
        component.injectAudioCallFragment(AudioCallFragment.this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link AudioCallFragment}.
     */
    @Override
    public void bindViews(View view) {

//        layout.setContext(getParentActivity());
//        tvTitle.setText("Profile");

    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.AudioCall(),  null);
        binding = FragmentAudioCallBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        String KEY_CONTACT_INFO = "contactInfo";

        bindDataToView();
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }


    /**
     * Bind the DeviceData to the View.
     **/
    private void bindDataToView() {

//        mTvContactName.setText(getName(mInfo.getName()));
//        mTvContactNumber.setText(getPhoneNumber(mInfo.getPhoneNumbers(), mInfo.getEmail()));


    }


    /************************************************ Views on-Click Listeners ***********************************************/

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }


    void onParentClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}