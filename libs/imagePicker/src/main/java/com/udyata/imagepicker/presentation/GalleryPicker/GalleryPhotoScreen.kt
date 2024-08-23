package com.udyata.imagepicker.presentation.GalleryPicker

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.udyata.composegalley.presentation.GalleryViewModel
import com.udyata.composegalley.presentation.PhotoEvent
import com.udyata.composegalley.presentation.SorterRows
import com.udyata.composegalley.presentation.galleryphotoscreen.SelectionSheet
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.data.model.PhotoSort
import com.udyata.imagepicker.helper_libs.pinchzoomgrid.PinchZoomGridLayout
import com.udyata.imagepicker.helper_libs.pinchzoomgrid.rememberPinchZoomGridState
import com.udyata.imagepicker.navigation.Screen
import com.udyata.imagepicker.presentation.GalleryPicker.components.CheckBox
import com.udyata.imagepicker.presentation.GalleryPicker.components.DebounceImageLoader
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryPhotoScreen(
    viewModel: GalleryViewModel,
    onSelectImage: (Uri) -> Unit,
    isMultiSelection: Boolean = false,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val groupedPhotos by viewModel.groupedPhotos.collectAsState()
    val photos by viewModel.photos.collectAsState()
    val selectedSortOrder by viewModel.selectedSort.collectAsState()

    val selectedPhotos = remember { mutableStateListOf<Photo>() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val cellsList = remember {
        listOf(
            GridCells.Fixed(5), // ↑ Zoom out
            GridCells.Fixed(4), // |
            GridCells.Fixed(3), // ↓ Zoom in
        )
    }
    val state = rememberPinchZoomGridState(
        cellsList = cellsList,
        initialCellsIndex = 1,
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                actions = {
//                    IconButton(onClick = { navController.navigate(Screen.GalleryAlbumScreen.route) }) {
//                        Icon(imageVector = Icons.Default.Album, contentDescription = "Album")
//                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box {
                PinchZoomGridLayout(state = state) {
                    val showStickyHeader by remember {
                        derivedStateOf { gridState.firstVisibleItemIndex >= 1 && !state.isZooming }
                    }
                    val showUpArrow = remember { derivedStateOf { gridState.firstVisibleItemIndex > 10 } }

                    LazyVerticalGrid(
                        columns = gridCells,
                        state = gridState,
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        item(span = { GridItemSpan(this.maxLineSpan) }) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(357.dp)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val title = when (selectedSortOrder) {
                                        PhotoSort.All -> "Gallery Photos"
                                        PhotoSort.Weekly -> "Weekly Photos"
                                        PhotoSort.Monthly -> "Monthly Photos"
                                        PhotoSort.Yearly -> "Yearly Photos"
                                        else  -> "Gallery Photos"
                                    }
                                    Text(text = title)
                                }
                            }
                        }

                        if (!showStickyHeader) {
                            item(span = { GridItemSpan(this.maxLineSpan) }) {
                                SorterRows(
                                    selectedSort = selectedSortOrder,
                                    onSortSelected = { sort ->
                                        viewModel.onEvent(PhotoEvent.UpdateSort(sort))
                                    }
                                )
                            }
                        }

                        groupedPhotos.forEach { group ->
                            val isGroupSelected = group.body.all { selectedPhotos.contains(it.photo) }

                            item(span = { GridItemSpan(this.maxLineSpan) }) {
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .pinchItem(key = "stickies"),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = group.header.date,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )

                                        if (isMultiSelection) {
                                            CheckBox(
                                                isChecked = isGroupSelected,
                                                onCheck = {
                                                    val photosInGroup = group.body.map { it.photo }
                                                    if (isGroupSelected) {
                                                        selectedPhotos.removeAll(photosInGroup)
                                                    } else {
                                                        selectedPhotos.addAll(photosInGroup)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }


                            items(group.body, key = { it.photo.id }) { body ->
                                DebounceImageLoader(
                                    groupName = group.header.date,
                                    modifier = Modifier.pinchItem(key = body),
                                    photo = body.photo,
                                    isSelected = selectedPhotos.contains(body.photo),
                                    onPhotoClick = { photo ->
                                        if (selectedPhotos.isNotEmpty() && isMultiSelection) {
                                            if (selectedPhotos.contains(photo)) {
                                                selectedPhotos.remove(photo)
                                            } else {
                                                selectedPhotos.add(photo)
                                            }
                                        } else {
                                            onSelectImage(body.photo.uri)
                                        }
                                    },
                                    onPhotoLongClick = { photo ->
                                        if (!selectedPhotos.contains(photo) && isMultiSelection) {
                                            selectedPhotos.add(photo)
                                        }
                                    },
                                    navController = navController
                                )
                            }

                            item(span = { GridItemSpan(this.maxLineSpan) }) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    if (showStickyHeader) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp)
                                .zIndex(1f)
                                .background(Color.Transparent)
                        ) {
                            SorterRows(
                                selectedSort = selectedSortOrder,
                                onSortSelected = { sort ->
                                    viewModel.onEvent(PhotoEvent.UpdateSort(sort))
                                }
                            )
                        }
                    }

                    SelectionSheet(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        selectionState = selectedPhotos.isNotEmpty(),
                        clearSelection = {
                            selectedPhotos.clear()
                        },
                        selectedItemCount = selectedPhotos.size,
                        onSubmit = {

                        }
                    )

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showUpArrow.value,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 80.dp, end = 20.dp),
                        enter = slideInVertically { it * 2 },
                        exit = slideOutVertically { it * 2 }
                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    gridState.scrollToItem(index = 0)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Scroll to Top"
                            )
                        }
                    }
                }
            }
        }
    }
}
