package com.udyata.imagepicker.presentation.EditActivity

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Deblur
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.udyata.imagepicker.data.model.ImageFilter
import com.udyata.imagepicker.data.model.ImageModification
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.repository.ImageRepository
import com.udyata.imagepicker.presentation.EditActivity.components.adjustments.Adjustment
import com.udyata.imagepicker.presentation.EditActivity.components.adjustments.AdjustmentFilter
import com.udyata.imagepicker.utils.flipHorizontally
import com.udyata.imagepicker.utils.gpuImage
import com.udyata.imagepicker.utils.mapToImageFilters
import com.udyata.imagepicker.utils.resizeAspectRatio
import com.udyata.imagepicker.utils.rotate
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EditViewModel (
    application: Application,
    private val loader: ImageLoader,
    private val request: ImageRequest.Builder,
    private val imageRepository: ImageRepository
) : AndroidViewModel(application) {

    private val applicationContext: Context = getApplication<Application>().applicationContext

    private var filterJob: Job? = null
    private var adjustmentJob: Job? = null
    private var cropperJob: Job? = null
    private var updateJob: Job? = null
    private var gpuImage: GPUImage? = null

    private var origImage: Bitmap? = null
    private var baseImage: Bitmap? = null
    var currentUri: Uri = Uri.EMPTY

    private val _mediaRef = MutableStateFlow<Photo?>(null)
    val mediaRef = _mediaRef.asStateFlow()

    private val _image = MutableStateFlow<Bitmap?>(null)
    val image = _image.asStateFlow()

    private val _filters = MutableStateFlow<List<ImageFilter>>(emptyList())
    val filters = _filters.asStateFlow()

    val currentFilter = mutableStateOf<ImageFilter?>(null)

    private var modifiedImages = mutableStateListOf<Bitmap>()
    val modifications = mutableStateListOf<ImageModification>()
    var canRevert = mutableStateOf(modifiedImages.isNotEmpty())

    fun addFilter(filter: ImageFilter) {
        filterJob?.cancel()
        filterJob = viewModelScope.launch(Dispatchers.IO) {
            if (baseImage == null) baseImage = image.value
            addModification(ImageModification(filter = filter))
        }
    }


    fun addCroppedImage(croppedImage: Bitmap) {
        cropperJob?.cancel()
        cropperJob = viewModelScope.launch(Dispatchers.IO) {
            baseImage = null
            addModification(ImageModification(croppedImage = croppedImage), updateFilters = true)
        }
    }


    @OptIn(ExperimentalCoilApi::class)
    fun loadImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                origImage = null
                gpuImage = null
                val request = request.data(uri).build()
                val result = loader.execute(request)

                if (result is SuccessResult) {
                    val bitmap = when (val image = result.image) {
                        is BitmapImage -> image.bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        else -> throw IllegalArgumentException("Unexpected image type: ${image::class.java}")
                    }
                    _image.emit(bitmap)
                    origImage = bitmap
                    baseImage = bitmap
                    gpuImage = applicationContext.gpuImage(bitmap)
                    _filters.emit(
                        gpuImage?.mapToImageFilters() ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                // Handle any errors during image loading
                e.printStackTrace()
            }
        }
    }




    fun revert() {
        viewModelScope.launch(Dispatchers.IO) {
            if (modifiedImages.isNotEmpty()) {
                modifications.removeLastOrNull()
                currentFilter.value = modifications.lastOrNull()?.filter
                updateImage(
                    modifiedImages.last(),
                    isRevertAction = true,
                    updateFilters = modifications.lastOrNull()?.croppedImage != null
                )
                modifiedImages.removeLast()
            }
            canRevert.value = modifiedImages.size > 0
        }
    }

    fun flipHorizontally() {
        viewModelScope.launch(Dispatchers.IO) {
            _image.value?.let {
                _image.emit(it.flipHorizontally())
            }
        }
    }

    fun setAngle(angle: Float) {
          viewModelScope.launch(Dispatchers.IO) {
              origImage?.let {
                  _image.emit(it.rotate(angle))
              }
          }
      }

    fun addAngle(angle: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _image.value?.let {
                _image.emit(it.rotate(angle))
            }
        }
    }


    private fun addModification(modification: ImageModification, updateFilters: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (modifications.any { it.adjustment != null && it.adjustment.first == modification.adjustment?.first }) {
                modifications.replaceAll { mod ->
                    if (mod.adjustment?.first == modification.adjustment?.first) {
                        modification
                    } else {
                        mod
                    }
                }
            } else {
                modifications.add(modification)
            }
            modification.croppedImage?.let {
                updateImage(it, updateFilters = updateFilters)
            } ?: gpuImage?.let {
                if (baseImage != null) {
                    it.setImage(baseImage)
                    if (modification.adjustment != null) {
                        it.setFilter(modification.adjustment.first.filter(modification.adjustment.second))
                        updateImage(it.bitmapWithFilterApplied, updateFilters = true)
                    } else if (modification.filter != null) {
                        it.setFilter(modification.filter.filter)
                        currentFilter.value = modification.filter
                        updateImage(it.bitmapWithFilterApplied, updateFilters = false)
                    }
                } else {
                    throw IllegalStateException("Base image is null")
                }
            }
        }
    }

    private fun removeModification(modification: ImageModification) {
        viewModelScope.launch(Dispatchers.IO) {
            modifications.remove(modification)
            modifications.lastOrNull()?.let { mod ->
                mod.croppedImage?.let {
                    updateImage(it, updateFilters = true)
                } ?: gpuImage?.let {
                    if (baseImage != null) {
                        it.setImage(baseImage)
                        if (modification.adjustment != null) {
                            it.setFilter(modification.adjustment.first.filter(modification.adjustment.second))
                            updateImage(it.bitmapWithFilterApplied, updateFilters = true)
                        } else if (modification.filter != null) {
                            it.setFilter(modification.filter.filter)
                            currentFilter.value = modification.filter
                            updateImage(it.bitmapWithFilterApplied, updateFilters = false)
                        }
                    } else {
                        throw IllegalStateException("Base image is null")
                    }
                }
            }
        }
    }

    private fun updateImage(
        bitmap: Bitmap,
        isRevertAction: Boolean = false,
        updateFilters: Boolean = true,
        onImageUpdated: () -> Unit = {}
    ) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isRevertAction) {
                canRevert.value = true
                image.value?.let { modifiedImages.add(it) }
            }
            _image.emit(bitmap)
            if (updateFilters) {
                gpuImage?.setImage(bitmap)
                _filters.emit(
                    gpuImage!!.mapToImageFilters()
                )
            }
            onImageUpdated()
        }
    }

    fun saveEditedImage(bitmap: Bitmap, onSaveResult: (Uri) -> Unit) {
        viewModelScope.launch {
            try {
                val uri = imageRepository.saveImageToGallery(bitmap)
                onSaveResult(uri)
            } catch (e: Exception) {
                // Handle any errors
                e.printStackTrace()
            }
        }
    }

    private val bitmapFormat: CompressFormat
        @RequiresApi(Build.VERSION_CODES.R)
        get() = when (mediaRef.value?.mimeType?.substringAfterLast("/")?.lowercase()) {
            "png" -> CompressFormat.PNG
            "jpeg", "jpg" -> CompressFormat.JPEG
            "webp" -> CompressFormat.WEBP_LOSSLESS
            else -> CompressFormat.PNG
        }

    fun addAdjustment(isScrolling: Boolean, adjustment: Pair<AdjustmentFilter, Float>) {
        adjustmentJob?.cancel()
        adjustmentJob = viewModelScope.launch(Dispatchers.IO) {
            if (baseImage == null) baseImage = image.value
            if (!isScrolling) {
                if (adjustment.second == adjustment.first.defaultValue) {
                    modifications.firstOrNull { it.adjustment?.first == adjustment.first }?.let {
                        println("Removed: ${adjustment.first.tag}")
                        removeModification(it)
                    }
                } else {
                    println("Added: ${adjustment.first.tag} with ${adjustment.second}")
                    addModification(ImageModification(adjustment = adjustment))
                }
            }
        }
    }
    fun revertAdjustmentToOriginal() {
        viewModelScope.launch(Dispatchers.IO) {
            origImage?.let {
                _image.emit(it)
                gpuImage?.setImage(it)
                _filters.emit(emptyList())
                modifications.clear()
            }
        }
    }


    companion object {

        val adjustmentFilters = listOf(
            // None (reset to original)
            AdjustmentFilter(
                tag = Adjustment.NONE,
                name = "None",
                icon = Icons.Default.Clear,
                minValue = 0f,
                maxValue = 1f,
                defaultValue = 0f,
                filter = { _ -> GPUImageFilter() }
            ),
            // Brightness
            AdjustmentFilter(
                tag = Adjustment.BRIGHTNESS,
                name = "Brightness",
                icon = Icons.Default.Exposure,
                minValue = -1f,
                maxValue = 1f,
                defaultValue = 0f,
                filter = { value -> GPUImageBrightnessFilter(value) }
            ),
            // Contrast
            AdjustmentFilter(
                tag = Adjustment.CONTRAST,
                name = "Contrast",
                icon = Icons.Default.Contrast,
                minValue = 0f,
                maxValue = 4f,
                defaultValue = 1f,
                filter = { value -> GPUImageContrastFilter(value) }
            ),
            // Saturation
            AdjustmentFilter(
                tag = Adjustment.SATURATION,
                name = "Saturation",
                icon = Icons.Default.InvertColors,
                minValue = 0f,
                maxValue = 2f,
                defaultValue = 1f,
                filter = { value -> GPUImageSaturationFilter(value) }
            ),
            // Vibrance
            AdjustmentFilter(
                tag = Adjustment.VIBRANCE,
                name = "Vibrance",
                icon = Icons.Default.Tune,
                minValue = 0f,
                maxValue = 2f,
                defaultValue = 1f,
                filter = { value -> GPUImageVibranceFilter(value) }
            ),
            // Warmth
            AdjustmentFilter(
                tag = Adjustment.WARMTH,
                name = "Warmth",
                icon = Icons.Default.WbSunny,
                minValue = 2000f,
                maxValue = 8000f,
                defaultValue = 5000f,
                filter = { value -> GPUImageWhiteBalanceFilter(value, 0f) }
            ),
            // Tint
            AdjustmentFilter(
                tag = Adjustment.TINT,
                name = "Tint",
                icon = Icons.Default.Colorize,
                minValue = -180f,
                maxValue = 180f,
                defaultValue = 0f,
                filter = { value -> GPUImageHueFilter(value) }
            ),
            // Highlights
            AdjustmentFilter(
                tag = Adjustment.HIGHLIGHTS,
                name = "Highlights",
                icon = Icons.Default.Highlight,
                minValue = 0f,
                maxValue = 2f,
                defaultValue = 1f,
                filter = { value -> GPUImageHighlightShadowFilter(value, 1f) }
            ),
            // Shadows
            AdjustmentFilter(
                tag = Adjustment.SHADOWS,
                name = "Shadows",
                icon = Icons.Default.WaterDrop,
                minValue = 0f,
                maxValue = 2f,
                defaultValue = 1f,
                filter = { value -> GPUImageHighlightShadowFilter(1f, value) }
            ),
            // Clarity (custom filter, using sharpening and contrast increase)
            AdjustmentFilter(
                tag = Adjustment.CLARITY,
                name = "Clarity",
                icon = Icons.Default.Dehaze,
                minValue = -1f,
                maxValue = 1f,
                defaultValue = 0f,
                filter = { value -> GPUImageSharpenFilter(value * 2f) }
            ),
            // Fade (reduces contrast)
            AdjustmentFilter(
                tag = Adjustment.FADE,
                name = "Fade",
                icon = Icons.Default.FilterVintage,
                minValue = 0f,
                maxValue = 1f,
                defaultValue = 0f,
                filter = { value -> GPUImageContrastFilter(1f - value) }
            ),
            // Grain (adds noise)
            AdjustmentFilter(
                tag = Adjustment.BLUR,
                name = "Blur",
                icon = Icons.Default.Deblur,
                minValue = 0f,
                maxValue = 1f,
                defaultValue = 0f,
                filter = { value -> GPUImageGaussianBlurFilter(value) }
            ),
            // Sharpness
            AdjustmentFilter(
                tag = Adjustment.SHARPNESS,
                name = "Sharpness",
                icon = Icons.Default.Tune,
                minValue = -4f,
                maxValue = 4f,
                defaultValue = 0f,
                filter = { value -> GPUImageSharpenFilter(value) }
            )
        )

    }

}