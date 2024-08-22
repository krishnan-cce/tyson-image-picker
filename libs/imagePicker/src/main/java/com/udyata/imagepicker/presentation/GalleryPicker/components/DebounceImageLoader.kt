package com.udyata.imagepicker.presentation.GalleryPicker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.EqualityDelegate
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.navigation.Screen


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebounceImageLoader(
    modifier: Modifier = Modifier,
    photo: Photo,
    isSelected: Boolean,
    onPhotoClick: (Photo) -> Unit,
    onPhotoLongClick: (Photo) -> Unit,
    navController: NavHostController,
    groupName: String
) {
    if (photo.id < 0L) {
        val groupId = extractGroupIdFromPhoto(photo)


        Box(
            modifier = modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .clickable { navController.navigate(Screen.RemainingPhotosScreen.createRoute(groupId,groupName)) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = photo.name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
        }
    } else {
        // Normal photo display logic
        val selectedSize by animateDpAsState(
            if (isSelected) 12.dp else 0.dp, label = "selectedSize"
        )
        val scale by animateFloatAsState(
            if (isSelected) 0.5f else 1f, label = "scale"
        )
        val selectedShapeSize by animateDpAsState(
            if (isSelected) 16.dp else 12.dp, label = "selectedShapeSize"
        )
        val strokeSize by animateDpAsState(
            targetValue = if (isSelected) 2.dp else 0.dp, label = "strokeSize"
        )
        val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
        val strokeColor by animateColorAsState(
            targetValue = if (isSelected) primaryContainerColor else Color.Transparent,
            label = "strokeColor"
        )

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(photo.uri)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .placeholderMemoryCacheKey(photo.toString())
                .scale(Scale.FIT)
                .build(),
            modelEqualityDelegate = MediaEqualityDelegate(),
            contentScale = ContentScale.FillBounds,
            filterQuality = FilterQuality.High
        )

        Box(
            modifier = modifier
                .combinedClickable(
                    enabled = true,
                    onClick = {
                        onPhotoClick(photo)
                    },
                    onLongClick = {
                        onPhotoLongClick(photo)
                    },
                )
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(RoundedCornerShape(selectedShapeSize))
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f)
                    .padding(selectedSize)
                    .clip(RoundedCornerShape(selectedShapeSize))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(selectedShapeSize)
                    )
                    .border(
                        width = strokeSize,
                        shape = RoundedCornerShape(selectedShapeSize),
                        color = strokeColor
                    )
            ) {
                Image(
                    modifier = Modifier
                        .aspectRatio(1f),
                    painter = painter,
                    contentDescription = photo.name,
                    contentScale = ContentScale.Crop,
                )

                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(150))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        CheckBox(isChecked = isSelected)
                    }
                }
            }
        }
    }
}


class MediaEqualityDelegate : EqualityDelegate {
    override fun equals(self: Any?, other: Any?): Boolean = true

    override fun hashCode(self: Any?): Int = 31
}

fun extractGroupIdFromPhoto(photo: Photo): Int {
    // The dummy photo's id is negative, so we reverse the calculation to get the groupId
    return if (photo.id < 0L) {
        ((-photo.id - 1) / 1000L).toInt()
    } else {
        // If the photo ID is not negative, it's not a dummy photo, so handle accordingly
        -1 // or throw an exception / handle error case
    }
}
