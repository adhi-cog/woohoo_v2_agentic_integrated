package com.sb.woohoo.ui.onboarding.intro;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.base.BaseFragment;
import com.sb.woohoo.ui.base.IBackPressInterceptor;
import com.sb.woohoo.ui.main.MainActivity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WooHooIntroFragment extends BaseFragment implements IBackPressInterceptor, WoohooIntroContract.View {

    private final String TAG = "IntroFragment";

    public WooHooIntroFragment() {
        // Required empty public constructor
    }

    public static WooHooIntroFragment newInstance() {

        Bundle args = new Bundle();

        WooHooIntroFragment fragment = new WooHooIntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String createTag() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_woo_hoo_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onBackPressed(BackCallback backCallback) {
        // Handle back pressed here
    }

    private void initViews(View view) {

        if (view != null) {
            // Skip intro video
            view.setOnClickListener(view1 -> skipIntro());
        }
    }


    @Override
    public void skipIntro() {
        // Call home and finish
        Intent goHome = new Intent(getActivity(), MainActivity.class);
        Objects.requireNonNull(getActivity()).startActivity(goHome);
        getActivity().finish();
    }
}
