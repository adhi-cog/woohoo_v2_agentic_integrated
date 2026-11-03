package com.sb.woohoo.ui.main.fragments.fnb.order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Modifiers;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

public class FnBOrdersListAdapter extends RecyclerView.Adapter<FnBOrdersListAdapter.MyViewHolder> {

    private List<Product> productsList;
    private Activity context;
    private OnOrderItemClickedListener onOrderItemClickedListener;

    public FnBOrdersListAdapter(Activity context, List<Product> productList, OnOrderItemClickedListener onOrderItemClickedListener) {
        this.context = context;
        this.productsList = productList;
        this.onOrderItemClickedListener = onOrderItemClickedListener;
    }

    public void updateAdapter(List<Product> list) {
        productsList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fnb_orders, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = productsList.get(position);
        product.taxCalculation(context, holder);

        if (product.getName() != null)
            holder.txtProductName.setText(product.getName());

        holder.txtQuantity.setText("" + product.getSelectedQuantity());

        holder.txtInstructions.setText(((MainActivity) context).getProductInstructions(product));

        holder.imgAdd.setOnClickListener(view -> onOrderItemClickedListener.onAdd(position, product));

        holder.imgRemove.setOnClickListener(view -> onOrderItemClickedListener.onRemove(position, product));

        holder.tvCustomise.setOnClickListener(view -> onOrderItemClickedListener.onCustomise(position, product));

        holder.txtInstructions.setOnClickListener(view -> showDialogForInstructions(holder.txtInstructions, product));

        String lModifierText = getSelectedModifiers(product);
        if(lModifierText.isEmpty()){
            holder.txtModifiers.setVisibility(View.GONE);
        }else{
            holder.txtModifiers.setText(lModifierText);
            holder.txtModifiers.setVisibility(View.VISIBLE);
        }
    }

    private void showDialogForInstructions(TextView txtInstructions, Product product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null);
        EditText editText = dialogLayout.findViewById(R.id.editText);
        builder.setCancelable(false);

        TextView buttonNegative = dialogLayout.findViewById(R.id.button_negative);
        TextView buttonPositive = dialogLayout.findViewById(R.id.button_positive);

        if (!txtInstructions.getText().toString().isEmpty()) {
            editText.setText(txtInstructions.getText().toString());
        }

        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();
        buttonPositive.setOnClickListener(v -> {
            hideKeyboard(editText);
            dialog.dismiss();
            product.setInstructions(editText.getText().toString());
            ((MainActivity) context).updateProductInstructions(product);
            txtInstructions.setText(editText.getText().toString());

        });

        buttonNegative.setOnClickListener(v -> {
            hideKeyboard(editText);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showDialog(TextView txtInstructions) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ALertDialogTheme);

        TextView textView = new TextView(context);
        textView.setText(context.getString(R.string.instructions_for_chef));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.WHITE);
        alertDialog.setCustomTitle(textView);
        alertDialog.setCancelable(false);
        final EditText input = new EditText(context);
        input.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stroke_edit_text));
        input.setMaxLines(4);
        input.setLines(4);
        input.setTextSize(16F);
        input.setCursorVisible(true);
        input.setTextColor(ContextCompat.getColor(context, R.color.white));
        input.setHintTextColor(ContextCompat.getColor(context, R.color.white));
        input.setHint(context.getString(R.string.enter_instruction));
        if (!txtInstructions.getText().toString().isEmpty()) {
            input.setText(txtInstructions.getText().toString());
        }
        input.setPadding(20, 20, 20, 20);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(100, 100, 100, 100);
        input.setLayoutParams(lp);
        alertDialog.setView(input); // uncomment this line
        alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideKeyboard(input);
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideKeyboard(input);
                dialogInterface.dismiss();
                txtInstructions.setText(input.getText().toString());
            }
        });

        AlertDialog dialog = alertDialog.create();
        //2. now setup to change color of the button
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, MethodUtils.getColorFromTheme(context, R.attr.color_secondary)));
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, MethodUtils.getColorFromTheme(context, R.attr.color_secondary)));
        });
        dialog.show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgAdd, imgRemove;
        public TextView txtProductName, txtQuantity, txtPrice, txtInstructions, txtModifiers, tvCustomise;
        public TextView txtConvinencesCharges, txtDeliveryChargesVal, txtServiceCharge, txtServiceChargeVal, txtGst, txtGstVal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtInstructions = itemView.findViewById(R.id.txtInstructions);
            txtModifiers = itemView.findViewById(R.id.txtModifiers);
            tvCustomise = itemView.findViewById(R.id.tvCustomise);

            txtConvinencesCharges = itemView.findViewById(R.id.txt_convinences_charges);
            txtDeliveryChargesVal = itemView.findViewById(R.id.txt_delivery_charges_val);

            txtServiceCharge = itemView.findViewById(R.id.txt_service_charge);
            txtServiceChargeVal = itemView.findViewById(R.id.txt_service_charge_val);

            txtGst = itemView.findViewById(R.id.txt_gst);
            txtGstVal = itemView.findViewById(R.id.txt_gst_val);

        }
    }

    public interface OnOrderItemClickedListener {
        void onAdd(int position, Product product);

        void onRemove(int position, Product product);
        void onCustomise(int position, Product product);
    }

    String getSelectedModifiers(Product product) {
        String modifiers = "";
        boolean isFirstTime = true;
        for (Modifiers m : product.getModifiers()) {
            boolean isModifierStrAdded = false;

            for (Modifiers.ModifiersOptions op : m.getOptions()) {
                if (op.getIs_checked()) {
                    if (isModifierStrAdded) {
                        modifiers += ", "+ op.getName() ;
                    } else {
                        if(isFirstTime) {
                            isFirstTime=false;
                        }
                        else{
                            modifiers += "\n";
                        }
                        modifiers += m.getName() + ": " + op.getName() ;
                        isModifierStrAdded = true;
                    }
                }
            }
        }
        return modifiers;
    }
}

