package com.sb.woohoo.ui.main.fragments.overview.saudipackages;

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
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.DataList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaudiTourismAdapter extends RecyclerView.Adapter<SaudiTourismAdapter.PackagesViewHolder> {

    private Context context;
    private Picasso mPicasso;
    private List<DataList> mList = new ArrayList<>();
    private PackagesItemClickListener packagesItemClickListener;

    public SaudiTourismAdapter(Context context, Picasso mPicasso, List<DataList> list, PackagesItemClickListener packagesItemClickListener) {
        this.context = context;
        this.mPicasso = mPicasso;
        this.mList = list;
        this.packagesItemClickListener = packagesItemClickListener;
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

    public void updateListAndNotify(List<DataList> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    class PackagesViewHolder extends RecyclerView.ViewHolder {

        public LayoutSaudiTourismBinding binding;

        PackagesViewHolder(LayoutSaudiTourismBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DataList category) {
            binding.txtTitle.setText(category.getName());
            binding.txtDescription.setText(Html.fromHtml(category.getDescription()));
            binding.txtDate.setText(getString(R.string.location)+" : "+ category.getCity());
            if (!TextUtils.isEmpty(category.getBackgroundImage())) {
                mPicasso.load(category.getBackgroundImage()).into(binding.imgEvent);
            } else {
                mPicasso.cancelRequest(binding.imgEvent);
                binding.imgEvent.setImageDrawable(null);
            }

            binding.txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_18));
            binding.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_16));
            binding.layoutEvent.setOnClickListener(v -> packagesItemClickListener.onPackagesItemClick(category));
        }
    }

    public interface PackagesItemClickListener {
        void onPackagesItemClick(DataList category);
    }
}
