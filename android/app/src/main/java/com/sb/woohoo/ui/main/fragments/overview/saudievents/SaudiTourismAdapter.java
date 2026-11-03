package com.sb.woohoo.ui.main.fragments.overview.saudievents;

import static com.sb.woohoo.ui.main.sip.utils.FileUtils.convertDate;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.LayoutOverviewCategoriesBinding;
import com.sb.woohoo.databinding.LayoutSaudiTourismBinding;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.Data;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.DataList;
import com.sb.woohoo.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SaudiTourismAdapter extends RecyclerView.Adapter<SaudiTourismAdapter.EventsViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<DataList> mList = new ArrayList<>();
    private EventItemClickListener eventItemClickListener;

    public SaudiTourismAdapter(Context context, Picasso mPicasso, List<DataList> list, EventItemClickListener eventItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.eventItemClickListener = eventItemClickListener;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSaudiTourismBinding binding = LayoutSaudiTourismBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        public LayoutSaudiTourismBinding binding;

        EventsViewHolder(LayoutSaudiTourismBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DataList category) {
            binding.txtTitle.setText(category.getTitle());
            binding.txtDescription.setText(Html.fromHtml(category.getShortDescription()));
            binding.txtDate.setText("Start : "+ convertDate(category.getStartDate())+ "      End : "+convertDate(category.getEndDate()));
            if (!TextUtils.isEmpty(category.getFeatureEventImage())) {
                mPicasso.load(category.getFeatureEventImage()).into(binding.imgEvent);
            } else if (!TextUtils.isEmpty(category.getS7featureEventImage())) {
                mPicasso.load(category.getS7featureEventImage()).into(binding.imgEvent);
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
