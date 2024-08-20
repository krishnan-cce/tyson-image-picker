package com.udyata.composegalley.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.data.model.PhotoSort


@Composable
fun SorterRows(modifier: Modifier = Modifier, onSortSelected: (PhotoSort) -> Unit) {
    Surface(
        modifier= modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onSortSelected(PhotoSort.All) }) {
                Text(text = "All")
            }
            TextButton(onClick = { onSortSelected(PhotoSort.Weekly) }) {
                Text(text = "Weekly")
            }
            TextButton(onClick = { onSortSelected(PhotoSort.Monthly) }) {
                Text(text = "Monthly")
            }
            TextButton(onClick = { onSortSelected(PhotoSort.Yearly) }) {
                Text(text = "Yearly")
            }
        }
    }
}
