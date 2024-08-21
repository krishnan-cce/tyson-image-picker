package com.udyata.imagepicker.data.model

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.udyata.imagepicker.presentation.EditActivity.components.adjustments.AdjustmentFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

@Immutable
data class ImageModification(
    val croppedImage: Bitmap? = null,
    val filter: ImageFilter? = null,
    val adjustment: Pair<AdjustmentFilter, Float>? = null
)

@Immutable
data class ImageFilter(
    val name: String = "",
    val filter: GPUImageFilter,
    val filterPreview: Bitmap,
    val group: String = "General"
)