package com.sb.woohoo.ui.main.fragments.overview.saudiexperience;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LayoutSaudiTourismBinding;
import com.sb.woohoo.databinding.LayoutSaudiTourismExperienceBinding;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.DataList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaudiTourismExperienceAdapter extends RecyclerView.Adapter<SaudiTourismExperienceAdapter.EventsViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<DataList> mList = new ArrayList<>();
    private EventItemClickListener eventItemClickListener;

    public SaudiTourismExperienceAdapter(Context context, Picasso mPicasso, List<DataList> list, EventItemClickListener eventItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.eventItemClickListener = eventItemClickListener;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutSaudiTourismExperienceBinding binding = LayoutSaudiTourismExperienceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EventsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateListAndNotify(List<DataList> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class EventsViewHolder extends RecyclerView.ViewHolder {

        public LayoutSaudiTourismExperienceBinding binding;

        EventsViewHolder(LayoutSaudiTourismExperienceBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DataList category) {
            binding.txtTitle.setText(category.getName());
            binding.txtDescription.setText("Destination : "+category.getPrimaryDestination());
            if (!TextUtils.isEmpty(category.getBackgroundImage())) {
                mPicasso.load(category.getBackgroundImage()).into(binding.imgEvent);
            } else {
                mPicasso.cancelRequest(binding.imgEvent);
                binding.imgEvent.setImageDrawable(null);
            }

            binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_18));
            binding.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_16));

            binding.layoutEvent.setOnClickListener(v -> eventItemClickListener.onEventItemClick(category));
        }
    }

    public interface EventItemClickListener {
        void onEventItemClick(DataList category);
    }
}
