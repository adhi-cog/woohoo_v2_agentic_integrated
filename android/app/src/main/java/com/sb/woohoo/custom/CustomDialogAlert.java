package com.sb.woohoo.custom;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static com.sb.woohoo.utils.others.MethodUtils.setActivityFullScreen;
import static com.sileria.android.Resource.getResources;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.databinding.CustomDialogAlertBinding;
import com.sb.woohoo.ui.main.CartListAdapter;
import com.sb.woohoo.ui.main.helpers.RecyclerItemTouchHelper;
import com.sb.woohoo.ui.main.pojo.RemoteAlertModel;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogAlert extends Dialog implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    CartListAdapter mAdapter;
    private Context context;
    private List<RemoteAlertModel.DataEntity> alertModel = new ArrayList<>();

    public CustomDialogAlert(@NonNull Context context, int themeResId, RemoteAlertModel.DataEntity alertModel) {
        super(context, themeResId);
        this.context = context;
        this.c = (Activity)context;
        this.alertModel.add(alertModel);
        setActivityFullScreen(c);

    }

    private Activity c;

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        setActivityFullScreen(c);
        super.setOnCancelListener(listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        setActivityFullScreen(c);
        super.setOnDismissListener(listener);
    }

    @Override
    public void dismiss() {
        setActivityFullScreen(c);
        super.dismiss();
    }

    @Override
    public void cancel() {
        setActivityFullScreen(c);
        super.cancel();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(this);
        }
        CustomDialogAlertBinding binding = CustomDialogAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            setDecorations();
        }
        setCancelable(true);
        prepareCart();

        setActivityFullScreen(c);


        mAdapter = new CartListAdapter(context, alertModel);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView);


        // making http call and fetching menu json


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CartListAdapter.MyViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(binding.recyclerView);

    }

    private void setDecorations() {
        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.gravity = Gravity.TOP;
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.getDecorView().setSystemUiVisibility(View.GONE);
            window.getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            setActivityFullScreen(window);
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
//            String name = cartList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final RemoteAlertModel.DataEntity deletedItem = alertModel.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            //mAdapter.restoreItem(deletedItem, deletedIndex);

            if (mAdapter.cartList == null || mAdapter.cartList.size() == 0) {
                dismiss();
            }
        }
    }

    private void prepareCart() {
    }

    public void updateData(RemoteAlertModel.DataEntity data) {
        this.alertModel.add(data);
        mAdapter.notifyDataSetChanged();
    }
}
