package com.udyata.imagepicker.presentation.EditActivity.components.filters

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale
import com.udyata.imagepicker.data.model.ImageFilter
import com.udyata.imagepicker.presentation.EditActivity.EditViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSelector(
    filters: List<ImageFilter>,
    viewModel: EditViewModel
) {
    val groups = filters.map { it.group }.distinct()
    var selectedGroup by remember { mutableStateOf(groups.firstOrNull() ?: "General") }

    Column {
        // Group Selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups.forEachIndexed{index, group ->
                item(
                    key = "${index}__${group}"
                ) {
                    Text(
                        text = group,
                        modifier = Modifier
                            .clickable { selectedGroup = group }
                            .background(
                                color = if (selectedGroup == group) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (selectedGroup == group) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Filter Selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = filters.filter { it.group == selectedGroup },
                key = { it.name }
            ) {
                FilterItem(
                    imageFilter = it,
                    currentFilter = viewModel.currentFilter
                ) {
                    viewModel.addFilter(it)
                }
            }
        }
    }
}


//@Composable
//fun FilterSelector(
//    filters: List<ImageFilter>,
//    viewModel: EditViewModel
//) {
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(
//            items = filters,
//            key = { it.name }
//        ) {
//            FilterItem(
//                imageFilter = it,
//                currentFilter = viewModel.currentFilter
//            ) {
//                viewModel.addFilter(it)
//            }
//        }
//    }
//}

@Composable
fun FilterItem(
    imageFilter: ImageFilter,
    currentFilter: MutableState<ImageFilter?>,
    onFilterSelect: () -> Unit
) {
    val isSelected = remember (currentFilter.value) {
        currentFilter.value?.name == imageFilter.name ||
                currentFilter.value == null && imageFilter.name == "None"
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val widthAnimation by animateDpAsState(
            targetValue = if (isSelected) 4.dp else 0.dp,
            label = "widthAnimation"
        )
        val colorAnimation by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.tertiary
            else Color.Transparent, label = "colorAnimation"
        )
        AsyncImage(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = widthAnimation,
                    shape = RoundedCornerShape(16.dp),
                    color = colorAnimation
                )
                .clickable(
                    enabled = !isSelected,
                    onClick = onFilterSelect
                ),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(imageFilter.filterPreview)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .scale(Scale.FIT)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = imageFilter.name
        )
        Text(
            text = imageFilter.name,
            fontWeight = if (isSelected) FontWeight.Bold
            else FontWeight.Normal
        )
    }
}