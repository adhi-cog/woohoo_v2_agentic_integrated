package com.sb.woohoo.ui.main.fragments.bluetoothSigned;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.HeaderLayoutBluetoothBinding;
import com.sb.woohoo.databinding.ItemBluetoothBinding;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Header;
import com.sb.woohoo.utils.woohoblutooth.BluetoothA2DPRequester;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RVBluetoothSignedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static int HEADER_VIEW = 0, ITEM_VIEW = 1;
    private List bluetoothDeviceList = new ArrayList(0);
    private BluetoothDeviceClickListener bluetoothDeviceClickListener;

    @Inject
    public RVBluetoothSignedAdapter(BluetoothSignedSettingsFragment bluetoothSignedSettingsFragment) {
            this.bluetoothDeviceClickListener = bluetoothSignedSettingsFragment;

    }

    public void updateList(List listOfBluetoothDevices) {
        bluetoothDeviceList.clear();
        bluetoothDeviceList.addAll(listOfBluetoothDevices);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HeaderLayoutBluetoothBinding binding = HeaderLayoutBluetoothBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ItemBluetoothBinding deviceItemBinding = ItemBluetoothBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        switch (viewType) {
            case HEADER_VIEW:
                return new HeaderViewHolder(binding);
            case ITEM_VIEW:
                return new DeviceItemViewHolder(deviceItemBinding);
            default:
                return new HeaderViewHolder(binding);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER_VIEW:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.onBind((Header) bluetoothDeviceList.get(position));
                break;
            case ITEM_VIEW:
                DeviceItemViewHolder deviceItemViewHolder = (DeviceItemViewHolder) holder;
                deviceItemViewHolder.onBind((BluetoothDevice) bluetoothDeviceList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceList != null ? bluetoothDeviceList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (bluetoothDeviceList.get(position) instanceof Header) {
            return HEADER_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    public void updateDeviceStatus(BluetoothDevice device) {
        for (int i = 0; i < bluetoothDeviceList.size(); i++) {
            if (bluetoothDeviceList.get(i) instanceof BluetoothDevice) {
                if (((BluetoothDevice) bluetoothDeviceList.get(i)).getAddress().equalsIgnoreCase(device.getAddress())) {
                    bluetoothDeviceList.remove(bluetoothDeviceList.get(i));
                    bluetoothDeviceList.add(i, device);
                    notifyDataSetChanged();
                }
            }
        }
    }

    /*****************************************************{@link DeviceItemViewHolder}******************************/

    class DeviceItemViewHolder extends RecyclerView.ViewHolder {

        public ItemBluetoothBinding binding;

        DeviceItemViewHolder(ItemBluetoothBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice.getName() != null) {
                binding.title.setText(!bluetoothDevice.getName().equalsIgnoreCase("") ?
                        bluetoothDevice.getName() : bluetoothDevice.getAddress());
            }
            switch (bluetoothDevice.getBondState()) {
                case BOND_BONDING:
                    binding.bluetoothStatus.setVisibility(View.VISIBLE);
                    binding.bluetoothStatus.setText(binding.bluetoothStatus.getContext().getString(R.string.bt_paring));
                case BOND_BONDED:
                    if (BluetoothA2DPRequester.isConnected(bluetoothDevice)) {
                        binding.bluetoothStatus.setText(binding.bluetoothStatus.getContext().getString(R.string.connected));
                        binding.bluetoothStatus.setVisibility(View.VISIBLE);
                    } else
                        binding.bluetoothStatus.setVisibility(View.GONE);
                    break;
                case BOND_NONE:
                    binding.bluetoothStatus.setVisibility(View.GONE);
                    binding.bluetoothStatus.setText(binding.bluetoothStatus.getContext().getString(R.string.Disconnected));
                    break;
            }
            binding.statusIcon.setVisibility(bluetoothDevice.getBondState() == BOND_BONDING ? View.VISIBLE : View.INVISIBLE);
            binding.btnUnpair.setVisibility(bluetoothDevice.getBondState() == BOND_BONDED ? View.VISIBLE : View.INVISIBLE);

            binding.title.setOnClickListener(v -> bluetoothDeviceClickListener.onClicked(bluetoothDevice));
            binding.btnUnpair.setOnClickListener(v -> bluetoothDeviceClickListener.onUnPairClicked(bluetoothDevice));
//            binding.title.setOnLongClickListener(v -> {
//                bluetoothDeviceClickListener.onLongClicked(bluetoothDevice);
//                return true;
//            });

        }
    }

    /*******************************{@link HeaderViewHolder****************************/
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderLayoutBluetoothBinding binding;

        HeaderViewHolder(HeaderLayoutBluetoothBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void onBind(Header header) {
            binding.tvHeader.setText(header.getHeader());

        }
    }

    /******************************************{@link BluetoothDeviceClickListener}***********************/

    public interface BluetoothDeviceClickListener {
        void onClicked(BluetoothDevice bluetoothDevice);
//        void onLongClicked(BluetoothDevice bluetoothDevice);

        void onUnPairClicked(BluetoothDevice bluetoothDevice);
    }
}
