package com.sb.woohoo.ui.main.fragments.buysim.phonenumber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.ItemPhoneNumberBinding;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetPhoneDetails;

import java.util.ArrayList;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.PackageViewHolder> {

    private Context context;
    private ArrayList<GetPhoneDetails> mList;
    private PackageItemClickListener packageItemClickListener;
    private Integer selectedPosition = null;

    public PhoneNumberAdapter(Context context, ArrayList<GetPhoneDetails> list, PackageItemClickListener packageItemClickListener){
        this.context = context;
        this.mList = list;
        this.packageItemClickListener = packageItemClickListener;
    }

    public void updatePackageList(ArrayList<GetPhoneDetails> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhoneNumberBinding binding = ItemPhoneNumberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PackageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        holder.bindViews(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder{
        public ItemPhoneNumberBinding binding;
        PackageViewHolder(ItemPhoneNumberBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(GetPhoneDetails data, int position) {
            binding.itemText.setText(data.getName());
            if (selectedPosition != null && position == selectedPosition) {
                binding.itemIcon.setImageResource(R.drawable.ph_select);
                binding.layoutMain.setBackgroundResource(R.drawable.bg_selected_rectangle);
            } else {
                binding.itemIcon.setImageResource(R.drawable.ph_unselect);
                binding.layoutMain.setBackgroundResource(android.R.color.transparent);
            }
            binding.layoutMain.setOnClickListener(view ->{
                selectedPosition = position;
                packageItemClickListener.onPackageListItemClick(data);
                notifyDataSetChanged();
            });
        }
    }

    public interface PackageItemClickListener{
        void onPackageListItemClick(GetPhoneDetails data);
    }
}
