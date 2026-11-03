//package com.sb.woohoo.ui.main.fragments.home;
//
//import android.content.res.ColorStateList;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.sb.woohoo.R;
//import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//
//public class RVAppsAdapter extends RecyclerView.Adapter<RVAppsAdapter.AppItemViewHolder> {
//
//    private AppItemClickListener appItemClickListener;
//    private List<DrawerGridItems> appItemList =new ArrayList<>(0);
//
//    public RVAppsAdapter(AppItemClickListener appItemClickListener) {
//        this.appItemClickListener = appItemClickListener;
//    }
//
//    public void updateAppList(List<DrawerGridItems> drawerGridItemsList){
//        this.appItemList.clear();
//        this.appItemList.addAll(drawerGridItemsList);
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public AppItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new AppItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_app_item,parent,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AppItemViewHolder holder, int position) {
//        holder.bindViews(appItemList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return appItemList!=null?appItemList.size():0;
//    }
//
//    /*****************************{@link AppItemViewHolder}*************************************/
//
//    public class AppItemViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.fragment_home_image_view_app_icon)
//        ImageView mAppIcon;
//        @BindView(R.id.fragment_home_app_title)
//        TextView mAppTitle;
//        @BindView(R.id.ll_fragment_home_item_app)
//        LinearLayout itemRootLayout;
//
//        public AppItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ButterKnife.bind(this,itemView);
//        }
//
//        public void bindViews(DrawerGridItems drawerGridItems) {
//            if (drawerGridItems.getTitle().equalsIgnoreCase("HotSoS")){
//                mAppIcon.setPadding(0,30,0,30);
//                mAppIcon.setImageTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.white)));
//                mAppIcon.setImageResource(drawerGridItems.getIcon());
//            }else {
//                mAppIcon.setImageResource(drawerGridItems.getIcon());
//                mAppIcon.setPadding(0,0,0,0);
//                mAppIcon.setImageTintList(null);
//            }
//
//
//            mAppTitle.setText(drawerGridItems.getTitle());
//            itemRootLayout.setOnClickListener(v -> {
//                if (appItemClickListener!=null){
//                    appItemClickListener.onAppSelected(drawerGridItems);
//                }
//            });
//        }
//    }
//}
