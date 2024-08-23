package com.udyata.imagepicker.presentation.GalleryAlbumScreen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.udyata.composegalley.presentation.GalleryViewModel
import com.udyata.composegalley.presentation.PhotoEvent
import com.udyata.composegalley.presentation.SorterRows
import com.udyata.composegalley.presentation.galleryphotoscreen.SelectionSheet
import com.udyata.imagepicker.data.model.Album
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.helper_libs.pinchzoomgrid.PinchZoomGridLayout
import com.udyata.imagepicker.helper_libs.pinchzoomgrid.rememberPinchZoomGridState
import com.udyata.imagepicker.presentation.GalleryAlbumScreen.components.AlbumSorterRows
import com.udyata.imagepicker.presentation.GalleryAlbumScreen.components.DebounceAlbumLoader
import com.udyata.imagepicker.presentation.GalleryPicker.components.CheckBox
import com.udyata.imagepicker.presentation.GalleryPicker.components.DebounceImageLoader
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryAlbumScreen(
    viewModel: GalleryAlbumViewModel,
    navController: NavHostController
) {
    val albums by viewModel.albums.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Albums") }
            )
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(albums, key = { it.id }) { album ->
                DebounceAlbumLoader(
                    album = album,
                    navController = navController,
                    groupName = album.name
                )
            }
        }
    }
}


