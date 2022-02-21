package io.research.exoplayerwsa

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.media.MediaCodecList
import android.net.Uri
import android.os.Bundle
import androidx.annotation.AnyRes
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import io.research.exoplayerwsa.databinding.ActivityVideoPlayerBinding



class ExoPlayerActivity : Activity() {

    companion object {
        const val URI = "VIDEO"
    }

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding : ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializePlayer() {

        Log.d("CODECS", MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.filter {
            it.supportedTypes[0].startsWith("video")
        }.joinToString { it.name })

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

       val item = if(intent.data != null){
            MediaItem.fromUri(intent.data!!)
        } else{
           MediaItem.fromUri(getResourceUri(R.raw.halp))
       }


        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(item)

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        exoPlayer.addListener(object : Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d("ERRROR", error.errorCode.toString())
            }
        })

        exoPlayer.addMediaSource(mediaSource)

        exoPlayer.playWhenReady = true
        binding.playerView.player = exoPlayer
        binding.playerView.requestFocus()
    }

    private fun releasePlayer() {
        exoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) releasePlayer()
    }

}