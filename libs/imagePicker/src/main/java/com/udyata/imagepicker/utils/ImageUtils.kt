package com.udyata.imagepicker.utils

/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import com.udyata.imagepicker.data.model.ImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import java.io.IOException

val sdcardRegex = "^/storage/[A-Z0-9]+-[A-Z0-9]+/.*$".toRegex()


fun Context.gpuImage(bitmap: Bitmap) = GPUImage(this).apply { setImage(bitmap) }

fun GPUImage.mapToImageFilters(): List<ImageFilter> {
    val gpuImage = this
    val imgFilters: ArrayList<ImageFilter> = ArrayList()

    //region:: Filters
    // Normal
    GPUImageFilter().also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "None",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Retro
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.2f, 0.0f,
            0.1f, 0.1f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Retro",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Just
    GPUImageColorMatrixFilter(
        0.9f,
        floatArrayOf(
            0.4f, 0.6f, 0.5f, 0.0f,
            0.0f, 0.4f, 1.0f, 0.0f,
            0.05f, 0.1f, 0.4f, 0.4f,
            1.0f, 1.0f, 1.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Just",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Hume
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.25f, 0.0f, 0.2f, 0.0f,
            0.0f, 1.0f, 0.2f, 0.0f,
            0.0f, 0.3f, 1.0f, 0.3f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Hume",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Desert
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.6f, 0.4f, 0.2f, 0.05f,
            0.0f, 0.8f, 0.3f, 0.05f,
            0.3f, 0.3f, 0.5f, 0.08f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Desert",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Old Times
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.0f, 0.05f, 0.0f, 0.0f,
            -0.2f, 1.1f, -0.2f, 0.11f,
            0.2f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Old Times",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Limo
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.0f, 0.0f, 0.08f, 0.0f,
            0.4f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.1f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Limo",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Sepia
    GPUImageSepiaToneFilter().also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Sepia",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Solar
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.5f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Solar",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Wole
    GPUImageSaturationFilter(2.0f).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Wole",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Neutron
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0f, 1f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0.6f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Neutron",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Bright
    GPUImageRGBFilter(1.1f, 1.3f, 1.6f).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Bright",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Milk
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.64f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Milk",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // BW
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "BW",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Clue
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Clue",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Muli
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Muli",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Aero
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0f, 0f, 1f, 0f,
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Aero",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Classic
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.763f, 0.0f, 0.2062f, 0f,
            0.0f, 0.9416f, 0.0f, 0f,
            0.1623f, 0.2614f, 0.8052f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Classic",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Atom
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.5162f, 0.3799f, 0.3247f, 0f,
            0.039f, 1.0f, 0f, 0f,
            -0.4773f, 0.461f, 1.0f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Atom",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Mars
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            0.0f, 0.0f, 0.5183f, 0.3183f,
            0.0f, 0.5497f, 0.5416f, 0f,
            0.5237f, 0.5269f, 0.0f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Mars",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }

    // Yeli
    GPUImageColorMatrixFilter(
        1.0f,
        floatArrayOf(
            1.0f, -0.3831f, 0.3883f, 0.0f,
            0.0f, 1.0f, 0.2f, 0f,
            -0.1961f, 0.0f, 1.0f, 0f,
            0f, 0f, 0f, 1f
        )
    ).also { filter ->
        gpuImage.setFilter(filter)
        imgFilters.add(
            ImageFilter(
                name = "Yeli",
                filter = filter,
                filterPreview = gpuImage.bitmapWithFilterApplied
            )
        )
    }
    //endregion
    return imgFilters
}

fun Bitmap.flipHorizontally(): Bitmap {
    val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, height / 2f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.flipVertically(): Bitmap {
    val matrix = Matrix().apply { postScale(1f, -1f, width / 2f, height / 2f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.resizeAspectRatio(ratio: Float): Bitmap {
    val newWidth = (height * ratio).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, height, true)
}


@Composable
fun rememberActivityResult(onResultCanceled: () -> Unit = {}, onResultOk: () -> Unit = {}) =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) onResultOk()
            if (it.resultCode == RESULT_CANCELED) onResultCanceled()
        }
    )



fun Modifier.verticalFadingEdge(percentage: Float) = this.fadingEdge(
    Brush.verticalGradient(
        0f to Color.Transparent,
        percentage to Color.Red,
        1f - percentage to Color.Red,
        1f to Color.Transparent
    )
)

fun Modifier.horizontalFadingEdge(percentage: Float) = this.fadingEdge(
    Brush.horizontalGradient(
        0f to Color.Transparent,
        percentage to Color.Red,
        1f - percentage to Color.Red,
        1f to Color.Transparent
    )
)

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }