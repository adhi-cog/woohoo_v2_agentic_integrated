package com.sb.woohoo.ui.main;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.LayoutDrawerGridItemBinding;
import com.sb.woohoo.ui.main.pojo.DrawerGridItems;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class GridRVAdapter
        extends RecyclerView.Adapter<GridRVAdapter.TopGridItemVH> implements View.OnLongClickListener {

    private List<DrawerGridItems> drawerGridItemsList;
    private GridItemClickListener gridItemClickListener;
    private Picasso mPicasso;
    private Context context;
    private boolean isDropped = false;

    public GridRVAdapter(Context context, GridItemClickListener gridItemClickListener, List<DrawerGridItems> drawerGridItemsList, Picasso mPicasso) {
        this.gridItemClickListener = gridItemClickListener;
        this.drawerGridItemsList = drawerGridItemsList;
        this.mPicasso = mPicasso;
        this.context = context;
    }

    public GridRVAdapter(Context context, GridItemClickListener gridItemClickListener, List<DrawerGridItems> drawerGridItemsList) {
        this.drawerGridItemsList = drawerGridItemsList;
        this.gridItemClickListener = gridItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public TopGridItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutDrawerGridItemBinding binding = LayoutDrawerGridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TopGridItemVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopGridItemVH holder, @SuppressLint("RecyclerView") int position) {
        holder.bindViews(drawerGridItemsList.get(position));
        holder.binding.rlDrawerGridItem.setTag(position);
        holder.binding.rlDrawerGridItem.setOnLongClickListener(this);
        holder.binding.rlDrawerGridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gridItemClickListener != null) {
                    gridItemClickListener.onGridItemSelected(drawerGridItemsList.get(position));
                }
            }
        });
        holder.binding.rlDrawerGridItem.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        isDropped = true;
                        int positionTarget = -1;

                        View viewSource = (View) event.getLocalState();
                        int viewId = v.getId();
                        final int rlItem = R.id.rl_drawer_grid_item;
                        final int rvBottom = R.id.rv_bottom_grid;
                        final int rvHome = R.id.rv_top_grid_one;
                        switch (viewId) {
                            case rlItem:
                            case rvHome:
                            case rvBottom:

                                RecyclerView target;
                                switch (viewId) {
                                    case rvHome:
                                        target = v.getRootView().findViewById(rvHome);
                                    case rvBottom:
                                        target = v.getRootView().findViewById(rvBottom);
                                        break;
                                    default:
                                        target = (RecyclerView) v.getParent();
                                        positionTarget = (int) v.getTag();
                                }

                                if (viewSource != null) {
                                    RecyclerView source = (RecyclerView) viewSource.getParent();
//                                    if (target.getId() == rvBottom && source.getId() == rvBottom) {

                               /* if(positionTarget < FAV_APPS_COUNT) {
                                    //dragging not allowed to 1st row
                                    Toast.makeText(v.getContext(), v.getResources().getString(R.string.bottom_drawer_error1) , Toast.LENGTH_LONG).show();
                                  return true;
                                }*/

                                        GridRVAdapter adapterSource = (GridRVAdapter) source.getAdapter();
                                        GridRVAdapter adapterTarget = (GridRVAdapter) target.getAdapter();
                                        int positionSource = (int) viewSource.getTag();
                               /* if(positionSource < FAV_APPS_COUNT) {
                                    //dragging not allowed from 1st row
                                    Toast.makeText(v.getContext(),  v.getResources().getString(R.string.bottom_drawer_error2) , Toast.LENGTH_LONG).show();
                                    return true;
                                }*/

                                        List<DrawerGridItems> listSource = Objects.requireNonNull(adapterSource).getItemList();

                                        if (listSource != null
                                                && listSource.size() > 0
                                                && adapterSource.equals(adapterTarget)) {
                                            try {
//                                                LoggerWrapper.i(DragDropTopListener.class.getSimpleName(),"*****bottom "+positionSource+"  "+positionTarget);
                                                DrawerGridItems item1 = listSource.get(positionSource);
                                                DrawerGridItems item2 = listSource.get(positionTarget);
                                                listSource.set(positionSource, item2);
                                                listSource.set(positionTarget, item1);
                                                adapterSource.notifyDataSetChanged();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                    }
//                            else if (target.getId()==rvBottom){
//                                DrawerGridItems item1 = listSource.get(positionSource);
//                                List<DrawerGridItems> customListTarget = Objects.requireNonNull(adapterTarget).getItemList();
//                                if (positionTarget>=0){
//                                        customListTarget.set(positionTarget, item1);
//                                    }
//
//                                adapterTarget.updateList(customListTarget);
//                            }
                                }
                                break;
                        }
                        break;
                }

                if (!isDropped && event.getLocalState() != null) {
                    ((View) event.getLocalState()).setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawerGridItemsList.size();
    }

    public List<DrawerGridItems> getItemList() {
        return drawerGridItemsList;
    }

    public void updateList(List<DrawerGridItems> listSource) {
        this.drawerGridItemsList = listSource;
        notifyDataSetChanged();

    }

    @Override
    public boolean onLongClick(View v) {

        if (v.getParent() instanceof RecyclerView) {
//            if (((RecyclerView) v.getParent()).getId() == R.id.rv_bottom_grid) {

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(data, shadowBuilder, v, 0);
                } else {
                    v.startDrag(data, shadowBuilder, v, 0);
                }

//            }

        }
        return false;
    }

    /****************************************{@link GridItemClickListener}**********************************/

    public interface GridItemClickListener {

        void onGridItemSelected(DrawerGridItems drawerGridItems);

        void onGridItemMoved(List<DrawerGridItems> drawerGridItemsList);
    }

    /****************************************{@link TopGridItemVH}***********************************************/
    public class TopGridItemVH extends RecyclerView.ViewHolder {


        public LayoutDrawerGridItemBinding binding;

        TopGridItemVH(LayoutDrawerGridItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        public void bindViews(DrawerGridItems drawerGridItems) {
            if (drawerGridItems != null &&
                    drawerGridItems.getData() != null &&
                    drawerGridItems.getData().getImage() != null && drawerGridItems.getData().getImage().length() > 0) {
                mPicasso.load(drawerGridItems.getData().getImage())
                        .into(binding.ivGridItemIcon);
            } else {
                if (drawerGridItems.getDrawable() == null)
                    binding.ivGridItemIcon.setImageResource(drawerGridItems.getIcon());
                else
                    binding.ivGridItemIcon.setImageDrawable(drawerGridItems.getDrawable());
            }
            binding.tvGridItemTitle.setText(drawerGridItems.getTitle());
        }
    }
}
