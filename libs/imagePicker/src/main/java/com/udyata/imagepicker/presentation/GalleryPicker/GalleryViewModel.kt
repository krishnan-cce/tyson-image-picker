package com.udyata.composegalley.presentation

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoFilter
import com.udyata.imagepicker.data.model.PhotoGroup
import com.udyata.imagepicker.data.model.PhotoSection
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.domain.usecase.GetPhotosUseCase
import com.udyata.imagepicker.utils.DateUtils.toCustomDateFormat
import com.udyata.imagepicker.utils.DateUtils.toCustomMonthlyFormat
import com.udyata.imagepicker.utils.DateUtils.toCustomWeeklyFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _selectedSort = MutableStateFlow<PhotoSort>(PhotoSort.All)
    val selectedSort: StateFlow<PhotoSort> = _selectedSort

    private val _filter = MutableStateFlow<PhotoFilter>(PhotoFilter.NoFilter)
    val filter: StateFlow<PhotoFilter> = _filter

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    val groupedPhotos: StateFlow<List<PhotoGroup>> = combine(_photos, _selectedSort, _filter) { photos, sort, filter ->
        processPhotos(photos, filter, sort)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            getPhotosUseCase(_filter.value, _selectedSort.value).collectLatest {
                _photos.value = it
            }
        }
    }



    private suspend fun processPhotos(
        photos: List<Photo>,
        filter: PhotoFilter,
        sort: PhotoSort
    ): List<PhotoGroup> {
        return withContext(Dispatchers.Default) {
            val filteredPhotos = photos.filter { filter.apply(it) }
            val sortedPhotos = filteredPhotos.sortedWith(sort.comparator)

            val grouped = when (sort) {
                PhotoSort.Weekly, PhotoSort.Monthly, PhotoSort.Yearly -> sortedPhotos.groupBy {
                    when (sort) {
                        PhotoSort.Weekly -> {
                            val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            val weekOfYear = localDate.get(WeekFields.ISO.weekOfYear())
                            "${localDate.year}-W$weekOfYear".toCustomWeeklyFormat()
                        }
                        PhotoSort.Monthly -> {
                            val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            "${localDate.year}-${localDate.month}".toCustomMonthlyFormat()
                        }
                        PhotoSort.Yearly -> {
                            val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            "${localDate.year}"
                        }
                        else -> ""
                    }
                }
                else -> sortedPhotos.groupBy { it.dateOnly.toCustomDateFormat() }
            }

            grouped.entries.toList().mapIndexed { groupIndex, (key, photos) ->
                val (displayedPhotos, remainingCount) = when (sort) {
                    PhotoSort.Weekly, PhotoSort.Monthly, PhotoSort.Yearly -> photos.take(10) to (photos.size - 10)
                    else -> photos to 0
                }

                val bodyItems = displayedPhotos.map { PhotoSection.Body(it) }.toMutableList()

                 if (remainingCount > 0) {
                    val dummyPhotoId = -1L * ((groupIndex * 1000L) + 1L)

                    Log.d("GalleryDebug", "GroupIndex: $groupIndex, Key: $key, RemainingCount: $remainingCount, DummyPhotoId: $dummyPhotoId")


                    val dummyPhoto = Photo(
                        id = dummyPhotoId,
                        uri = Uri.EMPTY,
                        thumbnailUri = null,
                        name = "+$remainingCount",
                        dateTaken = Date(),
                        size = 0L,
                        width = 0,
                        height = 0,
                        mimeType = ""
                    )
                    bodyItems.add(PhotoSection.Body(dummyPhoto))
                }else {
                    Log.d("GalleryDebug", "No remaining count for GroupIndex: $groupIndex, Key: $key")
                }

                PhotoGroup(
                    header = PhotoSection.Header(key),
                    body = bodyItems,
                    footer = null
                )
            }
        }
    }





    fun onEvent(event: PhotoEvent) {
        when (event) {
            is PhotoEvent.UpdateFilter -> {
                if (_filter.value != event.filter) {
                    _filter.value = event.filter
                    fetchPhotos()
                }
            }
            is PhotoEvent.UpdateSort -> {
                if (_selectedSort.value != event.sort) {
                    _selectedSort.value = event.sort
                    fetchPhotos()
                }
            }
        }
    }


}

sealed class PhotoEvent {
    data class UpdateFilter(val filter: PhotoFilter) : PhotoEvent()
    data class UpdateSort(val sort: PhotoSort) : PhotoEvent()
}
