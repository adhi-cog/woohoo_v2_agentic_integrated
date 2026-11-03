package com.sb.woohoo.ui.main.fragments.reservation.upcoming;

import static com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment.KEY_IS_FROM_DINING_RESTAURANT;
import static com.sb.woohoo.ui.main.fragments.reservation.booking.ReservationFragment.KEY_RESTAURANT_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentUpcomingServiceBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.DaggerUpcomingServiceComponent;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.UpcomingServiceComponent;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.UpcomingServiceContract;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.di.UpcomingServiceModule;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingService;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;

import javax.inject.Inject
        ;

public class UpcomingServiceFragment
        extends FeatureFragment<MainActivity>
        implements UpcomingServiceContract.View {

    private String restaurantId;
    private boolean isFromDiningRestaurants = false;

    // Presenter.
    @Inject
    UpcomingServicesPresenter upcomingServicesPresenter;

    // Adapters.
    @Inject
    UpcomingServicesAdapter upcomingServicesAdapter;

    // Variables.
    private int dotCount;
    private ImageView[] dots;
    private FragmentUpcomingServiceBinding binding;

    public static UpcomingServiceFragment newInstance(IntentInfo intentInfo) {
        UpcomingServiceFragment fragment;
        fragment = new UpcomingServiceFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    @Override
    public String createTag() {
        return UpcomingServiceFragment.class.getSimpleName();
    }

    private void initViews() {
        binding.viewpagerServices.setAdapter(upcomingServicesAdapter);
        binding.viewpagerServices.addOnPageChangeListener(pageChangeListener);
    }

    public static int recentPos = 0;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            recentPos = position;
            for (int i = 0; i < dotCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_not_select_item));
            }
            dots[position].setImageDrawable(ContextCompat.getDrawable(getParentActivity(), R.drawable.circle_slideshow_selectitem));
        }
    };

    private void initializeViewPagerIndicator() {
        binding.dots.removeAllViews();
        dotCount = upcomingServicesAdapter.getCount();
        dots = new ImageView[dotCount];

        for (int i = 0; i < dotCount; i++) {

            if (dots != null) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.circle_slideshow_not_select_item));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                binding.dots.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_slideshow_selectitem));
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentUpcomingServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        UpcomingServiceComponent upcomingServiceComponent =
                DaggerUpcomingServiceComponent
                        .builder()
                        .upcomingServiceModule(
                                new UpcomingServiceModule(this)
                        ).build();
        upcomingServiceComponent.injectUpcomingServiceFragment(this);
    }

    @Override
    public void bindViews(View view) {

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(R.string.title_upcoming_service);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.buttonCancel.setOnClickListener(view1 -> onCancelClicked());
        initViews();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            restaurantId = getArguments().getString(KEY_RESTAURANT_ID);
            isFromDiningRestaurants = getArguments().getBoolean(KEY_IS_FROM_DINING_RESTAURANT);
        }
        if (isFromDiningRestaurants) {
            upcomingServicesPresenter.getAllDiningReservations(restaurantId);
            binding.buttonCancel.setVisibility(View.GONE);
        } else {
            upcomingServicesPresenter.makeUpcomingServicesRequest();
            binding.buttonCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return null;/*reservationPresenter;*/
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        MethodUtils.showToastMessage(getContext(), message);
    }

    ArrayList<UpcomingService> upcomingServices;
    @Override
    public void onUpcomingServicesFetched(ArrayList<UpcomingService> upcomingServices) {
        if(!upcomingServices.isEmpty()){
            binding.upcomingErrorText.setVisibility(View.GONE);
            binding.buttonCancel.setVisibility(View.VISIBLE);
        }

        this.upcomingServices = upcomingServices;
        upcomingServicesAdapter.updateFragments(upcomingServices);
        initializeViewPagerIndicator();
        binding.viewpagerServices.setCurrentItem(0);
    }

    @Override
    public void onCancelBookingConfirm() {
        Toast.makeText(getActivity(), R.string.booking_cancelled, Toast.LENGTH_LONG).show();
        upcomingServicesPresenter.makeUpcomingServicesRequest();
    }

    void onRootClicked() {
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
    }


    public void errorMessage(String error) {
        binding.upcomingErrorText.setText(error);
        binding.upcomingErrorText.setVisibility(View.VISIBLE);
        binding.buttonCancel.setVisibility(View.GONE);
        Toast.makeText(getParentActivity(),
                getParentActivity().getString(R.string.item_not_available), Toast.LENGTH_SHORT).show();
       // getParentActivity().onBackPressed();
    }

    void onCancelClicked() {
        //Crashing when upcomingServices is null and click on cancel button
        if(upcomingServices != null && !upcomingServices.isEmpty()){
            int id = upcomingServices.get(binding.viewpagerServices.getCurrentItem()).getId();
            upcomingServicesPresenter.makeCancelBookingRequest(id);
        }
    }
}