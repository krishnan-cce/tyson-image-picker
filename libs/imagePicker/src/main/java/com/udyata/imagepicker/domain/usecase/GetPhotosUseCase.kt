package com.udyata.imagepicker.domain.usecase

import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoFilter
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.data.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow


class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(filter: PhotoFilter, sort: PhotoSort): Flow<List<Photo>> {
        return repository.getPhotos(filter, sort)
    }
}
