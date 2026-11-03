package com.sb.woohoo.ui.main.fragments.fnb.products;

import static com.sb.woohoo.ui.main.helpers.NetworkHelper.NETWORK_SUCCESS_CODE;
import static com.sb.woohoo.utils.Constants.FNB_PARAMS.FRAGMENT_PRODUCT_TYPE;
import static com.sileria.android.Kit.getSystemService;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentFnbCategoriesBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.fnb.categories.FnBCategoryBannerAdapter;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.Banner;
import com.sb.woohoo.ui.main.fragments.fnb.leftpane.FnBLeftPaneFragment;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrderFragment;
import com.sb.woohoo.ui.main.fragments.fnb.products.di.DaggerFnBProductsComponent;
import com.sb.woohoo.ui.main.fragments.fnb.products.di.FnBProductsComponent;
import com.sb.woohoo.ui.main.fragments.fnb.products.di.FnBProductsModule;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Modifiers;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FnBProductsFragment extends
        FeatureFragment<MainActivity>
        implements FnBProductsContract.Views,
        NonPopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;

    private List<Product> productList = new ArrayList<>();
    private FnBProductsAdapter fnBProductsAdapter;
    private FnBCategoryBannerAdapter fnBCategoryBannerAdapter;
    private String CATEGORY_ID;
    private String SUB_CATEGORY_ID;
    private Boolean SUB_CATEGORY_TYPE;

    @Inject
    FnBProductsPresenter fnBProductsPresenter;
    private List<Banner> bannerList = new ArrayList<>();

    private Product selectedProduct;
    private View rootView;
    private FragmentFnbCategoriesBinding binding;

    public static FnBProductsFragment newInstance() {
        return new FnBProductsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFnbCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if (getArguments() != null) {
            SUB_CATEGORY_ID = getArguments().getString("sub_category_id");
            CATEGORY_ID = getArguments().getString("category_id");
            SUB_CATEGORY_TYPE = getArguments().getBoolean("category_sub_type");
            if (SUB_CATEGORY_TYPE) {
                fnBProductsPresenter.makeProductsRequestFromSubCategoryId(SUB_CATEGORY_ID);
            } else {
                fnBProductsPresenter.makeProductsRequestFromCategoryId(CATEGORY_ID);
            }
        }
    }

    private void initViews() {

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            fnBCategoryBannerAdapter = new FnBCategoryBannerAdapter(getParentActivity(), bannerList);
            binding.bannerViewpager.setAdapter(fnBCategoryBannerAdapter);
            binding.tabLayout.setupWithViewPager(binding.bannerViewpager);
        }
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fnBProductsAdapter = new FnBProductsAdapter(getParentActivity(), productList, getParentActivity().getSelectedProductsList(), (view, product, position) -> {

            if (view.getId() == R.id.imgAddZeroCart) {
                if (product.getModifiers() == null || product.getModifiers().size() < 1) {
                    addQuantity(product);
                } else {
                    showproductDetailDialog(product, position);
                }
            } else if (view.getId() == R.id.imgAdd) {
//                add quantity
                if (product.getModifiers() == null || product.getModifiers().size() < 1) {
                    addQuantity(product);
                } else {
                    showBottomDialog(product, position);
                }
            } else if (view.getId() == R.id.imgMinus) {
//                minus quantity
                reduceQuantity(product);
            } else if (view.getId() == R.id.txt_details) {
                showproductDetailDialog(product, position);
            }

        });
        binding.recyclerCategories.setAdapter(fnBProductsAdapter);

        updateCartView();
        binding.tvViewCart.setOnClickListener(v -> onCartClick());

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view -> onBack());
        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setOnClickListener(view -> onCartClick());
    }

    private void switchToOrderFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", CATEGORY_ID);
        bundle.putBoolean("category_sub_type", SUB_CATEGORY_TYPE);
        bundle.putString("sub_category_id", SUB_CATEGORY_ID);
        bundle.putString("fragment_type", FRAGMENT_PRODUCT_TYPE);
        FnBOrderFragment fnBOrderFragment = FnBOrderFragment.newInstance();
        fnBOrderFragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingAdd(fnBOrderFragment);
    }

    private void updateCartView() {
        binding.fragmentSettingsRelativeLayoutHeader.layoutCartContainer.setVisibility(View.VISIBLE);
        if (getParentActivity().getTotalCartCount() == 0) {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.GONE);
            binding.rlCartContainer.setVisibility(View.GONE);
        } else {
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setVisibility(View.VISIBLE);
            binding.fragmentSettingsRelativeLayoutHeader.txtCartCount.setText("" + getParentActivity().getTotalCartCount());
            binding.rlCartContainer.setVisibility(View.VISIBLE);
            binding.tvItems.setText(getParentActivity().getTotalCartCount() + " Item | ");
            binding.tvAmount.setText(getParentActivity().getSelectedProductsList().get(0).getCurrency() + " " + getParentActivity().getTotalPrice());
        }
    }

    private void reduceQuantity(Product product) {
        if (getParentActivity().isProductAvailableInCart(product)) {
            getParentActivity().removeProductToCart(product);
            fnBProductsAdapter.updateAdapter(productList);
            updateCartView();
        }
    }

    private void addQuantity(Product product) {
        if (getParentActivity().isAvailableInStock(product)) {
            getParentActivity().addProductToCart(product);
            fnBProductsAdapter.updateAdapter(productList);
            updateCartView();
        } else {
            Toast.makeText(getParentActivity(), getString(R.string.quantity_cant_select_more), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

        FnBProductsComponent fnBProductsComponent = DaggerFnBProductsComponent.builder()
                .fnBProductsModule(new FnBProductsModule(this)).build();
        fnBProductsComponent.injectFnBProductsFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fnb_products));
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return fnBProductsPresenter;
    }

    @Override
    public void errorMessage(int errorCode) {
        if (errorCode == NETWORK_SUCCESS_CODE) {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.item_not_available));
        } else {
            MethodUtils.showToastMessage(getContext(),  getString(R.string.try_again));
        }
        onBack();
    }

    @Override
    public void onSuccess(List<Product> productsList, List<Banner> list) {
        if (productsList != null && !productsList.isEmpty()) {
            productList = productsList;

            for (int i = 0; i < productList.size(); i++) {
                Product lItem = productsList.get(i);
                if (CATEGORY_ID != null && !CATEGORY_ID.isEmpty()) {
                    lItem.setCategoryId(Integer.parseInt(CATEGORY_ID));
                }
                //Getting crash because SUB_CATEGORY_ID == null
                if(SUB_CATEGORY_ID != null && !SUB_CATEGORY_ID.isEmpty()){
                    lItem.setSubCategoryId((SUB_CATEGORY_ID));
                }
                for (int j = 0; j < getParentActivity().getSelectedProductsList().size(); j++) {
                    Product product = getParentActivity().getSelectedProductsList().get(j);
                    if (productList.get(i).getId() == product.getId()) {
                        productList.get(i).setSelectedQuantity(product.getSelectedQuantity());
                        break;
                    }
                }
            }
            fnBProductsAdapter.updateLayoutItemHeight(binding.layoutCategories.getHeight() / 2);
            fnBProductsAdapter.updateAdapter(productList);

            ArrayList<String> listOfProducts = new ArrayList();
            for (Product product : productList) {
                listOfProducts.add(product.getName());
            }

        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FnBLeftPaneFragment fragment = FnBLeftPaneFragment.getInstance(FnBProductsFragment.class.getSimpleName());
            fragment.onDataFetched((ArrayList<Banner>) list);
        } else {
            if (list != null && !list.isEmpty()) {
                binding.imgBannerDefault.setVisibility(View.GONE);
                binding.bannerViewpager.setVisibility(View.VISIBLE);
                binding.tabLayout.setVisibility(View.VISIBLE);
                this.bannerList = list;
                fnBCategoryBannerAdapter.updateAdapter(bannerList);
            } else {
                binding.imgBannerDefault.setVisibility(View.VISIBLE);
                binding.bannerViewpager.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public String createTag() {
        return this.getClass().getSimpleName();
    }


    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    void onBack() {
        getParentActivity().onBackPressed();

    }

    void onCartClick() {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", CATEGORY_ID);
        bundle.putBoolean("category_sub_type", SUB_CATEGORY_TYPE);
        bundle.putString("sub_category_id", SUB_CATEGORY_ID);
        bundle.putString("fragment_type", FRAGMENT_PRODUCT_TYPE);
        FnBOrderFragment fnBOrderFragment = FnBOrderFragment.newInstance();
        fnBOrderFragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fnBOrderFragment);
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
        fnBProductsPresenter.presenterCleanUps();
    }
    FnBModifiersAdapter adapter;
    private void showproductDetailDialog(final Product product, int position) {

        resetModifiers(product);
        selectedProduct = product;
        double total_amt = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), R.style.Theme_Dialog);
        LayoutInflater inflater = LayoutInflater.from(getParentActivity());
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_product_details, null);

        builder.setCancelable(true);
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
            total_amt = selectedProduct.getSelectedQuantity() * Double.parseDouble(product.getPrice());
        }

        final double[] finalTotal_amt = {total_amt};
        adapter = new FnBModifiersAdapter(getActivity(), selectedProduct.modifiers, selectedProduct.getCurrency(), (isChecked, price) -> {
            if (isChecked)
                finalTotal_amt[0] += Integer.parseInt(price);
            else
                finalTotal_amt[0] -= Integer.parseInt(price);
            txtPrice.setText(selectedProduct.getCurrency() + finalTotal_amt[0]);
            product.setAmount(Double.toString(finalTotal_amt[0]));
        }, (modifiers) -> {
            MethodUtils.showToastMessage(getActivity(), String.format(getString(R.string.max_item),""+modifiers.maxSelection));
        }
        );
        rvModifiers.setAdapter(adapter);

        txtPrice.setText(selectedProduct.getCurrency() + total_amt);
        product.setAmount(Double.toString(total_amt));

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
                switchToOrderFragment();
            }
        });

        imgCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        tvAddSpecialInstructions.setOnClickListener(v -> {
            et_special_instructions.setVisibility(View.VISIBLE);
        });

        tvDone.setOnClickListener(v -> {
            boolean isMinSelectionSelected = true;
            for (int i = 0; i < product.getModifiers().size() && isMinSelectionSelected; i++) {
                Modifiers localModifiers = product.getModifiers().get(i);
                if (localModifiers.minSelection > localModifiers.numberOfCheckboxesChecked && localModifiers.isMandatory()) {
                    isMinSelectionSelected = false;
                    MethodUtils.showToastMessage(getActivity(), "Select Minimum " + localModifiers.minSelection + " item from " + localModifiers.getName());
                }
            }
            if (isMinSelectionSelected) {
                dialog.dismiss();
                adapter.notifyDataSetChanged();
                addQuantity(product);
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MethodUtils.setDialogMargins(dialog);
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        domainIdBasedUI(tvAddSpecialInstructions);
    }
    private void domainIdBasedUI(TextView tvAddSpecialInstructions) {
        SharedPrefsHelper sharedPrefsHelper = WooHooApp.getSharedPrefsHelper();
        String domainId = sharedPrefsHelper.get(SharedPrefsKey.PREF_DOMAIN_ID, "");
        if (domainId.equals(Constants.CKS_DOMAIN_MOCK_ROOM_ID)) {
            if(tvAddSpecialInstructions!=null){
                tvAddSpecialInstructions.setVisibility(View.GONE);
            }
        }
    }


    private void resetModifiers(Product product) {
        for (Modifiers m : product.getModifiers())
            for (Modifiers.ModifiersOptions option : m.getOptions())
                option.setIs_checked(false);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
            getView().getRootView().clearFocus();
        }
    }

    private void showBottomDialog(Product product, int position) {
        binding.containerBottomDialog.dialogBottom.setVisibility(View.VISIBLE);

        binding.containerBottomDialog.tvTitle.setText(getString(R.string.str_repeat_customization_for) + product.getName());
        binding.containerBottomDialog.tvOptions.setText(getSelectedModifiers(product));
        binding.containerBottomDialog.btnYes.setOnClickListener(v -> {
            addQuantity(product);
            binding.containerBottomDialog.dialogBottom.setVisibility(View.GONE);
        });
        binding.containerBottomDialog.btnChoose.setOnClickListener(v -> {
            showproductDetailDialog(product, position);
            binding.containerBottomDialog.dialogBottom.setVisibility(View.GONE);
        });
    }

    String getSelectedModifiers(Product product) {
        String options = "";
        Product lastAddedProduct = getParentActivity().getSelectedProductsList().get(getParentActivity().getSelectedProductsList().size() - 1);

        for (Modifiers m : lastAddedProduct.getModifiers()) {
            for (Modifiers.ModifiersOptions op : m.getOptions()) {
                if (op.getIs_checked())
                    options += op.getName() + ", ";
            }
        }
        return options;
    }
}
