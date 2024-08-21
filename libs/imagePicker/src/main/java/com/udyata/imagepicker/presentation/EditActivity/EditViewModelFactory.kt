package com.udyata.imagepicker.presentation.EditActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udyata.imagepicker.di.AppModule

class EditViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            val context = application.applicationContext
            val request = AppModule.getImageRequest(context)
            val loader = AppModule.getImageLoader(context)
            val imageRepository = AppModule.provideImageRepository(context)
            return EditViewModel(application, loader, request, imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
