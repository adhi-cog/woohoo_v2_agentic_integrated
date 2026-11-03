package com.sb.woohoo.ui.main.fragments.webview;

import android.os.Bundle;
import android.view.View;

import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.fragments.webview.di.WebViewComponent;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.StartSession;

/**
 * Audio Call.
 * This needs to be updated
 *
 * @author subham satyam.
 */
public class WebViewFragment
        extends FeatureFragment<MainActivity>
        implements WebViewContracts.View {
/*

    private static final String VALUE_KEY = "app_data";
    private static final String VALUE_URL = "app_url";
    private static final String WEBVIEW_MODE = "webview_mode";


    private StartSession value;
    private String url;
    private WebviewLaunchMode webviewLaunchMode;

    // Presenter.
    @Inject
    WebViewPresenter mPresenter;
    @Inject
    AnalyticsHelper helper;
    private FragmentWebViewBinding binding;
*/


    public WebViewFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of {@link WebViewFragment} using the provided parameters.
     *
     * @return A new instance of {@link WebViewFragment}.
     */
    public static WebViewFragment newInstance(StartSession item) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        //args.putSerializable(VALUE_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {

    }

    @Override
    public void bindViews(View view) {

    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }
/*
    public void updateInstance(String appUrl) {
        url = appUrl;
        GeckoSession session = binding.webView.getSession();
        session.loadUri(url);
    }

    public enum WebviewLaunchMode {
        Dining,
        InfraSys,
        Default
    }

    public static WebViewFragment newInstance(String appUrl, WebviewLaunchMode webviewLaunchMode) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(VALUE_URL, appUrl);
        args.putSerializable(WEBVIEW_MODE, webviewLaunchMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String createTag() {
        return WebViewFragment.class.getSimpleName();
    }

    *//*********************************************** Fragment Callback Methods ***********************************************//*

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    *//**
     * Injects the {@link WebViewFragment} to {@link WebViewComponent}.
     *//*
    @Override
    public void initDagger() {
        WebViewComponent component =
                DaggerWebViewComponent
                        .builder()
                        .webViewModule(new WebViewModule(WebViewFragment.this))
                        .build();
        component.injectWebViewFragment(WebViewFragment.this);
    }

    *//**
     * Initializes the Views.
     *
     * @param view View of {@link WebViewFragment}.
     *//*
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
        helper.track(new Screen.WebView(), null);
        binding = FragmentWebViewBinding.inflate(inflater, container, false);
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
            } else {
                url = (String) bundle.getSerializable(VALUE_URL);
                webviewLaunchMode = (WebviewLaunchMode) bundle.getSerializable(WEBVIEW_MODE);
                bindDataToView();
            }

        }
    }

    @Override
    public BasePresenter getPresenter() {
        return mPresenter;
    }


    *//**
     * Bind the DeviceData to the View.
     **//*
    private void bindDataToView() {

        if (webviewLaunchMode == WebviewLaunchMode.Dining || webviewLaunchMode == WebviewLaunchMode.InfraSys) {
            binding.fragmentToDoToolbar.getRoot().setVisibility(View.VISIBLE);
            binding.imageButtonCross.setVisibility(View.VISIBLE);
            if (webviewLaunchMode == WebviewLaunchMode.InfraSys) {
                binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.drawer_menu_ordering));
            } else if (webviewLaunchMode == WebviewLaunchMode.Dining) {
                binding.fragmentToDoToolbar.commonToolbarTitle.setText(getString(R.string.fragment_restaurant_title));
            }

        } else {
            binding.fragmentToDoToolbar.getRoot().setVisibility(View.GONE);
            binding.imageButtonCross.setVisibility(View.GONE);
        }

        if (binding.fragmentToDoToolbar.getRoot().getVisibility() == View.VISIBLE) {
            //toolbar already has a back button, so don't need additional cross button
            binding.imageButtonCross.setVisibility(View.GONE);
        } else {

            binding.imageButtonCross.setVisibility(View.VISIBLE);
        }

        GeckoSession session = new GeckoSession();
        session.getSettings().setAllowJavascript(true);

        session.open(GeckoRuntime.getDefault(getActivity()));

        //session.getNavigationDelegate().onLocationChange(session,"location changed");
        // session.exitFullScreen();
        if (binding.webView.getSession() == null || !binding.webView.getSession().isOpen()) {
            binding.webView.setSession(session);
        }
        GeckoRuntime.getDefault(getActivity()).setServiceWorkerDelegate(new GeckoRuntime.ServiceWorkerDelegate() {
            @NonNull
            @Override
            public GeckoResult<GeckoSession> onOpenWindow(@NonNull String s) {
                LoggerWrapper.d("onOpenWindow", s);
                return null;
            }
        });
        session.setContentBlockingDelegate(new ContentBlocking.Delegate() {
            @Override
            public void onContentBlocked(@NonNull GeckoSession geckoSession, @NonNull ContentBlocking.BlockEvent blockEvent) {
                LoggerWrapper.d("onContentBlocked", blockEvent.uri);
            }

            @Override
            public void onContentLoaded(@NonNull GeckoSession geckoSession, @NonNull ContentBlocking.BlockEvent blockEvent) {
                LoggerWrapper.d("onContentLoaded", blockEvent.uri);
            }
        });

        session.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
            @Override
            public void onLocationChange(@NonNull GeckoSession geckoSession, @Nullable String s) {
                LoggerWrapper.d("onLocationChange", s);
            }

            @Nullable
            @Override
            public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession geckoSession, @NonNull LoadRequest loadRequest) {
                // LoggerWrapper.d("onLoadRequest",loadRequest.triggerUri);
                LoggerWrapper.d("onLoadRequest", loadRequest.uri);
                if (loadRequest.triggerUri != null) {
                    LoggerWrapper.d("triggerUri", loadRequest.uri);
                }
                if (!loadRequest.uri.equals("javascript:undefined")) {
                    LoggerWrapper.d("triggerUri", "accept");
                    return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                } else {
                    LoggerWrapper.d("triggerUri", "deny");
                    return GeckoResult.fromValue(AllowOrDeny.DENY);
                }
                // geckoSession.restoreState(new GeckoSession.SessionState(GeckoSession.SessionState.fromString()));

            }

            @Nullable
            @Override
            public GeckoResult<GeckoSession> onNewSession(@NonNull GeckoSession geckoSession, @NonNull String s) {
                LoggerWrapper.d("onNewSession", s);
                return null;
            }

            @Nullable
            @Override
            public GeckoResult<String> onLoadError(@NonNull GeckoSession geckoSession, @Nullable String s, @NonNull WebRequestError webRequestError) {
                LoggerWrapper.d("onLoadError", webRequestError.getMessage());
                return null;
            }
        });
        if (value != null) {
            session.loadUri(value.getData().getAppUrl());

        } else {
            session.loadUri(url);

        }

        //property set
        GeckoSessionSettings settings = session.getSettings();
        settings.setAllowJavascript(true);
        settings.setUseTrackingProtection(true);
        settings.setUserAgentMode(USER_AGENT_MODE_MOBILE);
        settings.setUserAgentOverride("");
        settings.setSuspendMediaWhenInactive(true);


        session.setProgressDelegate(new GeckoSession.ProgressDelegate() {
            @Override
            public void onPageStart(@NonNull GeckoSession geckoSession, @NonNull String s) {

            }

            @Override
            public void onPageStop(@NonNull GeckoSession geckoSession, boolean b) {

            }

            @Override
            public void onProgressChange(@NonNull GeckoSession geckoSession, int i) {
                binding.progressBar.setProgress(i);
                if (i > 1 && i < 99) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSecurityChange(@NonNull GeckoSession geckoSession, @NonNull SecurityInformation securityInformation) {

            }

            @Override
            public void onSessionStateChange(@NonNull GeckoSession geckoSession, @NonNull GeckoSession.SessionState sessionState) {

            }
        });


    }

    *//************************************************ Views on-Click Listeners ***********************************************//*

    void onBackPressed() {
        if (binding.webView.getSession() != null) {
            binding.webView.getSession().close();
        }
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

    @Override
    public void onDestroyView() {
        if (binding.webView.getSession() != null) {
            binding.webView.getSession().close();
        }
        super.onDestroyView();

    }*/
}