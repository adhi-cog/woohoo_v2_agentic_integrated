package com.sb.woohoo.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ravi on 26/09/17.
 */


public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {
    public List<RemoteAlertModel.DataEntity> cartList;
    private Context context;

    public CartListAdapter(Context context, List<RemoteAlertModel.DataEntity> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_dialog_alert, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RemoteAlertModel.DataEntity item = cartList.get(position);
        holder.name.setText(item.title);
        holder.displayText.setText(item.displayText);
        Picasso.get().load(item.mediaUrl).fit().into(holder.thumbnail);
        Picasso.get().load(item.mediaUrl).fit().into(holder.custom_dialog_big_image);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(RemoteAlertModel.DataEntity item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, displayText;
        public ImageView thumbnail;
        public RelativeLayout viewForeground;
        ImageView custom_dialog_big_image;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_custom_dialog_alert_name);
            thumbnail = view.findViewById(R.id.item_custom_dialog_alert_image);
            custom_dialog_big_image = view.findViewById(R.id.custom_dialog_big_image);
            viewForeground = view.findViewById(R.id.item_custom_dialog_alert_view_foreground);
            displayText = view.findViewById(R.id.description);
        }
    }
}
