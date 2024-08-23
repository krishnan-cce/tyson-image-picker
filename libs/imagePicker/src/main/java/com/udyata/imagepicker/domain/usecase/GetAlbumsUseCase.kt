package com.udyata.imagepicker.domain.usecase

import com.udyata.imagepicker.data.model.Album
import com.udyata.imagepicker.data.model.AlbumSort
import com.udyata.imagepicker.data.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetAlbumsUseCase(
    private val photoRepository: PhotoRepository
) {
    fun getAlbumList(): Flow<List<Album>> {
        return photoRepository.getAlbums()
    }


    fun getImagesForAlbum(albumId: Long): Flow<List<Album>> {
        return flow {
            val images = withContext(Dispatchers.IO) {
                photoRepository.fetchImagesFromContentResolver(albumId)
            }
            emit(images)
        }
    }

}
