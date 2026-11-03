package com.sb.woohoo.ui.main.fragments.remote;

import static com.sb.woohoo.utils.Constants.TVRemoteComponent.GET_CHANNELS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentTvRemoteBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.remote.di.DaggerTVRemoteComponent;
import com.sb.woohoo.ui.main.fragments.remote.di.TVRemoteComponent;
import com.sb.woohoo.ui.main.fragments.remote.di.TVRemoteModule;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelData;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.Constants;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class TVRemoteFragment extends FeatureFragment<MainActivity> implements TVRemoteContract.Views {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_NEWS;
    private TVRemoteAdapter tvRemoteAdapter;
    @Inject
    TVRemotePresenter presenter;

    private List<TVRemoteData> tvRemoteData = new ArrayList<>();
    private List<TVRemoteChannelData> tVRemoteChannelData = new ArrayList<>();
    private FragmentTvRemoteBinding binding;

    public static TVRemoteFragment newInstance() {
        TVRemoteFragment fragment = new TVRemoteFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTvRemoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void showSmartView() {
        try {
            Object service = getParentActivity().getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            statusBarManager.getMethod("expandSettingsPanel").invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open Quick Settings Panel.");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        presenter.makeRemoteAPI();
    }

    private void initViews() {
        if (tvRemoteData == null)
            tvRemoteData = new ArrayList<>();
        tvRemoteData.clear();
        binding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        tvRemoteAdapter = new TVRemoteAdapter(getActivity(), tVRemoteChannelData, data -> {
            TVRemoteChannelRequest tVRemoteChannelRequest= new TVRemoteChannelRequest();
            tVRemoteChannelRequest.setActionId(data.getActionId());
            tVRemoteChannelRequest.setActionData(data.getActionData());
            presenter.controlChannelTVCommand(tVRemoteChannelRequest);

        });
        binding.recyclerRestaurants.setAdapter(tvRemoteAdapter);
        binding.btnMirror.setOnClickListener(view -> {
            onButtonClickAction(Constants.TVRemoteComponent.MIRRORING.getValue());
            showSmartView();
        });
        binding.btnPowerOn.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.POWER_ON.getValue()));
        binding.btnPowerOff.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.POWER_OFF.getValue()));
        binding.btnVolDown.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.VOL_DOWN.getValue()));
        binding.btnVolUp.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.VOL_UP.getValue()));
        binding.btnChannelUp.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.CHANNEL_UP.getValue()));
        binding.btnChannelDown.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.CHANNEL_DOWN.getValue()));
        binding.btnMute.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.MUTE_ON.getValue()));
        binding.btnUnMute.setOnClickListener(view -> onButtonClickAction(Constants.TVRemoteComponent.MUTE_OFF.getValue()));
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<TVRemoteChannelData> tVRemoteChannelDataFilterList = new ArrayList<>();
                for (TVRemoteChannelData remote : tVRemoteChannelData ){
                    if(remote.getName().contains(s.toString()) ||
                            remote.getActionData().getChannelId().contains(s.toString())){
                        tVRemoteChannelDataFilterList.add(remote);
                    }
                }
                tvRemoteAdapter.updateTVRemoteChannelDataList(tVRemoteChannelDataFilterList);
            }
        });
    }

    private void onButtonClickAction(String remoteControlValue) {
        TVRemoteData obj = null;
        for (int i = 0; i < tvRemoteData.size(); i++) {
            String name = tvRemoteData.get(i).getName();
            if ((Objects.equals(name, remoteControlValue))) {
                obj = tvRemoteData.get(i);
            }
        }
        presenter.controlTVCommand(obj.getActionId());

    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void onTVRemoteMessage(String error) {
        Toast.makeText(getParentActivity(), error, Toast.LENGTH_SHORT).show();
//        getParentActivity().onBackPressed();
    }
    @Override
    public void onRemoteComponentResponse(){
        Toast.makeText(getParentActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
    }
    public String getActionIdFromChannelName(List<TVRemoteData> tVRemoteData){
        for (TVRemoteData remote : tVRemoteData ){
            if(GET_CHANNELS.getValue().equals(remote.getName())){
                return remote.getActionId();
            }
        }
        return null;
    }

    @Override
    public void onChannelListFetched(List<TVRemoteChannelData> tVRemoteChannelData){
        this.tVRemoteChannelData = tVRemoteChannelData;
        tvRemoteAdapter.updateTVRemoteChannelDataList(tVRemoteChannelData);
    }
    @Override
    public void onTVRemoteDataFetched(List<TVRemoteData> tVRemoteData) {
        tvRemoteData = tVRemoteData;
        String actionId = getActionIdFromChannelName(tVRemoteData);
        if(actionId != null) {
            presenter.makeChannelAPI(actionId);
        }
        for (int i = 0; i < tVRemoteData.size(); i++) {
            String name = tVRemoteData.get(i).getName();

            if (Objects.equals(name, Constants.TVRemoteComponent.MIRRORING.getValue())){
                binding.btnMirror.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.CHANNEL_UP.getValue()) ||
                    Objects.equals(name, Constants.TVRemoteComponent.CHANNEL_DOWN.getValue())){
                binding.linerChannel.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.VOL_UP.getValue()) ||
                    Objects.equals(name, Constants.TVRemoteComponent.VOL_DOWN.getValue())){
                binding.linerVol.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.POWER_ON.getValue())){
                binding.btnPowerOn.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.POWER_OFF.getValue())){
                binding.btnPowerOff.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.MUTE_ON.getValue())){
                binding.btnMute.setVisibility(View.VISIBLE);
            }
            if (Objects.equals(name, Constants.TVRemoteComponent.MUTE_OFF.getValue())){
                binding.btnUnMute.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void initDagger() {
        TVRemoteComponent tVRemoteComponent = DaggerTVRemoteComponent.builder()
                .tVRemoteModule(new TVRemoteModule(this))
                .build();

        tVRemoteComponent.injectTVRemoteFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.tv_remote));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBack());
    }

    void onBack() {
        MethodUtils.hideSoftInputKeyboard(getView(), getParentActivity());
        getParentActivity().onBackPressed();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        presenter.presenterCleanUps();
    }
}
