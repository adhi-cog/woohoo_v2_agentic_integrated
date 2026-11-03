package com.sb.woohoo.ui.main.fragments.calllog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentCallLogBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.ui.main.sip.state.SipCallActionState;

import java.util.List;

public class CallLogFragment extends FeatureFragment<MainActivity> {
    private static final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;

    private FragmentCallLogBinding binding;
    private CallLogAdapter adapter;

    public CallLogFragment() {
    }

    public static CallLogFragment newInstance() {
        return new CallLogFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCallLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        binding.commonToolbar.commonToolbarTitle.setText(R.string.call_log_title);
        binding.commonToolbar.commonToolbarBack.setOnClickListener(v -> onBackPressed());
        binding.commonToolbar.commonToolbarRightIcon.setVisibility(View.VISIBLE);
        binding.commonToolbar.commonToolbarRightIcon.setImageResource(R.drawable.ic_delete_white_24dp);
        binding.commonToolbar.commonToolbarRightIcon.setContentDescription(getString(R.string.clear_call_log));
        binding.commonToolbar.commonToolbarRightIcon.setOnClickListener(v -> onClearClicked());

        adapter = new CallLogAdapter(this::onEntryClicked);
        binding.callLogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.callLogRecyclerView.setAdapter(adapter);
        binding.callLogRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refreshLogs();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLogs();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    private void refreshLogs() {
        List<CallLogEntry> entries = CallLogStorage.getEntries();
        adapter.submitList(entries);
        boolean isEmpty = entries.isEmpty();
        binding.callLogEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.callLogRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void onClearClicked() {
        CallLogStorage.clear();
        refreshLogs();
    }

    private void onEntryClicked(CallLogEntry entry) {
        if (WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_FEATURE_LINPHONE, false)) {
            String number = entry.getNumber();
            SipCallActionState action = new SipCallActionState.outgoingCallInit(number, number, number, null);
            SipAccountManager.getInstance().sipCallActionSubject.onNext(action);
        } else {
            Toast.makeText(getContext(), getString(R.string.temp_feature_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void onBackPressed() {
        getParentActivity().onBackPressed();
    }
}
