package com.sb.woohoo.ui.main.fragments.livefeedaudio;

import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;

import android.Manifest;
import android.media.AudioManager;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sb.woohoo.R;
import com.sb.woohoo.WooHooApp;
import com.sb.woohoo.analytics.AnalyticsHelper;
import com.sb.woohoo.analytics.Screen;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.data.prefs.SharedPrefsHelper;
import com.sb.woohoo.data.prefs.SharedPrefsKey;
import com.sb.woohoo.databinding.FragmentLiveFeedAudioBinding;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.base.FeatureFragment;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.MainActivity;
import com.sb.woohoo.ui.main.callbacks.VideoCalling;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.di.DaggerLiveFeedAudioComponent;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.di.LiveFeedAudioComponent;
import com.sb.woohoo.ui.main.fragments.livefeedaudio.di.LiveFeedAudioModule;
import com.sb.woohoo.ui.main.pojo.IntentInfo;
import com.sb.woohoo.ui.main.pojo.socketevent.LiveFeedSocketData;
import com.sb.woohoo.ui.main.sip.SipAccountManager;
import com.sb.woohoo.ui.main.sip.LinphoneManager;
import com.sb.woohoo.ui.main.sip.call.models.SipCallData;
import com.sb.woohoo.ui.main.sip.state.SipCallActionState;
import com.sb.woohoo.ui.main.sip.state.SipCallState;
import com.squareup.picasso.Picasso;
import com.sb.woohoo.utils.others.MethodUtils;
import org.linphone.core.Core;
import org.linphone.core.Call;

import java.util.Objects;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Shows Live Feed Video Streaming.
 */
