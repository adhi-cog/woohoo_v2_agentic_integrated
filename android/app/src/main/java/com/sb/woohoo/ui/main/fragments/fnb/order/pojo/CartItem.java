package com.sb.woohoo.ui.main.fragments.fnb.order.pojo;

import java.util.ArrayList;

public class CartItem {
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public ArrayList<CartItemModifiers> getModifiers() {
        return modifiers;
    }

    public void setModifiers(ArrayList<CartItemModifiers> modifiers) {
        this.modifiers = modifiers;
    }

    public int id, quantity;
    public String instructions;
    public ArrayList<CartItemModifiers> modifiers;

    public static class CartItemModifiers {
        public int id;
        public ArrayList<String> options;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ArrayList<String> getOptions() {
            return options;
        }

        public void setOptions(ArrayList<String> options) {
            this.options = options;
        }
    }
}
