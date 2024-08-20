package com.udyata.imagepicker.data.repository

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {
    suspend fun saveImageToGallery(bitmap: Bitmap): Uri
}
