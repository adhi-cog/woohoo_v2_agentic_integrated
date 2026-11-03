package com.sb.woohoo.ui.main.fragments.amadeusServices.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AmadeusDynamicOrderRequest {
    @SerializedName("items")
    @Expose
    private List<Order> items = null;

    public List<Order> getItems() {
        return items;
    }

    public void setItems(List<Order> items) {
        this.items = items;
    }

    public static class Order {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("quantity")
        @Expose
        private int quantity;

        public Order(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
