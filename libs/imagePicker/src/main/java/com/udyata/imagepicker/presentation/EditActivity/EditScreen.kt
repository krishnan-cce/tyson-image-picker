package com.udyata.imagepicker.presentation.EditActivity

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.helper_libs.cropper.model.OutlineType
import com.udyata.imagepicker.helper_libs.cropper.model.RectCropShape
import com.udyata.imagepicker.helper_libs.cropper.settings.CropDefaults
import com.udyata.imagepicker.helper_libs.cropper.settings.CropOutlineProperty
import com.udyata.imagepicker.presentation.EditActivity.components.EditBottomBar
import com.udyata.imagepicker.presentation.EditActivity.components.EditId
import com.udyata.imagepicker.presentation.EditActivity.components.EditOption
import com.udyata.imagepicker.presentation.EditActivity.components.EditOptions
import com.udyata.imagepicker.presentation.EditActivity.components.MoreSelector
import com.udyata.imagepicker.presentation.EditActivity.crop.CropOptions
import com.udyata.imagepicker.presentation.EditActivity.crop.Cropper
import com.udyata.imagepicker.utils.getEditImageCapableApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditScreen(
    viewModel: EditViewModel,
    onNavigateUp: () -> Unit = {},
    onSaveResult: (Uri) -> Unit
) {
     val image by viewModel.image.collectAsState()
    val mediaRef by viewModel.mediaRef.collectAsState()
    var crop by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val editApps = remember(context, context::getEditImageCapableApps)

    val options = remember(editApps) {
        mutableStateListOf(
            EditOption(
                title = "Crop",
                id = EditId.CROP
            ),
            EditOption(
                title = "Adjust",
                isEnabled = false,
                id = EditId.ADJUST
            ),
            EditOption(
                title = "Filters",
                id = EditId.FILTERS
            ),
            EditOption(
                title = "Markup",
                isEnabled = false,
                id = EditId.MARKUP
            ),
        )
    }
    LaunchedEffect(editApps) {
        if (editApps.isNotEmpty()) {
            options.add(
                EditOption(
                    title = "More",
                    id = EditId.MORE
                )
            )
        }
    }
    val selectedOption = remember {
        mutableStateOf(options.first())
    }

    val scope = rememberCoroutineScope(getContext = { Dispatchers.IO })
     val cropEnabled by remember(selectedOption.value) { mutableStateOf(selectedOption.value.id == EditId.CROP) }
    val pagerState = rememberPagerState { options.size }
    LaunchedEffect(selectedOption.value.id) {
        pagerState.scrollToPage(
            when (selectedOption.value.id) {
                EditId.CROP -> 0
                EditId.ADJUST -> 1
                EditId.FILTERS -> 2
                EditId.MARKUP -> 3
                EditId.MORE -> 4
            }
        )
    }

    Scaffold(

        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                EditOptions(
                    options = options,
                    selectedOption = selectedOption
                )
                EditBottomBar(
                    onCancel = onNavigateUp,
                    enabled = !saving,
                    canRevert = viewModel.canRevert.value,
                    onOverride = { },
                    onRevert = viewModel::revert,
                    onSaveCopy = {
                        scope.launch {
                            image?.let { bitmap ->
                                viewModel.saveEditedImage(bitmap) { uri ->
                                    onSaveResult(uri)
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
                .padding(paddingValues),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize()

            ) {
                image?.let { imageBitmap ->
                    Cropper(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .background(Color.Black),
                        bitmap = imageBitmap,
                        cropEnabled = cropEnabled,
                        cropProperties = CropDefaults.properties(
                            cropOutlineProperty = CropOutlineProperty(
                                outlineType = OutlineType.RoundedRect,
                                cropOutline = RectCropShape(
                                    id = 0,
                                    title = OutlineType.RoundedRect.name
                                )
                            ),
                            maxZoom = 5f,
                            overlayRatio = 1f,
                            pannable = true,
                            fling = false,
                            rotatable = false
                        ),
                        crop = crop,
                        onCropStart = {
                            saving = true
                        },
                        onCropSuccess = { newImage ->
                            scope.launch {
                                viewModel.addCroppedImage(newImage)
                            }
                            crop = false
                            saving = false
                        }
                    )
                }

                this@Column.AnimatedVisibility(
                    visible = saving,
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(150))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

            }

            HorizontalPager(
                modifier = Modifier
                    .wrapContentHeight()
                    .animateContentSize(),
                userScrollEnabled = false,
                beyondBoundsPageCount = 1,
                verticalAlignment = Alignment.Bottom,
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        Log.d("EditScreen", "0")
                        CropOptions(
                            onMirrorPressed = {
                                viewModel.flipHorizontally()
                            },
                            onRotatePressed = {
                                //cropRotation += 90f
                                viewModel.addAngle(90f)
                            },
                            onAspectRationPressed = {

                            },
                            onCropPressed = {
                                crop = true
                            }
                        )
                    }
                    1 -> {


                    }

                    4 -> {
                        MoreSelector(
                            editApps = editApps,
                            currentUri = viewModel.currentUri
                        )
                    }
                }
            }
        }
    }
}