public class LiveFeedAudioFragment
        extends FeatureFragment<MainActivity>
        implements LiveFeedAudioContract.View, VideoCalling {

    // Constants.
    private static final String TWILIO_ACCESS_TOKEN = "twilio_token";
    private static final String LIVE_FEED_NAME = "name";
    private static final String LIVE_FEED_EMAIL = "email";
    private static final String LIVE_FEED_USER_ID = "user_id";
    private static final String LIVE_FEED_IMG_URL = "profile_image";
    private static final String SENDER_HUB_ID = "senderHubId";
    private static final String SENDER_CLIENT_ID = "senderClientId";
    private static final String CONTACT_LIVE_FEED = "contactLiveFeed";
    private static final String CONTACT_USER_ID = "contactUserId";
    private static final String IS_SIP_MODE = "sipMode";
    private static final String SIP_ADDRESS = "sip_address";
    private static final String SIP_DISPLAY_NAME = "sip_display_name";
    private static final String SIP_DISPLAY_NUMBER = "sip_display_number";
    private static final String SIP_IMAGE_URL = "sip_image_url";
    private static final int MIC_PERMISSION_REQUEST_CODE = 5001;


    private final String FEATURE_CATEGORY = Categories.Communication.COMMAND_COMMUNICATION_CONTACT;


    // Presenter.
    @Inject
    LiveFeedAudioPresenter liveFeedAudioPresenter;
    // Other Components.
    @Inject
    Picasso picasso;
    @Inject
    AnalyticsHelper helper;
    @Inject
    SharedPrefsHelper sharedPrefs;
    // Variables.
    private String roomName;
    private String twilioToken;
    private String userName;
    private String userEmail;
    private String userImg;
    private String senderHubId;
    private String senderClientId;
    private String contactUserId;
    private boolean contactLiveFeed;
    private String userId;
    private Ringtone ringtone;
    private Runnable pendingMicPermissionAction;

    private boolean isSipMode;
    private boolean isSipIncomingCall = false;
    private long callConnectedAtMs;
    private int callVolumeStream = AudioManager.STREAM_MUSIC;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentLiveFeedAudioBinding binding;


    public LiveFeedAudioFragment() { /*Required empty constructor*/ }

    public static LiveFeedAudioFragment newInstance(
            String twilioToken,
            LiveFeedSocketData participantData) {
        LiveFeedAudioFragment fragment = new LiveFeedAudioFragment();

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
            args.putBoolean(IS_SIP_MODE, false);
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

    public static LiveFeedAudioFragment newInstance(
            SipCallData participantData) {
        LiveFeedAudioFragment fragment = new LiveFeedAudioFragment();

        if (participantData != null) {
            String address = participantData.getAddress();
            Bundle args = new Bundle();
            args.putBoolean(IS_SIP_MODE, true);
            args.putString(SIP_ADDRESS, address);
            args.putString(SIP_DISPLAY_NAME, participantData.getDisplayName());
            args.putString(SIP_DISPLAY_NUMBER, participantData.getDisplayNumber());
            args.putString(SIP_IMAGE_URL, participantData.getImageUrl());

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
        return LiveFeedAudioFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            final ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helper.track(new Screen.LiveFeedAudio(),  null);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getParentActivity().getApplicationContext(), notification);
        binding = FragmentLiveFeedAudioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onIntentCreate(IntentInfo intentInfo) {

    }

    @Override
    public void initDagger() {
        LiveFeedAudioComponent liveFeedAudioComponent =
                DaggerLiveFeedAudioComponent
                        .builder()
                        .liveFeedAudioModule(new LiveFeedAudioModule(this))
                        .build();
        liveFeedAudioComponent.injectLiveFeedAudioFragment(LiveFeedAudioFragment.this);
    }

    @Override
    public void bindViews(View view) {
        setHint(HINT_NO_CHANGE, HINT_NO_CHANGE, View.GONE);
        setClickEvents();
        setDialPadClickEvents();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBundleArguments(getArguments());

        if (!isSipMode) {
            ringtone.play();
            bindDataToView();
            liveFeedAudioPresenter.setConnectingScreenTimer(!senderClientId.equalsIgnoreCase(""));
            enableView();
            liveFeedAudioPresenter.initializeEngine(twilioToken, roomName, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));
        } else {
            ensureMicPermissionThen(this::startSipFlow);
        }

    }

    private void ensureMicPermissionThen(Runnable onGranted) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            onGranted.run();
        } else {
            pendingMicPermissionAction = onGranted;
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIC_PERMISSION_REQUEST_CODE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (granted && pendingMicPermissionAction != null) {
                pendingMicPermissionAction.run();
            } else {
                MethodUtils.showToastMessage(getParentActivity(), getString(R.string.permission_needed));
            }
            pendingMicPermissionAction = null;
        }
    }

    private void startSipFlow() {
        liveFeedAudioPresenter.isSipMode = true;
        bindSipDataToView();
        processSipCall(SipAccountManager.getInstance().currentSipCallState);
        compositeDisposable.add(
                (Disposable)
                        SipAccountManager.getInstance()
                                .sipCallSubject
                                .subscribe(
                                        this::processSipCall,
                                        throwable -> {
                                        }));
    }

    private void processSipCall(SipCallState sipCallState) {
 Log.i("XXX" , "LiveFeedAudioFragment:: processSipCall : " + sipCallState);
        if (sipCallState instanceof SipCallState.End || sipCallState instanceof SipCallState.Released || sipCallState instanceof SipCallState.Error) {
            stopConnectedCallTimer();
            onBackPressed();
        } else if (sipCallState instanceof SipCallState.Connected || sipCallState instanceof SipCallState.Running) {
            isSipIncomingCall = false;
            onSipCallConnected();
        } else if (sipCallState instanceof SipCallState.OutgoingProgress || sipCallState instanceof SipCallState.OutgoingRinging || sipCallState instanceof SipCallState.OutgoingInit) {
            isSipIncomingCall = false;
            onSipCallConnecting();
        } else if (sipCallState instanceof SipCallState.IncomingReceived) {
            ringtone.play();

            isSipIncomingCall = true;
            onSipCallConnecting();
        }

        if(!(sipCallState instanceof SipCallState.IncomingReceived) && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return liveFeedAudioPresenter;
    }


    /******************************** {@code private} Methods ********************************/


    private void getBundleArguments(Bundle bundle) {
        if (bundle != null) {
            isSipMode = bundle.getBoolean(IS_SIP_MODE);

            if(!isSipMode) {
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
            } else {
                String sipAddress = bundle.getString(SIP_ADDRESS);
                userName = bundle.getString(SIP_DISPLAY_NAME);
                userEmail = bundle.getString(SIP_DISPLAY_NUMBER);
                userImg = bundle.getString(SIP_IMAGE_URL);

                if (TextUtils.isEmpty(userEmail)) {
                    userEmail = extractSipNumber(sipAddress);
                }
                if (TextUtils.isEmpty(userName)) {
                    userName = userEmail;
                }
            }

            //roomName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        }

        roomName = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
    }


    @Override
    public void onConnectedToRoom() {
        binding.liveFeedEnd.setVisibility(View.VISIBLE);
        binding.fragmentContentLiveFeedAudio.connectedStatusText.setText(getString(R.string.sip_call_connected_status));
        binding.fragmentContentLiveFeedAudio.connectedSecondaryDetailValue.setText(getString(R.string.sip_call_detail_secondary_value));
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setVisibility(View.VISIBLE);
        startConnectedCallTimer();
    }

    private void bindDataToView() {
        bindProfileImage(userImg);
        String displayName = !TextUtils.isEmpty(userName) ? userName : split(userEmail);
        String displayNumber = !TextUtils.isEmpty(userEmail) ? userEmail : "";
        setParticipantDetails(displayName, displayNumber);
    }

    private void bindSipDataToView() {
        bindProfileImage(userImg);
        String displayName = !TextUtils.isEmpty(userName) ? userName : "";
        String displayNumber = !TextUtils.isEmpty(userEmail) ? userEmail : "";
        if (TextUtils.isEmpty(displayName)) {
            displayName = displayNumber;
        }
        setParticipantDetails(displayName, displayNumber);
    }

    private void setParticipantDetails(String displayName, String displayNumber) {
        binding.personText.setText(displayName);
        binding.sipIncomingName.setText(displayName);
        binding.fragmentContentLiveFeedAudio.personNameCall.setText(displayName);
        if (!TextUtils.isEmpty(displayNumber)) {
            binding.personNumberText.setVisibility(View.VISIBLE);
            binding.personNumberText.setText(displayNumber);
        } else {
            binding.personNumberText.setVisibility(View.GONE);
        }
        String connectedPrimaryValue = !TextUtils.isEmpty(displayNumber) ? displayNumber : displayName;
        binding.fragmentContentLiveFeedAudio.personNumberCall.setVisibility(View.VISIBLE);
        binding.fragmentContentLiveFeedAudio.personNumberCall.setText(connectedPrimaryValue);
        binding.fragmentContentLiveFeedAudio.connectedSecondaryDetailValue.setText(getString(R.string.sip_call_detail_secondary_value));
    }

    private void bindProfileImage(String imagePath) {
        String imageUrl = resolveImageUrl(imagePath);
        if (!TextUtils.isEmpty(imageUrl)) {
            picasso.load(imageUrl).fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(binding.personImage);
            picasso.load(imageUrl).fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.contact_no_image)
                    .placeholder(R.drawable.contact_no_image)
                    .into(binding.fragmentContentLiveFeedAudio.personImageCall);
            picasso.load(imageUrl).fit()
                    .transform(new CircleImageTransform())
                    .error(R.drawable.ic_bell_white)
                    .placeholder(R.drawable.ic_bell_white)
                    .into(binding.sipIncomingAvatar);
        } else {
            binding.personImage.setImageResource(R.drawable.contact_no_image);
            binding.fragmentContentLiveFeedAudio.personImageCall.setImageResource(R.drawable.contact_no_image);
            binding.sipIncomingAvatar.setImageResource(R.drawable.ic_bell_white);
        }
    }

    private String split(String userEmail) {
        if (TextUtils.isEmpty(userEmail)) {
            return "";
        }
        String[] userName = userEmail.split("@");
        return userName[0];
    }

    private String resolveImageUrl(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return null;
        }
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        return IMAGE_BASE_URL + imagePath;
    }

    private String extractSipNumber(String sipAddress) {
        if (TextUtils.isEmpty(sipAddress)) {
            return "";
        }
        String value = sipAddress.trim();
        if (value.startsWith("sip:")) {
            value = value.substring(4);
        }
        int atIndex = value.indexOf('@');
        if (atIndex >= 0) {
            value = value.substring(0, atIndex);
        }
        int paramIndex = value.indexOf(';');
        if (paramIndex >= 0) {
            value = value.substring(0, paramIndex);
        }
        return value.trim();
    }

    @Override
    public void onUpdateCountDownTimer(String countDown) {
        binding.connectingTimeText.setVisibility(View.VISIBLE);
        String connectingText = getString(R.string.audio_get_connected);
        binding.connectingTimeText.setText(connectingText);
    }

    @Override
    public void onConnectingToStream() {
        if (isSipMode) {
            showPendingCallSurface(getString(R.string.sip_call_connecting_status));
            return;
        }
        binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        binding.fragmentContentLiveFeedAudio.connectedStatusText.setText(getString(R.string.sip_call_connecting_status));
        binding.fragmentContentLiveFeedAudio.connectedSecondaryDetailValue.setText(getString(R.string.sip_call_waiting_for_answer));
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setVisibility(View.GONE);
        liveFeedAudioPresenter.connectToAgoraRoom(twilioToken, roomName, Integer.parseInt(sharedPrefs.get(SharedPrefsKey.PREF_KEY_USER_ID, "0")));

    }

    void enableView() {
        binding.liveFeedCall.setVisibility(View.VISIBLE);
        binding.liveFeedEnd.setVisibility(View.VISIBLE);
    }

    void onSipCallConnected() {
        hideSipIncomingScreen();
        bindCallHeaders();
        binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        binding.fragmentContentLiveFeedAudio.connectedStatusText.setText(getString(R.string.sip_call_connected_status));
        binding.fragmentContentLiveFeedAudio.connectedSecondaryDetailValue.setText(getString(R.string.sip_call_detail_secondary_value));
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setVisibility(View.VISIBLE);
        binding.connectingTimeText.setText(getString(R.string.audio_get_connected));
        binding.liveFeedCall.setVisibility(View.GONE);
        binding.liveFeedEnd.setVisibility(View.VISIBLE);
        startConnectedCallTimer();
    }

    void onSipCallConnecting() {
        stopConnectedCallTimer();
        if (isSipIncomingCall) {
            showSipIncomingScreen();
        } else {
            showPendingCallSurface(resolveSipOutgoingStatus());
        }
    }

    public void onSipCallAccept() {
        SipAccountManager.getInstance().sipCallActionSubject.onNext(SipCallActionState.Accept.INSTANCE);
    }

    public void onSipCallEnd() {
        if(isSipIncomingCall) {
            SipAccountManager.getInstance().sipCallActionSubject.onNext(SipCallActionState.Reject.INSTANCE);
        } else {
            SipAccountManager.getInstance().sipCallActionSubject.onNext(SipCallActionState.End.INSTANCE);
        }

    }

    public void onSipCallDecline() {
    }

    private void sendAction() {
        if (contactLiveFeed) {
            liveFeedAudioPresenter.sendAcceptActionObj(senderClientId, senderHubId, roomName,
                    WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, null),
                    contactLiveFeed, userId);
        } else {
            liveFeedAudioPresenter.sendAcceptActionObj(senderClientId, senderHubId, roomName,
                    WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_SESSION_TOKEN, null),
                    contactLiveFeed, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopConnectedCallTimer();
        liveFeedAudioPresenter.endCall();
        getParentActivity().onEndLiveFeed();

       if(isSipMode) {
          onSipCallEnd();
        }
    }

    public void onBackPressed() {
        getParentActivity().onBackPressed();
    }

    private void setClickEvents() {
        binding.liveFeedCall.setOnClickListener(this::onViewClicked);
        binding.liveFeedEnd.setOnClickListener(this::onViewClicked);
        binding.sipIncomingAcceptButton.setOnClickListener(this::onViewClicked);
        binding.sipIncomingDeclineButton.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedAudio.dropInSpeaker.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedAudio.dropInSpeakerOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedAudio.mikeButtonOn.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedAudio.mikeButtonOff.setOnClickListener(this::onViewClicked);
        binding.fragmentContentLiveFeedAudio.endButton.setOnClickListener(this::onViewClicked);
    }

    private void setDialPadClickEvents() {
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewTextView1.setOnClickListener(view -> onDialPadKeyPressed('1'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout2.setOnClickListener(view -> onDialPadKeyPressed('2'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout3.setOnClickListener(view -> onDialPadKeyPressed('3'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout4.setOnClickListener(view -> onDialPadKeyPressed('4'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout5.setOnClickListener(view -> onDialPadKeyPressed('5'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout6.setOnClickListener(view -> onDialPadKeyPressed('6'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout7.setOnClickListener(view -> onDialPadKeyPressed('7'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout8.setOnClickListener(view -> onDialPadKeyPressed('8'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout9.setOnClickListener(view -> onDialPadKeyPressed('9'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewRelativeLayout0.setOnClickListener(view -> onDialPadKeyPressed('0'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewTextViewStar.setOnClickListener(view -> onDialPadKeyPressed('*'));
        binding.fragmentContentLiveFeedAudio.viewDialerInCall.fragmentDialerViewTextViewHash.setOnClickListener(view -> onDialPadKeyPressed('#'));
    }

    private void onDialPadKeyPressed(char key) {
        if (!isSipMode) {
            return;
        }
        Core core = LinphoneManager.getCore();
        if (core == null) {
            return;
        }
        Call call = core.getCurrentCall();
        if (call == null) {
            return;
        }
        call.sendDtmf(key);
        core.playDtmf(key, 120);
    }

    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.live_feed_call:
            case R.id.sip_incoming_accept_button:
                ringtone.stop();
                if(!isSipMode){
                    sendAction();

                }
                liveFeedAudioPresenter.onConnectCall();
                if(isSipMode){
                    Log.i("XXX" , "onViewClicked: onSipCallAccept");
                    isSipIncomingCall = false;
                   onSipCallAccept();
                }

                break;

            case R.id.live_feed_end:
            case R.id.sip_incoming_decline_button:
                if(!isSipMode){
                    onBackPressed();
                } else {
                    Log.e("SipCallEnd","End");
                    onSipCallEnd();
                    onBackPressed();
                }

                break;

            case R.id.drop_in_speaker:
                binding.fragmentContentLiveFeedAudio.dropInSpeakerOff.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedAudio.dropInSpeaker.setVisibility(View.GONE);
                liveFeedAudioPresenter.enableSpeaker(false);
                break;

            case R.id.drop_in_speaker_off:
                binding.fragmentContentLiveFeedAudio.dropInSpeakerOff.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedAudio.dropInSpeaker.setVisibility(View.VISIBLE);
                liveFeedAudioPresenter.enableSpeaker(true);
                break;

            case R.id.mike_button_on:
                binding.fragmentContentLiveFeedAudio.mikeButtonOn.setVisibility(View.GONE);
                binding.fragmentContentLiveFeedAudio.mikeButtonOff.setVisibility(View.VISIBLE);
                liveFeedAudioPresenter.enableAudio(false);
                break;

            case R.id.mike_button_off:
                binding.fragmentContentLiveFeedAudio.mikeButtonOn.setVisibility(View.VISIBLE);
                binding.fragmentContentLiveFeedAudio.mikeButtonOff.setVisibility(View.GONE);
                liveFeedAudioPresenter.enableAudio(true);
                break;

            //End button action on click
            case R.id.end_button:
                //Updating hub status
                if(!isSipMode){
                    onBackPressed();
                } else {
                    onSipCallEnd();
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        if(ringtone!=null && ringtone.isPlaying())
            ringtone.stop();

    }

    @Override
    public void onResume() {
        super.onResume();
        bindCallHeaders();
        configureCallVolumeControl();
    }

    private void showSipIncomingScreen() {
        bindCallHeaders();
        binding.sipIncomingLayout.setVisibility(View.VISIBLE);
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        binding.liveFeedConnectedLayout.setVisibility(View.GONE);
    }

    private void hideSipIncomingScreen() {
        binding.sipIncomingLayout.setVisibility(View.GONE);
    }

    private void bindCallHeaders() {
        if (binding == null) {
            return;
        }
        String roomLabel = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_DEVICE_NAME, "");
        if (TextUtils.isEmpty(roomLabel)) {
            roomLabel = WooHooApp.getSharedPrefsHelper().get(SharedPrefsKey.PREF_KEY_DEVICE_ID, "");
        }
        if (TextUtils.isEmpty(roomLabel)) {
            roomLabel = getString(R.string.sip_room_fallback);
        } else {
            roomLabel = formatRoomLabel(roomLabel);
        }
        binding.sipIncomingRoomLabel.setText(roomLabel);
        binding.fragmentContentLiveFeedAudio.connectedRoomLabel.setText(roomLabel);
    }

    private void configureCallVolumeControl() {
        if (binding == null || getContext() == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) requireContext().getSystemService(android.content.Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return;
        }
        callVolumeStream = resolveCallVolumeStream(audioManager);
        binding.fragmentContentLiveFeedAudio.callVolumeSeekBar.setMax(audioManager.getStreamMaxVolume(callVolumeStream));
        binding.fragmentContentLiveFeedAudio.callVolumeSeekBar.setProgress(audioManager.getStreamVolume(callVolumeStream));
        binding.fragmentContentLiveFeedAudio.callVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(callVolumeStream, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private int resolveCallVolumeStream(AudioManager audioManager) {
        if (isSipMode) {
            int voiceCallMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            if (voiceCallMax > 0) {
                return AudioManager.STREAM_VOICE_CALL;
            }
        }
        return AudioManager.STREAM_MUSIC;
    }

    private void startConnectedCallTimer() {
        if (binding == null) {
            return;
        }
        if (callConnectedAtMs == 0L) {
            callConnectedAtMs = SystemClock.elapsedRealtime();
        }
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setBase(callConnectedAtMs);
        binding.fragmentContentLiveFeedAudio.connectedDurationText.start();
    }

    private void stopConnectedCallTimer() {
        if (binding == null) {
            return;
        }
        callConnectedAtMs = 0L;
        binding.fragmentContentLiveFeedAudio.connectedDurationText.stop();
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setBase(SystemClock.elapsedRealtime());
    }

    private void showPendingCallSurface(String statusText) {
        hideSipIncomingScreen();
        bindCallHeaders();
        binding.liveFeedConnectingLayout.setVisibility(View.GONE);
        binding.liveFeedConnectedLayout.setVisibility(View.VISIBLE);
        binding.liveFeedCall.setVisibility(View.GONE);
        binding.liveFeedEnd.setVisibility(View.GONE);
        binding.fragmentContentLiveFeedAudio.connectedStatusText.setText(statusText);
        binding.fragmentContentLiveFeedAudio.connectedSecondaryDetailValue.setText(getString(R.string.sip_call_waiting_for_answer));
        binding.fragmentContentLiveFeedAudio.connectedDurationText.setVisibility(View.GONE);
    }

    private String resolveSipOutgoingStatus() {
        SipCallState currentState = SipAccountManager.getInstance().currentSipCallState;
        if (currentState instanceof SipCallState.OutgoingRinging) {
            return getString(R.string.sip_call_ringing_status);
        }
        return getString(R.string.sip_call_connecting_status);
    }

    private String formatRoomLabel(String label) {
        String trimmedLabel = label.trim();
        if (trimmedLabel.matches("\\d+[A-Za-z]?")) {
            return ("ROOM" + trimmedLabel).toUpperCase(Locale.US);
        }
        if (trimmedLabel.toUpperCase(Locale.US).startsWith("ROOM")) {
            return trimmedLabel.toUpperCase(Locale.US);
        }
        if (trimmedLabel.length() <= 6 && trimmedLabel.matches("[A-Za-z0-9]+")) {
            return ("ROOM" + trimmedLabel).toUpperCase(Locale.US);
        }
        return trimmedLabel.toUpperCase(Locale.US);
    }
}
