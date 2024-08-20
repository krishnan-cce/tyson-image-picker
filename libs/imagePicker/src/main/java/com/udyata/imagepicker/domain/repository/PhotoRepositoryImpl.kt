package com.udyata.imagepicker.domain.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoFilter
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.data.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.Date


class PhotoRepositoryImpl(
    private val contentResolver: ContentResolver
) : PhotoRepository {


    override fun getPhotos(filter: PhotoFilter, sort: PhotoSort): Flow<List<Photo>> = flow {
        val photos = withContext(Dispatchers.IO) {
            fetchPhotosFromContentResolver(filter, sort)
        }
        emit(photos)
    }


    private fun fetchPhotosFromContentResolver(filter: PhotoFilter, sort: PhotoSort): List<Photo> {
        val photos = mutableListOf<Photo>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )

        val selection = filter.getSelection()
        val selectionArgs = filter.getSelectionArgs()

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sort.sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)

                // Consider fallback strategy for date handling
                val dateTakenMillis = it.getLong(dateTakenColumn)
                val dateAddedMillis = it.getLong(dateAddedColumn)
                val dateModifiedMillis = it.getLong(dateModifiedColumn)
                val date = when {
                    dateTakenMillis > 0 -> Date(dateTakenMillis)
                    dateModifiedMillis > 0 -> Date(dateModifiedMillis * 1000)
                    dateAddedMillis > 0 -> Date(dateAddedMillis * 1000)
                    else -> Date(0) // Default fallback if no date is available
                }

                val size = it.getLong(sizeColumn)
                val width = it.getInt(widthColumn)
                val height = it.getInt(heightColumn)
                val mimeType = it.getString(mimeTypeColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                val photo = Photo(
                    id,
                    uri,
                    null,
                    name,
                    date,
                    size,
                    width,
                    height,
                    mimeType
                )
                if (filter.apply(photo)) {
                    photos.add(photo)
                }
            }
        }
        return photos
    }



}
