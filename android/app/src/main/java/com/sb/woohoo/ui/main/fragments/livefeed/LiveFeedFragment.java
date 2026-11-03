package com.sb.woohoo.ui.main.fragments.livefeed;

import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;
import static com.sb.woohoo.utils.Constants.IS_AGORA_ENABLED;

import android.annotation.SuppressLint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentLiveFeedBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.livefeed.di.DaggerLiveFeedComponent;
import com.sb.woohoo.ui.main.fragments.livefeed.di.LiveFeedComponent;
import com.sb.woohoo.ui.main.fragments.livefeed.di.LiveFeedModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedSocketData;
import com.sb.woohoo.utils.others.MethodUtils;
import com.squareup.picasso.Picasso;
import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoTrack;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Shows Live Feed Video Streaming.
 */
public class LiveFeedFragment
        extends FeatureFragment<MainActivity>
        implements LiveFeedContract.View, VideoCalling {

    // Constants.
    private static final String TWILIO_ACCESS_TOKEN = "twilio_token";
    private static final String LIVE_FEED_NAME = "name";
    private static final String LIVE_FEED_EMAIL = "email";
    private static final String LIVE_FEED_USER_ID = "user_id";
    private static final String LIVE_FEED_IMG_URL = "profile_image";
    private static final String DEFAULT_USER_NAME = "WooHoo User";
    private static final String SENDER_HUB_ID = "senderHubId";
    private static final String SENDER_CLIENT_ID = "senderClientId";
    private static final String CONTACT_LIVE_FEED = "contactLiveFeed";
    private static final String CONTACT_USER_ID = "contactUserId";
    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;


    // Presenter.
    @Inject
    LiveFeedPresenter liveFeedPresenter;
    @Inject
    AnalyticsHelper helper;
    @Inject
    SharedPrefsHelper sharedPrefs;
    // Other Components.
    @Inject
    Picasso picasso;

    public FragmentLiveFeedBinding binding;

    // Variables.
    private String roomName;
    private Handler mScreenTimeOutHandler = new Handler();
    private String twilioToken;
    private String userName;
    private String userEmail;
    private String userImg;
    private int screenTimeOutError = 5;
    private boolean touch = true;
    private String senderHubId;
    public String senderClientId;
    private String contactUserId;
    private String userId;
    private Ringtone ringtone;

    private int agoraUid;
    private boolean contactLiveFeed = false;

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


    private LiveFeedFragment() { /*Required empty constructor*/ }

    public static LiveFeedFragment newInstance(
            String twilioToken,
            LiveFeedSocketData participantData) {
        LiveFeedFragment fragment = new LiveFeedFragment();

        if (twilioToken != null && participantData != null) {
            String name = participantData.getFirstName() + " " + participantData.getLastName();
            String email = participantData.getEmail();
            String userId = String.valueOf(participantData.getUserId());
            String url = participantData.getImage();
            String senderHubId = participantData.getSenderHubId() != null ? participantData.getSenderHubId() : "";
            String senderClientId = participantData.getSenderClientId() != null ? participantData.getSenderClientId() : "";
            String contactUserId = participantData.getContactUserId() != null ? participantData.getContactUserId() : "";
            String contactLiveFeed = participantData.getContactLiveFeed() != null ? participantData.getContactLiveFeed() : "false";

            Bundle args = new Bundle();
            args.putString(TWILIO_ACCESS_TOKEN, twilioToken);
            args.putString(LIVE_FEED_NAME, name);
            args.putString(LIVE_FEED_EMAIL, email);
            args.putString(LIVE_FEED_USER_ID, userId);
            args.putString(LIVE_FEED_IMG_URL, url);
            args.putString(SENDER_HUB_ID, senderHubId);
            args.putString(SENDER_CLIENT_ID, senderClientId);
            args.putString(CONTACT_LIVE_FEED, contactLiveFeed);
            args.putString(CONTACT_USER_ID, contactUserId);

            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public String getCategory() {
        return FEATURE_CATEGORY;
    }

    @Override
    public String createTag() {
        return LiveFeedFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            final ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.LiveFeed(),  null);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getParentActivity().getApplicationContext(), notification);
        ringtone.play();

        binding = FragmentLiveFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        LiveFeedComponent liveFeedComponent =
                DaggerLiveFeedComponent
                        .builder()
                        .liveFeedModule(new LiveFeedModule(this))
                        .build();
        liveFeedComponent.injectLiveFeedFragment(LiveFeedFragment.this);
    }

    @Override
    public void bindViews(View view) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBundleArguments(getArguments());
        initViews();
        bindDataToView();
        enableView();
        Log.i("AgoraCall", "AgoraCall::joinRoom receiver : "  + twilioToken + ", roomName: " + roomName + ", contactUserId: " + contactUserId+ ", sender: " + userId);
        liveFeedPresenter.initializeEngine(twilioToken, roomName, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
        //joinRoom(twilioToken, roomName, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));

        liveFeedPresenter.setConnectingScreenTimer(!senderClientId.equalsIgnoreCase(""));
    }

    @Override
    public BasePresenter getPresenter() {
        return liveFeedPresenter;
    }


    /******************************** {@code private} Methods ********************************/


    private void getBundleArguments(Bundle bundle) {
        if (bundle != null) {

            //Twilio things needed to connect to room
            twilioToken = bundle.getString(TWILIO_ACCESS_TOKEN);
            userName = bundle.getString(LIVE_FEED_NAME);
            userEmail = bundle.getString(LIVE_FEED_EMAIL);
            userImg = bundle.getString(LIVE_FEED_IMG_URL);
            senderHubId = bundle.getString(SENDER_HUB_ID);
            senderClientId = bundle.getString(SENDER_CLIENT_ID);
            contactUserId = bundle.getString(CONTACT_USER_ID);
            userId = bundle.getString(LIVE_FEED_USER_ID);
            contactLiveFeed = Objects.requireNonNull(bundle.getString(CONTACT_LIVE_FEED)).equalsIgnoreCase("true");

            roomName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        }
    }

    private void initViews() {

        ((ViewGroup) binding.fragmentContentLiveFeed.status.getParent()).setOnClickListener(view -> {
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

        setClickEvents();
    }

    private void joinRoom(String twilioToken, String roomName, int uid) {
        if(IS_AGORA_ENABLED) {
            liveFeedPresenter.connectToAgoraRoom(twilioToken, roomName, uid);
        } else {
            try {

//                liveFeedPresenter.createAudioAndVideoTracks(binding.fragmentContentLiveFeed.primaryVideoView);
//                liveFeedPresenter.connectToRoom(twilioToken, roomName);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectedToRoom() {
        binding.liveFeedEnd.setVisibility(View.VISIBLE);
    }

    private void handleViews(boolean isTouched) {
        if (!isTouched) {
            if (binding.fragmentContentLiveFeed.stateLayout != null && binding.fragmentContentLiveFeed.status != null) {
                binding.fragmentContentLiveFeed.stateLayout.setVisibility(View.GONE);
                binding.fragmentContentLiveFeed.status.setBackground(null);
            }
        } else {
            binding.fragmentContentLiveFeed.stateLayout.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeed.status.setBackground(getResources().getDrawable(R.drawable.live_feed_gradient));
        }
    }

    private void bindDataToView() {

        picasso.load(IMAGE_BASE_URL + userImg).fit()
                .transform(new CircleImageTransform())
                .error(R.drawable.contact_no_image)
                .placeholder(R.drawable.contact_no_image)
                .into(binding.personImage);

        if (userName != null && !userName.isEmpty()) {
            binding.personText.setText(userName);
        } else {
            binding.personText.setText(split(userEmail));
        }
    }

    private String split(String userEmail) {
        String[] userName = userEmail.split("@");
        return userName[0];
    }

    @Override
    public void onUpdateCountDownTimer(String countDown) {
        binding.connectingTimeText.setVisibility(View.VISIBLE);
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String connectingText = String.format(getString(R.string.streaming_live_feed_in), countDown);
        binding.connectingTimeText.setText(connectingText);
    }

    @Override
    public void onConnectingToStream() {
        binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        mScreenTimeOutHandler.post(mScreenTimeOutRunnable);
        if(IS_AGORA_ENABLED) {
            liveFeedPresenter.connectToAgoraRoom(twilioToken, roomName, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
        }
    }

    @Override
    public void onRemoveParticipantVideo(VideoTrack videoTrack) {
//        if (videoTrack != null && binding.fragmentContentLiveFeed.primaryVideoView != null) {
//            videoTrack.removeRenderer(binding.fragmentContentLiveFeed.primaryVideoView);
//        }
    }

    @Override
    public void onMoveLocalVideoToThumbnailView() {
//        if (binding.fragmentContentLiveFeed.thumbnailVideoView != null && binding.fragmentContentLiveFeed.thumbnailVideoView.getVisibility() == View.GONE) {
//            binding.fragmentContentLiveFeed.thumbnailVideoView.setVisibility(View.VISIBLE);
//            liveFeedPresenter.moveVideoStreamView(binding.fragmentContentLiveFeed.primaryVideoView, binding.fragmentContentLiveFeed.thumbnailVideoView);
//            binding.fragmentContentLiveFeed.thumbnailVideoView.setMirror(liveFeedPresenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
//        }
    }

    @Override
    public void onAddPrimaryRenderer(VideoTrack videoTrack) {
//        binding.fragmentContentLiveFeed.primaryVideoView.setMirror(false);
//        liveFeedPresenter.addRenderer(videoTrack, binding.fragmentContentLiveFeed.primaryVideoView);
    }

    @Override
    public void onMoveLocalVideoToPrimaryView() {
        /*if (binding.fragmentContentLiveFeed.thumbnailVideoView != null && binding.fragmentContentLiveFeed.thumbnailVideoView.getVisibility() == View.VISIBLE) {
            binding.fragmentContentLiveFeed.thumbnailVideoView.setVisibility(View.GONE);
            liveFeedPresenter.moveVideoStreamView(binding.fragmentContentLiveFeed.thumbnailVideoView, binding.fragmentContentLiveFeed.primaryVideoView);
            binding.fragmentContentLiveFeed.primaryVideoView.setMirror(liveFeedPresenter.checkCameraSource(CameraCapturer.CameraSource.FRONT_CAMERA));
        }*/
    }

    @Override
    public void onMirrorPrimaryVideoStream(boolean mirrorStatus) {
//        binding.fragmentContentLiveFeed.primaryVideoView.setMirror(mirrorStatus);
    }

    @Override
    public void removeVideoPreview(boolean isPreviewOff) {
        if (isPreviewOff) {
            binding.fragmentContentLiveFeed.imgVideoUpstream.setVisibility(View.VISIBLE);
            binding.fragmentContentLiveFeed.videoContainer.setBackground(getResources().getDrawable(R.drawable.transparent_preview_bg));
        } else {
            binding.fragmentContentLiveFeed.imgVideoUpstream.setVisibility(View.GONE);
            binding.fragmentContentLiveFeed.videoContainer.setBackground(null);
        }
    }

    void enableView() {
        binding.connectingTimeText.setVisibility(senderClientId.equalsIgnoreCase("") ? View.INVISIBLE : View.VISIBLE);
        binding.liveFeedCall.setVisibility(View.VISIBLE);
        binding.liveFeedEnd.setVisibility(View.VISIBLE);
    }

    public void sendAction() {
        if (contactLiveFeed) {
            liveFeedPresenter.sendAcceptActionObj(senderClientId, senderHubId, roomName,
                    WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, null),
                    contactLiveFeed, userId);
        } else {
            liveFeedPresenter.sendAcceptActionObj(senderClientId, senderHubId, roomName,
                    WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, null),
                    contactLiveFeed, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        liveFeedPresenter.endCall();
        getParentActivity().onEndLiveFeed();
        if (mScreenTimeOutHandler != null) {
            mScreenTimeOutHandler.removeCallbacks(mScreenTimeOutRunnable);
        }
    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    private void setClickEvents() {
        binding.liveFeedCall.setOnClickListener(this::onViewClicked);
        binding.liveFeedEnd.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.ivVideoOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.ivVideoOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.dropInSpeaker.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.dropInSpeakerOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.mikeButtonOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.mikeButtonOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeed.endButton.setOnClickListener(this::onViewClicked);
    }
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.live_feed_call:
                ringtone.stop();
                ringtone.stop();
                sendAction();
                liveFeedPresenter.onConnectCall();
                break;

            case R.id.live_feed_end:
                liveFeedPresenter.endCall();
                getParentActivity().onEndLiveFeed();
                onBackPressed();
                break;

            case R.id.iv_video_on:
                binding.fragmentContentLiveFeed.ivVideoOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeed.ivVideoOn.setVisibility(View.GONE);
                liveFeedPresenter.enableVideo(false);
                binding.fragmentContentLiveFeed.ivCancelVideoUpstream.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_video_off:
                binding.fragmentContentLiveFeed.ivVideoOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeed.ivVideoOn.setVisibility(View.VISIBLE);
                liveFeedPresenter.enableVideo(true);
                binding.fragmentContentLiveFeed.ivCancelVideoUpstream.setVisibility(View.GONE);
                break;

            case R.id.drop_in_speaker:
                binding.fragmentContentLiveFeed.dropInSpeakerOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeed.dropInSpeaker.setVisibility(View.GONE);
                liveFeedPresenter.enableSpeaker(false);
                break;

            case R.id.drop_in_speaker_off:
                binding.fragmentContentLiveFeed.dropInSpeakerOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeed.dropInSpeaker.setVisibility(View.VISIBLE);
                liveFeedPresenter.enableSpeaker(true);
                break;

            case R.id.mike_button_on:
                binding.fragmentContentLiveFeed.mikeButtonOff.setVisibility(View.VISIBLE);
                liveFeedPresenter.enableAudio(false);
                break;

            case R.id.mike_button_off:
                binding.fragmentContentLiveFeed.mikeButtonOn.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeed.mikeButtonOff.setVisibility(View.GONE);
                liveFeedPresenter.enableAudio(true);
                break;

            //End button action on click
            case R.id.end_button:
                if(IS_AGORA_ENABLED){
                    liveFeedPresenter.endCall();
                    getParentActivity().onEndLiveFeed();
                }
                //Updating hub status
                onBackPressed();
                break;
        }
    }

    @Override
    public void onError(String message) {
        MethodUtils.showToastMessage(requireActivity(), message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ringtone !=null && ringtone.isPlaying())
            ringtone.stop();
    }
}
