package com.sb.woohoo.ui.main.fragments.livefeedhub;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_SESSION_TOKEN;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_DETAILS;
import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.LiveFeed.DROP_IN_REJECT;
import static com.sb.woohoo.utils.Constants.CONTACT_DETAILS;
import static com.sb.woohoo.utils.Constants.CONTACT_LIVE_FEED;
import static com.sb.woohoo.utils.Constants.HUB_DETAILS;
import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentLiveFeedHubBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedContract;
import com.sb.woohoo.ui.main.fragments.livefeedhub.di.DaggerLiveFeedToHubComponent;
import com.sb.woohoo.ui.main.fragments.livefeedhub.di.LiveFeedToHubComponent;
import com.sb.woohoo.ui.main.fragments.livefeedhub.di.LiveFeedToHubModule;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class LiveFeedToHubFragment
        extends FeatureFragment<MainActivity>
        implements LiveFeedToHubContract.View, VideoCalling {

    // Constants.
    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;

    private FragmentLiveFeedHubBinding binding;

    @Inject
    Picasso picasso;
    @Inject
    LiveFeedToHubPresenter presenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    SharedPrefsHelper sharedPrefs;

    private Handler mScreenTimeOutHandler = new Handler();

    private int screenTimeOutError = 5;

    private boolean touch = true;
    private Hub hubDetails;
    private String deviceId;
    private boolean isVideoEnabled = true, isMicEnabled = true;
    private boolean contactLiveFeed = false;
    private String mContactID="";

    /*private RotateCamera rotateCamera;*/

    /********************************** {@link Runnable}s ***********************************/

    private Runnable mScreenTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            screenTimeOutError--;
            mScreenTimeOutHandler.postDelayed(this, 1000);

            if (screenTimeOutError < 0) {
                handleViews(false);
                screenTimeOutError = 5;
                touch = false;
                mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
            }
        }
    };

    private Runnable viewsRunnable = new Runnable() {
        @Override
        public void run() {
//            imgRejectCall.setVisibility(View.GONE);
            binding.liveFeedButtonLayout.setVisibility(View.VISIBLE);
        }
    };
    private ContactInfo mContactInfo;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;


    /**************************** {@link FeatureFragment} Methods ****************************/


    public LiveFeedToHubFragment() {
        /*Empty Constructor*/
    }

    public static LiveFeedToHubFragment newInstance(Bundle bundle) {
        LiveFeedToHubFragment fragment = new LiveFeedToHubFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);

        }
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return LiveFeedToHubFragment.class.getSimpleName();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {
        LiveFeedToHubComponent liveFeedToHubComponent = DaggerLiveFeedToHubComponent.builder()
                .liveFeedToHubModule(new LiveFeedToHubModule(this)).build();

        liveFeedToHubComponent.injectLiveFeedToHubFragment(this);
    }

    @Override
    public void bindViews(View view) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.LiveFeedToHub(),  null);
        binding = FragmentLiveFeedHubBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHubDetails(getArguments());

        bindDataToView();

        if (hubDetails != null) {
            deviceId = String.valueOf(hubDetails.getId());
            if (hubDetails.getConnected() != null) {
                presenter.getHubUserStatus(deviceId, mContactInfo.getUser_id());
            } else {
                presenter.getHubStatus(deviceId);
            }

        }

        rotateCamera();

        if(IS_AGORA_ENABLED) {
            binding.fragmentContentLiveFeedToHub.primaryVideoViewAgora.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.primaryVideoViewAgora.setOnClickListener(view1 -> {
                if (touch) {
                    mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
                    screenTimeOutError = 5;
                    mScreenTimeOutHandler.post(mScreenTimeOutRunnable);
                } else {
                    touch = true;
                    handleViews(true);
                    mScreenTimeOutHandler.post(mScreenTimeOutRunnable);
                }
            });

        } else {
           /* binding.fragmentContentLiveFeedToHub.primaryVideoView.setOnClickListener(view1 -> {
                if (touch) {
                    mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
                    screenTimeOutError = 5;
                    mScreenTimeOutHandler.post(mScreenTimeOutRunnable);
                } else {
                    touch = true;
                    handleViews(true);
                    mScreenTimeOutHandler.post(mScreenTimeOutRunnable);
                }
            });*/
        }

        setClickEvents();

