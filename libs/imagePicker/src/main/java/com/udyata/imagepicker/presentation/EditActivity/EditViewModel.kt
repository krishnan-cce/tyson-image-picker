package com.udyata.imagepicker.presentation.EditActivity

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.udyata.imagepicker.data.model.ImageFilter
import com.udyata.imagepicker.data.model.ImageModification
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.repository.ImageRepository
import com.udyata.imagepicker.utils.flipHorizontally
import com.udyata.imagepicker.utils.resizeAspectRatio
import com.udyata.imagepicker.utils.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EditViewModel (
    private val loader: ImageLoader,
    private val request: ImageRequest.Builder,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var filterJob: Job? = null
    private var adjustmentJob: Job? = null
    private var cropperJob: Job? = null
    private var updateJob: Job? = null

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

    fun loadImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = request.data(uri).build()
                val result = loader.execute(request)

                if (result is SuccessResult) {
                    val bitmap = when (val image = result.image) {
                        is BitmapImage -> image.bitmap
                        else -> throw IllegalArgumentException("Unexpected image type: ${image::class.java}")
                    }
                    _image.emit(bitmap)
                    origImage = bitmap
                    baseImage = bitmap
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
            modification.croppedImage?.let {
                updateImage(it, updateFilters = updateFilters)
            }
        }
    }

    private fun removeModification(modification: ImageModification) {
        viewModelScope.launch(Dispatchers.IO) {
            modifications.remove(modification)
            modifications.lastOrNull()?.let { mod ->
                mod.croppedImage?.let {
                    updateImage(it, updateFilters = true)
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

    fun setAspectRatio(ratio: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _image.value?.let {
                val resizedBitmap = it.resizeAspectRatio(ratio)
                _image.emit(resizedBitmap)
            }
        }
    }


}