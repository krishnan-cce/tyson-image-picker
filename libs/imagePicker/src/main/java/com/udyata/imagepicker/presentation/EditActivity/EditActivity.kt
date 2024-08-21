package com.udyata.imagepicker.presentation.EditActivity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.udyata.imagepicker.di.AppModule
import com.udyata.imagepicker.theme.ComposeCropperTheme
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.Q)
class EditActivity : ComponentActivity() {

    private var ImageUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            enableEdgeToEdge()
            ComposeCropperTheme(darkTheme = false) {
                val context = LocalContext.current

                val viewModel: EditViewModel = viewModel(
                    factory = EditViewModelFactory(application)
                )

                LaunchedEffect(true) {
                    viewModel.loadImage(Uri.parse(ImageUri))
                }

                EditScreen(
                    viewModel = viewModel,
                    onNavigateUp = ::finish,
                    onSaveResult = { uri ->
                        sendResult(uri)
                    }
                )
            }
        }
    }

    private fun sendResult(item: Uri) {
        val uriString = item.toString()
        Log.d("EditActivity", "Edited Image URI: $uriString")
        val intent = Intent()
        intent.putExtra("edited_image_uri", uriString)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getIntentData() {
        ImageUri = intent.getStringExtra("imageUri") ?: ""
    }

}

