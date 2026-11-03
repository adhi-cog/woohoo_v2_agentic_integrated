package com.sb.woohoo.ui.main.fragments.video;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sb.woohoo.R;
import com.sb.woohoo.databinding.FragmentVideoListBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.base.FragmentTransactionManager;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.home.HomeFragment;
import com.sb.woohoo.ui.main.fragments.video.di.DaggerVideoSearchComponent;
import com.sb.woohoo.ui.main.fragments.video.di.VideoSearchComponent;
import com.sb.woohoo.ui.main.fragments.video.di.VideoSearchModule;
import com.sb.woohoo.ui.main.fragments.video.pojo.Video;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;

import java.util.List;

import javax.inject.Inject;

public class VideoListFragment
        extends FeatureFragment<MainActivity>
        implements VideoListContract.View,
        ClickListener {

    private final String FEATURE_CATEGORY = Categories.Skills.COMMAND_SKILL_VIDEO;

    // Presenter.
    @Inject
    VideoListPresenter videoListPresenter;

    // Adapters.
    @Inject
    VideoAdapter videoAdapter;

    // Variables.
    private String query;
    FragmentVideoListBinding binding;

    String DEFAULT_SEARCH_TEXT = "play top 20 chart";

    public static VideoListFragment newInstance(IntentInfo intentInfo) {
        VideoListFragment fragment;
        fragment = new VideoListFragment();
        fragment.processData(intentInfo, null);
        return fragment;
    }

    @Override
    public String createTag() {
        return VideoListFragment.class.getSimpleName();
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    void setVideoTile() {
        query = getString(R.string.Trending);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setText(query);
    }

    private String capitalize(@NonNull String string) {
        char[] charArray = string.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        return new String(charArray);
    }

    void setVideoLayoutViews(int view) {
        if (getCurrentVideosList().size() == 0 && view == View.INVISIBLE) {
            setViews(View.INVISIBLE);
            setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
            callVideoListApi(DEFAULT_SEARCH_TEXT);
        } else {
            setViews(View.VISIBLE);
            setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.VISIBLE);
        }
    }

    private List<Video> getCurrentVideosList() {
        return videoAdapter.getVideosList();
    }

    private void initViews() {
        // Set Toolbar Title.
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setSingleLine();
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setSelected(true);
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        // Set-up RecyclerView.
        binding.videoList.setEmptyView(binding.txtNoVideos);
        binding.videoList.setHasFixedSize(true);
        binding.videoList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.videoList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        binding.videoList.setAdapter(videoAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentVideoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        VideoSearchComponent videoSearchComponent =
                DaggerVideoSearchComponent
                        .builder()
                        .videoSearchModule(
                                new VideoSearchModule(this)
                        ).build();
        videoSearchComponent.injectVideoSearchFragment(this);
    }

    @Override
    public void bindViews(View view) {
        binding.interceptorLayout.setContext(getParentActivity());
        binding.fragmentSettingsRelativeLayoutHeader.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.ivSearch.setOnClickListener(view12 -> onSearchClick());
        initViews();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public BasePresenter getPresenter() {
        return videoListPresenter;
    }

    @Override
    public void playVideo(int ordinal) {
        Video video = videoAdapter.getVideo(ordinal);
        VideoFragment videoFragment = VideoFragment.newInstance(video);
        getParentActivity().switchFragmentUsingAdd(videoFragment);
    }

    @Override
    public void playVideo(Video video) {
        Fragment currentFragment =
                FragmentTransactionManager.getCurrentFragment(getFragmentManager());
        if (currentFragment instanceof VideoFragment) {
            ((VideoFragment) currentFragment).playVideo(video);
        } else {
            VideoFragment videoFragment = VideoFragment.newInstance(video);
            getParentActivity().switchFragmentUsingAdd(videoFragment);
        }
    }

    @Override
    public void showProgress() {
        if (binding.progressBar.getVisibility() == View.GONE) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (binding.progressBar.getVisibility() == View.VISIBLE) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String message) {
//        MethodUtils.showToastMessage(getContext(),message);
    }

    @Override
    public void onPositionClicked(int position) {
        Video video = videoAdapter.getVideo(position);
        VideoFragment videoFragment = VideoFragment.newInstance(video);
        getParentActivity().switchFragmentUsingAdd(videoFragment);
    }

    @Override
    public void updateCurrentVideosList(List<Video> videos) {

        binding.editSearch.setText("");
        videoAdapter.updateList(videos);
    }

    void onRootClicked() {
    }

    void onSearchClick() {
        if (!binding.editSearch.getText().toString().trim().equalsIgnoreCase("")) {
//            Toast.makeText(getActivity(), getString(R.string.enter_city_name), Toast.LENGTH_SHORT).show();
            callVideoListApi(binding.editSearch.getText().toString());
        } /*else {
            videoListPresenter.callVideoListApiBySearch(binding.editSearch.getText().toString());
            MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        }*/
    }

    void callVideoListApi(String input){
        videoListPresenter.callVideoListApiBySearch(input);
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
    }

    private void setViews(int view) {
        if (view == View.VISIBLE) {
            binding.interceptorLayout.setBackgroundColor(
                    MethodUtils.getColorFromTheme(getParentActivity(), R.attr.color_secondary));
            binding.videoList.setVisibility(View.VISIBLE);
        } else {
            binding.videoList.setVisibility(View.INVISIBLE);
            binding.interceptorLayout.setBackgroundColor(Color.BLACK);
        }
    }

    void showVideoListAfterPlaybackError() {
        setVideoLayoutViews(View.VISIBLE);
    }

    void processVideoAction(IntentInfo intentInfo) {
        Fragment currentFragment =
                FragmentTransactionManager.getCurrentFragment(getParentFragmentManager());
        if (currentFragment instanceof VideoFragment) {
            ((VideoFragment) currentFragment).processActions(intentInfo);
        }
    }

    void handleBackPressed() {
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        Fragment currentFragment = FragmentTransactionManager.getCurrentFragment(getParentActivity().getSupportFragmentManager());
        while (!(currentFragment instanceof HomeFragment)) {
            getParentActivity().onBackPressed();
            currentFragment = FragmentTransactionManager.getCurrentFragment(getParentActivity().getSupportFragmentManager());
        }
    }

    void onBackPressed() {
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        getParentActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MethodUtils.hideSoftInputKeyboard(binding.editSearch, getContext());
        featureFragmentCleanUps();
        getParentActivity().stopTextToSpeech(true);
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        videoListPresenter.presenterCleanUps();
    }
}
