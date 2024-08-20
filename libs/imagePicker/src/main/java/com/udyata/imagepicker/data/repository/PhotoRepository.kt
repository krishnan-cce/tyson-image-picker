package com.udyata.imagepicker.data.repository

import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoFilter
import com.udyata.imagepicker.data.model.PhotoSort
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
     fun getPhotos(filter: PhotoFilter, sort: PhotoSort): Flow<List<Photo>>
}
