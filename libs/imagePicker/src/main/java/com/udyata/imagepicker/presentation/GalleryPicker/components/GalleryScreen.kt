package com.udyata.composegalley.presentation

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
fun SorterRows(
    modifier: Modifier = Modifier,
    selectedSort: PhotoSort,
    onSortSelected: (PhotoSort) -> Unit
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
            SorterRowItem(
                sort = PhotoSort.All,
                isSelected = selectedSort == PhotoSort.All,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            SorterRowItem(
                sort = PhotoSort.Weekly,
                isSelected = selectedSort == PhotoSort.Weekly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            SorterRowItem(
                sort = PhotoSort.Monthly,
                isSelected = selectedSort == PhotoSort.Monthly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
            SorterRowItem(
                sort = PhotoSort.Yearly,
                isSelected = selectedSort == PhotoSort.Yearly,
                onSortSelected = onSortSelected,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SorterRowItem(
    modifier: Modifier = Modifier,
    sort: PhotoSort,
    isSelected: Boolean,
    onSortSelected: (PhotoSort) -> Unit
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


//@Composable
//fun SorterRowItem(
//    sort: PhotoSort,
//    isSelected: Boolean,
//    onSortSelected: (PhotoSort) -> Unit
//) {
//    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
//
//    TextButton(onClick = { onSortSelected(sort) }) {
//        Text(text = sort.name, color = textColor)
//    }
//}
