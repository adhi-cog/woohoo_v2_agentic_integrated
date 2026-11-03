package com.sb.woohoo.ui.main.fragments.fnb.order.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.TaxData;

import java.util.List;

public class PlaceOrderRequest {

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @SerializedName("instructions")
    @Expose
    private String instructions;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("items")
    @Expose
    private List<CartItem> items;

    @SerializedName("taxes")
    @Expose
    private List<TaxData> taxes;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void setTaxData(List<TaxData> aTaxList) {
        taxes = aTaxList;
    }
}
