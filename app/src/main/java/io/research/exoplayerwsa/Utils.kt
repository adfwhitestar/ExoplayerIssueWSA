package io.research.exoplayerwsa

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.AnyRes
import com.simform.videooperations.Common
import java.io.File


fun Context.getResourceUri(@AnyRes resourceId: Int): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()


fun Context.deleteCache() {
    try {
        val dir: File = cacheDir
        deleteDir(dir)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteDir(dir: File?): Boolean {
    return if (dir != null && dir.isDirectory) {
        val children: Array<String> = dir.list()?: arrayOf()
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
        dir.delete()
    } else if (dir != null && dir.isFile) {
        dir.delete()
    } else {
        false
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