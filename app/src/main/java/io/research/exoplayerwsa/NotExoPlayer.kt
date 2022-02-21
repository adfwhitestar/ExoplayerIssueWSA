package io.research.exoplayerwsa

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import io.research.exoplayerwsa.databinding.ActivityNotexplayerBinding

class NotExoPlayer :AppCompatActivity() {

    private val binding by lazy { ActivityNotexplayerBinding.inflate(layoutInflater) }
    private val mc by lazy { MediaController(this).apply {
        setAnchorView(binding.videoView1)
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.videoView1.apply {
            setMediaController(mc)
            setVideoURI(getResourceUri(R.raw.halp))
            requestFocus()
            start()
        }
    }

}