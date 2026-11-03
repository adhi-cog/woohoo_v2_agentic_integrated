package com.sb.woohoo.ui.main.fragments.fnb.products.pojo;

public class TaxData {
    private int mGst = 0;
    private int mConvenience = 0;
    private int mServiceTax = 0;

    private int mCategoryId =0;
    private String mGstText = null;
    private String mServiceTaxText = null;
    private String mConvenienceText = null;

    public String getTaxCurrency() {
        return mTaxCurrency;
    }

    public void setTaxCurrency(String aTaxCurrency) {
        this.mTaxCurrency = aTaxCurrency;
    }

    private String mTaxCurrency = null;

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int aCategoryId) {
        this.mCategoryId = aCategoryId;
    }

    public int getServiceTax() {
        return mServiceTax;
    }

    public void setServiceTax(int mServiceTax) {
        this.mServiceTax = mServiceTax;
    }

    public int getGst() {
        return mGst;
    }

    public void setGst(int mGst) {
        this.mGst = mGst;
    }

    public int getConvenience() {
        return mConvenience;
    }

    public void setConvenience(int mConvenience) {
        this.mConvenience = mConvenience;
    }

    public String getGstText() {
        return mGstText;
    }

    public void setGstText(String mGstText) {
        this.mGstText = mGstText;
    }

    public String getServiceTaxText() {
        return mServiceTaxText;
    }

    public void setServiceTaxText(String mServiceTaxText) {
        this.mServiceTaxText = mServiceTaxText;
    }

    public String getConvenienceText() {
        return mConvenienceText;
    }

    public void setConvenienceText(String mConvenienceText) {
        this.mConvenienceText = mConvenienceText;
    }

    public static TaxData getDefaultTax()
    {
        return new TaxData();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }

        if (o.getClass() == this.getClass()) {
            TaxData other = (TaxData) o;
            return other.getCategoryId() == (this.getCategoryId());
        } else {
            return false;
        }
    }
}
