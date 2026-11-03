package com.sb.woohoo.ui.main.fragments.settings.thingstosay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentThingsToSayBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.di.DaggerThingsToSayComponent;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.di.ThingsToSayComponent;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.di.ThingsToSayModule;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayItems;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple subclass.
 */
public class ThingsToSayFragment extends FeatureFragment<MainActivity> implements NonPopFragment, ThingsToSayContract.View {


    @Inject
    ThingsToSayAdapter thingsToSayAdapter;

    @Inject
    AnalyticsHelper helper;

    @Inject
    ThingsToSayPresenter presenter;
    private FragmentThingsToSayBinding binding;

    public ThingsToSayFragment() {
        // Required empty public constructor
    }

    public static ThingsToSayFragment newInstance() {
        return new ThingsToSayFragment();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

        ThingsToSayComponent thingsToSayComponent = DaggerThingsToSayComponent.builder()
                .thingsToSayModule(new ThingsToSayModule(this))
                .build();

        thingsToSayComponent.injectThingsToSay(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentUpdateInputInterceptorRelativeLayout.setContext(getParentActivity());
        binding.fragmentUpdateCommonToolbarLayout.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    private void updateUI() {
        binding.fragmentDataLoading.shimmerViewContainer.startShimmer();
        binding.fragmentUpdateCommonToolbarLayout.commonToolbarTitle.setText(getString(R.string.things_to_say));
        // Set Layout Manager.
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.fragmentThingsToSayRecyclerView.setLayoutManager(manager);
        binding.fragmentThingsToSayRecyclerView.setAdapter(thingsToSayAdapter);
        binding.fragmentThingsToSayRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        presenter.getList();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String createTag() {
        return ThingsToSayFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.ThingsToSay(),  null);
        binding = FragmentThingsToSayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }


    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void displayList(List<ThingsToSayItems> list) {
        binding.fragmentDataLoading.shimmerViewContainer.stopShimmer();
        binding.fragmentDataLoading.shimmerViewContainer.setVisibility(View.INVISIBLE);
        thingsToSayAdapter.initializeList(list);
    }
}
