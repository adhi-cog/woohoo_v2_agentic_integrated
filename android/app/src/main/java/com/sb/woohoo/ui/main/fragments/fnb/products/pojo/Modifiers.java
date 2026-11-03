package com.sb.woohoo.ui.main.fragments.fnb.products.pojo;

import java.util.ArrayList;

public class Modifiers {
    public int id, maxSelection, minSelection;
    public String name, description;
    public int numberOfCheckboxesChecked;

    public Modifiers(Modifiers m) {
        this.id = m.id;
        this.maxSelection = m.maxSelection;
        this.minSelection = m.minSelection;
        this.name = m.name;
        this.description = m.description;
        this.mandatory = m.mandatory;
        this.options = m.getOptions(m.options);
    }

    private ArrayList<ModifiersOptions> getOptions(ArrayList<ModifiersOptions> options) {
        ArrayList<ModifiersOptions> options1 = new ArrayList<>();
        for (ModifiersOptions op : options) {
            options1.add(new ModifiersOptions(op));
        }
        return options1;
    }

    public int getNumberOfCheckboxesChecked() {
        return numberOfCheckboxesChecked;
    }

    public void setNumberOfCheckboxesChecked(int numberOfCheckboxesChecked) {
        this.numberOfCheckboxesChecked = numberOfCheckboxesChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public int getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(int minSelection) {
        this.minSelection = minSelection;
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

    public ArrayList<ModifiersOptions> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<ModifiersOptions> options) {
        this.options = options;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public ArrayList<ModifiersOptions> options;
    public boolean mandatory;

    @Override
    public Object clone() {
        Modifiers modifiers = null;
        try {
            modifiers = (Modifiers) super.clone();
        } catch (CloneNotSupportedException e) {
            /*product = new Product(
                    this.getFirstName(), this.getLastName(), this.getAddress());*/
        }
        modifiers.options = (ArrayList<ModifiersOptions>) this.options.clone();
        return modifiers;
    }

    public class ModifiersOptions {
        public String name;
        public String price;
        public String option_id;

        public ModifiersOptions(ModifiersOptions op) {
            this.name = op.name;
            this.price = op.price;
            this.option_id = op.option_id;
            this.is_checked = op.is_checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOption_id() {
            return option_id;
        }

        public void setOption_id(String option_id) {
            this.option_id = option_id;
        }

        public boolean getIs_checked() {
            return is_checked;
        }

        public void setIs_checked(boolean is_checked) {
            this.is_checked = is_checked;
        }

        public boolean is_checked = false;

        @Override
        public Object clone() {
            ModifiersOptions options = null;
            try {
                options = (ModifiersOptions) super.clone();
            } catch (CloneNotSupportedException e) {
            /*product = new Product(
                    this.getFirstName(), this.getLastName(), this.getAddress());*/
            }
            return options;
        }
    }
}
