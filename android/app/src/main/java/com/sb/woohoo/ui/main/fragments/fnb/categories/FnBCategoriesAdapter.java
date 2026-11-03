package com.sb.woohoo.ui.main.fragments.fnb.categories;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.pojo.nlp.Context;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FnBCategoriesAdapter extends RecyclerView.Adapter<FnBCategoriesAdapter.MyViewHolder> {

    private List<FnBCategoriesResponse.Category> categoriesList;
    private Activity context;
    private CategoryClickedListener categoryClickedListener;
    private int mHeight = 0;

    public FnBCategoriesAdapter(Activity context, List<FnBCategoriesResponse.Category> categoriesList, CategoryClickedListener categoryClickedListener){
        this.context = context;
        this.categoriesList = categoriesList;
        this.categoryClickedListener = categoryClickedListener;
    }

    public void updateAdapter(List<FnBCategoriesResponse.Category> list){
        categoriesList =  list;
        notifyDataSetChanged();
    }

    public void updateLayoutItemHeight(int height){
        this.mHeight = height;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fnb_categories, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FnBCategoriesResponse.Category category= categoriesList.get(position);
        if (category.getName()!=null)
            holder.txtCategory.setText(category.getName());

        if (category.getImages() !=null && category.getImages().size() > 0){
            Picasso.get().load(category.getImages().get(0)).into(holder.imgCategory);
            holder.imgCategory.setBackgroundColor(MethodUtils.getColorFromTheme(context, R.attr.color_primary));
        }else{
            holder.imgCategory.setBackgroundColor(MethodUtils.getColorFromTheme(context, R.attr.color_primary));
        }

        holder.imgCategory.setOnClickListener(view -> {
            if (TextUtils.isEmpty(category.getIsEnabled()) || Boolean.parseBoolean(category.getIsEnabled()))
                categoryClickedListener.onCategoryClicked(position,category);
        });

        if (mHeight != 0) {
            Log.e("item_category","mHeight - " + mHeight);
            ViewGroup.LayoutParams params = holder.item_category.getLayoutParams();
            params.height = mHeight;
//            params.width = holder.item_category.getWidth();
            holder.item_category.setLayoutParams(params);
        }
        if (!TextUtils.isEmpty(category.getIsEnabled()) && !Boolean.parseBoolean(category.getIsEnabled())) {
            holder.viewItemDisabled.setVisibility(View.VISIBLE);
            holder.txtOpenAt.setText("Opening at "+category.getTimings().get(0).getStart_time());
        } else {
            holder.viewItemDisabled.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCategory;
        private TextView txtCategory;
        private RelativeLayout item_category;
        private RelativeLayout viewItemDisabled;
        private TextView txtOpenAt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            item_category = itemView.findViewById(R.id.item_category);
            viewItemDisabled = itemView.findViewById(R.id.view_item_disabled);
            txtOpenAt = itemView.findViewById(R.id.txt_open_at);

        }
    }

    public interface CategoryClickedListener{
        void onCategoryClicked(int position , FnBCategoriesResponse.Category category);
    }
}
