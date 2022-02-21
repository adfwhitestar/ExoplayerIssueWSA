package io.research.exoplayerwsa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.research.exoplayerwsa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonExo.setOnClickListener {
            startActivity(Intent(this,ExoPlayerActivity::class.java))
        }
        binding.buttonNotExo.setOnClickListener {
            startActivity(Intent(this,NotExoPlayer::class.java))
        }
        binding.buttonCompress.setOnClickListener {
            startActivity(Intent(this,CompressVideoActivity::class.java))
        }
    }

}