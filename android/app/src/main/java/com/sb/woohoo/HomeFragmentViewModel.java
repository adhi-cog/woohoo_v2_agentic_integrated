package com.sb.woohoo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeFragmentViewModel extends ViewModel {


    public MutableLiveData<Boolean> isBluetoothCheck = new MutableLiveData<>();

    public void checkBluetoothStatus(Boolean isBluetoothCheck) {
        this.isBluetoothCheck.postValue(isBluetoothCheck);
    }
}
