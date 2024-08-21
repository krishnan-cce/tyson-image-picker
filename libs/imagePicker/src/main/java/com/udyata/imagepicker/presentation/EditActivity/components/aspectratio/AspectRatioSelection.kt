package com.udyata.imagepicker.presentation.EditActivity.components.aspectratio

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.helper_libs.cropper.model.AspectRatio
import com.udyata.imagepicker.helper_libs.cropper.model.CropAspectRatio
import com.udyata.imagepicker.helper_libs.cropper.model.aspectRatios
import com.udyata.imagepicker.helper_libs.cropper.widgets.AspectRatioSelectionCard


@Composable
internal fun AnimatedAspectRatioSelection(
    modifier: Modifier = Modifier,
    onAspectRatioChange: (Int,CropAspectRatio) -> Unit,
    initialSelectedIndex: Int
) {

    val lazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(aspectRatios) { index, item ->


            val isSelected = index == initialSelectedIndex

            val color by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                animationSpec = tween(durationMillis = 300), label = ""
            )

            AspectRatioSelectionCard(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        Log.d("initialSelectedIndex", initialSelectedIndex.toString())
                        onAspectRatioChange(index,item)
                    },
                contentColor = MaterialTheme.colorScheme.surface,
                color = color,
                cropAspectRatio = item,
            )

        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewAnimatedAspectRatioSelection(modifier: Modifier = Modifier) {
//
//    AnimatedAspectRatioSelection {
//
//    }
//}