package com.udyata.imagepicker.domain.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.udyata.imagepicker.data.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageRepositoryImpl(private val context: Context) : ImageRepository {

    override suspend fun saveImageToGallery(bitmap: Bitmap): Uri {
        return withContext(Dispatchers.IO) {
            val filename = "edited_image_${System.currentTimeMillis()}.png"
            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDir, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val contentResolver = context.contentResolver
            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                uri
            } ?: throw IOException("Failed to create MediaStore entry for image")
        }
    }
}
