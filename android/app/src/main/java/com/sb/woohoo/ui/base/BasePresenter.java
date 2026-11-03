package com.sb.woohoo.ui.base;

import android.os.Bundle;

import com.sb.woohoo.ui.main.pojo.IntentInfo;

/**
 * Created by Shrey on 22/02/18.
 */

public abstract class BasePresenter implements BaseContract.Actions {

    public static final String DATA_KEY = "data_key";
    private FeatureFragment view;

    public abstract void processActions(IntentInfo intentInfo);

    public void processActions() {
        // TODO null checks
        Bundle arguments = getView().getArguments();
        if (arguments != null) {
            if (arguments.containsKey(DATA_KEY)) {
                IntentInfo intentInfo = (IntentInfo) arguments.getSerializable(DATA_KEY);
                processActions(intentInfo);
            }
        } else {
            processActions(null);
        }
    }

    public FeatureFragment getView() {
        return view;
    }

    @Override
    public void attachView(FeatureFragment view) {
        this.view = view;
    }

    public IntentInfo getIntentInfo() {
        Bundle arguments = getView().getArguments();
        if (arguments != null) {
            if (arguments.containsKey(DATA_KEY)) {
                return (IntentInfo) arguments.getSerializable(DATA_KEY);
            }
        }
        return null;
    }

    //TODO make this abstract later
    public void presenterCleanUps() {
    }
}
