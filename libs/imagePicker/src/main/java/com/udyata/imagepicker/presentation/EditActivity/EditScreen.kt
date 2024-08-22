package com.udyata.imagepicker.presentation.EditActivity

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.helper_libs.cropper.model.AspectRatio
import com.udyata.imagepicker.helper_libs.cropper.model.OutlineType
import com.udyata.imagepicker.helper_libs.cropper.model.RectCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.aspectRatios
import com.udyata.imagepicker.helper_libs.cropper.settings.CropDefaults
import com.udyata.imagepicker.helper_libs.cropper.settings.CropOutlineProperty
import com.udyata.imagepicker.presentation.EditActivity.components.EditBottomBar
import com.udyata.imagepicker.presentation.EditActivity.components.EditId
import com.udyata.imagepicker.presentation.EditActivity.components.EditOption
import com.udyata.imagepicker.presentation.EditActivity.components.EditOptions
import com.udyata.imagepicker.presentation.EditActivity.components.MoreSelector
import com.udyata.imagepicker.presentation.EditActivity.components.adjustments.AdjustmentFilter
import com.udyata.imagepicker.presentation.EditActivity.components.adjustments.AdjustmentSelector
import com.udyata.imagepicker.presentation.EditActivity.components.aspectratio.AnimatedAspectRatioSelection
import com.udyata.imagepicker.presentation.EditActivity.components.filters.FilterSelector
import com.udyata.imagepicker.presentation.EditActivity.components.filters.HorizontalScrubber
import com.udyata.imagepicker.presentation.EditActivity.crop.CropOptions
import com.udyata.imagepicker.presentation.EditActivity.crop.Cropper
import com.udyata.imagepicker.utils.getEditImageCapableApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditScreen(
    viewModel: EditViewModel,
    onNavigateUp: () -> Unit = {},
    onSaveResult: (Uri) -> Unit
) {
    val image by viewModel.image.collectAsState()
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
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
                isEnabled = true,
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
    val selectedAdjFilter = remember {
        mutableStateOf<Pair<AdjustmentFilter, Float>?>(null)
    }

    var showAspectRatioSelection by remember { mutableStateOf(false) }

    var cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                aspectRatio = AspectRatio.Original,
                cropOutlineProperty = CropOutlineProperty(
                    outlineType = OutlineType.RoundedRect,
                    cropOutline = RectCropShape(
                        id = 0,
                        title = OutlineType.RoundedRect.name
                    )
                ),
                handleSize = handleSize,
                maxZoom = 5f,
                overlayRatio = 1f,
                pannable = true,
                fling = false,
                rotatable = false
            )
        )
    }
    var initialSelectedIndex by remember { mutableIntStateOf(aspectRatios.indexOfFirst {
        it.aspectRatio == cropProperties.aspectRatio
    }.takeIf { it != -1 } ?: 0) }



//    val selectedAdjFilter = remember {
//        val noneFilter = EditViewModel.adjustmentFilters.find { it.tag == Adjustment.NONE }
//        mutableStateOf(noneFilter?.let { it to it.defaultValue })
//    }

    val scope = rememberCoroutineScope(getContext = { Dispatchers.IO })
    val filters by viewModel.filters.collectAsState(context = Dispatchers.IO)

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
                        cropProperties = cropProperties,
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
                verticalAlignment = Alignment.Bottom,
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        Column {
                            AnimatedVisibility(
                                visible = showAspectRatioSelection,
                                enter = fadeIn(tween(300)),
                                exit = fadeOut(tween(300))
                            ) {
                                AnimatedAspectRatioSelection(
                                    initialSelectedIndex = initialSelectedIndex,
                                    onAspectRatioChange = { index,cropAspectRatio ->
                                        cropProperties = cropProperties.copy(
                                            aspectRatio = cropAspectRatio.aspectRatio
                                        )
                                        initialSelectedIndex = index
                                    }
                                )
                            }

                            CropOptions(
                                onMirrorPressed = {
                                    viewModel.flipHorizontally()
                                },
                                onRotatePressed = {
                                    //cropRotation += 90f
                                    viewModel.addAngle(90f)
                                },
                                onAspectRationPressed = {
                                    showAspectRatioSelection = !showAspectRatioSelection
                                },
                                onCropPressed = {
                                    crop = true
                                }
                            )
                        }
                    }
                    1 -> {
                        Column {
                            val state =
                                rememberPagerState(pageCount = EditViewModel.adjustmentFilters::size)
                            LaunchedEffect(selectedAdjFilter.value) {
                                selectedAdjFilter.value?.let {
                                    val index = EditViewModel.adjustmentFilters.indexOf(it.first)
                                    state.scrollToPage(index)
                                }
                            }
                            AnimatedVisibility(
                                visible = selectedAdjFilter.value != null,
                                enter = slideInVertically(),
                                exit = slideOutVertically()
                            ) {
                                HorizontalPager(
                                    state = state,
                                    userScrollEnabled = false,
                                ) {
                                    val adjustment = EditViewModel.adjustmentFilters[it]
                                    var currentValue by rememberSaveable {
                                        mutableFloatStateOf(
                                            selectedAdjFilter.value?.second
                                                ?: adjustment.defaultValue
                                        )
                                    }
                                    HorizontalScrubber(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        displayValue = { value ->
                                            (value * 100).roundToInt().toString()
                                        },
                                        minValue = remember(adjustment, adjustment::minValue),
                                        maxValue = remember(adjustment, adjustment::maxValue),
                                        defaultValue = remember(
                                            adjustment,
                                            adjustment::defaultValue
                                        ),
                                        allowNegative = remember(adjustment) { adjustment.minValue < 0f },
                                        currentValue = currentValue,
                                        onValueChanged = { isScrolling, newValue ->
                                            scope.launch {
                                                if (selectedAdjFilter.value != null) {
                                                    viewModel.addAdjustment(
                                                        isScrolling,
                                                        selectedAdjFilter.value!!.first to newValue
                                                    )
                                                    currentValue = newValue
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            AdjustmentSelector(
                                viewModel = viewModel,
                                selectedFilter = selectedAdjFilter
                            )
                        }
                    }

                    2 -> {
                        FilterSelector(
                            filters = filters,
                            viewModel = viewModel
                        )
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


