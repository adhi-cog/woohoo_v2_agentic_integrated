package com.sb.woohoo.ui.main.fragments.templates.text.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.databinding.FragmentListBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.templates.text.list.di.DaggerListComponent;
import com.sb.woohoo.ui.main.fragments.templates.text.list.di.ListComponent;
import com.sb.woohoo.ui.main.fragments.templates.text.list.di.ListModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;
import com.sb.woohoo.ui.main.pojo.template.TextListTemplate;

import java.util.List;

import javax.inject.Inject;

public class ListFragment extends FeatureFragment<MainActivity>
        implements ListContract.View {

    @Inject
    ListPresenter mPresenter;

    @Inject
    LinearLayoutManager layoutManager;

    @Inject
    ItemAdapter mAdapter;

    private FragmentListBinding binding;

    public static ListFragment newInstance(TemplateData templateData) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("templateData", templateData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        ListComponent component = DaggerListComponent.builder()
                .listModule(new ListModule(this))
                .build();

        component.injectListTempleteComponent(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        TemplateData<TextListTemplate> textTemplateTemplateData = (TemplateData<TextListTemplate>) bundle.getSerializable("templateData");
        updateDataToViews(textTemplateTemplateData);
    }

    @Override
    public void processTemplates(TemplateData templateData) {
        super.processTemplates(templateData);

        TemplateData<TextListTemplate> textTemplateTemplateData = templateData;
        updateDataToViews(textTemplateTemplateData);
    }

    private void updateDataToViews(TemplateData<TextListTemplate> textTemplateTemplateData) {
        String title = (String) textTemplateTemplateData.getDisplayTemplate().get("title");
        String listTitle = textTemplateTemplateData.getTemplate().getListTitle();
        List<String> textList = textTemplateTemplateData.getTemplate().getTextList();

        binding.fragmentListTemplateTextViewTitle.setText(title);
        binding.fragmentListTemplateTextViewListTitle.setText(listTitle);
        mAdapter.addList(textList);
        binding.fragmentListTemplateProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void bindViews(View view) {
        binding.fragmentListTemplateImageViewBack.setOnClickListener(view1 -> goBack());
        initViews();
    }

    private void initViews() {
        binding.itemLists.setAdapter(mAdapter);
        binding.itemLists.setLayoutManager(layoutManager);
        binding.itemLists.setEmptyView(binding.fragmentListTemplateLinearLayoutNoItem);
        binding.fragmentListTemplateProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void goBack() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void onClickRootView() { /* Do nothing here  */ }
}
