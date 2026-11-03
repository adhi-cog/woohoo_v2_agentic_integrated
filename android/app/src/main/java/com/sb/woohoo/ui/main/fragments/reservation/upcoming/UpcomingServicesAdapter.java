package com.sb.woohoo.ui.main.fragments.reservation.upcoming;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingService;
import java.util.ArrayList;
import javax.inject.Inject;

public class UpcomingServicesAdapter extends FragmentStatePagerAdapter {
    private ArrayList<UpcomingService> listUpcomingServices = new ArrayList<>(0);
    @Inject
    public UpcomingServicesAdapter(UpcomingServiceFragment fragment) {
        super(fragment.getFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public int getCount() {
        return listUpcomingServices.size();
    }

    @Override
    public Fragment getItem(int position) {
        return UpcomingServiceDetailsFragment.newInstance(listUpcomingServices.get(position));
    }

    void updateFragments(ArrayList<UpcomingService> upcomingServices) {
        this.listUpcomingServices.clear();
        this.listUpcomingServices.addAll(upcomingServices);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
