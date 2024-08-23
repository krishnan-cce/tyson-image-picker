package com.udyata.imagepicker.presentation.GalleryAlbumScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udyata.imagepicker.data.model.Album
import com.udyata.imagepicker.domain.usecase.GetAlbumsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class RemainingAlbumViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    fun getImagesForAlbumId(albumId: String): StateFlow<List<Album>> {
        return getAlbumsUseCase.getImagesForAlbum(albumId.toLong()).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

}

