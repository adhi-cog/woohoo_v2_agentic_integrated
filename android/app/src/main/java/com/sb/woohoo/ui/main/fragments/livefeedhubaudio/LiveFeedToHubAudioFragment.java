package com.sb.woohoo.ui.main.fragments.livefeedhubaudio;

import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_SESSION_TOKEN;
import static com.sb.woohoo.data.prefs.SharedPrefsKey.PREF_KEY_USER_DETAILS;
import static com.sb.woohoo.ui.main.helpers.SocketHelper.SocketEvents.LiveFeed.DROP_IN_REJECT;
import static com.sb.woohoo.utils.Constants.CONTACT_DETAILS;
import static com.sb.woohoo.utils.Constants.CONTACT_LIVE_FEED;
import static com.sb.woohoo.utils.Constants.HUB_DETAILS;
import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.sb.woohoo.databinding.FragmentLiveFeedHubAudioBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.ContactInfo;
import com.sb.woohoo.ui.main.fragments.livefeed.LiveFeedContract;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.di.DaggerLiveFeedToHubAudioComponent;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.di.LiveFeedToHubAudioComponent;
import com.sb.woohoo.ui.main.fragments.livefeedhubaudio.di.LiveFeedToHubAudioModule;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.Hub;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserData;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class LiveFeedToHubAudioFragment
        extends FeatureFragment<MainActivity>
        implements LiveFeedToHubAudioContract.View, VideoCalling {

    // Constants.
    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;

    @Inject
    Picasso picasso;
    @Inject
    LiveFeedToHubAudioPresenter presenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    SharedPrefsHelper sharedPrefs;

    private FragmentLiveFeedHubAudioBinding binding;
    private Handler mScreenTimeOutHandler = new Handler();
    private int screenTimeOutError = 5;

    private boolean touch = true;
    private Hub hubDetails;
    private String deviceId;
    private boolean isVideoEnabled = false, isMicEnabled = true;
    private boolean contactLiveFeed = false;

    /*private RotateCamera rotateCamera;*/


    private Runnable mScreenTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            screenTimeOutError --;
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
            binding.imgRejectCall.setVisibility(View.VISIBLE);
//            liveFeedButtonsLayout.setVisibility(View.VISIBLE);
        }
    };
    private ContactInfo mContactInfo;

    /**************************** {@link FeatureFragment} Methods ****************************/


    public LiveFeedToHubAudioFragment() {
        /*Empty Constructor*/
    }

    public static LiveFeedToHubAudioFragment newInstance(Bundle bundle) {
        LiveFeedToHubAudioFragment fragment = new LiveFeedToHubAudioFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);

        }
        return fragment;
    }
    public static LiveFeedToHubAudioFragment newInstance(IntentInfo<StaffMember> contactsDataIntentInfo) {
        LiveFeedToHubAudioFragment fragment = new LiveFeedToHubAudioFragment();
        Bundle args = new Bundle();
        args.putSerializable("contact", contactsDataIntentInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return LiveFeedToHubAudioFragment.class.getSimpleName();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {
    }

    @Override
    public void initDagger() {
        LiveFeedToHubAudioComponent liveFeedToHubAudioComponent = DaggerLiveFeedToHubAudioComponent.builder()
                .liveFeedToHubAudioModule(new LiveFeedToHubAudioModule(this)).build();

        liveFeedToHubAudioComponent.injectLiveFeedToHubAudioFragment(this);
    }

    public void bindViews(View view) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helper.track(new Screen.LiveFeedToHubAudio(),  null);
        binding = FragmentLiveFeedHubAudioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHubDetails(getArguments());

        bindDataToView();

        setClickEvents();
        if (hubDetails != null) {
            deviceId = String.valueOf(hubDetails.getId());
            if (hubDetails.getConnected() != null) {
                presenter.getHubUserStatus(deviceId, mContactInfo.getUser_id());
            } else {
                presenter.getHubStatus(deviceId);
            }
        }

//        rotateCamera();

        binding.fragmentContentLiveFeedToHubAudio.relContainer.setOnClickListener(view1 -> {
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

//         binding.imgRejectCall.setVisibility(View.INVISIBLE);
    }

    private void getHubDetails(Bundle bundle) {
        if (bundle != null) {
            hubDetails = bundle.getParcelable(HUB_DETAILS);
            contactLiveFeed = bundle.containsKey(CONTACT_LIVE_FEED) && bundle.getBoolean(CONTACT_LIVE_FEED);
            mContactInfo = bundle.containsKey(CONTACT_DETAILS) ? bundle.getParcelable(CONTACT_DETAILS) : null;
        }
    }

    private void bindDataToView() {
        if (contactLiveFeed) {
            setDetails(mContactInfo.getName(), mContactInfo.getImageURL());
        } else {
            UserData user = new Gson().fromJson(WooHooApp.getSharedPrefsHelper().getUserDetails(PREF_KEY_USER_DETAILS), UserData.class);
            setDetails(user.getFirstName() + " " + user.getLastName(), IMAGE_BASE_URL.concat(user.getImage()));
        }
    }

    private void setDetails(String name, String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            picasso.load(imageURL)
                    .fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(binding.personImage);

            picasso.load(imageURL)
                    .fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(binding.fragmentContentLiveFeedToHubAudio.personImageCall);
        } else {
            picasso.load(R.drawable.contact_no_image)
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.personImage);
            picasso.load(R.drawable.contact_no_image)
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.fragmentContentLiveFeedToHubAudio.personImageCall);
        }
        binding.personText.setText(MethodUtils.setWordWithCaps(name));
    }


    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    private void handleViews(boolean isTouched) {
        if (isTouched) {
            binding.fragmentContentLiveFeedToHubAudio.stateLayout.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHubAudio.status.setBackground(getResources().getDrawable(R.drawable.live_feed_gradient));
//            imgRotateLeft.setVisibility(View.VISIBLE);
//            imgRotateRight.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentContentLiveFeedToHubAudio.stateLayout.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHubAudio.status.setBackground(null);
//            imgRotateLeft.setVisibility(View.GONE);
//            imgRotateRight.setVisibility(View.GONE);
        }
    }

