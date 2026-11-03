package com.sb.woohoo.ui.main.fragments.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.RecyclerViewDivider;
import com.sb.woohoo.databinding.FragmentToDoBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.PopFragment;
import com.sb.woohoo.ui.main.fragments.todo.di.DaggerToDoComponent;
import com.sb.woohoo.ui.main.fragments.todo.di.ToDoComponent;
import com.sb.woohoo.ui.main.fragments.todo.di.ToDoModule;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Shows and Manages To-Do's of User.
 *
 * @author Ritwik Jamuar
 */
@SuppressWarnings("DanglingJavadoc")
public class ToDoFragment
        extends FeatureFragment<MainActivity>
        implements ToDoContract.View, PopFragment {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_TODO;


    // Adapter.
    @Inject
    ToDoAdapter mToDoAdapter;

    @Inject
    AnalyticsHelper helper;

    // Presenter.
    @Inject
    ToDoPresenter mPresenter;

    private FragmentToDoBinding binding;

    public ToDoFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link ToDoFragment}
     * using the provided parameters.
     *
     * @return A new instance of {@link ToDoFragment}.
     */
    public static ToDoFragment newInstance(IntentInfo model) {
        ToDoFragment fragment = new ToDoFragment();
        fragment.processData(model, null);
        return fragment;
    }

    public static ToDoFragment newInstance() {
        return new ToDoFragment();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return ToDoFragment.class.getSimpleName();
    }

    /************************************* Fragment Callbacks *************************************/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.ToDo(),  null);
        binding = FragmentToDoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    /**
     * Injects the {@link ToDoFragment} to {@link ToDoComponent}.
     */
    @Override
    public void initDagger() {
        ToDoComponent component =
                DaggerToDoComponent
                        .builder()
                        .toDoModule(new ToDoModule(ToDoFragment.this))
                        .build();
        component.injectToDoFragment(ToDoFragment.this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link ToDoFragment}.
     */
    @Override
    public void bindViews(View view) {
        setUpRecyclerView();
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.fragment_to_do_title)); // Set Toolbar Title.
        binding.fragmentToDoToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
    }

    /************************************** private Methods ***************************************/

    /**
     * Configures the {@link RecyclerView} for Use.
     */
    private void setUpRecyclerView() {
        // Set Divider in RecyclerView using DividerItemDecoration.
        RecyclerViewDivider itemDecor = new RecyclerViewDivider(ContextCompat.getDrawable(requireContext(), R.drawable.recycler_view_divider));
        binding.fragmentToDoRecyclerView.addItemDecoration(itemDecor);

        // Set Layout Manager.
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.fragmentToDoRecyclerView.setLayoutManager(manager);

        // Set Adapter.
        binding.fragmentToDoRecyclerView.setAdapter(mToDoAdapter);

        // Disable Over-Scroll in RecyclerView.
        binding.fragmentToDoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    /********************************** Listeners *********************************/

    void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onRootClicked() {
    }

    /******************************* {@link ToDoPresenter} Callbacks ******************************/

    @Override
    public void showEmptyToDoList() {
        binding.fragmentToDoRecyclerView.setVisibility(View.GONE);
        binding.fragmentToDoTextViewEmptyToDoList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyToDoList() {
        binding.fragmentToDoRecyclerView.setVisibility(View.VISIBLE);
        binding.fragmentToDoTextViewEmptyToDoList.setVisibility(View.GONE);
    }

    @Override
    public void setToDosToRecyclerView(List<ToDoData> toDoList) {
        hideEmptyToDoList();
        mToDoAdapter.initializeList(toDoList);
    }

    @Override
    public void addNewToDoItemsToRecyclerView(ToDoData toDoList) {
        mToDoAdapter.addItem(toDoList);
    }

    @Override
    public void showError(String message) {
        MethodUtils.showToastMessage(getActivity(), message);
    }

    @Override
    public void showProgress() {
        binding.fragmentToDoProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeProgress() {
        binding.fragmentToDoProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void scrollToLast() {
        binding.fragmentToDoRecyclerView.smoothScrollToPosition(getToDoAdapterItemCount() - 1);
    }

    @Override
    public int getToDoAdapterItemCount() {
        return mToDoAdapter.getItemCount();
    }

    @Override
    public ToDoData getToDoItemFromAdapter(int position) {
        return mToDoAdapter.getToDoItem(position);
    }

    @Override
    public void showToastMessage(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
    }

    @Override
    public void notifyAdapter(int position) {
        mToDoAdapter.deleteItem(position);
    }
}
