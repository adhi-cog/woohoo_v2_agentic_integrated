package com.sb.woohoo.ui.main.fragments.fnb.history;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryData;

import java.util.ArrayList;

public class FnBHistoryListAdapter extends RecyclerView.Adapter<FnBHistoryListAdapter.MyViewHolder> {

    private ArrayList<FnBHistoryData> ordersList;
    private Activity context;

    public FnBHistoryListAdapter(Activity context, ArrayList<FnBHistoryData> ordersList){
        this.context = context;
        this.ordersList = ordersList;
    }

    public void updateAdapter(ArrayList<FnBHistoryData> list){
        ordersList =  list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FnBHistoryData item = ordersList.get(position);

        holder.tvOrderId.setText(String.format(context.getString(R.string.order_id), String.valueOf(item.getId())));
        holder.tvOrderDateTime.setText(item.getCreatedAt().substring(0,10) + " " + item.getCreatedAt().substring(11,16));  //2021-02-03T19:46:03.484Z
        holder.tvOrderStatus.setText(item.getCurrentStatus());
        holder.tvTotalPrice.setText(item.getCurrency() + " " + item.getAmount());

        holder.recyclerViewItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        FnBOrderItemsListAdapter adapter = new FnBOrderItemsListAdapter(context, item.getItems());
        holder.recyclerViewItems.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView recyclerViewItems;
        private TextView tvOrderId, tvOrderDateTime, tvOrderStatus, tvTotalPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDateTime = itemView.findViewById(R.id.tvOrderDateTime);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            recyclerViewItems = itemView.findViewById(R.id.recycler_view_items);
        }
    }



    class FnBOrderItemsListAdapter extends RecyclerView.Adapter<FnBOrderItemsListAdapter.MyViewHolder> {

        private ArrayList<FnBHistoryData.Items> itemsList;
        private Activity context;

        public FnBOrderItemsListAdapter(Activity context, ArrayList<FnBHistoryData.Items> itemsList) {
            this.context = context;
            this.itemsList = itemsList;
        }

        public void updateAdapter(ArrayList<FnBHistoryData.Items> list) {
            itemsList = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_order_items, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            FnBHistoryData.Items item = itemsList.get(position);

            holder.txtNameNQty.setText(item.getName() + " x " + item.getQuantity());
            float amount = Float.parseFloat(item.getPrice()) * Integer.parseInt(item.getQuantity());
            holder.txtRate.setText(item.getCurrency() + " " + amount);
           if(!TextUtils.isEmpty(item.getInstructions())) {
               holder.txtDescription.setVisibility(View.VISIBLE);
               holder.txtDescription.setText(item.getInstructions());
           } else {
               holder.txtDescription.setVisibility(View.GONE);
           }
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNameNQty, txtRate, txtDescription;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                txtNameNQty = itemView.findViewById(R.id.txtNameNQty);
                txtRate = itemView.findViewById(R.id.txtRate);
                txtDescription = itemView.findViewById(R.id.txtDescription);
            }
        }
    }
}
