package com.example.android.wellnesscoach.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android.wellnesscoach.R
import com.example.android.wellnesscoach.databinding.ActivityUserBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class UserActivity : AppCompatActivity(), Player.Listener, View.OnClickListener {
    private lateinit var userActivityBinding: ActivityUserBinding
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private val audioUrl = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"
    private val videoURl =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private var playBackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActivityBinding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(userActivityBinding.root)
        supportActionBar?.title=intent?.extras?.get("USERNAME")?.toString()
        userActivityBinding.video.setOnClickListener(this)
        userActivityBinding.audio.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        intializePlayer()
    }

    private fun intializePlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        userActivityBinding.exoplayerView.player = simpleExoPlayer
    }


    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        playBackPosition = simpleExoPlayer.currentPosition
        simpleExoPlayer.release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            userActivityBinding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            userActivityBinding.progressBar.visibility = View.INVISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video -> playMedia(videoURl);
            R.id.audio -> playMedia(audioUrl);
        }
    }

    private fun playMedia(url: String) {
        val mediaItem: MediaItem = MediaItem.fromUri(url)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.seekTo(0)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(this@UserActivity)
    }
}