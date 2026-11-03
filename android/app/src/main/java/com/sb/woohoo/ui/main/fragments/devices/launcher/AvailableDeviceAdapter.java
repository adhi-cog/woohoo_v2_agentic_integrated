package com.sb.woohoo.ui.main.fragments.devices.launcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.AvailableDevicesLauncherListItemBinding;
import com.sb.woohoo.ui.main.fragments.devices.LightConstants;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class AvailableDeviceAdapter extends RecyclerView.Adapter<AvailableDeviceAdapter.MyViewHolder> {

    private List<Light> lightList;
    private DeviceLauncherFragment mView;

    @Inject
    AvailableDeviceAdapter(DeviceLauncherFragment deviceLauncherFragment) {
        this.mView = deviceLauncherFragment;
        lightList = new ArrayList<>();
    }

    List<Light> getLightList() {
        return lightList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AvailableDevicesLauncherListItemBinding binding = AvailableDevicesLauncherListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (lightList != null && lightList.size() > position) {
            holder.bindViews(lightList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return lightList != null ? lightList.size() : 0;
    }


    void addList(List<Light> newLight) {
        lightList.clear();
        lightList.addAll(newLight);
        notifyDataSetChanged();
    }

    void updateItemInAdapter(Light light, int position) {
        lightList.remove(position);
        lightList.add(position, light);
        notifyDataSetChanged();
    }

    void deleteItemInAdapter(int position) {
        lightList.remove(position);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public AvailableDevicesLauncherListItemBinding binding;

        MyViewHolder(AvailableDevicesLauncherListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
        public void bindViews(Light light) {
            binding.linearlayout.setTag(light);
            binding.lightSetting.setTag(light);
            State state = light.getState();
            if (state != null) {
                if (state.isReachable()) {
                    binding.textViewRechable.setText(R.string.light_reachable);
                } else {
                    binding.textViewRechable.setText(R.string.unreachable);
                }
            }
            binding.textViewDevice.setText(light.getName());
            binding.imageViewDevice.setImageResource(R.drawable.philips_hue);

            binding.lightSetting.setOnClickListener(this::onClickLightSetting);
            binding.linearlayout.setOnClickListener(this::onClick);
        }

        void onClickLightSetting(View view) {
            Light light = (Light) view.getTag();
            mView.onClickLightSetting(light);
        }

        void onClick(View view) {
            Light light = (Light) view.getTag();
            if (light.getState().isReachable()) {
                IntentInfo<Light> intentInfo = new IntentInfo<>(light, LightConstants.Actions.ACTION_DEFAULT);
                mView.onItemClicked(intentInfo);
            }
        }
    }
}