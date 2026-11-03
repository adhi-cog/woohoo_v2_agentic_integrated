package com.sb.woohoo.ui.main.fragments.reminders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentReminderBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.reminders.di.DaggerReminderComponent;
import com.sb.woohoo.ui.main.fragments.reminders.di.ReminderComponent;
import com.sb.woohoo.ui.main.fragments.reminders.di.ReminderModule;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import java.util.List;

import javax.inject.Inject;

public class ReminderFragment extends FeatureFragment<MainActivity> implements ReminderContract.View, PopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_REMINDER;

    @Inject
    ReminderAdapter remindersAdapter;

    @Inject
    AnalyticsHelper helper;

    @Inject
    ReminderPresenter reminderPresenter;

    @Inject
    List<Reminder> reminderList;
    private FragmentReminderBinding binding;

    public ReminderFragment() {
    }

    public static ReminderFragment newInstance(IntentInfo intentInfo) {
        ReminderFragment fragment;
        fragment = new ReminderFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return ReminderFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.Reminder(),  null);
        binding = FragmentReminderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ReminderComponent reminderComponent = DaggerReminderComponent.builder()
                .reminderModule(new ReminderModule(this))
                .build();

        reminderComponent.injectReminderFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());

        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(getString(R.string.fragment_reminder_text_view_title));
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());

        showProgress();
        initViews();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return reminderPresenter;
    }

    private void initViews() {
        binding.reminderList.setHasFixedSize(true);
        binding.reminderList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.reminderList.setAdapter(remindersAdapter);
        binding.reminderList.setEmptyView(binding.emptyView);
        binding.reminderList.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void allReminders(List<Reminder> reminders) {
        remindersAdapter.notifyDataSetChanged();
        removeProgress();
    }

    public void deleteReminder() {
        reminderPresenter.deleteReminderFromUI(1);
    }

    @Override
    public void scrollList(int position) {
        remindersAdapter.notifyDataSetChanged();
        removeProgress();
        if (position != RecyclerView.NO_POSITION)
            binding.reminderList.smoothScrollToPosition(position);
    }

    @Override
    public void respondVoice(String response) {
        getParentActivity().onRespondToUser(response);
    }

    public void showProgress() {
        binding.fragmentToDoProgressBar.setVisibility(View.VISIBLE);
    }

    public void removeProgress() {
        binding.fragmentToDoProgressBar.setVisibility(View.GONE);
    }

    void onRootClicked() {

    }

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        featureFragmentCleanUps();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        reminderPresenter.presenterCleanUps();
    }
}
