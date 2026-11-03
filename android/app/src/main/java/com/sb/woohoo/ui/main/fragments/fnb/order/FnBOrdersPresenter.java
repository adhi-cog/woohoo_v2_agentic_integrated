package com.sb.woohoo.ui.main.fragments.fnb.order;

import androidx.annotation.Nullable;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.OrderData;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderRequest;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.TaxData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import javax.inject.Inject;

public class FnBOrdersPresenter extends BasePresenter implements FnBOrdersContract.Actions {

    private FnBOrdersRepository fnBOrdersRepository;
    private FnBOrderFragment mView;
    private double mTaxFinal = 0;
    private double mGrandTotal;
    private String mCurrency = "";
    private double mTotalAmount = 0f;

    @Inject
    public FnBOrdersPresenter(FnBOrdersRepository fnBOrdersRepository, FnBOrderFragment mView) {
        this.fnBOrdersRepository = fnBOrdersRepository;
        this.mView = mView;
        fnBOrdersRepository.onAttach(this);
    }

    public void makePlaceOrderRequest(PlaceOrderRequest placeOrderRequest){
        fnBOrdersRepository.makePlaceOrderRequest(placeOrderRequest);
        mView.showProgress();
    }
    @Override
    public void processActions(IntentInfo intentInfo) {

    }

    @Override
    public void onSuccess(String successMessage, @Nullable OrderData orderData) {
        mView.onSuccess(successMessage, orderData);
        mView.hideProgress();
    }


    @Override
    public void errorMessage() {
        mView.errorMessage();
        mView.hideProgress();
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        fnBOrdersRepository.repositoryCleanUps();
    }

    public void updateTaxAndTotal()
    {
        mView.setTotalAmountVisible();

        for(int i=0; i<mView.getParentActivity().getSelectedProductsList().size(); i++)
        {
            Product lProduct = mView.getParentActivity().getSelectedProductsList().get(i);
            TaxData lTax = mView.getParentActivity().getTaxDataFromList(lProduct);

            updateTotal(lProduct);
            calculateTax(lTax);
        }

        double lTotalAmount = mView.getParentActivity().getTotalPrice();

        mView.setTotalValue(lTotalAmount);
    }

    private void updateTotal(Product aProduct) {
        mTotalAmount = aProduct.getSelectedQuantity() * Double.parseDouble(aProduct.getPrice());
    }

    private void calculateTax(TaxData lTax)
    {
        int lConve = lTax.getConvenience();
        int lServiceTax = lTax.getServiceTax();
        int lGst = lTax.getGst();
        double lConveTotal = 0;
        double lServiceTaxTotal = 0;
        double lGstTaxTotal = 0;

        String lConvText = lTax.getConvenienceText();
        String lServiceText = lTax.getServiceTaxText();
        String lGstText = lTax.getGstText();

        LoggerWrapper.d("FNBOrderFrag", " mTotalAmount  =  " + mTotalAmount);
        LoggerWrapper.d("FNBOrderFrag", "lConveTotal Fee  =  " + lConve);
        LoggerWrapper.d("FNBOrderFrag", "lServiceTax Fee  =  " + lServiceTax);
        LoggerWrapper.d("FNBOrderFrag", "lGst Fee  =  " + lGst);
        LoggerWrapper.d("FNBOrderFrag", "lConve Name  =  " + lConvText);
        LoggerWrapper.d("FNBOrderFrag", "lServiceTax Name  =  " + lServiceText);
        LoggerWrapper.d("FNBOrderFrag", "lGst Name  =  " + lGstText);

        //ConvenienceFee Calculation.......
        lConveTotal = (lConve / 100.0f) * mTotalAmount;

        mView.updateDeliveryCharges(lConveTotal);

        //ServiceTax Calculation......
        double lSTax = mTotalAmount + lConveTotal;
        lServiceTaxTotal = (lServiceTax / 100.0f) * lSTax;

        //GST CALCULATION.........
        double lGTax = mTotalAmount + lConveTotal + lServiceTaxTotal;
        lGstTaxTotal = (lGst / 100.0f) * lGTax;

        mGrandTotal = mGrandTotal + mTotalAmount + lConveTotal + lServiceTaxTotal + lGstTaxTotal;
        mView.updateGrandTotal();

    }

    public double getFinalTax() {
        return mTaxFinal;
    }

    public double getGrandTotal() {
        return mGrandTotal;
    }

    public double getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(double aTotalAmount) {
        this.mTotalAmount = aTotalAmount;
    }

    public void setGrandTotal(double aGAmount) {
        this.mGrandTotal = aGAmount;
    }

    public void setCurrency(String aCurrency) {
        this.mCurrency = aCurrency;
    }

    public String getCurrency() {
        return this.mCurrency ;
    }
}

