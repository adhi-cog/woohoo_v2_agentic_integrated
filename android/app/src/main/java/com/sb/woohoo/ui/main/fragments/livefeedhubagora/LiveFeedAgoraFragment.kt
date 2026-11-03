package com.sb.woohoo.ui.main.fragments.livefeedhubagora

import com.sb.woohoo.ui.base.FeatureFragment
import com.sb.woohoo.ui.main.pojo.IntentInfo
import com.sb.woohoo.ui.base.BasePresenter
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread
import com.sb.woohoo.R
import com.sb.woohoo.ui.main.MainActivity
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import java.lang.Exception
import java.lang.RuntimeException

class LiveFeedAgoraFragment : FeatureFragment<MainActivity?>() {

    override fun onIntentCreate(intentInfo: IntentInfo<*>?) {}
    override fun initDagger() {}
    override fun bindViews(view: View) {}
    override fun getCategory(): String {
        return ""
    }

    override fun getPresenter(): BasePresenter? {
        return null
    }

    private var mRtcEngine: RtcEngine? = null
    private var mCallEnd = false
    private var mMuted = false
    private var mLocalContainer: FrameLayout? = null
    private var mRemoteContainer: RelativeLayout? = null
    private var mLocalVideo: VideoCanvas? = null
    private var mRemoteVideo: VideoCanvas? = null
    private var mCallBtn: ImageView? = null
    private var mMuteBtn: ImageView? = null
    private var mSwitchCameraBtn: ImageView? = null


