package com.sb.woohoo.ui.main.fragments.amadeusServices;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrderData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AmadeusOrdersAdapter extends RecyclerView.Adapter<AmadeusOrdersAdapter.OrderViewHolder> {

    private List<AmadeusOrderData> ordersList = new ArrayList<>();
    private OnAmadeusOrderClick onAmadeusOrderClick;

    @Inject
    public AmadeusOrdersAdapter() {
    }

    public void updateListOnUI(List<AmadeusOrderData> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }

    public void setOnAmadeusOrderClick(OnAmadeusOrderClick onAmadeusOrderClick) {
        this.onAmadeusOrderClick = onAmadeusOrderClick;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amadeus_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (!TextUtils.isEmpty(ordersList.get(position).getImageUrl())) {
            Picasso.get().load(ordersList.get(position).getImageUrl()).fit().into(holder.imgServiceOrder);
        } else if (ordersList.get(position).getImageLocalId() != 0){
            holder.imgServiceOrder.setImageResource(ordersList.get(position).getImageLocalId());
        } else {
            holder.imgServiceOrder.setImageResource(R.drawable.ic_towel);
        }
        holder.tvServiceOrder.setText(ordersList.get(position).getItemName());
        holder.tvOrderCounter.setText(""+ordersList.get(position).getOrderCount());
        holder.ivAddOrder.setOnClickListener(view -> {
            ordersList.get(position).setOrderCount(ordersList.get(position).getOrderCount() + 1);
            holder.tvOrderCounter.setText(String.valueOf(ordersList.get(position).getOrderCount()));
        });
        holder.ivSubOrder.setOnClickListener(view -> {
            if (ordersList.get(position).getOrderCount() > 0) {
                ordersList.get(position).setOrderCount(ordersList.get(position).getOrderCount() - 1);
                holder.tvOrderCounter.setText(String.valueOf(ordersList.get(position).getOrderCount()));
            }
        });
        holder.tvConfirmOrder.setOnClickListener(view -> {
            if (ordersList.get(position).getId() != 0) {
                onAmadeusOrderClick.onOrderItemClick(String.valueOf(ordersList.get(position).getId()), ordersList.get(position).getOrderCount());
            } else
                onAmadeusOrderClick.onOrderItemClick(ordersList.get(position).getItemName(), ordersList.get(position).getOrderCount());
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgServiceOrder, ivSubOrder, ivAddOrder;
        private TextView tvConfirmOrder, tvOrderCounter, tvServiceOrder;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgServiceOrder = itemView.findViewById(R.id.img_service_towel);
            ivSubOrder = itemView.findViewById(R.id.iv_sub_towel);
            ivAddOrder = itemView.findViewById(R.id.iv_add_towel);
            tvConfirmOrder = itemView.findViewById(R.id.tv_confirm_towel_order);
            tvOrderCounter = itemView.findViewById(R.id.tv_towel_counter);
            tvServiceOrder = itemView.findViewById(R.id.tv_service_towel);
        }
    }

    public interface OnAmadeusOrderClick{
        void onOrderItemClick(String orderName, int orderCount);
    }
}
