package com.sb.woohoo.ui.main.fragments.buysim.buypackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.ItemPackageBinding;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private Context context;
    private List<GetEachPackageDetails> mList;
    private PackageItemClickListener packageItemClickListener;

    public PackageAdapter(Context context, ArrayList<GetEachPackageDetails> list,PackageItemClickListener packageItemClickListener){
        this.context = context;
        this.mList = list;
        this.packageItemClickListener = packageItemClickListener;
    }

    public void updatePackageList(ArrayList<GetEachPackageDetails> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPackageBinding binding = ItemPackageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PackageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        holder.bindViews(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder{
        public ItemPackageBinding binding;
        PackageViewHolder(ItemPackageBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(GetEachPackageDetails data) {
            binding.txtName.setText(data.getName());
            binding.txtPrice.setText(""+data.getPrice());
            binding.txtPriceUnit.setText(data.getPriceUnit());
            binding.txtUsd.setText("$"+data.getUsdPrice());
            binding.txtOne.setText(data.getInfo().get(0));
            binding.txtTwo.setText(data.getInfo().get(1));
            binding.txtThree.setText(data.getInfo().get(2));
            binding.txtFour.setText(data.getInfo().get(3));
            binding.btnBuyNow.setOnClickListener(view ->
                    packageItemClickListener.onPackageListItemClick(data));
        }
    }

    public interface PackageItemClickListener{
        void onPackageListItemClick(GetEachPackageDetails data);
    }
}
