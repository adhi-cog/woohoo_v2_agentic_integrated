package com.sb.woohoo.ui.base;

import static com.sileria.android.Kit.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class FeatureFragment<T extends BaseActivity> extends BaseFragment<T> implements IHintBarState, BaseContract.Views {

    private final String TAG = FeatureFragment.class.getSimpleName();
    private BasePresenter presenter;
    private IntentInfo intentInfo;

    public abstract void onIntentCreate(IntentInfo intentInfo);

    public void onNewIntent(IntentInfo intentInfo) {
        setInstance(intentInfo);
        BasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.processActions(intentInfo);
        }
    }

    public boolean isGpsSupported() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public abstract void initDagger();

    public abstract void bindViews(View view);

    public abstract String getCategory();

    private void changeHintBarState(int hintText, int backgroundColor, int visibility) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            parentActivity = getActivity();
        }
        if (parentActivity instanceof MainActivity) {
            // #FIXME: in surveillance mode sometimes, we get null instance when getActivity is called
            Activity activity = getActivity();
            if (activity != null) {
                ((MainActivity) Objects.requireNonNull(activity))
                        .changeHintBarState(hintText, backgroundColor, visibility);
            }
        } else {
            LoggerWrapper.e(TAG, "Parent Activity not attached!");
        }
    }

    private void setHintBarClickListener(View.OnClickListener hintBarClickListener) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            parentActivity = getActivity();
        }
        if (parentActivity instanceof MainActivity) {
            ((MainActivity) Objects.requireNonNull(getActivity())).setHintBarClickListener(hintBarClickListener);
        } else {
            LoggerWrapper.e(TAG, "Parent Activity not attached!");
        }
    }

    private void resetHintBarClickListener() {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            parentActivity = getActivity();
        }
        if (parentActivity instanceof MainActivity) {
            ((MainActivity) Objects.requireNonNull(getActivity())).resetHintBarClickListener();
        } else {
            LoggerWrapper.e(TAG, "Parent Activity not attached!");
        }
    }

    private void handleExplicitFollowUp(IntentInfo intentInfo, ExplicitFollowUp explicitFollowUp) {
        if (intentInfo != null && intentInfo.needsExplicitFollowUp() && explicitFollowUp != null) {
            explicitFollowUp.explicitFollowUpQuery(intentInfo);
        }
    }

    // Call this method in each instance of the fragment
    public void processData(IntentInfo intentInfo, ExplicitFollowUp explicitFollowUp) {
        handleExplicitFollowUp(intentInfo, explicitFollowUp);
        setInstance(intentInfo);
        onIntentCreate(intentInfo);
    }

    private void setInstance(IntentInfo intentInfo) {
        Bundle args = new Bundle();
        args.putSerializable(BasePresenter.DATA_KEY, intentInfo);
        setArguments(args);
        setIntentInfo(intentInfo);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachParent((T) getActivity());

        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initDagger();

        // FIXME : Better Implementation needed for VideoFragment.
        BasePresenter presenter = getPresenter();
        if (presenter != null) {
            // Set presenter instance
            setPresenter(presenter);
            // Set view reference for presenter
            presenter.attachView(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        // Process action from intent
        // FIXME : Better Implementation needed for VideoFragment.
        BasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.processActions();
        }
    }

    public IntentInfo getIntentInfo() {
        return intentInfo;
    }

    public void setIntentInfo(IntentInfo intentInfo) {
        this.intentInfo = intentInfo;
    }

    @Override
    public FeatureFragment getFragmentView() {
        return this;
    }

    @Override
    public void setArguments(Bundle args) {
        try {
            if (super.isRemoving()) {
                Bundle getArgs = super.getArguments();
                if (getArgs != null)
                    getArgs.putAll(args);
            } else {
                super.setArguments(args);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public abstract BasePresenter getPresenter();

    public void setPresenter(BasePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String createTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void setHint(int hintText, int backgroundColour, int visibility) {
        changeHintBarState(hintText, backgroundColour, visibility);
    }

    public void respondToUser(String message) {
        getParentActivity().onRespondToUser(message);
    }

    //TODO make this abstract later
    public void featureFragmentCleanUps() {

    }

    public void processTemplates(TemplateData templateData) {

    }

    public interface ExplicitFollowUp {
        void explicitFollowUpQuery(IntentInfo intentInfo);
    }
}
