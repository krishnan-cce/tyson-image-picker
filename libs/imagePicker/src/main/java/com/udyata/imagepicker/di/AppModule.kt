package com.udyata.imagepicker.di

import android.content.ContentResolver
import android.content.Context
import coil3.ImageLoader
import coil3.request.ImageRequest
import com.udyata.imagepicker.data.repository.ImageRepository
import com.udyata.imagepicker.data.repository.PhotoRepository
import com.udyata.imagepicker.domain.repository.ImageRepositoryImpl
import com.udyata.imagepicker.domain.repository.PhotoRepositoryImpl
import com.udyata.imagepicker.domain.usecase.GetPhotosUseCase

object AppModule {

    fun getImageLoader(context: Context): ImageLoader = ImageLoader(context)

    fun getImageRequest(context: Context): ImageRequest.Builder =
        ImageRequest.Builder(context)

    fun provideContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }

    fun providePhotoRepository(contentResolver: ContentResolver): PhotoRepository {
        return PhotoRepositoryImpl(contentResolver)
    }

    fun provideImageRepository(context: Context): ImageRepository {
        return ImageRepositoryImpl(context)
    }

    fun provideGetPhotosUseCase(photoRepository: PhotoRepository): GetPhotosUseCase {
        return GetPhotosUseCase(photoRepository)
    }

}