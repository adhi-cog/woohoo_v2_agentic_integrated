package com.sb.woohoo.ui.main.fragments.overview.saudidestinations;


import static com.sb.woohoo.analytics.CustomEvents.category_name_tourism;
import static com.sb.woohoo.analytics.CustomEvents.destinations_name_st;
import static com.sb.woohoo.analytics.CustomEvents.events_details_name_st;
import static com.sb.woohoo.ui.main.fragments.overview.SaudiTourismConstants.Params.ITEM_DESTINATIONS_DETAILS;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.BAD_GATEWAY_ERROR_CODE;
import static com.sb.woohoo.ui.main.helpers.NetworkHelper.EMPTY_RESULT_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.di.DaggerSaudiTourismDestinationsComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.Data;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentSaudiTourismDestinationsBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.di.SaudiTourismDestinationsComponent;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.di.SaudiTourismDestinationsModule;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.DataItem;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinationsdetails.DestinationsDetailFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.utils.LocaleHelper;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SaudiTourismDestinationsFragment extends FeatureFragment<MainActivity> implements SaudiTourismDestinationsContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    @Inject
    AnalyticsHelper helper;
    @Inject
    Picasso picasso;
    @Inject
    SaudiTourismDestinationsPresenter saudiTourismDestinationsPresenter;

    private FragmentSaudiTourismDestinationsBinding binding;

    public static SaudiTourismDestinationsFragment newInstance() {
        SaudiTourismDestinationsFragment fragment = new SaudiTourismDestinationsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.OverviewCategories(), null);
        binding = FragmentSaudiTourismDestinationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        saudiTourismDestinationsPresenter.destinationsAPIRequest(LocaleHelper.getLanguage(getActivity()));
    }

    private void initViews() {
        saudiTourismAdapter = new SaudiTourismDestinationsAdapter(getActivity(), WooHooApp.get(getParentActivity()).getWooHooAppComponent().getPicasso(), destinationslist, eventDetails -> {

            StatsDumpRequest statsDumpRequest = new StatsDumpRequest();
            statsDumpRequest.setCategoryName(category_name_tourism);
            statsDumpRequest.setEventName(destinations_name_st);
            statsDumpRequest.setIntentIdentifier(""+eventDetails.getId());
            statsDumpRequest.setIntentIdentifierName(eventDetails.getTitle());
            getParentActivity().callStatsDumpAPI(statsDumpRequest);

            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM_DESTINATIONS_DETAILS, eventDetails);
            DestinationsDetailFragment eventDetailFragment = DestinationsDetailFragment.newInstance();
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
        SaudiTourismDestinationsComponent saudiTourismDestinationsComponent = DaggerSaudiTourismDestinationsComponent.builder()
                .saudiTourismDestinationsModule(new SaudiTourismDestinationsModule(this)).build();
        saudiTourismDestinationsComponent.injectSaudiTourismDestinationsFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.overview_title_st_destination));

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return saudiTourismDestinationsPresenter;
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
        saudiTourismDestinationsPresenter.presenterCleanUps();
    }

    List<DataItem> destinationslist = new ArrayList<>();
    SaudiTourismDestinationsAdapter saudiTourismAdapter;

    public void onDestinationsResponse(List<Data> list) {
        if (list != null) {
            ArrayList<DataItem> arrayList = new ArrayList<>();
            for(int i=0; i<list.size(); i++){
                arrayList.addAll(list.get(i).getCities());
            }
            destinationslist.clear();
            destinationslist.addAll(arrayList);
            saudiTourismAdapter.updateListAndNotify(destinationslist);
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