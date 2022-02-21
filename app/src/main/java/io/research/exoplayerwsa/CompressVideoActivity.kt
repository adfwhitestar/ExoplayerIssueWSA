package io.research.exoplayerwsa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.simform.videooperations.*
import io.research.exoplayerwsa.databinding.ActivityCompressVideoBinding
import java.io.File
import java.util.concurrent.CompletableFuture

class CompressVideoActivity : AppCompatActivity(R.layout.activity_compress_video),
    View.OnClickListener,
    FileSelection {
    private var isInputVideoSelected: Boolean = false

    var height: Int? = 0
    var width: Int? = 0
    var retriever: MediaMetadataRetriever? = null

    var orgFilePath: String? = null
    var compFilePath: String? = null


    val binding by lazy { ActivityCompressVideoBinding.inflate(layoutInflater) }
    val ffmpegQueryExtension by lazy { FFmpegQueryExtension() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnVideoPath.setOnClickListener(this)
        binding.btnCompress.setOnClickListener(this)
        binding.btnOpenOrgInExo.setOnClickListener(this)
        binding.btnOpenCompressedInExo.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(
                    this@CompressVideoActivity,
                    maxSelection = 1,
                    isImageSelection = false,
                    isAudioSelection = false
                )
            }
            R.id.btnCompress -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(this, "Please Select a video", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        processStart()
                        compressProcess()
                    }
                }
            }
            R.id.btnOpenCompressedInExo -> {
                if(compFilePath==null){
                    Toast.makeText(this,"Please Compress a Video First",Toast.LENGTH_SHORT).show()
                    return
                }
                startActivity(Intent(this, ExoPlayerActivity::class.java).apply {
                    data = compFilePath?.toUri()
                })
            }
            R.id.btnOpenOrgInExo -> {
                if(orgFilePath==null){
                    Toast.makeText(this,"Please Select a Video First",Toast.LENGTH_SHORT).show()
                    return
                }
                startActivity(Intent(this, ExoPlayerActivity::class.java).apply {
                    data = orgFilePath?.toUri()
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val mediaFiles =
                data.getParcelableArrayListExtra<MediaFile>(FilePickerActivity.MEDIA_FILES)
            (this as FileSelection).selectedFiles(mediaFiles, requestCode)
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, requestCode: Int) {
        when (requestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    binding.tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                    CompletableFuture.runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(binding.tvInputPathVideo.text.toString())
                        val bit = retriever?.frameAtTime
                        width = bit?.width
                        height = bit?.height
                    }
                    orgFilePath = mediaFiles[0].path
                    binding.inputFileSize.text =
                        "Input file Size : ${Common.getFileSize(File(binding.tvInputPathVideo.text.toString()))}"
                } else {
                    Toast.makeText(this, "Not selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getFilePath(context: Context, fileExtension: String): String {
        val dir = File(context.cacheDir.toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var extension: String? = null
        when {
            TextUtils.equals(fileExtension, Common.IMAGE) -> {
                extension = "%03d.jpg"
            }
            TextUtils.equals(fileExtension, Common.VIDEO) -> {
                extension = ".mp4"
            }
            TextUtils.equals(fileExtension, Common.GIF) -> {
                extension = ".gif"
            }
            TextUtils.equals(fileExtension, Common.MP3) -> {
                extension = ".mp3"
            }
        }
        val dest = File(
            dir.path + File.separator + Common.OUT_PUT_DIR + System.currentTimeMillis()
                .div(1000L) + extension
        )
        return dest.absolutePath
    }

    private fun compressProcess() {
        val outputPath = getFilePath(this, Common.VIDEO)
        compFilePath = outputPath
        val query = ffmpegQueryExtension.compressor(
            binding.tvInputPathVideo.text.toString(),
            width,
            height,
            outputPath
        )
        CallBackOfQuery().callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                binding.tvOutputPath.text = logMessage.text
            }

            override fun success() {
                binding.tvOutputPath.text = "Output Path : \\n${outputPath} Output file Size : \\n${
                    Common.getFileSize(
                        File(outputPath)
                    )
                }"
                processStop()
            }

            override fun cancel() {
                processStop()
            }

            override fun failed() {
                processStop()
            }
        })
    }

    private fun processStop() {
        runOnUiThread {
            binding.btnVideoPath.isEnabled = true
            binding.btnCompress.isEnabled = true
            binding.mProgressLayout.isVisible = false
        }
    }

    private fun processStart() {
        binding.btnVideoPath.isEnabled = false
        binding.btnCompress.isEnabled = false
        binding.mProgressLayout.isVisible = true
    }
}