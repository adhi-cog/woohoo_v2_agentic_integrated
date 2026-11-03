package com.sb.woohoo.ui.main.fragments.buysim.buypackage;

import static com.sb.woohoo.utils.Constants.BUT_SIM_PARAMS.PACKAGE_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentBuySimBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.di.BuySimComponent;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.di.BuySimModule;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.di.DaggerBuySimComponent;
import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetEachPackageDetails;
import com.sb.woohoo.ui.main.fragments.buysim.cart.CartVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.OtpVerificationFragment;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.PhoneNumberFragment;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

public class BuySimFragment extends FeatureFragment<MainActivity> implements BuySimContract.Views {

    private static final String ARG_VOICE_SLOTS = "voice_slots";
    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_BUY_SIM;
    private PackageAdapter packageAdapter;
    private ArrayList<GetEachPackageDetails> packageList = new ArrayList<>();
    @Inject
    BuySimPresenter presenter;
    private FragmentBuySimBinding binding;

    public static BuySimFragment newInstance() {
        BuySimFragment fragment = new BuySimFragment();
        return fragment;
    }

    public static BuySimFragment newInstance(HashMap<String, Object> slots) {
        BuySimFragment fragment = new BuySimFragment();
        if (slots != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_VOICE_SLOTS, slots);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuySimBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        presenter.makePackageApi();
    }

    private void initViews() {
        binding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        packageAdapter = new PackageAdapter(getActivity(), new ArrayList<>(),data -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PACKAGE_ID, data);
            PhoneNumberFragment fragment = PhoneNumberFragment.newInstance();
            fragment.setArguments(bundle);
            getParentActivity().switchFragmentUsingReplace(fragment);
        });
        binding.recyclerRestaurants.setAdapter(packageAdapter);

    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

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
        BuySimComponent buySimComponent = DaggerBuySimComponent.builder()
                .buySimModule(new BuySimModule(this))
                .build();

        buySimComponent.injectBuySimFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.package_selection));
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

    @Override
    public void onSuccess(ArrayList<GetEachPackageDetails> data) {
        packageList = data == null ? new ArrayList<>() : data;
        packageAdapter.updatePackageList(data);
        handleVoiceSlotsFromArguments();
    }

    public void handleVoiceSlots(HashMap<String, Object> slots) {
        if (slots == null || packageList == null || packageList.isEmpty()) {
            return;
        }

        GetEachPackageDetails selected = findPackage(slots);
        if (selected == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(PACKAGE_ID, selected);
        PhoneNumberFragment fragment = PhoneNumberFragment.newInstance();
        fragment.setArguments(bundle);
        getParentActivity().switchFragmentUsingReplace(fragment);
    }

    @SuppressWarnings("unchecked")
    private void handleVoiceSlotsFromArguments() {
        if (getArguments() == null) {
            return;
        }
        Object slots = getArguments().getSerializable(ARG_VOICE_SLOTS);
        if (slots instanceof HashMap) {
            handleVoiceSlots((HashMap<String, Object>) slots);
        }
    }

    private GetEachPackageDetails findPackage(HashMap<String, Object> slots) {
        String requestedName = firstSlot(slots, "package_name", "name", "selected_package", "package");
        String requestedNumber = firstSlot(slots, "package_number", "package_index", "number", "ordinal");

        if (requestedNumber != null) {
            try {
                int index = Integer.parseInt(requestedNumber.replaceAll("[^0-9]", "")) - 1;
                if (index >= 0 && index < packageList.size()) {
                    return packageList.get(index);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (requestedName == null) {
            return null;
        }

        String needle = normalizeVoiceText(requestedName);
        for (GetEachPackageDetails item : packageList) {
            String name = normalizeVoiceText(item.getName());
            if (name.contains(needle) || needle.contains(name)) {
                return item;
            }
        }
        return null;
    }

    private String firstSlot(HashMap<String, Object> slots, String... keys) {
        for (String key : keys) {
            if (slots.containsKey(key) && slots.get(key) != null) {
                String value = String.valueOf(slots.get(key)).trim();
                if (!value.isEmpty() && !"null".equalsIgnoreCase(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    private String normalizeVoiceText(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Override
    public void errorMessage(int emptyResultCode) {

    }

    @Override
    public void onErrorResponse(int apiFailErrorCode) {

    }

}
