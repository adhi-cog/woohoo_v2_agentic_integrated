package com.sb.woohoo.ui.main.fragments.reservation.upcoming;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemUpcomingBookingsBinding;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.PropertyService;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingService;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpcomingServiceDetailsFragment extends Fragment {

    private String TAG = UpcomingServiceDetailsFragment.class.getSimpleName();
    private ItemUpcomingBookingsBinding binding;


    static UpcomingServiceDetailsFragment newInstance(UpcomingService service) {
        UpcomingServiceDetailsFragment truitonFrag = new UpcomingServiceDetailsFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("service", service);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ItemUpcomingBookingsBinding.inflate(inflater, container, false);

        UpcomingService service = ((UpcomingService) getArguments().getSerializable("service"));
        PropertyService property = service.getPropertyService();

        if (TextUtils.isEmpty(property.getThumbnailUrl())) {
            Picasso.get().load(R.drawable.ic_tick_28dp).into(binding.imgService);
        } else {
            Picasso.get().load(property.getThumbnailUrl()).into(binding.imgService);
        }
        binding.tvBookingConfirm.setText(property.getName());
        if (service.getLocal_start_time() != null)
            binding.tvBookingDate.setText(service.getLocal_start_time());
        else if (service.getStartTimeInFormat() != null)
            binding.tvBookingDate.setText(getDateTimeFromFormat(service.getStartTimeInFormat(), "dd MMM yyyy HH:mm a"));

        if (!TextUtils.isEmpty(property.getBackgroundImageUrl())) {
            Picasso.get().load(property.getBackgroundImageUrl()).fit().into(binding.imgService);
        }
        return binding.getRoot();
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
