package com.sb.woohoo.ui.main.fragments.overview.saudievents;


import static com.sb.woohoo.analytics.CustomEvents.category_name_tourism;
import static com.sb.woohoo.analytics.CustomEvents.event_name_st;
import static com.sb.woohoo.analytics.CustomEvents.events_details_name_st;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_EVENT_DETAILS;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSaudiTourismEventsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.saudieventdetail.EventDetailFragment;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.di.DaggerSaudiTourismEventsComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.di.SaudiTourismEventsComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.di.SaudiTourismEventsModule;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.DataList;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SaudiTourismEventsFragment extends FeatureFragment<MainActivity> implements SaudiTourismEventsContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    SaudiTourismEventsPresenter saudiTourismEventsPresenter;

    private FragmentSaudiTourismEventsBinding binding;

    public static SaudiTourismEventsFragment newInstance() {
        SaudiTourismEventsFragment fragment = new SaudiTourismEventsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentSaudiTourismEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        saudiTourismEventsPresenter.eventsAPIRequest(LocaleHelper.getLanguage(getActivity()));
    }

    private void initViews() {
        saudiTourismAdapter = new SaudiTourismAdapter(getActivity(), WooHooApp.get(
                getParentActivity()).getWooHooAppComponent().getPicasso(),
                eventslist, eventDetails -> {

            StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
            statsDumpRequest.setCategoryName(category_name_tourism);
            statsDumpRequest.setEventName(events_details_name_st);
            statsDumpRequest.setIntentIdentifier(""+eventDetails.getId());
            statsDumpRequest.setIntentIdentifierName(eventDetails.getTitle());
            getParentActivity().callStatsDumpAPI(statsDumpRequest);

            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM_EVENT_DETAILS, eventDetails);
            EventDetailFragment eventDetailFragment = EventDetailFragment.newInstance();
            eventDetailFragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(eventDetailFragment);

        });
        binding.recyclerCategories.setAdapter(saudiTourismAdapter);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }
    @Override
    public void initDagger() {
        SaudiTourismEventsComponent saudiTourismEventsComponent = DaggerSaudiTourismEventsComponent.builder()
                .saudiTourismEventsModule(new SaudiTourismEventsModule(this)).build();
        saudiTourismEventsComponent.injectSaudiTourismEventsFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_st_events));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return saudiTourismEventsPresenter;
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
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
        saudiTourismEventsPresenter.presenterCleanUps();
    }

    List<DataList> eventslist = new ArrayList<>();
    SaudiTourismAdapter saudiTourismAdapter;

    public void onCategoriesResponse(List<DataList> list) {
        if (list != null && !list.isEmpty()) {
            eventslist.clear();
            eventslist.addAll(list);
            saudiTourismAdapter.updateListAndNotify(eventslist);
        }
    }

    @Override
    public void onErrorResponse(int errorCode) {
        if (errorCode == EMPTY_RESULT_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.no_result_fetched));
        } else if (errorCode == BAD_GATEWAY_ERROR_CODE) {
            MethodUtils.showToastMessage(getContext(), getString(R.string.bad_gateway));
        } else {
            MethodUtils.showToastMessage(getContext(), getString(R.string.try_again));
        }
        getParentActivity().onBackPressed();
    }

    @Override
    public void showProgress() {
        if (binding.shimmerFrameLayout.isShimmerStarted()) {
            binding.shimmerFrameLayout.stopShimmer();
        }
        binding.shimmerFrameLayout.startShimmer();
        binding.shimmerFrameLayout.setVisibility(View.VISIBLE);
        binding.recyclerCategories.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.shimmerFrameLayout.stopShimmer();
        binding.shimmerFrameLayout.setVisibility(View.GONE);
        binding.recyclerCategories.setVisibility(View.VISIBLE);
    }
}