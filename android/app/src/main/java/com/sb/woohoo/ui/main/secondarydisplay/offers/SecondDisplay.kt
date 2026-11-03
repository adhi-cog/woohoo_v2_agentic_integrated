package com.sb.woohoo.ui.main.secondarydisplay.offers

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.Tracks
import com.sb.woohoo.R
import com.sb.woohoo.custom.LoggerWrapper
import com.sb.woohoo.databinding.PresentationOffersBinding
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMulti
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayData
import com.sb.woohoo.ui.main.secondarydisplay.SecondaryDisplayImage
import com.sb.woohoo.utils.PlayVideoUtil
import com.squareup.picasso.Picasso

import java.util.Timer



class SecondDisplay(
    var mContext: Context,
    display: Display,
    val secondDisplayStateListener: SecondDisplayStateListener
) : Presentation(mContext, display), SecondDisplayDataListener {

    private var player: ExoPlayer? = null

    var index = 0
    val delay: Long = 0
    var intevalPeriod: Long = 5000
    var imagesList: SecondaryDisplayData? = null
    var slideHandler: Handler? = null
    var audioState: Boolean = false
    var lastVolume: Float = 0F

    val slideShowRunnable: Runnable by lazy { Runnable { doNext() } }

    var timer: Timer? = null

    // ViewBinding
    private lateinit var binding: PresentationOffersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = PresentationOffersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notify that the second display is created
        secondDisplayStateListener.onSecondDisplayCreated(this)
    }

    override fun showOffers(data: SecondaryDisplayData) {
        stopSlideShow()
        data.takeIf { it.images.isNotEmpty() }?.let {
            Log.i("StartDisplay", "StartDisplay")
            if (it.images.size == 1) {
                loadView(it.images[0])
            } else {
                intevalPeriod = it.intervalTime
                imagesList = it
                doNext()
            }
        }
    }

    private fun addCustomListener() {
        Log.i("Player", "addCustomListener")

        player?.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {}
            override fun onTracksChanged(tracks: Tracks) {}

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    doNext()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                doNext()
            }
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
        })
    }

    private fun doNext() {
        Log.i("StartDisplay", "doNext $index")

        if (index >= imagesList?.images?.size ?: 0) {
            index = 0
        }

        imagesList?.images?.get(index)?.let {
            Log.i("SecondaryDisplay", "URL: ${it}")
            loadView(it)
            index++
        }
    }

    private fun loadView(secondaryDisplayImage: SecondaryDisplayImage) {
        Log.i("StartDisplay", "loadView ${secondaryDisplayImage.image}")

        if (secondaryDisplayImage.image.endsWith(".mp4", ignoreCase = true)) {
            stopSlideShow()
            binding.wallpaper.visibility = View.GONE
            binding.videoViewSd.visibility = View.VISIBLE

            player = PlayVideoUtil.initialisePlayer(mContext, player)
            player?.let {
                if (audioState) {
                    it.volume = if (lastVolume == 0F) 1F else lastVolume
                } else {
                    it.volume = 0F
                }
            }

            addCustomListener()
            binding.videoViewSd.player = player
            PlayVideoUtil.playVideo(mContext as Activity, secondaryDisplayImage.image, player)
        } else {
            binding.videoViewSd.visibility = View.GONE
            binding.wallpaper.visibility = View.VISIBLE

            player?.let { releasePlayer() }
            Picasso.get()
                .load(secondaryDisplayImage.image)
                .fit()
                .into(binding.wallpaper)

            secondaryDisplayImage.title?.let {
                binding.title.apply {
                    visibility = View.VISIBLE
                    text = it
                }
            } ?: run {
                binding.title.visibility = View.GONE
            }

            secondaryDisplayImage.description?.let {
                binding.description.apply {
                    visibility = View.VISIBLE
                    text = it
                }
            } ?: run {
                binding.description.visibility = View.GONE
            }

            if (slideHandler == null) {
                slideHandler = Handler()
            }

            Log.i("StartDisplay", "waiting for next..")
            slideHandler?.postDelayed(slideShowRunnable, intevalPeriod)
        }
    }

    override fun loadScreenSaversAll(screenSaverMulti: ScreenSaverMulti) {
        screenSaverMulti.let {
            val images = ArrayList<SecondaryDisplayImage>()
            if (screenSaverMulti.slideShow && !screenSaverMulti.slideShowImages.isNullOrEmpty()) {
                for (url in screenSaverMulti.getSlideShowImages()) {
                    images.add(SecondaryDisplayImage(url, screenSaverMulti.getTitle(), screenSaverMulti.getDescription()))
                }
            } else if (screenSaverMulti.getImageUrl() != null) {
                images.add(SecondaryDisplayImage(screenSaverMulti.getImageUrl(), screenSaverMulti.getTitle(), screenSaverMulti.getDescription()))
            }

            if (images.isNotEmpty()) {
                showOffers(SecondaryDisplayData(screenSaverMulti.slideShowTimeInMillis, images))
            } else {
                LoggerWrapper.e("SecondDisplay", "loadScreenSaverMulti: no screensaver found")
            }
        }
    }

    fun syncSecondDisplayWithActiveScreenSavers(data: SecondaryDisplayImage) {
        stopSlideShow()
        loadView(data)
    }

    fun stopSlideShow() {
        slideHandler?.removeCallbacks(slideShowRunnable)
        slideHandler = null
        releasePlayer()
    }

    fun releasePlayer() {
        player?.let {
            it.stop()
            it.release()
        }
        player = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopSlideShow()
        index = 0
    }

    fun changeAudioState(audioState: Boolean) {
        this.audioState = audioState
        if (!audioState) {
            player?.let {
                lastVolume = it.volume
                it.volume = 0F
            }
        }
    }
}
