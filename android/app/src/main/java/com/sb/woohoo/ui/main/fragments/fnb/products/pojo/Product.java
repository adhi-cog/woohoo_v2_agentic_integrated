package com.sb.woohoo.ui.main.fragments.fnb.products.pojo;

import android.app.Activity;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.fnb.order.FnBOrdersListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Product {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("images")
    @Expose
    private List<Object> images = null;
    @SerializedName("stock")
    @Expose
    private int stock;
    @SerializedName("quantity")
    @Expose
    private int selectedQuantity;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("categoryId")
    @Expose
    private int categoryId;
    @SerializedName("subCategoryId")
    @Expose
    private Object subCategoryId;
    @SerializedName("domainId")
    @Expose
    private int domainId;
    @SerializedName("propertyId")
    @Expose
    private Object propertyId;
    @SerializedName("instructions")
    @Expose
    private String instructions;

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Object getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Object subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Object propertyId) {
        this.propertyId = propertyId;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    /* -- Modifiers -- */
    public ArrayList<Modifiers> modifiers;

    public ArrayList<Modifiers> getModifiers() {
        return modifiers;
    }

    public void setModifiers(ArrayList<Modifiers> modifiers) {
        this.modifiers = modifiers;
    }

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.description = product.description;
        this.tags = product.tags;
        this.stock = product.stock;
        this.images = product.images;
        this.amount = product.amount;
        this.price = product.price;
        this.currency = product.currency;
        this.createdAt = product.createdAt;
        this.updatedAt = product.updatedAt;
        this.categoryId = product.categoryId;
        this.subCategoryId = product.categoryId;
        this.domainId = product.domainId;
        this.propertyId = product.propertyId;
        this.instructions = product.instructions;
        this.selectedQuantity = product.selectedQuantity;
        this.modifiers = product.getModifiersList(product.modifiers);
    }

    private ArrayList<Modifiers> getModifiersList(ArrayList<Modifiers> modifiers) {
        ArrayList<Modifiers> modifiers1 = new ArrayList<>();
        for(Modifiers m : modifiers)
            modifiers1.add(new Modifiers(m));
        return modifiers1;
    }

    @Override
    public Object clone() {
        Product product = null;
        try {
            product = (Product) super.clone();
        } catch (CloneNotSupportedException e) {
            /*product = new Product(
                    this.getFirstName(), this.getLastName(), this.getAddress());*/
        }
        product.modifiers = (ArrayList<Modifiers>) this.modifiers.clone();
        return product;
    }

    public void taxCalculation(Activity context, FnBOrdersListAdapter.MyViewHolder holder)
    {
        double lTotalPrice;
        if(getAmount()!= null) {
            lTotalPrice = (Double.parseDouble(getAmount()) * getSelectedQuantity());
        } else {
            lTotalPrice = (Double.parseDouble(getPrice()) * getSelectedQuantity());
        }
        TaxData lTaxData = ((MainActivity) context).getTaxDataFromList(this);
        int lConve = lTaxData.getConvenience();
        int lServiceTax = lTaxData.getServiceTax();
        int lGst = lTaxData.getGst();
        double lConveTotal = 0;
        double lServiceTaxTotal = 0;
        double lGstTaxTotal = 0;

        String lConvText = lTaxData.getConvenienceText();
        String lServiceText = lTaxData.getServiceTaxText();
        String lGstText = lTaxData.getGstText();

        if (lTotalPrice <= 0) {
            return;
        }

        //ConvenienceFee Calculation.......
        lConveTotal = (lConve / 100.0f) * lTotalPrice;

        holder.txtDeliveryChargesVal.setText(String.format("%s %s",
                getCurrency(),
                String.format(Locale.US, "%.2f", lConveTotal)));

        LoggerWrapper.d("FNBOrderFrag", "ConvenienceFee Calculation  =  " + lConveTotal);

        if (lConvText != null)
        {
            if (lConve > 0)
            {
                holder.txtConvinencesCharges.setText(
                        context.getString(R.string.tax_names,
                                lConvText, lConve));
            } else {
                holder.txtConvinencesCharges.setText(lConvText);
            }
            holder.txtDeliveryChargesVal.setVisibility(View.VISIBLE);
            holder.txtConvinencesCharges.setVisibility(View.VISIBLE);
        } else {
            holder.txtDeliveryChargesVal.setVisibility(View.GONE);
            holder.txtConvinencesCharges.setVisibility(View.GONE);
        }

        //ServiceTax Calculation......
        double lSTax = lTotalPrice + lConveTotal;
        lServiceTaxTotal = (lServiceTax / 100.0f) * lSTax;

        LoggerWrapper.d("FNBOrderFrag", "ServiceTax Calculation  =  " + lServiceTaxTotal);

        holder.txtServiceChargeVal.setText(String.format("%s %s",
                getCurrency(),
                String.format(Locale.US, "%.2f", lServiceTaxTotal)));

        if (lServiceText != null) {
            if (lServiceTax > 0) {
                holder.txtServiceCharge.setText(
                        context.getString(R.string.tax_names,
                                lServiceText, lServiceTax));
            } else {
                holder.txtGst.setText(lGstText);
            }
            holder.txtServiceChargeVal.setVisibility(View.VISIBLE);
            holder.txtServiceCharge.setVisibility(View.VISIBLE);
        } else {
            holder.txtServiceChargeVal.setVisibility(View.GONE);
            holder.txtServiceCharge.setVisibility(View.GONE);
        }

        //GST CALCULATION.........
        double lGTax = lTotalPrice + lConveTotal + lServiceTaxTotal;
        lGstTaxTotal = (lGst / 100.0f) * lGTax;

        LoggerWrapper.d("FNBOrderFrag", "GST Calculation  =  " + lGstTaxTotal);

        holder.txtGstVal.setText(String.format("%s %s",
                getCurrency(),
                String.format(Locale.US, "%.2f", lGstTaxTotal)));

        if (lGstText != null) {
            if (lGst > 0) {
                holder.txtGst.setText(
                        context.getString(R.string.tax_names,
                                lGstText, lGst));
            } else {
                holder.txtGst.setText(lGstText);
            }
            holder.txtGstVal.setVisibility(View.VISIBLE);
            holder.txtGst.setVisibility(View.VISIBLE);
        } else {
            holder.txtGstVal.setVisibility(View.GONE);
            holder.txtGst.setVisibility(View.GONE);
        }

        holder.txtPrice.setText(String.format("%s %s",
                getCurrency(),
                String.format(Locale.US, "%.2f", lTotalPrice)));
    }
}
