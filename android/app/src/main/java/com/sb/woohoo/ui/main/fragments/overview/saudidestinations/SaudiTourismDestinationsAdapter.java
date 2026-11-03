package com.sb.woohoo.ui.main.fragments.overview.saudidestinations;

import static com.sb.woohoo.utils.Constants.BASE_URL_FOR_ST;
import static com.sileria.android.Resource.getString;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LayoutSaudiTourismBinding;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.DataItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaudiTourismDestinationsAdapter extends RecyclerView.Adapter<SaudiTourismDestinationsAdapter.PackagesViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<DataItem> mList = new ArrayList<>();
    private DestinationsItemClickListener destinationsItemClickListener;

    public SaudiTourismDestinationsAdapter(Context context, Picasso mPicasso, List<DataItem> list, DestinationsItemClickListener destinationsItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.destinationsItemClickListener = destinationsItemClickListener;
    }

    @NonNull
    @Override
    public PackagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSaudiTourismBinding binding = LayoutSaudiTourismBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PackagesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PackagesViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateListAndNotify(List<DataItem> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class PackagesViewHolder extends RecyclerView.ViewHolder {

        public LayoutSaudiTourismBinding binding;

        PackagesViewHolder(LayoutSaudiTourismBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DataItem category) {
            binding.txtTitle.setText(category.getHeading());
            binding.txtDescription.setText(Html.fromHtml(category.getDescription()));
            String bestFor = String.join(",", category.getBestFor());
            binding.txtDate.setText(getString(R.string.location)+" : "+category.getTitle()+ "      "+getString(R.string.best_for)+" : "+bestFor);
            if (!TextUtils.isEmpty(category.getImage())) {
                mPicasso.load(BASE_URL_FOR_ST+category.getImage()).into(binding.imgEvent);
            }else {
                mPicasso.cancelRequest(binding.imgEvent);
                binding.imgEvent.setImageDrawable(null);
            }

            binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_18));
            binding.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_16));
//
            binding.layoutEvent.setOnClickListener(v -> destinationsItemClickListener.onDestinationsItemClick(category));
        }
    }

    public interface DestinationsItemClickListener {
        void onDestinationsItemClick(DataItem category);
    }
}