//        imgRejectCall.setVisibility(View.INVISIBLE);
    }

    private void getHubDetails(Bundle bundle) {
        if (bundle != null) {
            hubDetails = bundle.getParcelable(HUB_DETAILS);
            contactLiveFeed = bundle.containsKey(CONTACT_LIVE_FEED) && bundle.getBoolean(CONTACT_LIVE_FEED);
            mContactInfo = bundle.containsKey(CONTACT_DETAILS) ? bundle.getParcelable(CONTACT_DETAILS) : null;

            mContactID=""+hubDetails.getContactUserID();
        }
    }

    private void bindDataToView() {


        if (contactLiveFeed) {
            setDetails(mContactInfo.getName(), mContactInfo.getImageURL(), mContactInfo.getEmail());
        } else {
            UserData user = new Gson().fromJson(WooHooApp.getSharedPrefsHelper().getUserDetails(PREF_KEY_USER_DETAILS), UserData.class);
            setDetails(user.getFirstName() + " " + user.getLastName(), IMAGE_BASE_URL.concat(user.getImage()), user.getEmail());
        }
    }

    private void setDetails(String name, String imageURL, String email) {
        if (imageURL != null && !imageURL.isEmpty()) {
            picasso.load(imageURL)
                    .fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(binding.personImage);
        } else {
            picasso.load(R.drawable.contact_no_image)
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.personImage);
        }
        if (name.contains("@")) {
            String[] emailSplitter = name.split("@");
            binding.personText.setText(MethodUtils.setWordWithCaps(emailSplitter[0]));
        } else {
            binding.personText.setText(MethodUtils.setWordWithCaps(name));
        }
        binding.personText.setText(name != null && !name.equalsIgnoreCase("") ? MethodUtils.setWordWithCaps(name) : splitEmail(email));
    }

    private String splitEmail(String email) {
        String[] array;
        array = email.split("@");
        return MethodUtils.setWordWithCaps(array[0]);
    }


    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    private void handleViews(boolean isTouched) {
        if (isTouched) {
            binding.fragmentContentLiveFeedToHub.stateLayout.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.status.setBackground(getResources().getDrawable(R.drawable.live_feed_gradient));
            binding.fragmentContentLiveFeedToHub.imgLeftRotate.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.imgRightRotate.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentContentLiveFeedToHub.stateLayout.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.status.setBackground(null);
            binding.fragmentContentLiveFeedToHub.imgLeftRotate.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.imgRightRotate.setVisibility(View.GONE);
        }
    }

    private void rotateCamera() {

        binding.fragmentContentLiveFeedToHub.imgRightRotate.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
                    getParentActivity().rotateCamera(true, deviceId, mContactID, 1, 1);
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_CANCEL:
                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
                    getParentActivity().rotateCamera(false, deviceId, mContactID, 1, 0);
                    break;
            }

            return true;
        });

        binding.fragmentContentLiveFeedToHub.imgLeftRotate.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
                    getParentActivity().rotateCamera(true, deviceId,
                            mContactID, 2, 1);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
                    getParentActivity().rotateCamera(false, deviceId, mContactID, 2, 0);
                    break;
            }
            return true;
        });
    }

    void joinAgoraRoom(String twilioToken) {
        try {

           presenter.connectToAgoraRoom(twilioToken, deviceId, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
           /*FeatureFragment frag = new LiveFeedAgoraFragment();
            Bundle b = new Bundle();
            b.putString("Token", twilioToken);
            b.putString("ChannelName", deviceId);
            b.putInt("ContactUserId", 512);
            frag.setArguments(b);
            getParentActivity().switchFragmentUsingReplace(frag);*/
//            imgRejectCall.setVisibility(View.VISIBLE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }




    /********************************* {@code public} Methods ********************************/

    void enableView() {
        mScreenTimeOutHandler.post(viewsRunnable);
    }

    /************************** {@link LiveFeedContract.View} Callbacks **********************/

    @Override
    public void onMirrorPrimaryVideoStream(boolean mirrorStatus) {
        //binding.fragmentContentLiveFeedToHub.primaryVideoView.setMirror(mirrorStatus);
    }

    @Override
    public void removeVideoPreview(boolean isPreviewOff) {
        if (isPreviewOff) {
            binding.fragmentContentLiveFeedToHub.imgVideoUpstream.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.videoContainer.setBackground(getResources().getDrawable(R.drawable.transparent_preview_bg));
        } else {
            binding.fragmentContentLiveFeedToHub.imgVideoUpstream.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.videoContainer.setBackground(null);
        }

    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    @Override
    public void onError(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
        getParentActivity().onBackPressed();
    }

    @Override
    public void onMoveLocalVideoToThumbnailView() {
        /*if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            presenter.moveVideoStreamView(binding.fragmentContentLiveFeedToHub.primaryVideoView, thumbnailVideoView);
            thumbnailVideoView.setMirror(presenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
        }*/
    }

    @Override
    public void onMoveLocalVideoToPrimaryView() {
        /*if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            thumbnailVideoView.setVisibility(View.GONE);
            presenter.moveVideoStreamView(thumbnailVideoView, binding.fragmentContentLiveFeedToHub.primaryVideoView);
            binding.fragmentContentLiveFeedToHub.primaryVideoView.setMirror(presenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
        }*/
    }

    @Override
    public void onUpdateCountDownTimer(String countDown) {
        try {
            binding.connectingTimeText.setVisibility(View.VISIBLE);
            String hubName = MethodUtils.setWordWithCaps(hubDetails.getName()) + " " + Html.fromHtml("&#8226");
            String connectingText = String.format(getString(R.string.livefeed_in), hubName, countDown);
            binding.connectingTimeText.setText(connectingText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectingToStream(String connectingText) {
        binding.connectingTimeText.setText(connectingText);
        binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        mScreenTimeOutHandler.post(mScreenTimeOutRunnable);

        presenter.enableAudio(isMicEnabled);
        presenter.enableVideo(isVideoEnabled);

        updateViewForLiveFeed(isMicEnabled, isVideoEnabled);
    }

    private void updateViewForLiveFeed(boolean isMicDisabled, boolean isVideoDisabled) {
        if (!isMicDisabled) {
            binding.fragmentContentLiveFeedToHub.mikeButtonOff.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.mikeButtonOn.setVisibility(View.GONE);
        } else {
            binding.fragmentContentLiveFeedToHub.mikeButtonOff.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.mikeButtonOn.setVisibility(View.VISIBLE);
        }

        if (!isVideoDisabled) {
            binding.fragmentContentLiveFeedToHub.ivVideoOn.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.ivVideoOff.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.ivCancelVideoUpstream.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentContentLiveFeedToHub.ivVideoOn.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHub.ivVideoOff.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHub.ivCancelVideoUpstream.setVisibility(View.GONE);
        }
    }

    private JsonObject sendRejectAction() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("receiverHubId", deviceId);
        jsonObject.addProperty("token", WooHooApp.getSharedPrefsHelper().get(PREF_KEY_SESSION_TOKEN, null));
        return jsonObject;
    }

    @Override
    public void requestLiveFeedParams() {
        if (contactLiveFeed) {
            presenter.initiateLiveFeed(true, hubDetails.getContactUserID() != null ?
                    String.valueOf(hubDetails.getContactUserID()) : mContactInfo.getUser_id());
        } else {
            presenter.initiateLiveFeed(false, null);
        }
    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
    }
    public void onDisplayError(String errorMessage) {
        MethodUtils.showToastMessage(requireActivity(), errorMessage);
    }

    public void onCallAccepted() {
        presenter.onConnectCall();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.endCall();
        featureFragmentCleanUps();
        if (mScreenTimeOutHandler != null) {
            mScreenTimeOutHandler.removeCallbacks(viewsRunnable);
            mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().onEndLiveFeed();

    }

    /******************************* Listeners *******************************/

    private void setClickEvents() {
        binding.imgVideoOff.setOnClickListener(this::onViewClicked);
        binding.imgVideoOn.setOnClickListener(this::onViewClicked);
        binding.imgCallEnd.setOnClickListener(this::onViewClicked);
        binding.imgMuteOff.setOnClickListener(this::onViewClicked);
        binding.imgMuteOn.setOnClickListener(this::onViewClicked);
//        binding.fragmentContentLiveFeedToHub.primaryVideoView.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.imgLeftRotate.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.imgRightRotate.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.ivVideoOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.ivVideoOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.dropInSpeakerOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.dropInSpeaker.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.mikeButtonOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.mikeButtonOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHub.endButton.setOnClickListener(this::onViewClicked);
        binding.imgRejectCall.setOnClickListener(this::onViewClicked);
    }

    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_video_off:
                isVideoEnabled = false;
                binding.imgVideoOff.setVisibility(View.GONE);
                binding.imgVideoOn.setVisibility(View.VISIBLE);
                presenter.enableVideo(true);
                break;
            case R.id.img_video_on:
                isVideoEnabled = true;
                binding.imgVideoOff.setVisibility(View.VISIBLE);
                binding.imgVideoOn.setVisibility(View.GONE);
                presenter.enableAudio(false);
                break;
            case R.id.img_mute_off:
                isMicEnabled = false;
                binding.imgMuteOff.setVisibility(View.GONE);
                binding.imgMuteOn.setVisibility(View.VISIBLE);
                presenter.enableAudio(true);
                break;
            case R.id.img_mute_on:
                isMicEnabled = true;
                binding.imgMuteOff.setVisibility(View.VISIBLE);
                binding.imgMuteOn.setVisibility(View.GONE);
                presenter.enableAudio(false);
                break;
            case R.id.iv_video_on:
                binding.fragmentContentLiveFeedToHub.ivVideoOn.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHub.ivVideoOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHub.ivCancelVideoUpstream.setVisibility(View.VISIBLE);
                presenter.enableVideo(false);
                break;
            case R.id.iv_video_off:
                binding.fragmentContentLiveFeedToHub.ivVideoOn.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHub.ivVideoOff.setVisibility(View.GONE);
                presenter.enableVideo(true);
                binding.fragmentContentLiveFeedToHub.ivCancelVideoUpstream.setVisibility(View.GONE);
                break;
            case R.id.drop_in_speaker:
                binding.fragmentContentLiveFeedToHub.dropInSpeakerOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHub.dropInSpeaker.setVisibility(View.GONE);
                presenter.enableSpeaker(false);
                break;
            case R.id.drop_in_speaker_off:
                binding.fragmentContentLiveFeedToHub.dropInSpeakerOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHub.dropInSpeaker.setVisibility(View.VISIBLE);
                presenter.enableSpeaker(true);
                break;
            case R.id.mike_button_on:
                binding.fragmentContentLiveFeedToHub.mikeButtonOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHub.mikeButtonOn.setVisibility(View.GONE);
                presenter.enableAudio(false);
                break;
            case R.id.mike_button_off:
                binding.fragmentContentLiveFeedToHub.mikeButtonOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHub.mikeButtonOn.setVisibility(View.VISIBLE);
                presenter.enableAudio(true);
                break;
            case R.id.end_button:
                if(IS_AGORA_ENABLED) {
                    onDestroyView();
                    getParentActivity().onBackPressed();
                    break;
                }
            case R.id.img_call_end:
                getParentActivity().emitEventToServer(DROP_IN_REJECT, sendRejectAction());
                getParentActivity().onBackPressed();
                break;
        }
    }

    void onRootViewClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        presenter.presenterCleanUps();
    }

    public void setupLocalVideo(RtcEngine mRtcEngine) {

        /*getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {*/
                binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
                binding.liveFeedConnectingLayout.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHub.thumbnailVideoViewAgora.setVisibility(View.VISIBLE);

                final SurfaceView view = RtcEngine.CreateRendererView(getParentActivity());
                view.setZOrderMediaOverlay(true);
                binding.fragmentContentLiveFeedToHub.thumbnailVideoViewAgora.addView(view);
                // Initializes the local video view.
                // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
                mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
                mRtcEngine.setupLocalVideo(mLocalVideo);
        mRtcEngine.enableLocalVideo(true);
        mRtcEngine.enableLocalAudio(true);
            /*}
        });*/
    }

    public void setupRemoteVideo(int uid, RtcEngine mRtcEngine) {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                binding.fragmentContentLiveFeedToHub.primaryVideoView.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHub.primaryVideoViewAgora.setVisibility(View.VISIBLE);
                ViewGroup parent = binding.fragmentContentLiveFeedToHub.primaryVideoViewAgora;
                if (parent.indexOfChild(mLocalVideo.view) > -1) {
                    parent =  binding.fragmentContentLiveFeedToHub.thumbnailVideoViewAgora;
                }

                // Only one remote video view is available for this
                // tutorial. Here we check if there exists a surface
                // view tagged as this uid.
                if (mRemoteVideo != null) {
                    return;
                }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly calling SurfaceView.
         */
                final SurfaceView view = RtcEngine.CreateRendererView(getParentActivity());
                view.setZOrderMediaOverlay(parent ==  binding.fragmentContentLiveFeedToHub.thumbnailVideoViewAgora);
                parent.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
                // Initializes the video view of a remote user.
                mRtcEngine.setupRemoteVideo(mRemoteVideo);
            }
        });
    }

    public void onRemoteUserLeft(int uid, RtcEngine mRtcEngine) {

        binding.liveFeedConnectedLayout.setVisibility(View.GONE);
        binding.liveFeedConnectingLayout.setVisibility(View.VISIBLE);

        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
                    removeFromParent(mRemoteVideo);
                    // Destroys remote view
                    mRemoteVideo = null;
                    /*Toast.makeText(getParentActivity(), "User left the call.", Toast.LENGTH_SHORT).show();
                    onBackPressed();*/
                }
            }
        });
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    public void removeVideo() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
    }
}
