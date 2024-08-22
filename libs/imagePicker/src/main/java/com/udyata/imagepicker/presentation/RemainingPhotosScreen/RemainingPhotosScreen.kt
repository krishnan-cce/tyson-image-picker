package com.udyata.imagepicker.presentation.RemainingPhotosScreen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.udyata.composegalley.presentation.GalleryViewModel
import com.udyata.imagepicker.presentation.GalleryPicker.components.DebounceImageLoader

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RemainingPhotosScreen(
    groupId: Int,
    groupName: String,
    viewModel: GalleryViewModel,
    onSelectImage: (Uri) -> Unit,
    navController:NavHostController
 ) {
    val groupPhotos by viewModel.getPhotosForGroup(groupId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
                .padding(it)
        ) {
            items(groupPhotos) { photo ->
                DebounceImageLoader(
                    modifier = Modifier.fillMaxWidth(),
                    photo = photo,
                    isSelected = false,
                    onPhotoClick = { onSelectImage(photo.uri) },
                    onPhotoLongClick = {},
                    navController = navController,
                    groupName = groupName
                )
            }
        }
    }
}
