package io.research.exoplayerwsa

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.AnyRes


fun Context.getResourceUri(@AnyRes resourceId: Int): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()