//    private void rotateCamera() {
//
//        imgRotateRight.setOnTouchListener((v, event) -> {
//
//            switch (event.getAction()) {
//
//                case MotionEvent.ACTION_DOWN:
//                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
//                    getParentActivity().rotateCamera(true, deviceId, 1, 1);
//                    break;
//
//                case MotionEvent.ACTION_UP:
//
//                case MotionEvent.ACTION_CANCEL:
//                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
//                    getParentActivity().rotateCamera(false, deviceId, 1, 0);
//                    break;
//            }
//
//            return true;
//        });
//
//        imgRotateLeft.setOnTouchListener((v, event) -> {
//
//            switch (event.getAction()) {
//
//                case MotionEvent.ACTION_DOWN:
//                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
//                    getParentActivity().rotateCamera(true, deviceId, 2, 1);
//                    break;
//
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    // TODO : Emit Rotate Camera event from SocketHelper through MainActivity -> MainPresenter -> HelperInteractor.
//                    getParentActivity().rotateCamera(false, deviceId, 2, 0);
//                    break;
//            }
//            return true;
//        });
//    }

    void joinRoom(String twilioToken) {
        try {
            presenter.createAudioAndVideoTracks();
            presenter.connectToRoom(twilioToken, deviceId);
//             binding.imgRejectCall.setVisibility(View.VISIBLE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    void joinAgoraRoom(String agoraToken) {
        try {
            presenter.connectToAgoraRoom(agoraToken, deviceId, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
//             binding.imgRejectCall.setVisibility(View.VISIBLE);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectedToRoom() {
        binding.fragmentContentLiveFeedToHubAudio.endButton.setVisibility(View.VISIBLE);
    }

    /********************************* {@code public} Methods ********************************/

    void enableView() {
        mScreenTimeOutHandler.post(viewsRunnable);
    }

    /************************** {@link LiveFeedContract.View} Callbacks **********************/
//
//    @Override
//    public void onMirrorPrimaryVideoStream(boolean mirrorStatus) {
//        primaryVideoView.setMirror(mirrorStatus);
//    }

//    @Override
//    public void removeVideoPreview(boolean isPreviewOff) {
//            if (isPreviewOff) {
//                imgVideoUpstream.setVisibility(View.VISIBLE);
//                videoContainer.setBackground(getResources().getDrawable(R.drawable.transparent_preview_bg));
//            } else {
//                imgVideoUpstream.setVisibility(View.GONE);
//                videoContainer.setBackground(null);
//            }
//    }
    @Override
    public void onError(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
        onBackPressed();
    }

//    @Override
//    public void onMoveLocalVideoToThumbnailView() {
//        if (thumbnailVideoView.getVisibility() == View.GONE) {
//            thumbnailVideoView.setVisibility(View.VISIBLE);
////            presenter.moveVideoStreamView(primaryVideoView, thumbnailVideoView);
////            thumbnailVideoView.setMirror(presenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
//        }
//    }

//    @Override
//    public void onMoveLocalVideoToPrimaryView() {
//        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
//            thumbnailVideoView.setVisibility(View.GONE);
////            presenter.moveVideoStreamView(thumbnailVideoView, primaryVideoView);
////            primaryVideoView.setMirror(presenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
//        }
//    }

//    @Override
//    public void onAddPrimaryRenderer(VideoTrack videoTrack) {
//        primaryVideoView.setMirror(false);
//        presenter.addRenderer(videoTrack, primaryVideoView);
//    }
//
//    @Override
//    public void onRemoveParticipantVideo(VideoTrack videoTrack) {
//        videoTrack.removeRenderer(primaryVideoView);
//    }

    @Override
    public void onUpdateCountDownTimer(String countDown) {
        try {
            binding.connectingTimeText.setVisibility(View.VISIBLE);
            String hubName = MethodUtils.setWordWithCaps(hubDetails.getName()) + " " + Html.fromHtml("&#8226");
            String connectingText = String.format(getString(R.string.audio_connected_in), hubName, countDown);
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
//        presenter.enableVideo(isVideoEnabled);

        updateViewForLiveFeed(isMicEnabled, isVideoEnabled);
    }

    private void updateViewForLiveFeed(boolean isMicDisabled, boolean isVideoDisabled) {
        if (!isMicDisabled) {
            binding.fragmentContentLiveFeedToHubAudio.mikeButtonOff.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeedToHubAudio.mikeButtonOn.setVisibility(View.GONE);
        } else {
            binding.fragmentContentLiveFeedToHubAudio.mikeButtonOff.setVisibility(View.GONE);
            binding.fragmentContentLiveFeedToHubAudio.mikeButtonOn.setVisibility(View.VISIBLE);
        }

//        if (!isVideoDisabled) {
//            ivVideoOn.setVisibility(View.GONE);
//            ivVideoOff.setVisibility(View.VISIBLE);
////            ivCancelVideoUpstream.setVisibility(View.VISIBLE);
//        } else {
//            ivVideoOn.setVisibility(View.VISIBLE);
//            ivVideoOff.setVisibility(View.GONE);
////            ivCancelVideoUpstream.setVisibility(View.GONE);
//        }
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
            presenter.initiateLiveFeed(true, String.valueOf(hubDetails.getContactUserID()));
        } else {
            presenter.initiateLiveFeed(false, null);
        }
    }

    @Override
    public void showMessage(String message) {
        MethodUtils.showToastMessage(getParentActivity(), message);
    }

    public void onCallAccepted() {
        presenter.onConnectCall();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.endCall();

        if (mScreenTimeOutHandler != null){
            mScreenTimeOutHandler.removeCallbacks(viewsRunnable);
            mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentActivity().onEndLiveFeed();
        featureFragmentCleanUps();
    }

    /******************************* Listeners *******************************/

    private void setClickEvents() {
        binding.imgVideoOff.setOnClickListener(this::onViewClicked);
        binding.imgVideoOn.setOnClickListener(this::onViewClicked);
        binding.imgCallEnd.setOnClickListener(this::onViewClicked);
        binding.imgMuteOff.setOnClickListener(this::onViewClicked);
        binding.imgMuteOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHubAudio.dropInSpeaker.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHubAudio.dropInSpeakerOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHubAudio.mikeButtonOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHubAudio.mikeButtonOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedToHubAudio.endButton.setOnClickListener(this::onViewClicked);
        binding.imgRejectCall.setOnClickListener(this::onViewClicked);
    }

    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_video_off:
                isVideoEnabled = false;
                binding.imgVideoOff.setVisibility(View.GONE);
                binding.imgVideoOn.setVisibility(View.VISIBLE);
//                    presenter.enableVideo(true);
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
//                case R.id.iv_video_on:
//                    ivVideoOn.setVisibility(View.GONE);
//                    ivVideoOff.setVisibility(View.VISIBLE);
////                    ivCancelVideoUpstream.setVisibility(View.VISIBLE);
//                    presenter.enableVideo(false);
//                    break;
//                case R.id.iv_video_off:
//                    ivVideoOn.setVisibility(View.VISIBLE);
//                    ivVideoOff.setVisibility(View.GONE);
//                    presenter.enableVideo(true);
////                    ivCancelVideoUpstream.setVisibility(View.GONE);
//                    break;
            case R.id.drop_in_speaker:
                binding.fragmentContentLiveFeedToHubAudio.dropInSpeakerOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHubAudio.dropInSpeaker.setVisibility(View.GONE);
                presenter.enableSpeaker(false);
                break;
            case R.id.drop_in_speaker_off:
                binding.fragmentContentLiveFeedToHubAudio.dropInSpeakerOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHubAudio.dropInSpeaker.setVisibility(View.VISIBLE);
                presenter.enableSpeaker(true);
                break;
            case R.id.mike_button_on:
                binding.fragmentContentLiveFeedToHubAudio.mikeButtonOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedToHubAudio.mikeButtonOn.setVisibility(View.GONE);
                presenter.enableAudio(false);
                break;
            case R.id.mike_button_off:
                binding.fragmentContentLiveFeedToHubAudio.mikeButtonOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedToHubAudio.mikeButtonOn.setVisibility(View.VISIBLE);
                presenter.enableAudio(true);
                break;
            case R.id.img_reject_call:
            case R.id.img_call_end:
            case R.id.end_button:
                getParentActivity().emitEventToServer(DROP_IN_REJECT, sendRejectAction());
                getParentActivity().onBackPressed();
                break;
        }
    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    void onRootViewClicked() {
    }

    @Override
    public void featureFragmentCleanUps() {
        super.featureFragmentCleanUps();
        presenter.presenterCleanUps();
    }
}
