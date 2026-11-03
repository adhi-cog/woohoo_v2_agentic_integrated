package com.sb.woohoo.ui.main.fragments.fnb.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentFnbHistoryBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.history.di.DaggerFnBHistoryComponent;
import com.sb.woohoo.ui.main.fragments.fnb.history.di.FnBHistoryComponent;
import com.sb.woohoo.ui.main.fragments.fnb.history.di.FnBHistoryModule;
import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class FnBHistoryFragment extends FeatureFragment<MainActivity> implements FnBHistoryContract.Views{

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;


    private FnBHistoryListAdapter fnBHistoryListAdapter;

    @Inject
    AnalyticsHelper helper;

    @Inject
    FnBHistoryPresenter fnBHistoryPresenter;
    private FragmentFnbHistoryBinding binding;

    public static FnBHistoryFragment newInstance() {
        FnBHistoryFragment fnBHistory = new FnBHistoryFragment();
        return fnBHistory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.FnBOrder(),  null);
        binding = FragmentFnbHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isKeyboardOpen()){
        hideKeyboard();
        }
        initViews();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
        fnBHistoryPresenter.makeOrderHistoryRequest();
    }

    private void initViews() {
        binding.recyclerOrders.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        FnBHistoryComponent fnBHistoryComponent = DaggerFnBHistoryComponent.builder()
                .fnBHistoryModule(new FnBHistoryModule(this)).build();
        fnBHistoryComponent.injectFnBHistoryFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_past_order));
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    void onBack() {
        if(isKeyboardOpen()){
            hideKeyboard();
        } else {
            getParentActivity().onBackPressed();
        }
    }

    private void hideKeyboard() {
        if(getView() != null) {
            InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
        }
    }

    private boolean isKeyboardOpen() {
        InputMethodManager imm = (InputMethodManager) getParentActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        fnBHistoryPresenter.presenterCleanUps();
    }

    @Override
    public void errorMessage() {
        MethodUtils.showToastMessage(getContext(), getString(R.string.no_result_fetched));
        getParentActivity().onBackPressed();
    }

    @Override
    public void onSuccess(String successMessage, ArrayList<FnBHistoryData> orders) {
        fnBHistoryListAdapter = new FnBHistoryListAdapter(getParentActivity(), orders);
        binding.recyclerOrders.setAdapter(fnBHistoryListAdapter);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }
}
