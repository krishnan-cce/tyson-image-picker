package com.udyata.imagepicker.data.model

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

@Immutable
data class ImageModification(
    val croppedImage: Bitmap? = null,
    val filter: ImageFilter? = null,
)

@Immutable
data class ImageFilter(
    val name: String = "",
    val filterPreview: Bitmap
)