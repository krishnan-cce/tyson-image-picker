package com.udyata.composecropper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale
import com.udyata.composecropper.ui.theme.ComposeCropperTheme

import com.udyata.imagepicker.presentation.GalleryPicker.GalleryPickerActivity


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCropperTheme {
                val context = LocalContext.current
                val editedImageUri = remember { mutableStateOf<Uri?>(null) }

                val galleryPickerLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == AppCompatActivity.RESULT_OK) {
                            val item = result.data?.getStringExtra("edited_image_uri")
                            item?.let { uri ->
                                editedImageUri.value = Uri.parse(uri)
                                Log.d("result --->MainActivity", "Edited Image URI: $uri")
                            }
                        }
                    }



                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {

                        Button(onClick = {
                            editedImageUri.value = null
                            val intent =
                                Intent(context, GalleryPickerActivity::class.java).apply {
                                    putExtra("isMultiSelection", false)
                                }
                            galleryPickerLauncher.launch(intent)

                        }) {
                            Text(text = "Single Picker")
                        }

                        Button(onClick = {
                            editedImageUri.value = null
                            val intent =
                                Intent(context, GalleryPickerActivity::class.java).apply {
                                    putExtra("isMultiSelection", true)
                                }
                            galleryPickerLauncher.launch(intent)

                        }) {
                            Text(text = "Multi Picker")
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    editedImageUri.value?.let { uri ->

                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(uri)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .scale(Scale.FIT)
                                .build(),
                            contentScale = ContentScale.FillBounds,
                            filterQuality = FilterQuality.High
                        )

                        Image(
                            painter = painter,
                            contentDescription = "Edited Image",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                        )
                    }

                }
            }
        }
    }

}
