package com.sb.woohoo.ui.main.fragments.reservation.booking;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentReservationBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.reservation.booking.di.DaggerReservationComponent;
import com.sb.woohoo.ui.main.fragments.reservation.booking.di.ReservationComponent;
import com.sb.woohoo.ui.main.fragments.reservation.booking.di.ReservationModule;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationDateSlot;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationServicesResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.TimeSlot;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.BookingConfirmFragment;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.UpcomingServiceFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class ReservationFragment
        extends FeatureFragment<MainActivity>
        implements ReservationContract.View {

    public static String KEY_IS_FROM_DINING_RESTAURANT = "isFromDiningRestaurant";
    public static String KEY_RESTAURANT_ID = "restaurant_id";


    // Presenter.
    @Inject
    ReservationPresenter reservationPresenter;

    // Adapters.
    @Inject
    ReservationServicesAdapter servicesAdapter;

    DatesAdapter datesAdapter;
    @Inject
    TimeSlotAdapter timeSlotAdapter;

    // Variables.
    private String query;
    int currentServiceId = -1;
    int guestCount = 0;
    String selectedDate = null;
    String selectedTime;
    String selectedDateTime;
    private String restaurantId;
    private boolean isFromDiningRestaurants = false;
    private FragmentReservationBinding binding;

    public static ReservationFragment newInstance(IntentInfo intentInfo) {
        ReservationFragment fragment;
        fragment = new ReservationFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    public static ReservationFragment newInstance(String restaurantId, boolean isFromDiningRestaurants) {
        ReservationFragment fragment;
        fragment = new ReservationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_FROM_DINING_RESTAURANT, isFromDiningRestaurants);
        bundle.putString(KEY_RESTAURANT_ID, restaurantId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String createTag() {
        return ReservationFragment.class.getSimpleName();
    }

    private void initViews() {
        // Set Toolbar Title.

        // Set-up RecyclerView.
        binding.rvListService.setHasFixedSize(true);
        binding.rvListService.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvListService.setAdapter(servicesAdapter);

        populateDateDatapter(ReservationDateSlot.getDateArray());

        binding.rvSelectTime.setHasFixedSize(true);
        binding.rvSelectTime.setLayoutManager(new GridLayoutManager(getContext(), 5));
        binding.rvSelectTime.setAdapter(timeSlotAdapter);

        binding.llTextViewGuest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount = 1;
                setCheckedGuestUI(binding.llTextViewGuest1);
            }
        });
        binding.llTextViewGuest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount = 2;
                setCheckedGuestUI(binding.llTextViewGuest2);
            }
        });
        binding.llTextViewGuest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount = 3;
                setCheckedGuestUI(binding.llTextViewGuest3);
            }
        });
        binding.llTextViewGuest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount = 4;
                setCheckedGuestUI(binding.llTextViewGuest4);
            }
        });
        binding.llTextViewGuest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount = 5;
                setCheckedGuestUI(binding.llTextViewGuest5);
            }
        });

        if (isFromDiningRestaurants) {
            binding.tvSelectService.setVisibility(View.GONE);
            binding.layoutCustomerName.setVisibility(View.VISIBLE);
        } else {
            binding.tvSelectService.setVisibility(View.VISIBLE);
            binding.layoutCustomerName.setVisibility(View.GONE);
        }

        binding.toolbar.commonToolbarBack.setOnClickListener(view -> onBackPressed());
        binding.btnSubmit.setOnClickListener(view -> onSubmitClicked());
        binding.tvLoadMore.setOnClickListener(view -> showLoadMore());
        binding.tvYourBookings.setOnClickListener(view -> showUpcomingBookingsFragment());
    }

    private void populateDateDatapter(ArrayList<ReservationDateSlot> dateArray) {
        if (datesAdapter == null) {
            datesAdapter = new DatesAdapter(this, new ArrayList<>());
            datesAdapter.setFromDiningRestaurants(isFromDiningRestaurants);
            binding.rvSelectDate.setHasFixedSize(true);
            binding.rvSelectDate.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvSelectDate.setAdapter(datesAdapter);
        }
        if (!isFromDiningRestaurants)
            datesAdapter.updateList(dateArray);
    }

    private void setCheckedGuestUI(View tvGuest) {
        binding.llTextViewGuest1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_rect_shape));
        binding.llTextViewGuest2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_rect_shape));
        binding.llTextViewGuest3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_rect_shape));
        binding.llTextViewGuest4.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_rect_shape));
        binding.llTextViewGuest5.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_rect_shape));

        tvGuest.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.gradient_service_items));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ReservationComponent reservationComponent =
                DaggerReservationComponent
                        .builder()
                        .reservationModule(
                                new ReservationModule(this)
                        ).build();
        reservationComponent.injectReservationFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        String domainId = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID) ||
                domainId.equals(Constants.CKS_DOMAIN_USER_ID) ||
                domainId.equalsIgnoreCase(Constants.PARK_ROYAL_DOMAIN_ID)) {
            binding.toolbar.commonToolbarTitle.setText(getString(R.string.reservation));
        } else if (domainId.equals(Constants.REGENT_DOMAIN_ID)) {
            binding.toolbar.commonToolbarTitle.setText(getString(R.string.your_bookings));
        } else
            binding.toolbar.commonToolbarTitle.setText(R.string.reservation_screen_title);

        if (getArguments() != null) {
            restaurantId = getArguments().getString(KEY_RESTAURANT_ID);
            isFromDiningRestaurants = getArguments().getBoolean(KEY_IS_FROM_DINING_RESTAURANT);
        }
        initViews();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservationPresenter.makeServicesRequest(restaurantId, isFromDiningRestaurants);
    }

    @Override
    public BasePresenter getPresenter() {
        return null;/*reservationPresenter;*/
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String message) {
        MethodUtils.showToastMessage(getContext(), message);
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
        String message = TextUtils.isEmpty(error) ? getString(R.string.item_not_available) : error;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    public void onServicesFetched(ArrayList<ReservationServicesResponse.ReservationService> reservationServices) {
        if (reservationServices == null || reservationServices.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.no_result_fetched), Toast.LENGTH_SHORT).show();
            return;
        }
        servicesAdapter.updateList(reservationServices);
        currentServiceId = -1;
        selectedDate = null;
        selectedTime = null;
        timeSlotAdapter.updateList(new ArrayList<>());
    }

    private void showServiceDateAndTime() {
        if (selectedDate == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String date = dateFormat.format(Calendar.getInstance().getTime());
            reservationPresenter.makeTimeSlotRequest(String.valueOf(currentServiceId), date);
        } else {
            reservationPresenter.makeTimeSlotRequest(String.valueOf(currentServiceId), selectedDate);
        }
    }

    public void onTimeSlotFetched(ArrayList<TimeSlot> slots) {
        timeSlotAdapter.updateList(slots);
    }

    public void onServiceClicked(ReservationServicesResponse.ReservationService reservationService) {
        if (currentServiceId != -1) {
            selectedTime = null;
            selectedDate = null;
            timeSlotAdapter.updateList(new ArrayList());
            timeSlotAdapter.notifyDataSetChanged();
            datesAdapter.reset();
        }

        currentServiceId = reservationService.getId();
    }

    public void onDateItemClicked(ReservationDateSlot item) {
        selectedTime = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedDate = dateFormat.format(item.getDate());
        if (isFromDiningRestaurants) {
            reservationPresenter.getDiningRestaurantTimeSlot(restaurantId, selectedDate);
        } else {
            if (currentServiceId == -1) {
                Toast.makeText(getContext(), getParentActivity().getString(R.string.select_service), Toast.LENGTH_LONG).show();
                datesAdapter.reset();
                selectedDate = null;
                return;
            }
            reservationPresenter.makeTimeSlotRequest(String.valueOf(currentServiceId), selectedDate);
        }
    }

    // Booking a service
    void onSubmitClicked() {

        if (isValid()) {
            if (isFromDiningRestaurants) {
                DiningRestaurantRequest diningRestaurantRequest = new DiningRestaurantRequest();
                diningRestaurantRequest.setTimestamp(selectedDateTime);
                diningRestaurantRequest.setRestaurantId(restaurantId);
                diningRestaurantRequest.setNo_of_person(String.valueOf(guestCount));
                if (!TextUtils.isEmpty(binding.etFirstName.getText().toString()))
                    diningRestaurantRequest.setFirst_name(binding.etFirstName.getText().toString());
                else
                    diningRestaurantRequest.setFirst_name("");
                if (!TextUtils.isEmpty(binding.etLastName.getText().toString()))
                    diningRestaurantRequest.setLast_name(binding.etLastName.getText().toString());
                else
                    diningRestaurantRequest.setLast_name("");
                getParentActivity().switchFragmentUsingAdd(BookingConfirmFragment.newInstance(diningRestaurantRequest));
//                reservationPresenter.makeDiningRestaurantBooking(diningRestaurantRequest);
            } else {
                ReservationRequest reservationRequest = new ReservationRequest();
                reservationRequest.setServiceId(String.valueOf(currentServiceId));
                reservationRequest.setCount(guestCount);
                reservationRequest.setDate(selectedDate);
                reservationRequest.setTime(selectedTime);

                reservationPresenter.makeReservation(reservationRequest);
            }
        }
    }


    void showLoadMore() {
        timeSlotAdapter.populateList();
    }

    void showUpcomingBookingsFragment() {
        UpcomingServiceFragment fragment = new UpcomingServiceFragment();
        if (isFromDiningRestaurants) {
            fragment.setArguments(createDiningReservationBundle());
        }
        getParentActivity().switchFragmentUsingAdd(fragment);
    }

    private boolean isValid() {
        if (isFromDiningRestaurants && TextUtils.isEmpty(binding.etFirstName.getText().toString())) {
            Toast.makeText(getContext(), getParentActivity().getString(R.string.error_first_name_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isFromDiningRestaurants && currentServiceId == -1) {
            Toast.makeText(getContext(), getParentActivity().getString(R.string.select_service), Toast.LENGTH_LONG).show();
            return false;
        }
        if (guestCount == 0) {
            Toast.makeText(getContext(), getParentActivity().getString(R.string.select_guests), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(getContext(), getParentActivity().getString(R.string.select_reservation_date), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(getContext(), getParentActivity().getString(R.string.select_reservation_time), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onBookingResponse(BookingConfirmResponse.Data data) {
        getParentActivity().switchFragmentUsingAdd(BookingConfirmFragment.newInstance(data));
    }

    private Bundle createDiningReservationBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_FROM_DINING_RESTAURANT, isFromDiningRestaurants);
        bundle.putString(KEY_RESTAURANT_ID, restaurantId);
        return bundle;
    }

    public void onDiningRestaurantsDatesResponse(List<String> data) {
        datesAdapter.updateList(getReservationDatesArray(data));
    }

    private ArrayList<ReservationDateSlot> getReservationDatesArray(List<String> data) {
        ArrayList<ReservationDateSlot> dateList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < data.size(); i++) {
            try {
                Date date = format.parse(data.get(i));
                calendar.setTime(date);
                String monthname = (String) DateFormat.format("MMM", date);
                ReservationDateSlot reservationDateSlot = new ReservationDateSlot(calendar.getTimeInMillis(), false);
                reservationDateSlot.setDateMonth(monthname);
                dateList.add(reservationDateSlot);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return dateList;
    }

    public void onDiningRestaurantTimeSlotResponse(List<String> data) {
        onTimeSlotFetched(getReservationTimeSlotArray(data));
    }

    private ArrayList<TimeSlot> getReservationTimeSlotArray(List<String> data) {
        ArrayList<TimeSlot> dateList = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        for (int i = 0; i < data.size(); i++) {
            Date date = null;
            try {
                date = inputFormat.parse(data.get(i));
                String formattedDate = outputFormat.format(date);
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setTime(formattedDate);
                timeSlot.setDateTime(data.get(i));
                timeSlot.setSelected(false);
                dateList.add(timeSlot);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateList;
    }

    public void onTimeSlotItemClicked(TimeSlot item) {
        selectedTime = item.getTime();
        selectedDateTime = item.getDateTime();
    }

    public void updateLoadMoreVisibility(boolean isVisible) {
        if (isVisible) {
            binding.tvLoadMore.setVisibility(View.VISIBLE);
        } else
            binding.tvLoadMore.setVisibility(View.GONE);
    }
}
