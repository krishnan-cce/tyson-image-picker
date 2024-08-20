package com.udyata.composegalley.presentation

import android.os.Build
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

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _filter = MutableStateFlow<PhotoFilter>(PhotoFilter.NoFilter)
    val filter: StateFlow<PhotoFilter> = _filter

    private val _sort = MutableStateFlow<PhotoSort>(PhotoSort.DateTakenDesc)
    val sort: StateFlow<PhotoSort> = _sort


    val groupedPhotos: StateFlow<List<PhotoGroup>> = combine(_photos, _filter, _sort) { photos, filter, sort ->
        processPhotos(photos, filter, sort)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch(Dispatchers.IO) { // Move fetch to IO dispatcher
            getPhotosUseCase(_filter.value, _sort.value).collectLatest {
                _photos.value = it
            }
        }
    }



    private suspend fun processPhotos(photos: List<Photo>, filter: PhotoFilter, sort: PhotoSort): List<PhotoGroup> {



        return withContext(Dispatchers.Default) { // Offload processing to Default dispatcher
            val filteredPhotos = photos.filter { filter.apply(it) }
            val sortedPhotos = filteredPhotos.sortedWith(sort.comparator)

            val grouped = when (sort) {
                PhotoSort.All -> sortedPhotos.groupBy { it.dateOnly.toCustomDateFormat() }
                PhotoSort.Weekly -> sortedPhotos.groupBy {
                    val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val weekOfYear = localDate.get(WeekFields.ISO.weekOfYear())
                    "${localDate.year}-W$weekOfYear".toCustomWeeklyFormat()
                }
                PhotoSort.Monthly -> sortedPhotos.groupBy {
                    val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    "${localDate.year}-${localDate.month}".toCustomMonthlyFormat()
                }
                PhotoSort.Yearly -> sortedPhotos.groupBy {
                    val localDate = it.dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    "${localDate.year}"
                }
                else -> sortedPhotos.groupBy { it.dateOnly.toCustomDateFormat() }
            }

            grouped.map { (key, photos) ->
                PhotoGroup(
                    header = PhotoSection.Header(key),
                    body = photos.map { PhotoSection.Body(it) },
                    footer = PhotoSection.Footer
                )
            }
        }
    }

    private fun updateFilter(filter: PhotoFilter) {
        if (_filter.value != filter) {
            _filter.value = filter
            fetchPhotos()
        }
    }

    private fun updateSort(sort: PhotoSort) {
        if (_sort.value != sort) {
            _sort.value = sort
            fetchPhotos()
        }
    }

    fun onEvent(event: PhotoEvent) {
        when (event) {
            is PhotoEvent.UpdateFilter -> updateFilter(event.filter)
            is PhotoEvent.UpdateSort -> updateSort(event.sort)
        }
    }


}

sealed class PhotoEvent {
    data class UpdateFilter(val filter: PhotoFilter) : PhotoEvent()
    data class UpdateSort(val sort: PhotoSort) : PhotoEvent()
}