    var mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            Log.i(
                "AgoraCall",
                "AgoraCall::onJoinChannelSuccess : roomName: $channel, uid: $uid"
            )
           setupLocalVideo()
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i("AgoraCall", "AgoraCall::onUserJoined : $uid")
           setupRemoteVideo(uid)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            onRemoteUserLeft(uid)
        }

        override fun onError(err: Int) {
            super.onError(err)
            Log.i("AgoraCall", "AgoraCall::onError : $err")
        }

        override fun onConnectionStateChanged(state: Int, reason: Int) {
            super.onConnectionStateChanged(state, reason)
            Log.i(
                "AgoraCall",
                "AgoraCall::onConnectionStateChanged state: $state , reason:$reason"
            )
        }
    }

    private val mRtcEventHandler2: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.i("--Agora--", "Join channel success, uid: $uid")
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.i("--Agora--", "First remote video decoded, uid: $uid")
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Log.i("--Agora--", "User offline, uid: $uid")
                onRemoteUserLeft(uid)
            }
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        var parent: ViewGroup? = mRemoteContainer
        if (parent!!.indexOfChild(mLocalVideo!!.view) > -1) {
            parent = mLocalContainer
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            return
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly calling SurfaceView.
         */
        val view = RtcEngine.CreateRendererView(parentActivity!!.baseContext)
        view.setZOrderMediaOverlay(parent === mLocalContainer)
        parent!!.addView(view)
        mRemoteVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        // Initializes the video view of a remote user.
        mRtcEngine!!.setupRemoteVideo(mRemoteVideo)
    }

    private fun onRemoteUserLeft(uid: Int) {
        if (mRemoteVideo != null && mRemoteVideo!!.uid == uid) {
            removeFromParent(mRemoteVideo)
            // Destroys remote view
            mRemoteVideo = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_chat_view, container, false)
        //helper.track(new Screen.LiveFeedToHub(),  null);
        initUI(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEngineAndJoinChannel()
    }

    private fun initUI(view: View) {
        mLocalContainer = view.findViewById(R.id.local_video_view_container)
        mRemoteContainer = view.findViewById(R.id.remote_video_view_container)
        mCallBtn = view.findViewById(R.id.btn_call)
        mMuteBtn = view.findViewById(R.id.btn_mute)
        mSwitchCameraBtn = view.findViewById(R.id.btn_switch_camera)
        mCallBtn?.setOnClickListener { onCallClicked() }
        mMuteBtn?.setOnClickListener { onLocalAudioMuteClicked() }
        mSwitchCameraBtn?.setOnClickListener { onSwitchCameraClicked() }
    }

    private fun initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine()
        //setupVideoConfig()
        setupLocalVideo()
        joinChannel()
    }

  /*  private fun initializeEngine() {
        mRtcEngine = try {
            RtcEngine.create(parentActivity!!.baseContext, AGORA_APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e("--Agora--", Log.getStackTraceString(e))
            throw RuntimeException(
                """
    NEED TO check rtc sdk init fatal error
    ${Log.getStackTraceString(e)}
    """.trimIndent()
            )
        }
    }*/


    private fun initializeEngine() {
        Log.i("AgoraCall", "AgoraCall::initializeEngine")
        try {
            mRtcEngine = RtcEngine.create(
                this.parentActivity,
                this.parentActivity?.getString(R.string.agora_app_id),
                mRtcEventHandler
            ).apply {
                this.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)

                this.enableAudio()
                this.enableAudio()
            }
            /*
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));*/
        } catch (e: Exception) {
            Log.e("LiveFeedPresenter", Log.getStackTraceString(e))
            throw RuntimeException(
                """
                NEED TO check rtc sdk init fatal error
                ${Log.getStackTraceString(e)}
                """.trimIndent()
            )
        }
    }

    private fun setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine!!.enableVideo()

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        val view = RtcEngine.CreateRendererView(parentActivity!!.baseContext)
        view.setZOrderMediaOverlay(true)
        mLocalContainer!!.addView(view)
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        mRtcEngine!!.setupLocalVideo(mLocalVideo)
    }

    private fun joinChannel() {

        val agoraToken = arguments?.get("Token")
        val channelName = arguments?.get("ChannelName")
        val uid = arguments?.getInt("ContactUserId")?:0

        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        mRtcEngine!!.joinChannel(agoraToken as String?, channelName as String?, "Extra Optional Data", uid)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCallEnd) {
            leaveChannel()
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */RtcEngine.destroy()
    }

    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    fun onLocalAudioMuteClicked() {
        mMuted = !mMuted
        // Stops/Resumes sending the local audio stream.
        mRtcEngine!!.muteLocalAudioStream(mMuted)
        val res = if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute
        mMuteBtn!!.setImageResource(res)
    }

    fun onSwitchCameraClicked() {
        // Switches between front and rear cameras.
        mRtcEngine!!.switchCamera()
    }

    fun onCallClicked() {
        if (mCallEnd) {
            startCall()
            mCallEnd = false
            mCallBtn!!.setImageResource(R.drawable.ic_live_feed_reject_call)
        } else {
            endCall()
            mCallEnd = true
            mCallBtn!!.setImageResource(R.drawable.ic_live_feed_accept_call)
        }

        showButtons(!mCallEnd)
    }

    private fun startCall() {
        setupLocalVideo()
        joinChannel()
    }

    private fun endCall() {
        removeFromParent(mLocalVideo)
        mLocalVideo = null
        removeFromParent(mRemoteVideo)
        mRemoteVideo = null
        leaveChannel()
    }

    private fun showButtons(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        mMuteBtn!!.visibility = visibility
        mSwitchCameraBtn!!.visibility = visibility
    }

    private fun removeFromParent(canvas: VideoCanvas?): ViewGroup? {
        if (canvas != null) {
            val parent = canvas.view.parent
            if (parent != null) {
                val group = parent as ViewGroup
                group.removeView(canvas.view)
                return group
            }
        }
        return null
    }

    private fun switchView(canvas: VideoCanvas?) {
        val parent = removeFromParent(canvas)
        if (parent === mLocalContainer) {
            if (canvas!!.view is SurfaceView) {
                (canvas.view as SurfaceView).setZOrderMediaOverlay(false)
            }
            mRemoteContainer!!.addView(canvas.view)
        } else if (parent === mRemoteContainer) {
            if (canvas!!.view is SurfaceView) {
                (canvas.view as SurfaceView).setZOrderMediaOverlay(true)
            }
            mLocalContainer!!.addView(canvas.view)
        }
    }

    fun onLocalContainerClick(view: View?) {
        switchView(mLocalVideo)
        switchView(mRemoteVideo)
    }
}