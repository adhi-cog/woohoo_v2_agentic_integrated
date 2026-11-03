package com.sb.woohoo.ui.main.fragments.devices.discovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.AvailableDevicesListItemBinding;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class LightDiscoveryAdapter extends RecyclerView.Adapter<LightDiscoveryAdapter.MyViewHolder> {

    private List<Light> lightList;
    private DeviceDiscoveryFragment mFragment;

    @Inject
    LightDiscoveryAdapter(DeviceDiscoveryFragment deviceDiscoveryFragment) {
        this.mFragment = deviceDiscoveryFragment;
        this.lightList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvailableDevicesListItemBinding binding = AvailableDevicesListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LightDiscoveryAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.bindViews(lightList.get(position));
    }

    @Override
    public int getItemCount() {
        return lightList.size();
    }

    void updateDevices(List<Light> devices) {
        lightList.clear();
        lightList.addAll(devices);
        notifyDataSetChanged();
    }

    Light getItemAt(int index) {
        if (lightList.size() > index) {
            return lightList.get(index);
        }
        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public AvailableDevicesListItemBinding binding;

        MyViewHolder(AvailableDevicesListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(Light light) {
            binding.linearlayout.setTag(light);

            binding.textViewDevice.setText(light.getName());
            binding.imageViewDevice.setImageResource((R.drawable.philips_hue));

            binding.linearlayout.setOnClickListener(this::onClick);
        }

        void onClick(View view) {
            mFragment.onItemClicked((Light) view.getTag());
        }
    }
}