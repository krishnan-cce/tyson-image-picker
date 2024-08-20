package com.udyata.imagepicker.presentation.EditActivity.components

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.theme.ComposeCropperTheme
import kotlinx.parcelize.Parcelize

@Composable
fun EditOptions(
    modifier: Modifier = Modifier,
    selectedOption: MutableState<EditOption>,
    options: SnapshotStateList<EditOption>
) {
    val filteredOptions = remember(options.size) {
        options.filter { it.isEnabled }
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        userScrollEnabled = true,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        items(
            items = filteredOptions,
        ) {
            InputChip(
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = null,
                shape = RoundedCornerShape(8.dp),
                selected = selectedOption.value == it,
                onClick = {
                    selectedOption.value = it
                },
                label = {
                    Text(text = it.title)
                },
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true, wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
private fun Preview() {
    ComposeCropperTheme(
        darkTheme = false
    ) {
        Surface(
            color = Color.Black
        ) {
            val options = remember {
                listOf(
                    EditOption(
                        title = "Crop",
                        id = EditId.CROP
                    ),
                    EditOption(
                        title = "Adjust",
                        id = EditId.ADJUST
                    ),
                    EditOption(
                        title = "Filters",
                        id = EditId.FILTERS
                    ),
                    EditOption(
                        title = "Markup",
                        id = EditId.MARKUP
                    ),
                    EditOption(
                        title = "More",
                        id = EditId.MORE
                    ),
                ).toMutableStateList()
            }
            EditOptions(
                selectedOption = remember { mutableStateOf(options.first()) },
                options = options
            )
        }
    }
}

@Parcelize
data class EditOption(
    val id: EditId,
    val title: String,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
) : Parcelable

@Parcelize
enum class EditId : Parcelable {
    CROP, ADJUST, FILTERS, MARKUP, MORE
}