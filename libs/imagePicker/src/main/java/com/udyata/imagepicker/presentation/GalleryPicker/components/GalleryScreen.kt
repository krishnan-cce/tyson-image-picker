package com.udyata.composegalley.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SorterRowItem(
                sort = PhotoSort.All,
                isSelected = selectedSort == PhotoSort.All,
                onSortSelected = onSortSelected
            )
            SorterRowItem(
                sort = PhotoSort.Weekly,
                isSelected = selectedSort == PhotoSort.Weekly,
                onSortSelected = onSortSelected
            )
            SorterRowItem(
                sort = PhotoSort.Monthly,
                isSelected = selectedSort == PhotoSort.Monthly,
                onSortSelected = onSortSelected
            )
            SorterRowItem(
                sort = PhotoSort.Yearly,
                isSelected = selectedSort == PhotoSort.Yearly,
                onSortSelected = onSortSelected
            )
        }
    }
}

@Composable
fun SorterRowItem(
    sort: PhotoSort,
    isSelected: Boolean,
    onSortSelected: (PhotoSort) -> Unit
) {
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    TextButton(onClick = { onSortSelected(sort) }) {
        Text(text = sort.name, color = textColor)
    }
}


//@Composable
//fun SorterRows(modifier: Modifier = Modifier, onSortSelected: (PhotoSort) -> Unit) {
//    Surface(
//        modifier= modifier
//            .fillMaxWidth()
//            .padding(start = 8.dp, end = 8.dp)
//    ) {
//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            TextButton(onClick = { onSortSelected(PhotoSort.All) }) {
//                Text(text = "All")
//            }
//            TextButton(onClick = { onSortSelected(PhotoSort.Weekly) }) {
//                Text(text = "Weekly")
//            }
//            TextButton(onClick = { onSortSelected(PhotoSort.Monthly) }) {
//                Text(text = "Monthly")
//            }
//            TextButton(onClick = { onSortSelected(PhotoSort.Yearly) }) {
//                Text(text = "Yearly")
//            }
//        }
//    }
//}
