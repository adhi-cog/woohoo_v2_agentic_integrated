package com.sb.woohoo.ui.main.fragments.fnb.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.EcommerceEvent;
import com.sb.woohoo.analytics.FnbEvent;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentFnbOrdersBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.order.di.DaggerFnBOrdersComponent;
import com.sb.woohoo.ui.main.fragments.fnb.order.di.FnBOrdersComponent;
import com.sb.woohoo.ui.main.fragments.fnb.order.di.FnBOrdersModule;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.CartItem;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.OrderData;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderRequest;
import com.sb.woohoo.ui.main.fragments.fnb.products.FnBModifiersAdapter;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Modifiers;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class FnBOrderFragment extends FeatureFragment<MainActivity> implements FnBOrdersContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private String CATEGORY_ID;
    private String SUB_CATEGORY_ID;
    private Boolean SUB_CATEGORY_TYPE;
    private String FRAGMENT_TYPE;
    private boolean isOrderPlacedSuccessfully = false;
    private FnBOrdersListAdapter fnBOrdersListAdapter;
    private boolean isRemoveProductDialogOpen = false;
    private boolean mIsOrderPlaced = false;
    @Inject
    AnalyticsHelper helper;

    @Inject
    FnBOrdersPresenter fnBOrdersPresenter;
    private FragmentFnbOrdersBinding binding;

    public static FnBOrderFragment newInstance() {
        FnBOrderFragment fnBOrderFragment = new FnBOrderFragment();
        return fnBOrderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.FnBOrder(), null);
        fnBOrdersPresenter.setTotalAmount(getParentActivity().getTotalPrice());
        binding = FragmentFnbOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isKeyboardOpen()) {
            hideKeyboard();
        }
        onClickEvents();
        if (getArguments() != null) {
            SUB_CATEGORY_ID = getArguments().getString("sub_category_id");
            CATEGORY_ID = getArguments().getString("category_id");
            SUB_CATEGORY_TYPE = getArguments().getBoolean("category_sub_type");
            FRAGMENT_TYPE = getArguments().getString("fragment_type");
        }
        initViews();
    }

    private void resetTotal(){
        fnBOrdersPresenter.setGrandTotal(0);
    }

    private void initViews() {

        ArrayList<String> listOfProducts = new ArrayList<String>();
        if (!getParentActivity().getSelectedProductsList().isEmpty()) {
            for (Product product : getParentActivity().getSelectedProductsList()) {
                listOfProducts.add(product.getName());
            }
            String lCurrency = getParentActivity().getSelectedProductsList().get(0).getCurrency();
            fnBOrdersPresenter.setCurrency(lCurrency);
        }


        binding.recyclerOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        fnBOrdersListAdapter = new FnBOrdersListAdapter(getParentActivity(), getParentActivity().getSelectedProductsList(), new FnBOrdersListAdapter.OnOrderItemClickedListener() {
            @Override
            public void onAdd(int position, Product product) {
                resetTotal();
                addProductQuantity(product);
            }

            @Override
            public void onRemove(int position, Product product) {
                resetTotal();
                removeProductInSelectedProductsList(product);
                helper.trackEcommerce(new EcommerceEvent.RemoveFromCart(product.getName(), Double.parseDouble(product.getPrice()), "" + product.getId()));
            }

            @Override
            public void onCustomise(int position, Product product) {
                if (product.getModifiers() == null || product.getModifiers().size() < 1)
                    Toast.makeText(getActivity(), R.string.msg_no_modifiers, Toast.LENGTH_LONG).show();
                else
                    showProductDetailDialog(product, position);
            }
        });
        binding.recyclerOrders.setAdapter(fnBOrdersListAdapter);

        ShowOrUpdateTaxAndTotal();
        domainIdBasedUI();
        helper.trackEcommerce(new EcommerceEvent.BeginCheckout(listOfProducts, getParentActivity().getTotalPrice()));
    }

    private void domainIdBasedUI() {
        SharedPrefsHelper sharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        String domainId = sharedPrefsHelper.get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID)) {
            binding.tvInstructionsForChef.setVisibility(View.GONE);
            binding.etSpecialInstructions.setVisibility(View.GONE);
        }
    }

    FnBModifiersAdapter adapter;
    private HashMap checkedList;

    private void showProductDetailDialog(final Product selectedProduct, int position) {

        getParentActivity().reduceInTotalPrice(selectedProduct);
        double total_amt = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), R.style.Theme_Dialog);
        LayoutInflater inflater = LayoutInflater.from(getParentActivity());
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_product_details, null);

        builder.setCancelable(false);
        TextView txtPrice = dialogLayout.findViewById(R.id.txt_price);
        TextView txt_title = dialogLayout.findViewById(R.id.txt_title);
        TextView txt_description = dialogLayout.findViewById(R.id.txt_description);
        TextView txtQuantity = dialogLayout.findViewById(R.id.txtQuantity);
        ImageView img_banner = dialogLayout.findViewById(R.id.img_banner);
        ImageView imgCancel = dialogLayout.findViewById(R.id.imgCancel);
        RecyclerView rvModifiers = dialogLayout.findViewById(R.id.rvModifiers);
        TextView tvDone = dialogLayout.findViewById(R.id.txt_done);
        LinearLayout layout_add_to_bag = dialogLayout.findViewById(R.id.layout_add_to_bag);
        TextView tvAddSpecialInstructions = dialogLayout.findViewById(R.id.txt_special_instructions);
        EditText et_special_instructions = dialogLayout.findViewById(R.id.et_special_instructions);
        txt_title.setText(getString(R.string.str_customise) + selectedProduct.getName());
        txt_description.setText(selectedProduct.getDescription());
        txtQuantity.setText("" + selectedProduct.getSelectedQuantity());
        et_special_instructions.setText(getParentActivity().getProductInstructions(selectedProduct));

        if (selectedProduct.getSelectedQuantity() <= 1) {
            total_amt = Double.parseDouble(selectedProduct.getPrice());
        } else {
            total_amt = selectedProduct.getSelectedQuantity() * Double.parseDouble(selectedProduct.getPrice());
        }

        final double[] finalTotal_amt = {total_amt};
        adapter = new FnBModifiersAdapter(getActivity(), selectedProduct.modifiers, selectedProduct.getCurrency(), (isChecked, price) -> {
            if (isChecked)
                finalTotal_amt[0] += Integer.parseInt(price);
            else
                finalTotal_amt[0] -= Integer.parseInt(price);
            txtPrice.setText(selectedProduct.getCurrency() + finalTotal_amt[0]);
            selectedProduct.setAmount(Double.toString(finalTotal_amt[0]));
        }, (modifiers) -> {

        });
        rvModifiers.setAdapter(adapter);


        txtPrice.setText(selectedProduct.getCurrency() + finalTotal_amt[0]);
        selectedProduct.setAmount(Double.toString(finalTotal_amt[0]));

        if (selectedProduct.getImages() != null && selectedProduct.getImages().size() > 0) {
            Picasso.get().load((String) selectedProduct.getImages().get(0)).into(img_banner);
            img_banner.setBackgroundColor(MethodUtils.getColorFromTheme(getParentActivity(), R.attr.color_primary));
        } else {
            img_banner.setBackgroundColor(MethodUtils.getColorFromTheme(getParentActivity(), R.attr.color_primary));
        }
        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(dialogInterface -> {
            Log.i("EditText", "hideKeyboard: onDismiss");
            hideKeyboard();
            selectedProduct.setInstructions(et_special_instructions.getText().toString().trim());
            getParentActivity().updateProductInstructions(selectedProduct);
        });

        layout_add_to_bag.setOnClickListener(view -> {
            if (selectedProduct.getSelectedQuantity() == 0) {
                Toast.makeText(getParentActivity(), getString(R.string.please_select_quantity), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                selectedProduct.setInstructions(et_special_instructions.getText().toString().trim());
                getParentActivity().updateProductInstructions(selectedProduct);
            }
        });

        imgCancel.setVisibility(View.GONE);
        imgCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        tvAddSpecialInstructions.setOnClickListener(v -> {
            et_special_instructions.setVisibility(View.VISIBLE);
        });

        tvDone.setOnClickListener(v -> {
            dialog.dismiss();
            adapter.notifyDataSetChanged();
            resetTotal();
            getParentActivity().addInTotalPrice(selectedProduct);
            updatePriceViewAndRecyclerView(selectedProduct);
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(dialog);
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void ShowOrUpdateTaxAndTotal()
    {
        checkNoteForSpecificDomain();
        if (getParentActivity().getSelectedProductsList().isEmpty())
        {
            binding.txtTotalValue.setVisibility(View.GONE);
            binding.hint1.setVisibility(View.GONE);
            resetTotal();
            binding.txtGrandTotalValue.setText(String.valueOf(fnBOrdersPresenter.getGrandTotal()));
            return;
        }

        fnBOrdersPresenter.updateTaxAndTotal();
    }


    private void checkNoteForSpecificDomain() {
        SharedPrefsHelper sharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        String domainId = sharedPrefsHelper.get(SharedPrefsKey.PREF_DOMAIN_ID, "");

        if (domainId.equals(Constants.POSTILLION_DOMAIN_ID)) {
            if (binding.txtConvinencesCharges.getVisibility() == View.GONE &&
                    binding.txtServiceChargeVal.getVisibility() == View.GONE &&
                    binding.txtGst.getVisibility() == View.GONE) {
                binding.tvAdditionalCharges.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        FnBOrdersComponent fnBOrdersComponent = DaggerFnBOrdersComponent.builder()
                .fnBOrdersModule(new FnBOrdersModule(this)).build();
        fnBOrdersComponent.injectFnBOrdersFragment(this);
    }

    @Override
    public void bindViews(View view) {
        helper.trackFnb(new FnbEvent.FnbCartPresented());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_confirm_order));
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void onClickEvents() {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBack());
        binding.btnAddMore.setOnClickListener(view -> onAddMore());
        binding.btnPlaceOrder.setOnClickListener(view -> onPlaceOrder());
    }

    void onBack() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("category_sub_type", SUB_CATEGORY_TYPE);
        bundle.putString("sub_category_id", SUB_CATEGORY_ID);
        bundle.putString("category_id", CATEGORY_ID);

        if (isKeyboardOpen()) {
            hideKeyboard();
        }
        getParentActivity().onBackPressed();
        helper.trackFnb(new FnbEvent.FnbBackPressed(mIsOrderPlaced, getClass().getSimpleName()));
    }

    private void hideKeyboard() {
        if (getView() != null) {
            InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
        }
    }

    private boolean isKeyboardOpen() {
        InputMethodManager imm = (InputMethodManager) getParentActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    void onAddMore() {
        onBack();
    }

    void onPlaceOrder() {
        if (getParentActivity().getSelectedProductsList().isEmpty()) {
            Toast.makeText(getParentActivity(), getString(R.string.place_order_failed), Toast.LENGTH_SHORT).show();
        } else {
            PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();
            //As per backend data total amount should be grandTotal(Including tax)
            placeOrderRequest.setAmount(String.valueOf(fnBOrdersPresenter.getGrandTotal()));
            placeOrderRequest.setCurrency(fnBOrdersPresenter.getCurrency());
            placeOrderRequest.setItems(prepareItemList(getParentActivity().getSelectedProductsList()));
            placeOrderRequest.setTaxData(getParentActivity().getTaxList());
            placeOrderRequest.setInstructions(  binding.etSpecialInstructions.getEditableText().toString());
            fnBOrdersPresenter.makePlaceOrderRequest(placeOrderRequest);
        }
//        getParentActivity().onBackPressed();
    }

    private List<CartItem> prepareItemList(ArrayList<Product> selectedProductsList) {
        ArrayList<CartItem> items = new ArrayList<>();
        for (Product product : selectedProductsList) {
            CartItem item = new CartItem();
            item.id = product.getId();
            item.quantity = product.getSelectedQuantity();
            item.instructions = product.getInstructions();

            ArrayList<CartItem.CartItemModifiers> cartModifiers = new ArrayList<>();
            for (Modifiers m : product.getModifiers()) {
                CartItem.CartItemModifiers modifier = new CartItem.CartItemModifiers();
                modifier.id = m.id;

                ArrayList<String> opts = new ArrayList<>();
                for (Modifiers.ModifiersOptions op : m.getOptions()) {
                    if (op.is_checked) {
                        if (op.getOption_id() != null)
                            opts.add(op.getOption_id());
                        else
                            opts.add(op.getName());
                    }
                }
                modifier.options = opts;
                cartModifiers.add(modifier);
            }
            item.modifiers = cartModifiers;
            items.add(item);
        }
        return items;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().stopTextToSpeech(false);
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        fnBOrdersPresenter.presenterCleanUps();
    }

    @Override
    public void errorMessage() {
        helper.trackFnb(new FnbEvent.FnbCartPlacedError(-1, CATEGORY_ID, SUB_CATEGORY_ID,
                fnBOrdersPresenter.getCurrency(), fnBOrdersPresenter.getTotalAmount()));
        MethodUtils.showToastMessage(getContext(), getString(R.string.no_result_fetched));
        getParentActivity().onBackPressed();
    }

    @Override
    public void onSuccess(String successMessage, OrderData orderData) {
//        Toast.makeText(getActivity(), successMessage, Toast.LENGTH_SHORT).show();
        helper.trackFnb(new FnbEvent.FnbCartPlacedSuccess(successMessage));
        isOrderPlacedSuccessfully = true;
        binding.layoutOrderDetails.setVisibility(View.GONE);
        binding.layoutOrderPlaced.setVisibility(View.VISIBLE);
        ArrayList<String> listOfProducts = new ArrayList();
        for (Product product : orderData.getItems()) {
            listOfProducts.add(product.getName());
        }
        mIsOrderPlaced = true;
        helper.trackEcommerce(new EcommerceEvent.Purchase(listOfProducts, "" + orderData.getId(), getParentActivity().getTotalPrice()));
        getParentActivity().resetSelectedData();
//        getParentActivity().onBackPressed();
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }


    void addProductQuantity(Product product) {
        if (getParentActivity().isAvailableInStock(product)) {
            //getParentActivity().addProductToCart(product);
            getParentActivity().addProductToCartFromOrderFrag(product);
            updatePriceViewAndRecyclerView(product);
        } else {
            Toast.makeText(getParentActivity(), getString(R.string.quantity_cant_select_more), Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePriceViewAndRecyclerView(Product product) {
        ShowOrUpdateTaxAndTotal();
        if (getParentActivity().getSelectedProductsList().isEmpty()) {
            fnBOrdersListAdapter.updateAdapter(new ArrayList<>());
        } else {
            fnBOrdersListAdapter.updateAdapter(getParentActivity().getSelectedProductsList());
        }
    }

    public void removeProductInSelectedProductsList(Product product) {

        if (getParentActivity().isProductAvailableInCart(product)) {
            //getParentActivity().removeProductToCart(product);
            getParentActivity().removeProductToCartFromOrderFrag(product);
            updatePriceViewAndRecyclerView(product);
        }

        /*MainActivity.UPDATE_QUANTITY update_quantity = getParentActivity().removeProductInSelectedProductsList(product);
        if (!isRemoveProductDialogOpen && update_quantity == MainActivity.UPDATE_QUANTITY.QUANTITY_ZERO) {
            showAlertDialogToRemoveProduct(product);
        } else if (update_quantity == MainActivity.UPDATE_QUANTITY.QUANTITY_REMOVED) {
            Product selectedProduct = getParentActivity().getSelectedProductsMap().get(product.getId());
            updatePriceViewAndRecyclerView(selectedProduct);
        }*/
    }

    private void showAlertDialogToRemoveProduct(Product product) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getParentActivity(), R.style.AlertDialogCustom);
        alertDialog.setMessage(getString(R.string.want_to_remove_product_from_cart));
        alertDialog.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            getParentActivity().reduceInTotalPrice(product);
            getParentActivity().removeProductFromList(product);
            updatePriceViewAndRecyclerView(product);
            isRemoveProductDialogOpen = false;
        });

        alertDialog.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            isRemoveProductDialogOpen = false;
        });

        alertDialog.setCancelable(false);

        AlertDialog dialog = alertDialog.create();
        //2. now setup to change color of the button
//        dialog.setOnShowListener(arg0 -> {
//            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getParentActivity(), R.color.colorPrimaryDark));
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getParentActivity(), R.color.colorPrimaryDark));
//        });
        isRemoveProductDialogOpen = true;
        dialog.show();
    }

    public void updateGrandTotal() {
        binding.txtGrandTotalValue.setText(String.format("%s %s",
                fnBOrdersPresenter.getCurrency(),
                String.format(Locale.US, "%.2f", fnBOrdersPresenter.getGrandTotal())));
    }

    public void updateDeliveryCharges(double aConveTotal)
    {
        binding.txtDeliveryChargesVal.setText(String.format("%s %s",
                fnBOrdersPresenter.getCurrency(),
                String.format(Locale.US, "%.2f", aConveTotal)));
    }

    public void setTotalAmountVisible()
    {
        binding.txtTotalValue.setVisibility(View.VISIBLE);
        binding.hint1.setVisibility(View.VISIBLE);
    }

    public void setTotalValue(double aTotalAmount)
    {
        binding.txtTotalValue.setText(String.format("%s %s",
                fnBOrdersPresenter.getCurrency(),
                String.format(Locale.US, "%.2f", aTotalAmount)));

        if(fnBOrdersPresenter.getFinalTax() > 0)
        {
            binding.txtGstVal.setText(String.format("%s %s",
                    fnBOrdersPresenter.getCurrency(),
                    String.format(Locale.US, "%.2f", fnBOrdersPresenter.getFinalTax())));

            binding.txtGst.setText(requireContext().getString(R.string.total_tax));

            binding.txtGstVal.setVisibility(View.VISIBLE);
            binding.txtGst.setVisibility(View.VISIBLE);
        }else{
            binding.txtGstVal.setVisibility(View.GONE);
            binding.txtGst.setVisibility(View.GONE);
        }
    }
}
