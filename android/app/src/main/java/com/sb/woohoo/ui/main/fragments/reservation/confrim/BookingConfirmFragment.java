package com.sb.woohoo.ui.main.fragments.reservation.confrim;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentReservationConfirmBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.di.BookingConfirmComponent;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.di.BookingConfirmModule;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.di.DaggerBookingConfirmComponent;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class BookingConfirmFragment
        extends FeatureFragment<MainActivity>
        implements BookingConfirmContract.View {


    // Presenter.
    @Inject
    BookingConfirmPresenter bookingConfirmPresenter;

    // Variables.
    private Object data;
    private boolean isFromDiningRestaurants = false;
    private FragmentReservationConfirmBinding binding;

    public static BookingConfirmFragment newInstance(BookingConfirmResponse.Data data) {
        BookingConfirmFragment fragment;
        fragment = new BookingConfirmFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", data);
        fragment.setArguments(b);
        return fragment;
    }

    public static BookingConfirmFragment newInstance(DiningRestaurantRequest diningRestaurantRequest) {
        BookingConfirmFragment fragment;
        fragment = new BookingConfirmFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", diningRestaurantRequest);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public String createTag() {
        return BookingConfirmFragment.class.getSimpleName();
    }

    private void initViews() {
        // Set Toolbar Title.
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        BookingConfirmComponent reservationComponent =
                DaggerBookingConfirmComponent
                        .builder()
                        .bookingConfirmModule(
                                new BookingConfirmModule(this)
                        ).build();
        reservationComponent.injectBookingConfirmFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        initViews();
        setClickEvents();
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (requireArguments().getSerializable("data") instanceof BookingConfirmResponse.Data) {
            data = requireArguments().getSerializable("data");
        } else if (requireArguments().getSerializable("data") instanceof DiningRestaurantRequest) {
            data = requireArguments().getSerializable("data");
            isFromDiningRestaurants = true;
        }

        if (isFromDiningRestaurants) {
            binding.layoutDiningReservation.setVisibility(View.VISIBLE);
            binding.layoutBooking.setVisibility(View.GONE);
            DiningRestaurantRequest diningRestaurantRequest = (DiningRestaurantRequest)data;
            if (!TextUtils.isEmpty(diningRestaurantRequest.getFirst_name()) && !TextUtils.isEmpty(diningRestaurantRequest.getLast_name()))
                binding.tvBookingName.setText(MessageFormat.format("{0} {1}", diningRestaurantRequest.getFirst_name(), diningRestaurantRequest.getLast_name()));
            binding.tvBookingDateReservation.setText(MessageFormat.format("{0} Guests | {1} | {2}",
                    diningRestaurantRequest.getNo_of_person(),
                    getDateTimeFromFormat(diningRestaurantRequest.getTimestamp(), "HH:mm a"),
                    getDateTimeFromFormat(diningRestaurantRequest.getTimestamp(), "dd MMMM, yyyy")));
//            tvBookingNoteReservation.setText();
        } else {
            binding.layoutDiningReservation.setVisibility(View.GONE);
            binding.layoutBooking.setVisibility(View.VISIBLE);
            binding.tvBookingDate.setText(String.format(getResources().getString(R.string.booking_confirmation_slot_details), ((BookingConfirmResponse.Data)data).getStartTime()));
        }
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

    private void setClickEvents() {
        binding.interceptorLayout.setOnClickListener(view -> onRootClicked());
        binding.btnOk.setOnClickListener(view -> onBackPressed());
        binding.btnCancel.setOnClickListener(view -> cancelBooking());
        binding.btnCancelReservation.setOnClickListener(view -> cancelDiningRestaurantBooking());
        binding.btnConfirm.setOnClickListener(view -> confirmDiningRestaurantBooking());
    }

    void onRootClicked() {
    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void cancelBooking() {
        bookingConfirmPresenter.makeCancelBookingRequest(((BookingConfirmResponse.Data) data).getId());
    }

    void cancelDiningRestaurantBooking() {
        onCancelBookingConfirm();
    }

    void confirmDiningRestaurantBooking() {
        bookingConfirmPresenter.makeDiningRestaurantBooking((DiningRestaurantRequest) data);
    }

    public void onBookingResponse(BookingConfirmResponse.Data data) {
        Toast.makeText(getActivity(), R.string.booking_confirmed, Toast.LENGTH_LONG).show();
        getParentActivity().onBackPressed();
    }

    @Override
    public void onCancelBookingConfirm() {
        Toast.makeText(getActivity(), R.string.booking_cancelled, Toast.LENGTH_LONG).show();
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
        //reservationPresenter.presenterCleanUps();
    }

    public void errorMessage(String error) {
        Toast.makeText(getActivity(), getString(R.string.item_not_available), Toast.LENGTH_SHORT).show();
        getParentActivity().onBackPressed();

    }

    private String getDateTimeFromFormat(String inputDate, String outputFormatStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputFormatStr);
        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}