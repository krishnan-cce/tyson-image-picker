package com.udyata.imagepicker.presentation.GalleryAlbumScreen.components

import com.udyata.imagepicker.data.model.AlbumSort
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.data.model.PhotoSort

@Composable
fun AlbumSorterRows(
    modifier: Modifier = Modifier,
    selectedSort: AlbumSort,
    onSortSelected: (AlbumSort) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = Color.White.copy(.95f)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AlbumSorterRowItem(
                sort = AlbumSort.All,
                isSelected = selectedSort == AlbumSort.All,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            AlbumSorterRowItem(
                sort = AlbumSort.Weekly,
                isSelected = selectedSort == AlbumSort.Weekly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            AlbumSorterRowItem(
                sort = AlbumSort.Monthly,
                isSelected = selectedSort == AlbumSort.Monthly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            AlbumSorterRowItem(
                sort = AlbumSort.Yearly,
                isSelected = selectedSort == AlbumSort.Yearly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSorterRowItem(
    modifier: Modifier = Modifier,
    sort: AlbumSort,
    isSelected: Boolean,
    onSortSelected: (AlbumSort) -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    AssistChip(
        onClick = { onSortSelected(sort) },
        label = {
            Text(
                text = sort.name,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor
        ),
        border = null,
        modifier = modifier
            .height(40.dp)
    )
}
