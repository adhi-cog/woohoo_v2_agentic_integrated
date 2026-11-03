package com.sb.woohoo.ui.main.fragments.webviewdefault;

import static com.sb.woohoo.ui.main.fragments.video.VideoFragment.hookWebView;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sb.woohoo.R;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.databinding.FragmentWebViewDefaultBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.NonPopFragment;
import com.sb.woohoo.ui.main.fragments.webviewdefault.di.DaggerWebViewDefaultComponent;
import com.sb.woohoo.ui.main.fragments.webviewdefault.di.WebViewDefaultComponent;
import com.sb.woohoo.ui.main.fragments.webviewdefault.di.WebViewDefaultModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StartSession;

import javax.inject.Inject;

public class WebViewDefaultFragment
        extends FeatureFragment<MainActivity>
        implements WebViewDefaultContracts.View, NonPopFragment {

    private static final String VALUE_KEY = "app_data";
    private static final String VALUE_URL = "app_url";
    private static final String WEBVIEW_MODE = "webview_mode";


    private StartSession value;
    private String url;
    private WebviewLaunchMode webviewLaunchMode;

    // Presenter.
    @Inject
    WebViewDefaultPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    private FragmentWebViewDefaultBinding binding;


    public WebViewDefaultFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link WebViewDefaultFragment} using the provided parameters.
     *
     * @return A new instance of {@link WebViewDefaultFragment}.
     */
    public static WebViewDefaultFragment newInstance(StartSession item) {
        WebViewDefaultFragment fragment = new WebViewDefaultFragment();
        Bundle args = new Bundle();
        args.putSerializable(VALUE_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    public enum WebviewLaunchMode {
        Dining,
        InfraSys,
        Default
    }

    public static WebViewDefaultFragment newInstance(String appUrl, WebviewLaunchMode webviewLaunchMode) {
        WebViewDefaultFragment fragment = new WebViewDefaultFragment();
        Bundle args = new Bundle();
        args.putString(VALUE_URL, appUrl);
        args.putSerializable(WEBVIEW_MODE, webviewLaunchMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String createTag() {
        return WebViewDefaultFragment.class.getSimpleName();
    }

    /*********************************************** Fragment Callback Methods ***********************************************/

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    /**
     * Injects the {@link WebViewDefaultFragment} to {@link WebViewDefaultComponent}.
     */
    @Override
    public void initDagger() {
        WebViewDefaultComponent component =
                DaggerWebViewDefaultComponent
                        .builder()
                        .webViewDefaultModule(new WebViewDefaultModule(WebViewDefaultFragment.this))
                        .build();
        component.injectWebViewDefaultFragment(WebViewDefaultFragment.this);
    }

    /**
     * Initializes the Views.
     *
     * @param view View of {@link WebViewDefaultFragment}.
     */
    @Override
    public void bindViews(View view) {

        binding.fragmentToDoToolbar.commonToolbarBack.setOnClickListener(view1 -> onBackPressed());
        binding.imageButtonCross.setOnClickListener(view1 -> onCrossClicked());
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helper.track(new Screen.WebView(),  null);
        hookWebView();
        binding = FragmentWebViewDefaultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value = (StartSession) bundle.getSerializable(VALUE_KEY);
            if (value != null) {
                bindDataToView();
            }else{
                url =  (String)bundle.getSerializable(VALUE_URL);
                webviewLaunchMode = (WebviewLaunchMode) bundle.getSerializable(WEBVIEW_MODE) ;
                bindDataToView();
            }

        }
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }


    /**
     * Bind the DeviceData to the View.
     **/
    private void bindDataToView() {

        if (webviewLaunchMode == WebviewLaunchMode.Dining || webviewLaunchMode == WebviewLaunchMode.InfraSys){
            binding.fragmentToDoToolbar.getRoot().setVisibility(View.VISIBLE);
            binding.imageButtonCross.setVisibility(View.VISIBLE);
            if( webviewLaunchMode == WebviewLaunchMode.InfraSys) {
                binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.drawer_menu_ordering));
            } else if( webviewLaunchMode == WebviewLaunchMode.Dining) {
                binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.fragment_restaurant_title));
            }

        } else {
            binding.fragmentToDoToolbar.getRoot().setVisibility(View.GONE);
            binding.imageButtonCross.setVisibility(View.GONE);
        }

        if(binding.fragmentToDoToolbar.getRoot().getVisibility() == View.VISIBLE){
            //toolbar already has a back button, so don't need additional cross button
            binding.imageButtonCross.setVisibility(View.GONE);
        } else{

            binding.imageButtonCross.setVisibility(View.VISIBLE);
        }


        binding.webViewDefault.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                binding.progressBar.setProgress(progress);
                if (progress > 1 && progress < 99) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.webViewDefault.setWebViewClient(
                new SSLTolerentWebViewClient()
        );
        binding.webViewDefault.getSettings().setJavaScriptEnabled(true);
        if(value!=null){
            binding.webViewDefault.loadUrl(value.getData().getAppUrl());

        }else{
            binding.webViewDefault.loadUrl(url);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((MainActivity)context).enableFullWidthMode();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getParentActivity().disableFullWidthMode();
        }
    }

    /************************************************ Views on-Click Listeners ***********************************************/

    void onBackPressed() {
            getParentActivity().onBackPressed();
    }

    void onParentClicked() {
    }

    void onCrossClicked() {
        onBackPressed();
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        mPresenter.presenterCleanUps();
    }
}