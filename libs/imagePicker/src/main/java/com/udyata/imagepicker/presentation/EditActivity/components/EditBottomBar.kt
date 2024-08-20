package com.udyata.imagepicker.presentation.EditActivity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.udyata.imagepicker.R
import com.udyata.imagepicker.theme.ComposeCropperTheme


@Composable
fun EditBottomBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    canRevert: Boolean = false,
    onCancel: () -> Unit,
    onOverride: () -> Unit,
    onRevert: () -> Unit,
    onSaveCopy: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onCancel, enabled = enabled) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(id = R.string.action_cancel)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*OutlinedButton(onClick = onOverride, enabled = enabled) {
                Text(text = stringResource(R.string.override))
            }*/
            TextButton(onClick = onRevert, enabled = canRevert, shape = RectangleShape) {
                Text(text = "Revert")
            }
            Button(onClick = onSaveCopy, enabled = enabled, shape = RectangleShape) {
                Text(text = stringResource(R.string.save_copy))
            }
        }
    }
}

@Preview(showBackground = true, wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
private fun Preview() {
    ComposeCropperTheme(
        darkTheme = true
    ) {
        Surface(
            color = Color.Black
        ) {
            EditBottomBar(
                onCancel = { /*TODO*/ },
                onOverride = { /*TODO*/ },
                onRevert = {},
                onSaveCopy = {}
            )
        }
    }
}