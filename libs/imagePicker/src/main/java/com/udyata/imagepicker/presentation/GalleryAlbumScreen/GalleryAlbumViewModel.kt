package com.udyata.imagepicker.presentation.GalleryAlbumScreen

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udyata.imagepicker.data.model.Album
import com.udyata.imagepicker.data.model.AlbumGroup
import com.udyata.imagepicker.data.model.AlbumSection
import com.udyata.imagepicker.data.model.AlbumSort
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoFilter
import com.udyata.imagepicker.data.model.PhotoGroup
import com.udyata.imagepicker.data.model.PhotoSection
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.domain.usecase.GetAlbumsUseCase
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class GalleryAlbumViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            getAlbumsUseCase.getAlbumList().collectLatest {
                _albums.value = it
            }
        }
    }

    fun getImagesForAlbumId(albumId: String): StateFlow<List<Album>> {
        return getAlbumsUseCase.getImagesForAlbum(albumId.toLong()).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
}

