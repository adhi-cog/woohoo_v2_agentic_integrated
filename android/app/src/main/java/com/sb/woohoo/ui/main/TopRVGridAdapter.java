package com.sb.woohoo.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.custom.SwipeAndDragHelper;
import com.sb.woohoo.databinding.LayoutDrawerGridItemBinding;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;

import java.util.List;

public class TopRVGridAdapter extends RecyclerView.Adapter<TopRVGridAdapter.GridItemVH>
        implements SwipeAndDragHelper.ActionCompletionContract {

    private List<DrawerGridItems> drawerGridItemsList;
    private GridRVAdapter.GridItemClickListener gridItemClickListener;

    public TopRVGridAdapter(GridRVAdapter.GridItemClickListener gridItemClickListener, List<DrawerGridItems> drawerGridItemsList) {
        this.drawerGridItemsList = drawerGridItemsList;
        this.gridItemClickListener = gridItemClickListener;
    }

    @NonNull
    @Override
    public TopRVGridAdapter.GridItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutDrawerGridItemBinding binding = LayoutDrawerGridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TopRVGridAdapter.GridItemVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopRVGridAdapter.GridItemVH holder, int position) {
        holder.bindViews(drawerGridItemsList.get(position));
        holder.binding.rlDrawerGridItem.setTag(position);
    }

    @Override
    public int getItemCount() {
        return drawerGridItemsList != null ? drawerGridItemsList.size() : 0;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        orderList(oldPosition, newPosition);
        gridItemClickListener.onGridItemMoved(drawerGridItemsList);

    }

    private void orderList(int oldPosition, int newPosition) {
        DrawerGridItems drawerGridItemsOld = drawerGridItemsList.get(oldPosition);
        DrawerGridItems drawerGridItemsNew = drawerGridItemsList.get(newPosition);
        drawerGridItemsList.set(oldPosition, drawerGridItemsNew);
        drawerGridItemsList.set(newPosition, drawerGridItemsOld);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {

    }


    List<DrawerGridItems> getItemList() {
        return drawerGridItemsList;
    }

    public void updateList(List<DrawerGridItems> listSource) {
        this.drawerGridItemsList = listSource;
    }

    /****************************************{@link GridRVAdapter.GridItemClickListener}**********************************/

    public interface GridItemClickListener {

        void onGridItemSelected(DrawerGridItems drawerGridItems);

        void onGridItemMoved(List<DrawerGridItems> drawerGridItemsList);
    }

    /****************************************{@link GridRVAdapter.TopGridItemVH}***********************************************/
    public class GridItemVH extends RecyclerView.ViewHolder {

        public LayoutDrawerGridItemBinding binding;

        GridItemVH(LayoutDrawerGridItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DrawerGridItems drawerGridItems) {
//          if (mPicasso!=null){
//              mPicasso.load(drawerGridItems.getIcon())
//                      .transform(new CircleImageTransform())
//                      .into(ivIcon);
//          }
            binding.ivGridItemIcon.setImageResource(drawerGridItems.getIcon());
            binding.tvGridItemTitle.setText(drawerGridItems.getTitle());
            binding.rlDrawerGridItem.setOnClickListener(v -> {
                if (gridItemClickListener != null) {
                    gridItemClickListener.onGridItemSelected(drawerGridItems);
                }
            });
        }
    }
}
